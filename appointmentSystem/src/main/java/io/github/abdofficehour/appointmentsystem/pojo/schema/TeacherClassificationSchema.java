package io.github.abdofficehour.appointmentsystem.pojo.schema;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Data
@Setter
@Getter
public class TeacherClassificationSchema {
    private String classification;
    private List<Map<String,Object>> teachers;
}
