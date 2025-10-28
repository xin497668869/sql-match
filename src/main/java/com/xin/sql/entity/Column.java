package com.xin.sql.entity; /**
 * @author 497668869@qq.com
 * @version 1.0
 * @description
 */

import com.xin.sql.sql.SqlCreate;
import com.xin.sql.util.StringUtils;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
@Data
public class Column {

    private String TABLE_CATALOG;
    private String TABLE_SCHEMA;

    private String TABLE_NAME;
    private String COLUMN_NAME;

    private String IS_NULLABLE;
    private String COLUMN_COMMENT;
    private String COLUMN_TYPE;
    private String EXTRA;
    private String PRIVILEGES;
    private String COLLATION_NAME;
    private String COLUMN_DEFAULT;
    private String COLUMN_KEY;


    public static String compareTip(Column aColumn, Column bColumn) {
        String tips = "";
        if (!Objects.equals(aColumn.getCOLUMN_TYPE(), bColumn.getCOLUMN_TYPE())) {
            tips = tips + String.format("类型type 不一致, 原 %s : 现 %s; ", aColumn.getCOLUMN_TYPE(), bColumn.getCOLUMN_TYPE());
        }
        if (!Objects.equals(aColumn.getIS_NULLABLE(), bColumn.getIS_NULLABLE())) {
            tips = tips + String.format("是否可空 不一致, 原 %s : 现 %s; ", SqlCreate.isNullStr(aColumn.getIS_NULLABLE()), SqlCreate.isNullStr(bColumn.getIS_NULLABLE()));
        }
        if (!Objects.equals(aColumn.getCOLUMN_COMMENT(), bColumn.getCOLUMN_COMMENT())) {
            tips = tips + String.format("注释 不一致, 原 %s : 现 %s; ", aColumn.getCOLUMN_COMMENT(), bColumn.getCOLUMN_COMMENT());
        }
        if (!Objects.equals(aColumn.getCOLUMN_DEFAULT(), bColumn.getCOLUMN_DEFAULT())) {
            tips = tips + String.format("默认值 不一致, 原 %s : 现 %s; ", aColumn.getCOLUMN_DEFAULT(), bColumn.getCOLUMN_DEFAULT());
        }

        if (!Objects.equals(aColumn.getEXTRA(), bColumn.getEXTRA())) {
            tips = tips + String.format("Extra 不一致, 原 %s : 现 %s; ", aColumn.getEXTRA(), bColumn.getEXTRA());
        }
//        if (!Objects.equals(aColumn.getCollation(), bColumn.getCollation())) {
//            tips = tips + String.format("编码 不一致, 原 %s : 现 %s; ", aColumn.getCollation(), bColumn.getCollation());
//        }
        return tips;
    }

    public String toTableCreateColumn() {
        String autoIncrement = SqlCreate.getExtra(getEXTRA());
        String nullStr = SqlCreate.isNullStr(getIS_NULLABLE());
        String defaultStr = SqlCreate.getDefault(this);
        String comment = SqlCreate.getComment(getCOLUMN_COMMENT());

        return "`" + getCOLUMN_NAME() + "` " + getCOLUMN_TYPE() + " "
                + Stream.of(nullStr, autoIncrement, defaultStr, comment)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.joining(" "));
    }

    private static Map<String, String> polarDifferenceColumnTypeName = new HashMap<>();

    static {
        polarDifferenceColumnTypeName.put("bigint(20)", "bigint");
        polarDifferenceColumnTypeName.put("bigint(20) unsigned", "bigint unsigned");
        polarDifferenceColumnTypeName.put("bigint(11)", "bigint");
        polarDifferenceColumnTypeName.put("int(11)", "int");
        polarDifferenceColumnTypeName.put("int(11) unsigned", "int unsigned");
        polarDifferenceColumnTypeName.put("int(10) unsigned", "int unsigned");
        polarDifferenceColumnTypeName.put("int(10)", "int");
        polarDifferenceColumnTypeName.put("int(5)", "int");
        polarDifferenceColumnTypeName.put("int(4)", "int");
        polarDifferenceColumnTypeName.put("smallint(6)", "smallint");
        polarDifferenceColumnTypeName.put("smallint(5) unsigned", "smallint unsigned");
        polarDifferenceColumnTypeName.put("tinyint(4)", "tinyint");
        polarDifferenceColumnTypeName.put("tinyint(2)", "tinyint");
        polarDifferenceColumnTypeName.put("tinyint(3) unsigned", "tinyint unsigned");
        polarDifferenceColumnTypeName.put("tinyint(2) unsigned", "tinyint unsigned");
        polarDifferenceColumnTypeName.put("tinyint(1) unsigned", "tinyint unsigned");
        polarDifferenceColumnTypeName.put("decimal(6,2) unsigned", "decimal(6,2)");
    }

    public boolean compared(Column column, boolean isCommentCompared) {
        if (this == column) return true;
        if (column == null || getClass() != column.getClass()) return false;

        if (COLUMN_NAME != null ? !COLUMN_NAME.equals(column.COLUMN_NAME) : column.COLUMN_NAME != null) return false;
        if (isCommentCompared) {
            if (COLUMN_COMMENT != null ? !COLUMN_COMMENT.replace("\r\n", "\n").equals(column.COLUMN_COMMENT.replace("\r\n", "\n")) : column.COLUMN_COMMENT != null)
                return false;
        }
        String aColumnType = COLUMN_TYPE;
        String bColumnType = column.COLUMN_TYPE;

        if (polarDifferenceColumnTypeName.containsKey(aColumnType) || polarDifferenceColumnTypeName.containsKey(bColumnType)) {

            for (Map.Entry<String, String> keyValue : polarDifferenceColumnTypeName.entrySet()) {

                aColumnType = aColumnType.replace(keyValue.getKey(), keyValue.getValue());

                bColumnType = bColumnType.replace(keyValue.getKey(), keyValue.getValue());
            }
        }


        if (aColumnType != null ? !aColumnType.equals(bColumnType) : bColumnType != null) return false;

        if (!"datetime".equals(aColumnType)) {
            if (IS_NULLABLE != null ? !IS_NULLABLE.equals(column.IS_NULLABLE) : column.IS_NULLABLE != null)
                return false;
        }

        String aExtra = EXTRA.replace("DEFAULT_GENERATED", "");
        String bExtra = column.EXTRA.replace("DEFAULT_GENERATED", "");
        ;

        if (aExtra != null ? !aExtra.equals(bExtra) : bExtra != null) return false;
//        if (Privileges != null ? !Privileges.equals(column.Privileges) : column.Privileges != null) return false;
//        if (Collation != null ? !Collation.equals(column.Collation) : column.Collation != null) return false;
        String aColumnDefault = null;
        String bColumnDefault = null;

        if (COLUMN_DEFAULT != null) {
            aColumnDefault = COLUMN_DEFAULT.replace("DEFAULT_GENERATED", "");
        }
        if (column.COLUMN_DEFAULT != null) {
            bColumnDefault = column.COLUMN_DEFAULT.replace("DEFAULT_GENERATED", "");
        }
        return aColumnDefault != null ? aColumnDefault.equals(bColumnDefault) : bColumnDefault == null;
    }
}