package io.github.abdofficehour.appointmentsystem.pojo;

import io.github.abdofficehour.appointmentsystem.pojo.enumclass.Aim;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Setter
@Getter
public class ClassroomEvent {
    private int id;
    private LocalDate appointmentDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String applicant;
    private int classroom;
    private Boolean isMedia;
    private Boolean isComputer;
    private Boolean isSound;
    private Aim aim;
    private String events;
    private String approve;
    private int state;
}
