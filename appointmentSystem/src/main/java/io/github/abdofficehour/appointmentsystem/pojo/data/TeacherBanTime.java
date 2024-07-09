package io.github.abdofficehour.appointmentsystem.pojo.data;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class TeacherBanTime {
    private int startDate;
    private int endDate;
    private int startTime;
    private int endTime;
}
