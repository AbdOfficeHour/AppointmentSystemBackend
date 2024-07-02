package io.github.abdofficehour.appointmentsystem.pojo.schema.officeHourTimeTable;

import lombok.*;

/**
 * 这个类用于表示时间段
 */
@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Period {
    private Long start;
    private Long end;
}
