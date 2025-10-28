package com.xin.sql.tool;

import com.xin.sql.db.vo.DataBaseSchemaContext;
import com.xin.sql.entity.Index;
import com.xin.sql.entity.TableSchedule;

import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author 497668869@qq.com
 * @version 1.0
 * @description 工具类
 */
public class Tools {


    public static final Predicate<Index> IS_PRIMARY_PREDICATE = index -> "PRIMARY".equals(index.getINDEX_NAME());


    /**
     * 获取数据库表的索引
     */
    public static List<Index> getIndexsWithoutKey(DataBaseSchemaContext mysql, TableSchedule tableSchedule) throws SQLException {
        List<Index> indexs = mysql.getIndexs(tableSchedule.getTABLE_NAME());
        if (indexs == null) {
            return Collections.emptyList();
        }
        List<Index> indices = indexs.stream().filter(IS_PRIMARY_PREDICATE.negate()).collect(Collectors.toList());
        indices.sort(Comparator.comparing(Index::getSEQ_IN_INDEX));
        return indices;
    }

    /**
     * 获取数据库表的primary key
     */
    public static List<Index> getPrimaryKeys(DataBaseSchemaContext mysql, TableSchedule tableSchedule) throws SQLException {
        List<Index> indexs = mysql.getIndexs(tableSchedule.getTABLE_NAME());
        if (indexs == null) {
            return Collections.emptyList();
        }
        return indexs.stream().filter(IS_PRIMARY_PREDICATE).collect(Collectors.toList());
    }

    public static List<Index> getPrimaryKeys(List<Index> indexs) throws SQLException {
        return indexs.stream().filter(IS_PRIMARY_PREDICATE).collect(Collectors.toList());
    }

    /**
     * 求出属于A但不属于B的字符串
     */
    public static <T> List<T> removeFrom_A(Collection<T> aList, Collection<T> bList) {
        List<T> orgAList = new ArrayList<>(aList);
        orgAList.removeAll(bList);
        return orgAList;
    }

    /**
     * 求出属于B但不属于A的字符串
     */
    public static <T> List<T> addToA(Collection<T> aList, Collection<T> bList) {
        List<T> orgAList = new ArrayList<>(bList);
        orgAList.removeAll(aList);
        return orgAList;
    }

    /**
     * 交集
     */
    public static <T> List<T> intersection(Collection<T> aList, Collection<T> bList) {
        ArrayList<T> nAList = new ArrayList<>(aList);
        ArrayList<T> intersection = new ArrayList<>();
        for (T b : bList) {
            if (nAList.remove(b)) {
                intersection.add(b);
            }
        }
        return intersection;
    }

    public static <T, D> Map<D, T> getMap(List<T> list, Function<T, D> keyMap) {
        return list.stream().collect(Collectors.toMap(keyMap, o -> o));
    }

    /**
     * show index 出来的结构是这样子的, 一个索引会对应多行记录, 根据 Seq_in_index 排索引的顺序, 所以需要先
     * 进行key的分组, 分组完最好根据Seq_in_index 排个序
     * Seq_in_index
     * table1	    0	PRIMARY	            1	name	A	2				BTREE
     * table1	    0	PRIMARY	            2	id	        A	2958			BTREE
     * table1	    1	school_id_index	    1	version     A	60				BTREE
     * table1	    1	school_id_index 	2	name	A	60				BTREE
     * table1	    1	school_id_index2	1	time	A	2958			BTREE
     * 根据index进行分组
     */
    public static Map<String, List<Index>> getIndexGroupMap(List<Index> aIndexs) {
        Comparator<Index> comparingIndex = Comparator.comparing(Index::getSEQ_IN_INDEX);
        Map<String, List<Index>> collect = aIndexs.stream().collect(Collectors.groupingBy(Index::getINDEX_NAME, Collectors.toList()));

        for (List<Index> indices : collect.values()) {
            indices.sort(comparingIndex);
        }
        return collect;
    }
}
