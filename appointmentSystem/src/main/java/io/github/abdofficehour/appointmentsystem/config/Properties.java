package io.github.abdofficehour.appointmentsystem.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "custom.config")
@Data
@Setter
@Getter
public class Properties {
    private String mainAppUrl;
    private String tokenUrl;
}
