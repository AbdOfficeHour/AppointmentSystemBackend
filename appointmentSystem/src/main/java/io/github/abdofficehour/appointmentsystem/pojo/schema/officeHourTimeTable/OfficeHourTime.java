package io.github.abdofficehour.appointmentsystem.pojo.schema.officeHourTimeTable;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
