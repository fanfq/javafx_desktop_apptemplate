package com.elseplus.app.test;

import com.alibaba.fastjson2.JSONObject;
import com.elseplus.app.model.UserCache;
import org.junit.jupiter.api.Test;

public class UserCacheTest {

    @Test
    public void test(){

        UserCache userCache = UserCache.getInstance();
        System.out.println(JSONObject.toJSONString(userCache));

        userCache.setUsername("zz1111");
        userCache.setPassword("zsd123");
        userCache.upd();
    }
}
