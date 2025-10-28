package com.xin.sql.db.mysql;


import com.xin.sql.db.DataBase;
import com.xin.sql.property.ConnMsg;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 497668869@qq.com
 * @version 1.0
 * @description
 */
public class MySql implements DataBase {

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Connection conn;
    private ConnMsg    connMsg;

    public MySql(ConnMsg connMsg) {
        this.connMsg = connMsg;
    }


    @Override
    public void connect() throws SQLException {
        conn = DriverManager.getConnection("jdbc:mysql://" + connMsg.getHost() + ":" + connMsg.getPort() + "/"
                + connMsg.getDatabase()
                + "?useUnicode=true&characterEncoding=UTF8&serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true", connMsg.getUsername(), connMsg.getPassword());
    }

    @Override
    public void close() throws SQLException {
        conn.close();
    }

    @Override
    public List<Map<String, Object>> executeSql(String sql) throws SQLException {
        String lowerSql = sql.toLowerCase().trim();
        if (lowerSql.startsWith("insert")
                || lowerSql.startsWith("update")
                || lowerSql.startsWith("delete")) {
            dmlSql(sql);
            return null;
        } else {
            return querySql(sql);
        }
    }

    @Override
    public String getDatabase() {
        return connMsg.getDatabase();
    }

    public boolean dmlSql(String sql) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            return stmt.execute(sql);
        }
    }

    public List<Map<String, Object>> querySql(String sql) throws SQLException {

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {// executeQuery会返回结果的集合，否则返回空值

            List<Map<String, Object>> res = new ArrayList<>();
            ResultSetMetaData md = rs.getMetaData(); //得到结果集(rs)的结构信息，比如字段数、字段名等

            int columnCount = md.getColumnCount();
            while (rs.next()) {
                Map<String, Object> data = new HashMap<>();

                for (int i = 1; i <= columnCount; i++) {
                    data.put(md.getColumnLabel(i), rs.getObject(i));
                }
                res.add(data);
            }
            return res;
        }
    }

    public static String underlineToCamel(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (c == '_') {
                if (++i < len) {
                    sb.append(Character.toUpperCase(param.charAt(i)));
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
