package com.jgw.delingha.sql.operator;

import com.jgw.delingha.sql.entity.BasePackageCodeEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.objectbox.Property;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;

/**
 * @author : J-T
 * @date : 2022/2/21 15:13
 * description :包装Operator基类
 */
public abstract class BasePackageCodeOperator<T extends BasePackageCodeEntity> extends BaseWaitUploadCodeOperator<T> {


    /**
     * 找箱
     */
    public abstract T findBox(long configId, long boxId);

    public T findBox(Property<T> configProperty, long configId, Property<T> isBoxProperty, Property<T> orderBy,
                     Property<T> idProperty, long boxId) {
        QueryBuilder<T> queryBuilder = box.query()
                .equal(configProperty, configId)
                .equal(isBoxProperty, true)
                .less(idProperty, boxId)
                .orderDesc(orderBy);
        return queryBuilder.build().findFirst();
    }

    /**
     * 找最后一箱 倒序
     */
    public abstract T findLastBox(long configId);

    public T findLastBox(Property<T> configProperty,long configId,Property<T> isBoxProperty, Property<T> orderBy){
        QueryBuilder<T> queryBuilder = box.query()
                .equal(configProperty,configId)
                .equal(isBoxProperty,true)
                .orderDesc(orderBy);
        return queryBuilder.build().findFirst();
    }


    /**
     * 根据configId查找箱码和子码的数量
     */
    public abstract Map<String, Integer> queryBoxAndChildCountByConfig(long configId);

    public Map<String, Integer> queryBoxAndChildCountByConfig(Property<T> configProperty, Property<T> boxProperty, long configId) {
        HashMap<String, Integer> map = new HashMap<>();
        Query<T> boxQuery = box.query()
                .equal(configProperty, configId)
                .equal(boxProperty, true)
                .build();
        long boxCount = queryCountByQB(boxQuery);
        Query<T> childQuery = box.query()
                .equal(configProperty, configId)
                .equal(boxProperty, false)
                .build();
        long childCount = queryCountByQB(childQuery);
        map.put("boxCount", new Long(boxCount).intValue());
        map.put("childCount", new Long(childCount).intValue());
        return map;
    }

    /**
     * 根据父码删除所有的子码
     */
    public abstract void deleteAllSonByPrentCode(String code);

    /**
     * 根据父码删除所有的子码
     */
    public void deleteAllSonByPrentCode(Property<T> parentProperty, String code) {
        Query<T> query = box.query()
                .equal(parentProperty, code, QueryBuilder.StringOrder.CASE_INSENSITIVE)
                .build();
        removeDataByQuery(query);
    }

    /**
     * 根据父码查询count数量个子码
     */
    public abstract List<T> querySonListByParentCode(String parentCode, int count);

    public List<T> querySonListByParentCode(Property<T> parentProperty, String parentCode, Property<T> boxProperty, int count) {
        Query<T> query = box.query()
                .equal(parentProperty, parentCode, QueryBuilder.StringOrder.CASE_INSENSITIVE)
                .equal(boxProperty, false)
                .build();
        return queryListByLimit(query, 0, count);
    }

    /**
     * 根据父码查询 所有子码
     */
    public abstract List<T> querySonListByBoxCode(String code);

    public abstract List<T> querySonListByBoxCode(String code, int offset, int count);

    public List<T> querySonListByBoxCode(Property<T> parentProperty, String parentCode, Property<T> orderBy, int offset, int count) {
        Query<T> query = box.query()
                .equal(parentProperty, parentCode, QueryBuilder.StringOrder.CASE_INSENSITIVE)
                .orderDesc(orderBy)
                .build();
        return queryListByLimit(query, offset, count);
    }

    /**
     * 更新父码为非满箱状态
     */
    public abstract void updateParentCodeIsFull(String boxCode, boolean isFull);

    /**
     * 清除空箱
     */
    public abstract void clearEmptyBox();

    public void clearEmptyBox(Property<T> isBoxProperty, Property<T> parentOuterCodeProperty) {
        List<T> boxList = box.query().equal(isBoxProperty, true).build().find();
        String[] strings = box.query()
                .equal(isBoxProperty, false)
                .build()
                .property(parentOuterCodeProperty)
                .distinct()
                .findStrings();
        List<String> realBoxList = Arrays.asList(strings);
        ArrayList<T> temp = new ArrayList<>();
        for (T e : boxList) {
            if (!realBoxList.contains(e.getCode())) {
                temp.add(e);
            }
        }
        removeData(temp);
    }

    public void deleteAllSonByPrentCodes(List<String> codes) {
        for (String code : codes) {
            deleteAllSonByPrentCode(code);
            removeEntityByCode(code);
        }
    }

    /**
     * 修改码的父码
     */
    public abstract void updateCodeParentCode(String boxCode, String codeTypeId, List<T> list);

    public abstract List<T> queryBoxListByConfigurationId(long configurationId);

    public abstract List<T> queryBoxListByConfigurationId(long configurationId, long id, int pageSize);

    public List<T> queryBoxListByConfigurationId(Property<T> configProperty, long configurationId
            , Property<T> idProperty, long id, Property<T> boxProperty, int pageSize) {
        Query<T> query = box.query()
                .equal(configProperty, configurationId)
                .greaterOrEqual(idProperty, id)
                .equal(boxProperty, true)
                .build();
        return queryListByLimit(query, 0, pageSize);
    }

    public abstract int queryBoxCodeSize(Long configId);

    public int queryBoxCodeSize(Property<T> configProperty, long configurationId
            , Property<T> boxProperty) {
        Query<T> query = box.query()
                .equal(configProperty, configurationId)
                .equal(boxProperty, true)
                .build();
        return (int) queryCountByQB(query);
    }

    public abstract List<String> queryParentCodeList();

    public List<String> queryParentCodeList(Property<T> boxProperty, Property<T> codeProperty) {
        String[] strings = box.query()
                .equal(boxProperty, true)
                .build()
                .property(codeProperty)
                .findStrings();
        return new ArrayList<>(Arrays.asList(strings));

    }

    public abstract List<String> queryRealParentCodeList();

    public List<String> queryRealParentCodeList(Property<T> parentOuterCodeIdProperty) {
        String[] strings = box.query()
                .build()
                .property(parentOuterCodeIdProperty)
                .distinct()
                .findStrings();
        return new ArrayList<>(Arrays.asList(strings));
    }

    public abstract boolean isRepeatBoxCode(String code);

    public boolean isRepeatBoxCode(Property<T> outerCodeProperty, String code, Property<T> boxProperty) {
        T unique = box.query()
                .equal(outerCodeProperty, code, QueryBuilder.StringOrder.CASE_INSENSITIVE)
                .equal(boxProperty, true)
                .build().findUnique();
        return unique != null;
    }
}
