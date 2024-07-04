package io.github.abdofficehour.appointmentsystem.pojo.data;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Setter
@Getter
@NoArgsConstructor
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


    /**
     * 测试用构造函数
     * @param appointmentDate 日期
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param student 学生
     * @param teacher 教室
     */
    public OfficeHourEvent(LocalDate appointmentDate, LocalDateTime startTime, LocalDateTime endTime,String student,String teacher){
        this.appointmentDate = appointmentDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.student = student;
        this.teacher = teacher;
    }
}
