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
    private boolean matchComment=true;
    private boolean tipsShow=true;
    private String filterTables;
}
