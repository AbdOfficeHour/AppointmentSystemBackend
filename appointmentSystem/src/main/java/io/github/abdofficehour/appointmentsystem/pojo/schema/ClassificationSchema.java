package io.github.abdofficehour.appointmentsystem.pojo.schema;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class ClassificationSchema {
    private String classification;
    private String teacherId;
    private String teacherName;
}
