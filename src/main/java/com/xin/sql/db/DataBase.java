package com.xin.sql.db;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author xin
 * @version 1.0
 */
public interface DataBase {

    void connect() throws SQLException;

    void close() throws SQLException;

    List<Map<String, Object>> executeSql(String sql) throws SQLException;


    String getDatabase();

}
