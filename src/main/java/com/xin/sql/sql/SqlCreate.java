package com.xin.sql.sql;

import com.xin.sql.db.vo.ModifySql;
import com.xin.sql.entity.Column;
import com.xin.sql.entity.Index;
import com.xin.sql.entity.TableSchedule;
import com.xin.sql.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author 497668869@qq.com
 * @version 1.0
 * @description 负责生成一些修改的sql
 */
public class SqlCreate {

    public static List<ModifySql> dropIndexSql(List<List<Index>> deleteIndex) {
        return deleteIndex.stream().map(indices -> new ModifySql("DROP INDEX `" + indices.get(0).getINDEX_NAME() + "`")).collect(Collectors.toList());

    }

    public static String getIndexTypeSql(Index sampleIndex) {
        String index = "INDEX";
        if (sampleIndex.getNON_UNIQUE().equals(0L)) {
            index = "UNIQUE INDEX";
        }
        return index;
    }

    public static String getAddPrimaryKey(List<Index> bPrimaryKeys) {
        if (bPrimaryKeys == null || bPrimaryKeys.isEmpty()) {
            return "";
        } else {
            return "ADD " + getPrimaryKey(bPrimaryKeys);
        }
    }

    public static String getDropPrimaryKey() {
        return "DROP PRIMARY KEY";
    }

    public static String getDropTable(String tableName) {
        return "DROP TABLE " + tableName + " ;";
    }

    public static String getDropColumn(String columnName) {
        return "DROP COLUMN `" + columnName + "`";
    }

    public static String getAddColumn(String columnName, Column column, Column preColumn) {
        return "ADD " + getfModityOrAddColumn(columnName, column, preColumn);
    }

    public static String getModifyColumn(String columnName, Column column, Column preColumn) {
        return "MODIFY " + getfModityOrAddColumn(columnName, column, preColumn);
    }

    private static String getfModityOrAddColumn(String columnName, Column column, Column preColumn) {
        String position;
        if (preColumn == null) {
            position = "FIRST";
        } else {
            position = "AFTER `" + preColumn.getCOLUMN_NAME() + "`";
        }


        // 类似 MODIFY COLUMN `tsttt2`  varchar(255) NULL DEFAULT '0' COMMENT '测试属性' AFTER `tettt`
        return "COLUMN " + Stream.of(columnName, column.getCOLUMN_TYPE(), isNullStr(column.getIS_NULLABLE()), SqlCreate.getExtra(column.getEXTRA()), getDefault(column), getComment(column.getCOLUMN_COMMENT()), position).filter(StringUtils::isNotEmpty).collect(Collectors.joining(" "));
    }


    public static String getAlterTable(TableSchedule aTableSchedule, List<ModifySql> addOrDropSqlComponents, boolean tipsShow) {
        String sql = "ALTER TABLE `" + aTableSchedule.getTABLE_NAME() + "`\n";
        for (int i = 0; i < addOrDropSqlComponents.size() - 1; i++) {
            ModifySql modifySql = addOrDropSqlComponents.get(i);
            if (tipsShow) {
                sql += modifySql.getSql() + ", " + getTips(modifySql.getTip()) + "\n";
            } else {
                sql += modifySql.getSql() + ", " + "\n";
            }
        }

        ModifySql lastModifySql = addOrDropSqlComponents.get(addOrDropSqlComponents.size() - 1);
        if (tipsShow) {
            sql += lastModifySql.getSql() + ";" + getTips(lastModifySql.getTip());
        } else {
            sql += lastModifySql.getSql() + ";";
        }
        return sql;
    }

    public static String getPrimaryKey(List<Index> indexGroup) {
        if (indexGroup == null || indexGroup.isEmpty()) {
            return "";
        } else {
            return "PRIMARY KEY " + indexGroup.stream().map(Index::getCOLUMN_NAME).collect(Collectors.joining("`,`", "(`", "`)"));
        }
    }

    public static String getComment(String comment) {
        if (!StringUtils.isEmpty(comment)) {
            return "COMMENT '" + comment + "'";
        } else {
            return "";
        }
    }

    /**
     * ADD `idx_school_uid` (`school_uid`) USING BTREE
     */
    public static ModifySql getAddIndex(List<Index> indexs) {
        //多个index的comment和 index类型, tablename都是一样的
        Index sampleIndex = indexs.get(0);
        String suffix = ") USING " + sampleIndex.getINDEX_TYPE() + " " + getComment(sampleIndex.getCOMMENT());
        String prefix = "ADD " + getIndexTypeSql(sampleIndex) + " `" + sampleIndex.getINDEX_NAME() + "` " + "(";
        return new ModifySql(indexs.stream().map(Index.indexColumn).collect(Collectors.joining(",", prefix, suffix)));

    }

    public static String getDefault(Column column) {
        if (column.getCOLUMN_DEFAULT() != null) {
            if ("CURRENT_TIMESTAMP".equalsIgnoreCase(column.getCOLUMN_DEFAULT())) {
                return "DEFAULT " + column.getCOLUMN_DEFAULT() + "";
            } else {
                return "DEFAULT '" + column.getCOLUMN_DEFAULT() + "'";
            }
        } else {
            if ("datetime".equalsIgnoreCase(column.getCOLUMN_TYPE()) && "NOT".equalsIgnoreCase(column.getIS_NULLABLE())) {
                return "DEFAULT '1970-01-01 00:00'";
            }
            return "";
        }
    }

    /**
     * 字节编码后续再支持
     *
     * @param collation
     * @return
     */
    public static String getCollation(String collation) {
        return "CHARACTER SET " + collation;
    }

    public static String getExtra(String extra) {
        if (extra.toLowerCase().equals("auto_increment")) {
            return "AUTO_INCREMENT";
        } else if (extra.toLowerCase().equals("on update CURRENT_TIMESTAMP".toLowerCase())) {
            return "ON UPDATE CURRENT_TIMESTAMP";
        } else if (StringUtils.isNotBlank(extra)) {
            return extra.toUpperCase();
        }
        return "";
    }

    public static String getTips(String tip) {
        if (StringUtils.isBlank(tip)) {
            return "";
        } else {
            return " # " + tip;
        }
    }

    public static String isNullStr(String isNull) {
        if ("YES".equalsIgnoreCase(isNull)) {
            return "NULL";
        }
        if ("NO".equalsIgnoreCase(isNull)) {
            return "NOT NULL";
        }

        throw new RuntimeException("不知道是什么类型的null");
    }
}
