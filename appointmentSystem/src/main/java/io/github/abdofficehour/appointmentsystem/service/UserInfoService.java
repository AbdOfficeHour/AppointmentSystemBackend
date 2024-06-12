package io.github.abdofficehour.appointmentsystem.service;

import io.github.abdofficehour.appointmentsystem.mapper.UserInfoMapper;
import io.github.abdofficehour.appointmentsystem.pojo.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserInfoService {

    @Autowired
    UserInfoMapper userInfoMapper;

    /**
     * 根据id查找用户
     * @param id 学号
     * @return UserInfo对象
     */
    public UserInfo searchUserById(String id){
        UserInfo userInfo;
        try{
            userInfo = userInfoMapper.selectById(id);
        }catch (Exception e){
            userInfo = null;
        }
        return userInfo;
    }

}
