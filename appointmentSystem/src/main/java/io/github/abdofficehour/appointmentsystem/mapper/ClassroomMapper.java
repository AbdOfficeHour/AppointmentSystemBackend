package io.github.abdofficehour.appointmentsystem.mapper;

import io.github.abdofficehour.appointmentsystem.pojo.data.Classroom;
import io.github.abdofficehour.appointmentsystem.pojo.schema.classroomClassification.ClassroomClassificationSchema;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ClassroomMapper {

    List<ClassroomClassificationSchema> selectClassification();

    Classroom selectById(int id);

}
