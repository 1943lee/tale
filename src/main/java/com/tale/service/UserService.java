package com.tale.service;

import com.blade.ioc.annotation.Bean;
import com.blade.kit.DateKit;
import com.blade.kit.EncryptKit;
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
     * 添加用户
     */
    public boolean add(Users user) {
        int time = DateKit.nowUnix();
        user.setCreated(time);
        user.setGroupName(TaleConst.NORMAL_USER_GROUP_NAME);
        String pwd = EncryptKit.md5(user.getUsername() + user.getPassword());
        user.setPassword(pwd);

        Integer cid = user.save().asInt();
        return cid > 0;
    }

    /**
     * 修改用户
     */
    public boolean update(Users user) {
        Integer cid = user.getUid();
        Users oldUser = select(Users::getPassword).from(Users.class).byId(cid);
        if(!oldUser.getPassword().equals(user.getPassword())) {
            String pwd = EncryptKit.md5(user.getUsername() + user.getPassword());
            user.setPassword(pwd);
        }

        return user.updateById(cid) > 0;
    }

    /**
     * 根据用户名统计
     */
    public long countByCol(TypeFunction<Users, Object> type, Object key) {
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
        long count = this.countByCol(Users::getUid, cid);
        if (count > 0) {
            deleteById(Users.class, cid);
        }
    }

}
