package io.github.abdofficehour.appointmentsystem.pojo.schema.officeHourTimeTable;

import lombok.*;

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
