package io.github.abdofficehour.appointmentsystem.mapper;

import io.github.abdofficehour.appointmentsystem.pojo.UserInfo;
import io.github.abdofficehour.appointmentsystem.pojo.schema.ClassificationSchema;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.*;

@Mapper
public interface UserInfoMapper {
    public List<UserInfo> findAll();

    public UserInfo selectById(String id);

    public List<String> searchRole(String id);

    public List<String> searchAuth(String id);

    public List<UserInfo> selectAllByIdList(String searchData);

    public List<ClassificationSchema> selectAllClassification();
}
