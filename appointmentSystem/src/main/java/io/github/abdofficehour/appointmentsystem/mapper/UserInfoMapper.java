package io.github.abdofficehour.appointmentsystem.mapper;

import io.github.abdofficehour.appointmentsystem.pojo.UserInfo;
import io.github.abdofficehour.appointmentsystem.pojo.schema.ClassificationSchema;
import org.apache.ibatis.annotations.Mapper;

import java.util.*;

@Mapper
public interface UserInfoMapper {
    List<UserInfo> findAll();

    UserInfo selectById(String id);

    List<String> searchRole(String id);

    List<String> searchAuth(String id);

    List<UserInfo> selectAllByIdList(String searchData);

    List<ClassificationSchema> selectAllClassification();
}
