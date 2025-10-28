package com.xin.sql.sql;

import com.alibaba.fastjson.JSON;
import com.xin.sql.db.DataBase;
import com.xin.sql.entity.Column;
import com.xin.sql.entity.Index;
import com.xin.sql.entity.TableSchedule;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author 497668869@qq.com
 * @version 1.0
 * @description 负责sql的查询, 查询索引, 字段等信息
 */
@Slf4j
public class SqlSelect {

    public static final Map<String, String> TABLE_MAP = new HashMap<>();
    public static final Map<String, String> INDEX_MAP = new HashMap<>();
    public static final Map<String, String> COLUMN_MAP = new HashMap<>();

    static {
        COLUMN_MAP.put("Field", "COLUMN_NAME");
        COLUMN_MAP.put("Comment", "COLUMN_COMMENT");
        COLUMN_MAP.put("Type", "COLUMN_TYPE");
        COLUMN_MAP.put("Null", "IS_NULLABLE");
        COLUMN_MAP.put("Extra", "EXTRA");
        COLUMN_MAP.put("Privileges", "PRIVILEGES");
        COLUMN_MAP.put("Collation", "COLLATION_NAME");
        COLUMN_MAP.put("Key", "COLUMN_KEY");
        COLUMN_MAP.put("Default", "COLUMN_DEFAULT");

        INDEX_MAP.put("Comment", "COMMENT");
        INDEX_MAP.put("Null", "NULLABLE");
        INDEX_MAP.put("Table", "TABLE_NAME");
        INDEX_MAP.put("Cardinality", "CARDINALITY");
        INDEX_MAP.put("Non_unique", "NON_UNIQUE");
//        INDEX_MAP.put("Collation","");
        INDEX_MAP.put("Column_name", "COLUMN_NAME");
        INDEX_MAP.put("Packed", "PACKED");
        INDEX_MAP.put("Sub_part", "SUB_PART");
        INDEX_MAP.put("Index_comment", "INDEX_COMMENT");
        INDEX_MAP.put("Seq_in_index", "SEQ_IN_INDEX");
        INDEX_MAP.put("Key_name", "INDEX_NAME");
        INDEX_MAP.put("Index_type", "INDEX_TYPE");

        TABLE_MAP.put("Comment", "TABLE_COMMENT");
        TABLE_MAP.put("Name", "TABLE_NAME");
        TABLE_MAP.put("Data_free", "");
        TABLE_MAP.put("Create_options", "");
        TABLE_MAP.put("Check_time", "");
        TABLE_MAP.put("Collation", "");
        TABLE_MAP.put("Create_time", "");
        TABLE_MAP.put("Avg_row_length", "");
        TABLE_MAP.put("Row_format", "");
        TABLE_MAP.put("Version", "");
        TABLE_MAP.put("Checksum", "");
        TABLE_MAP.put("Update_time", "");
        TABLE_MAP.put("Max_data_length", "");
        TABLE_MAP.put("Index_length", "");
        TABLE_MAP.put("Auto_increment", "");
        TABLE_MAP.put("Engine", "ENGINE");
        TABLE_MAP.put("Data_length", "");
        TABLE_MAP.put("Rows", "");
    }

    private Map<DataBase, Map<String, List<Column>>> dataBaseColumnMap = new HashMap<>();
    private Map<DataBase, Map<String, List<Index>>> dataBaseIndexMap = new HashMap<>();
    private List<TableSchedule> tableSchedules;

    public void mapKeyChange(List<Map<String, Object>> values, Map<String, String> keyMap) {
        for (Map<String, Object> value : values) {
            for (String key : keyMap.keySet()) {
                if (value.containsKey(key)) {
                    value.put(keyMap.get(key), value.get(key));
                }
            }
        }
    }

