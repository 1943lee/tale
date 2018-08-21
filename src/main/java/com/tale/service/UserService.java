package com.tale.service;

import com.blade.ioc.annotation.Bean;
import com.blade.kit.StringKit;
import com.tale.bootstrap.TaleConst;
import com.tale.model.entity.Users;
import com.tale.model.params.PageParam;
import io.github.biezhi.anima.core.AnimaQuery;
import io.github.biezhi.anima.core.functions.TypeFunction;
import io.github.biezhi.anima.page.Page;

import static io.github.biezhi.anima.Anima.deleteById;
import static io.github.biezhi.anima.Anima.select;

/**
 * 用户Service
 * Created by liChenYu on 2018/8/20
 */
@Bean
public class UserService {

    /**
     * 获取用户，只获取非admin的用户
     * @return
     */
    public Page<Users> getUsers(PageParam pageParam) {
        AnimaQuery<Users> query = select().from(Users.class).exclude(Users::getPassword);

        // 用户类别条件
        query.and(Users::getGroupName, TaleConst.NORMAL_USER_GROUP_NAME);

        return query.page(pageParam.getPage(), pageParam.getLimit());
    }

    /**
     * 根据用户名统计
     */
    public long countByUsername(TypeFunction<Users, Object> type, Object key) {
        return select().from(Users.class).where(type, key).count();
    }

    /**
     * 根据id获取用户
     *
     * @param id 唯一标识
     */
    public Users getUser(String id) {
        Users user = null;
        if (StringKit.isNotBlank(id)) {
            if (StringKit.isNumber(id)) {
                user = select().from(Users.class).byId(id);
            }
        }
        return user;
    }

    /**
     * 根据用户id删除
     *
     * @param cid 用户id
     */
    public void delete(int cid) {
        Users user = this.getUser(cid + "");
        if (null != user) {
            deleteById(Users.class, cid);
        }
    }

}
