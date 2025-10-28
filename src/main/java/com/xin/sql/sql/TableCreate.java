package com.xin.sql.sql;

import com.xin.sql.db.vo.DataBaseSchemaContext;
import com.xin.sql.entity.Column;
import com.xin.sql.entity.Index;
import com.xin.sql.entity.TableSchedule;
import com.xin.sql.tool.Tools;
import com.xin.sql.util.StringUtils;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 497668869@qq.com
 * @version 1.0
 * @description 专门负责生成表的DDL 生成代码
 */
public class TableCreate {

    private final DataBaseSchemaContext dbSchemaContext;
    private final TableSchedule tableSchedule;

    public TableCreate(DataBaseSchemaContext dbSchemaContext, TableSchedule tableSchedule) {
        this.dbSchemaContext = dbSchemaContext;
        this.tableSchedule = tableSchedule;
    }

    public String newTableSql() throws SQLException {
        List<Column> column1s = dbSchemaContext.getColumn(tableSchedule.getTABLE_NAME());
        List<Index> indexs = dbSchemaContext.getIndexs(tableSchedule.getTABLE_NAME());
        if (indexs == null) {
            indexs = Collections.emptyList();
        }

        List<String> createTableComponent = new ArrayList<>();

        createTableComponent.addAll(column1s.stream()
                                            .map(Column::toTableCreateColumn)
                                            .collect(Collectors.toList()));

        createTableComponent.add(getKey(indexs));
        createTableComponent.addAll(getIndexWithoutKey(indexs));
        return createTableComponent.stream().filter(s -> !StringUtils.isEmpty(s))
                .map(e -> "  " + e)
                .collect(Collectors.joining(",\n"
                        , "CREATE TABLE `" + tableSchedule.getTABLE_NAME() + "` (\n"
                        , "\n) "
//                                                    "ENGINE=" + tableSchedule.getENGINE()
//                                                    + " DEFAULT CHARSET=" + tableSchedule.getTABLE_COLLATION().substring(0, tableSchedule.getTABLE_COLLATION().indexOf("_"))
//                                                    + " COLLATE=" + tableSchedule.getTABLE_COLLATION() + " "
                                + SqlCreate.getComment(tableSchedule.getTABLE_COMMENT()) + ";"));

    }


    public List<String> getIndexWithoutKey(List<Index> indexs) {
        Map<String, List<Index>> indexGroup = indexs.stream().filter(Tools.IS_PRIMARY_PREDICATE.negate()).collect(Collectors.groupingBy(Index::getINDEX_NAME, Collectors.toList()));
        Comparator<Index> comparingIndex = Comparator.comparing(Index::getSEQ_IN_INDEX);
        for (List<Index> indices : indexGroup.values()) {
            indices.sort(comparingIndex);
        }
        return indexGroup.keySet().stream().map(key -> {
            List<Index> indices = indexGroup.get(key);
            String union = "";
            if (indices.get(0).getNON_UNIQUE().equals(0L)) {
                union = "UNIQUE ";
            }
            return union + "KEY `" + key + "` " + indices.stream().map(Index.indexColumn).collect(Collectors.joining(",", "(", ")")) + " USING " + indices.get(0).getINDEX_TYPE();
        }).collect(Collectors.toList());
    }

    public String getKey(List<Index> indexs) throws SQLException {
        List<Index> keyGroup = Tools.getPrimaryKeys(indexs);
        return SqlCreate.getPrimaryKey(keyGroup);
    }


}