    public void init(DataBase dataBase, String schema, Predicate<TableSchedule> tableSchedulePredicate) throws SQLException {

        //初始化表
//        tableSchedules = JSON.parseArray(JSON.toJSONString(dataBase.executeSql("SELECT * FROM information_schema. TABLES WHERE TABLE_SCHEMA = '" + schema + "'")),
//                                         TableSchedule.class);
        List<Map<String, Object>> show_table_status = dataBase.executeSql("SHOW TABLE STATUS" + " FROM " + schema);
        mapKeyChange(show_table_status, TABLE_MAP);

        tableSchedules = JSON.parseArray(JSON.toJSONString(show_table_status),
                        TableSchedule.class)
                .stream()
                .filter(tableSchedulePredicate)
                .collect(Collectors.toList());

        for (TableSchedule schedule : tableSchedules) {
            schedule.setTABLE_SCHEMA(schema);
        }
        //初始化索引
//        List<Index> indices = JSON.parseArray(JSON.toJSONString(dataBase.executeSql("SELECT * FROM information_schema.STATISTICS  WHERE TABLE_SCHEMA = '" + schema + "'")),
//                                              Index.class);
        List<Index> indices = tableSchedules.parallelStream().flatMap(tableSchedule -> {

            try {
                List<Map<String, Object>> indexMap = dataBase.executeSql("SHOW INDEX FROM `" + tableSchedule.getTABLE_NAME() + "` FROM " + schema);
                mapKeyChange(indexMap, INDEX_MAP);
                List<Index> eachIndexes = JSON.parseArray(JSON.toJSONString(indexMap),
                        Index.class);
                for (Index eachIndex : eachIndexes) {
                    eachIndex.setTABLE_SCHEMA(tableSchedule.getTABLE_SCHEMA());
                    eachIndex.setTABLE_NAME(tableSchedule.getTABLE_NAME());
                }
                return eachIndexes.stream();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());

        Map<String, List<Index>> indexsMap = indices.stream().collect(Collectors.groupingBy(Index::getTABLE_NAME));
        dataBaseIndexMap.put(dataBase, indexsMap);

        //初始化列信息
        List<Column> columns = tableSchedules.parallelStream().flatMap(tableSchedule -> {

            try {
                List<Map<String, Object>> columnMaps = dataBase.executeSql("SHOW FULL COLUMNS FROM `" + tableSchedule.getTABLE_NAME() + "` FROM " + schema);
                mapKeyChange(columnMaps, COLUMN_MAP);

                List<Column> eachColumns = JSON.parseArray(JSON.toJSONString(columnMaps),
                        Column.class);
                for (Column eachColumn : eachColumns) {
                    eachColumn.setTABLE_SCHEMA(tableSchedule.getTABLE_SCHEMA());
                    eachColumn.setTABLE_NAME(tableSchedule.getTABLE_NAME());
                    // 兼容polar DB, mysql 没有这个配置
                    eachColumn.setEXTRA(eachColumn.getEXTRA().replace("DEFAULT_GENERATED ", ""));
                    eachColumn.setEXTRA(eachColumn.getEXTRA().replace("DEFAULT_GENERATED", ""));
                    // 兼容polar DB, mysql 最小只能是 1970-01-01 08:00:01
                    if (eachColumn.getCOLUMN_DEFAULT() != null) {
                        eachColumn.setCOLUMN_DEFAULT(eachColumn.getCOLUMN_DEFAULT().replace("0000-00-00 00:00:00", "1970-01-01 08:00:01"));
                        eachColumn.setCOLUMN_DEFAULT(eachColumn.getCOLUMN_DEFAULT().replace("1970-01-01 00:00:01", "1970-01-01 08:00:01"));

                    }
                }
                return eachColumns.stream();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }).collect(Collectors.toList());

        for (Column column : columns) {
            if ("NULL".equals(column.getCOLUMN_DEFAULT())) {
                column.setCOLUMN_DEFAULT(null);
            }
        }
        Map<String, List<Column>> columnsMap = columns.stream().collect(Collectors.groupingBy(Column::getTABLE_NAME));
        dataBaseColumnMap.put(dataBase, columnsMap);
    }

    public List<TableSchedule> selectTableSchedules() throws SQLException {
        return tableSchedules;
    }

    public List<Index> selectIndexs(DataBase mySql, String tableName) throws SQLException {
        return dataBaseIndexMap.get(mySql).get(tableName);
    }

    public List<Index> selectIndexs(DataBase mySql) throws SQLException {
        List<Index> indices = new ArrayList<>();
        for (List<Index> value : dataBaseIndexMap.get(mySql).values()) {
            indices.addAll(value);
        }
        return indices;
    }


    public List<Column> selectColumns(DataBase mySql) throws SQLException {
        List<Column> columns = new ArrayList<>();
        for (List<Column> value : dataBaseColumnMap.get(mySql).values()) {
            columns.addAll(value);
        }
        return columns;
    }

    public List<Column> selectColumns(DataBase mySql, String tableName) throws SQLException {
        return dataBaseColumnMap.get(mySql).get(tableName);
    }
}
