package io.github.abdofficehour.appointmentsystem.service;

import io.github.abdofficehour.appointmentsystem.mapper.UserInfoMapper;
import io.github.abdofficehour.appointmentsystem.pojo.data.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

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

    /**
     * 根据用户信息查找权限
     * @return 用户所有权限
     */
    public HashMap<String,List<String>> SearchAuthorityByUser(String id){
        HashMap<String, List<String>> authority = new HashMap<>();
        try{
            authority.put("role",userInfoMapper.searchRole(id));
            authority.put("credit",userInfoMapper.searchAuth(id));
        }catch (Exception e){
            authority = null;
        }
        return authority;
    }

    /**
     * 模糊搜索查询学生
     * @return UserInfo对象列表，表示查询到的所有学生
     */
    public List<UserInfo> SearchUserBySearchData(String searchData){
        return userInfoMapper.selectAllByIdList("%"+searchData+"%");
    }

}
