package com.xin.sql.entity;

import lombok.Data;

import java.util.function.Function;

/**
 * @author 497668869@qq.com
 * @version 1.0
 * @description
 */
@Data
public class Index {

    public static Function<Index, String> indexColumn = index -> {
        if (index.getSUB_PART() != null) {
            return "`" + index.getCOLUMN_NAME() + "`(" + index.getSUB_PART() + ")";
        } else {
            return "`" + index.getCOLUMN_NAME() + "`";
        }
    };
    private String COMMENT;
    private String NULLABLE;
    private String TABLE_NAME;
    private Long CARDINALITY;
    private Long SUB_PART;
    private Long PACKED;
    private Long NON_UNIQUE;
    private String COLLATION;
    private String COLUMN_NAME;
    private String INDEX_COMMENT;
    private Long SEQ_IN_INDEX;
    private String INDEX_NAME;
    private String INDEX_TYPE;

    private String TABLE_SCHEMA;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Index index = (Index) o;

        if (COMMENT != null ? !COMMENT.equals(index.COMMENT) : index.COMMENT != null) return false;
        if (NULLABLE != null ? !NULLABLE.equals(index.NULLABLE) : index.NULLABLE != null) return false;
        if (TABLE_NAME != null ? !TABLE_NAME.equals(index.TABLE_NAME) : index.TABLE_NAME != null) return false;
        if (SUB_PART != null ? !SUB_PART.equals(index.SUB_PART) : index.SUB_PART != null) return false;
//        if (Packed != null ? !Packed.equals(index.Packed) : index.Packed != null) return false;
        if (NON_UNIQUE != null ? !NON_UNIQUE.equals(index.NON_UNIQUE) : index.NON_UNIQUE != null) return false;
//        if (Collation != null ? !Collation.equals(index.Collation) : index.Collation != null) return false;
        if (COLUMN_NAME != null ? !COLUMN_NAME.equals(index.COLUMN_NAME) : index.COLUMN_NAME != null) return false;
        if (INDEX_COMMENT != null ? !INDEX_COMMENT.equals(index.INDEX_COMMENT) : index.INDEX_COMMENT != null)
            return false;
        if (SEQ_IN_INDEX != null ? !SEQ_IN_INDEX.equals(index.SEQ_IN_INDEX) : index.SEQ_IN_INDEX != null) return false;
        if (INDEX_NAME != null ? !INDEX_NAME.equals(index.INDEX_NAME) : index.INDEX_NAME != null) return false;
        return INDEX_TYPE != null ? INDEX_TYPE.equals(index.INDEX_TYPE) : index.INDEX_TYPE == null;
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + (COMMENT != null ? COMMENT.hashCode() : 0);
        result = 31 * result + (NULLABLE != null ? NULLABLE.hashCode() : 0);
        result = 31 * result + (TABLE_NAME != null ? TABLE_NAME.hashCode() : 0);
//        result = 31 * result + (Cardinality != null ? Cardinality.hashCode() : 0);
        result = 31 * result + (SUB_PART != null ? SUB_PART.hashCode() : 0);
//        result = 31 * result + (Packed != null ? Packed.hashCode() : 0);
        result = 31 * result + (NON_UNIQUE != null ? NON_UNIQUE.hashCode() : 0);
//        result = 31 * result + (Collation != null ? Collation.hashCode() : 0);
        result = 31 * result + (COLUMN_NAME != null ? COLUMN_NAME.hashCode() : 0);
        result = 31 * result + (INDEX_COMMENT != null ? INDEX_COMMENT.hashCode() : 0);
        result = 31 * result + (SEQ_IN_INDEX != null ? SEQ_IN_INDEX.hashCode() : 0);
        result = 31 * result + (INDEX_NAME != null ? INDEX_NAME.hashCode() : 0);
        result = 31 * result + (INDEX_TYPE != null ? INDEX_TYPE.hashCode() : 0);
        return result;
    }
}
