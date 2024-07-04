package io.github.abdofficehour.appointmentsystem.pojo.schema.timeTable;

import lombok.*;

import java.util.List;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ClassroomTimeTable {
    private String classroom;
    private List<SpecialTime> classroomTime;
    private List<TimeTable> timeTable;
}
