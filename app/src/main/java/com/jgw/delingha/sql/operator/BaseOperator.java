package com.jgw.delingha.sql.operator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jgw.common_library.utils.ClassUtils;
import com.jgw.delingha.sql.ObjectBoxUtils;
import com.jgw.delingha.sql.entity.BaseEntity;

import java.util.List;

import io.objectbox.Box;
import io.objectbox.Property;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;

/**
 * author : Cxz
 * data : 2020/1/20
 * descripstion :
 */
public abstract class BaseOperator<T extends BaseEntity> {


    public final Box<T> box;

    public BaseOperator() {
        box = initBox();
    }

    protected Box<T> initBox() {
        //noinspection unchecked
        Class<T> clazz = (Class<T>) ClassUtils.getClassBySuperClass(this, BaseEntity.class);
        return ObjectBoxUtils.boxFor(clazz);
    }

    /**
     * 新增或更新
     *
     * @return 新增后的id
     */
    public long putData(T entity) {
        return box.put(entity);
    }


    public void putData(List<T> entity) {
        box.put(entity);
    }

    /**
     * 删除
     *
     * @param id 数据id
     */
    public boolean removeData(long id) {
        return box.remove(id);
    }

    /**
     * 通过Entity对象进行删除
     */
    public boolean removeData(T entity) {
        return box.remove(entity);
    }

    public void removeData(List<T> list) {
        box.remove(list);
    }

    public long removeDataByQuery(Query<T> query) {
        return query.remove();
    }

    /**
     * 删除表中所有数据
     */
    public void deleteAll() {
        box.removeAll();
    }


    /**
     * 根据查询条件字段和条件查询是否存在唯一对象
     *
     * @param property 查询条件字段
     * @param string   条件
     * @return 查询对象
     */
    public T queryEntityByString(Property<T> property, String string) {
        return box.query()
                .equal(property, string, QueryBuilder.StringOrder.CASE_INSENSITIVE)
                .build()
                .findUnique();
    }

    /**
     * 根据查询条件字段和条件删除对象
     *
     * @param property 查询条件字段
     * @param string   条件
     * @return 删除的对象个数
     */
    public long removeEntityByString(Property<T> property, String string) {
        Query<T> build = box.query()
                .equal(property, string, QueryBuilder.StringOrder.CASE_INSENSITIVE)
                .build();
        return removeDataByQuery(build);
    }

    /**
     * 根据id查询数据
     */
    @Nullable
    public final T queryDataById(long id) {
        return box.get(id);
    }

    /**
     * 查找所有的数据
     */
    public List<T> queryAll() {
        return box.query().build().find();
    }

    /**
     * 查询表中记录的条数
     */
    public long queryCount() {
        return box.count();
    }


    /**
     * 清空表后加入新数据
     *
     * @param list 获取的远程数据
     */
    public void saveData(List<T> list) {
        box.removeAll();
        putData(list);
    }

    /**
     * 根据传入的queryBuilder 偏移量和查询数量 查询
     *
     * @param qb     queryBuilder
     * @param offset 偏移量 不等于0时有效
     * @param limit  查询数量 大于0时有效
     * @return 数据列表
     */
    @Nullable
    public List<T> queryListByLimit(@Nullable Query<T> qb, int offset, int limit) {
        List<T> list;
        if (qb == null) {
            qb = box.query().build();
        }
        list = qb.find(offset, limit);
        return list;
    }

    @Nullable
    public List<T> queryListByPage(@Nullable Query<T> qb, int page, int pageSize) {
        int offset = page <= 0 ? 0 : (page - 1) * pageSize;
        return queryListByLimit(qb, offset, pageSize);
    }

    @Nullable
    public List<T> queryListByQuery(@Nullable Query<T> qb) {
        return queryListByLimit(qb, 0, 0);
    }

    @Nullable
    public List<T> queryListByPage(int page, int pageSize) {
        return queryListByPage(null, page, pageSize);
    }

    public long queryCountByQB(@Nullable Query<T> qb) {
        if (qb == null) {
            qb = box.query().build();
        }
        return qb.count();
    }

    public T queryUniqueDataByQB(@NonNull Query<T> qb) {
        return qb.findUnique();
    }

    public long deleteDataByQB(@NonNull Query<T> qb) {
        return qb.remove();
    }
}
