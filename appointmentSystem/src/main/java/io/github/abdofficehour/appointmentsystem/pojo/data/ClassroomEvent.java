package io.github.abdofficehour.appointmentsystem.pojo.data;

import io.github.abdofficehour.appointmentsystem.pojo.enumclass.Aim;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
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

    public ClassroomEvent(LocalDate appointmentDate, LocalDateTime startTime, LocalDateTime endTime, String applicant, int classroom, Boolean isMedia, Boolean isComputer, Boolean isSound, Aim aim, String events, String approve, int state) {
        this.appointmentDate = appointmentDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.applicant = applicant;
        this.classroom = classroom;
        this.isMedia = isMedia;
        this.isComputer = isComputer;
        this.isSound = isSound;
        this.aim = aim;
        this.events = events;
        this.approve = approve;
        this.state = state;
    }
}
