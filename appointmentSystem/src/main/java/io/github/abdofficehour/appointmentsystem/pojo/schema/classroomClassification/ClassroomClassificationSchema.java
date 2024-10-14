package io.github.abdofficehour.appointmentsystem.pojo.schema.classroomClassification;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class ClassroomClassificationSchema {
    private String classification;
    private int classroomId;
    private String classroom;
}
