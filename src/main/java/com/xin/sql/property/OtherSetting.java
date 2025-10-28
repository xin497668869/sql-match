package com.xin.sql.property;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 497668869@qq.com
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtherSetting {
    /**
     * show modify sql if comment is difference
     */
    private boolean matchComment=true;
    /**
     * show more tips in modify sql
     */
    private boolean tipsShow=true;

    /**
     * filterTables by ',' such as atable,btable
     */
    private String filterTables;
}
