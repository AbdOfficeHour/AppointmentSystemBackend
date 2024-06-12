package io.github.abdofficehour.appointmentsystem.pojo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Setter
@Getter
public class OfficeHourEvent {
    private int id;
    private LocalDate appointmentDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String student;
    private String teacher;
    private String note;
    private String question;
    private String refuseResult;
    private String workSummary;
    private int state;
}
