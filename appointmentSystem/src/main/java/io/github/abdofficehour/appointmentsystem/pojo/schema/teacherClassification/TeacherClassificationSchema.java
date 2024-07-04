package io.github.abdofficehour.appointmentsystem.pojo.schema.teacherClassification;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class TeacherClassificationSchema {
    private String classification;
    private String teacherId;
    private String teacherName;
}
