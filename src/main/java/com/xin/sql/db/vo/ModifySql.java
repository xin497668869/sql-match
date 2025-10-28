package com.xin.sql.db.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 497668869@qq.com
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModifySql {
    private String sql;
    private String tip;

    public ModifySql(String sql) {
        this.sql = sql;
    }
}
