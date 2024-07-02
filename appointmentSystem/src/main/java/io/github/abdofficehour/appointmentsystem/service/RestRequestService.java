package io.github.abdofficehour.appointmentsystem.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.abdofficehour.appointmentsystem.config.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;

/**
 * 用于向鉴权中心发送请求
 */
@Service
public class RestRequestService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private Properties properties;

    /**
     * 向鉴权中心验证token
     */
    public String AuthToken(String token) throws JsonProcessingException {

        String url = properties.getMainAppUrl() + properties.getTokenUrl() + String.format("?token=%s",token);

        try {
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);


            String responseStatus = responseEntity.getStatusCode().toString();
            if (!Objects.equals(responseStatus, "200 OK")) {
                return "";
            }

            String responseBody = responseEntity.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            return jsonNode.get("data").get("id").asText();
        }catch (Exception e){
            return "";
        }
    }
}
