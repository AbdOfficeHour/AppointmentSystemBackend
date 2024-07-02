package io.github.abdofficehour.appointmentsystem.pojo.schema.officeHourTimeTable;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class OfficeHourTimetable {
    private String name;
    private OfficeHourTime officeHourTime;
    private TimeTable timeTable;
}
