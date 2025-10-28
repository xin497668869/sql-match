package com.xin.sql;

import com.alibaba.fastjson.JSON;
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


        String arg0= JSON.toJSONString(new ConnMsg("localhost",3306,"flink_test","xin",""));
        String arg1= JSON.toJSONString(new ConnMsg("localhost",3306,"flink_test2","xin",""));
        String arg2= JSON.toJSONString(new OtherSetting(true,true,""));


        ConnMsg connMsgA = JSON.parseObject(arg0, ConnMsg.class);
        ConnMsg connMsgB = JSON.parseObject(arg1, ConnMsg.class);
        OtherSetting otherSetting = JSON.parseObject(arg2, OtherSetting.class);

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
