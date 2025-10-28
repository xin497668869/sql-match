package com.xin.sql.db.vo;

import com.xin.sql.db.DataBase;
import com.xin.sql.entity.Column;
import com.xin.sql.entity.Index;
import com.xin.sql.entity.TableSchedule;
import com.xin.sql.sql.SqlSelect;
import com.xin.sql.util.StringUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author 497668869@qq.com
 * @since 1.0
 */
public class DataBaseSchemaContext {
    private final String    schema;
    private final SqlSelect sqlSelect;
    private       DataBase  dataBase;

    public DataBaseSchemaContext(DataBase dataBase, String schema) {
        this.dataBase = dataBase;
        this.schema = schema;
        this.sqlSelect = new SqlSelect();

    }

    public DataBaseSchemaContext(DataBase dataBase) {
        this.dataBase = dataBase;
        this.schema = dataBase.getDatabase();
        this.sqlSelect = new SqlSelect();

    }

    public void init(Predicate<TableSchedule> tableSchedulePredicate) throws SQLException {
        dataBase.connect();
        sqlSelect.init(dataBase, schema, tableSchedulePredicate);
    }

    public DataBase getDataBase() {
        return dataBase;
    }

    public List<TableSchedule> getTableSchedules() throws SQLException {
        return sqlSelect.selectTableSchedules();
    }

    public List<Index> getIndexs(String tableName) throws SQLException {
        return sqlSelect.selectIndexs(dataBase, tableName);
    }

    public List<Index> getIndexs() throws SQLException {
        return sqlSelect.selectIndexs(dataBase);
    }

    public List<Column> getColumn() throws SQLException {
        return sqlSelect.selectColumns(dataBase).stream().filter(a -> {
            boolean notEmpty = StringUtils.isNotEmpty(a.getCOLUMN_NAME());
            if (!notEmpty) {
                System.out.println("此列有异常, 可能由换行造成, 暂时忽略 " + a);
            }
            return notEmpty;
        }).collect(Collectors.toList());
    }

    public List<Column> getColumn(String tableName) throws SQLException {
        return sqlSelect.selectColumns(dataBase, tableName).stream().filter(a -> {
            boolean notEmpty = StringUtils.isNotEmpty(a.getCOLUMN_NAME());
            if (!notEmpty) {
                System.out.println("此列有异常, 可能由换行造成, 暂时忽略 " + a);
            }
            return notEmpty;
        }).collect(Collectors.toList());
    }
}
