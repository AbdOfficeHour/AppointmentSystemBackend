package io.github.abdofficehour.appointmentsystem.pojo.schema.timeTable;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TableEvent {
    private LocalDate appointmentDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
