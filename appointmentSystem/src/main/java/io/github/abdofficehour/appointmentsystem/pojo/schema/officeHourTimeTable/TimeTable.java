package io.github.abdofficehour.appointmentsystem.pojo.schema.officeHourTimeTable;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 这个类用于表示TableInfo/officehour接口
 * 中的timeTable字段
 */
@Data
@Setter
@Getter
public class TimeTable {
    private Long date;
    private List<Period> busy;
    private List<Period> available;
}
