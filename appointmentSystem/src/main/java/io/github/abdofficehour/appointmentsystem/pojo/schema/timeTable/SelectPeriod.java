package io.github.abdofficehour.appointmentsystem.pojo.schema.timeTable;
import lombok.*;

/**
 * 这个类用于表示时间段
 */
@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SelectPeriod {
    private Long startTime;
    private Long endTime;
    public SelectPeriod(long start, long end) {
        this.startTime = start;
        this.endTime = end;
    }
}