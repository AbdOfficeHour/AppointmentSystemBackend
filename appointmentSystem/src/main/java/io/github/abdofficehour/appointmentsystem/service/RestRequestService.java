package io.github.abdofficehour.appointmentsystem.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.abdofficehour.appointmentsystem.config.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
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

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 向鉴权中心验证token
     */
    public Boolean AuthToken(String token) throws JsonProcessingException {

        String url = properties.getMainAppUrl() + properties.getTokenUrl();

        try {

            // 创建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            // 创建请求体
            String josnRequestBody = objectMapper.writeValueAsString(new HashMap<String,Object>(){{put("token",token);}});
            HttpEntity<String> request = new HttpEntity<>(josnRequestBody,headers);
            // 发起post请求
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url,request,String.class);
//            ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);


            String responseStatus = responseEntity.getStatusCode().toString();
//            if (!Objects.equals(responseStatus, "200 OK")) {
//                return "";
//            }
//
//            String responseBody = responseEntity.getBody();
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode jsonNode = objectMapper.readTree(responseBody);
//
//            return jsonNode.get("data").get("id").asText();

            return Objects.equals(responseStatus, "200 OK");

        }catch (Exception e){
            System.out.println(e);
            return false;
        }
    }
}
