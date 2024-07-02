package io.github.abdofficehour.appointmentsystem.pojo.schema.officeHourTimeTable;

import lombok.*;

/**
 * 这个类用于表示TableInfo/officehour接口
 * 中的officeHourTime字段
 */
@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OfficeHourTime {
    private Long date;
    private Long start_time;
    private Long end_time;
}
