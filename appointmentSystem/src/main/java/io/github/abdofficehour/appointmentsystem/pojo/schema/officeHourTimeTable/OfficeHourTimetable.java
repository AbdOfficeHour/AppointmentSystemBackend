package io.github.abdofficehour.appointmentsystem.pojo.schema.officeHourTimeTable;

import lombok.*;

import java.util.List;

/**
 * 这个类用于表示TableInfo/officehour接口
 * 的返回体
 */
@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OfficeHourTimetable {
    private String name;
    private List<OfficeHourTime> officeHourTime;
    private List<TimeTable> timeTable;
}
