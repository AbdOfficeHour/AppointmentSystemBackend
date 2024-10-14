package io.github.abdofficehour.appointmentsystem.pojo.data;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeacherTimeTable {
    private int id;
    private LocalDate appointmentDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String teacherId;

    public TeacherTimeTable(LocalDate appointmentDate, LocalDateTime startTime, LocalDateTime endTime, String teacherId) {
        this.appointmentDate = appointmentDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.teacherId = teacherId;
    }
}
