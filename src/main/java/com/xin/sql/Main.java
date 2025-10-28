package com.xin.sql;

import com.xin.sql.db.mysql.MySql;
import com.xin.sql.property.ConnMsg;
import com.xin.sql.property.OtherSetting;
import com.xin.sql.service.MatchSql;

import java.sql.SQLException;
import java.util.List;

/**
 * @author 497668869@qq.com
 * @since 1.0
 */
public class Main {


    public static void main(String[] args) throws SQLException {


        ConnMsg connMsgA = new ConnMsg("localhost",3306,"flink_test","xin","");
        ConnMsg connMsgB = new ConnMsg("localhost",3306,"flink_test2","xin","");
        OtherSetting otherSetting = new OtherSetting(true,true,"");

        MySql fromDatabase = new MySql(connMsgA);
        MySql toDatabase = new MySql(connMsgB);


        MatchSql matchSql = new MatchSql(otherSetting, fromDatabase, toDatabase);
        List<String> allModifySql = matchSql.match();

        System.out.println();
        for (String sql : allModifySql) {

            System.out.println(sql);
            System.out.println();
            System.out.println();
        }

        System.exit(0);
    }


}
