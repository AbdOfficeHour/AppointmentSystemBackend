package io.github.abdofficehour.appointmentsystem.pojo.schema.officeHourTimeTable;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Setter
@Getter
public class TimeTable {
    private Long date;
    private List<Period> busy;
    private List<Period> available;
}
