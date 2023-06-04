package pl.szawara.GithubApi.service;

import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class RestMessageCreator {
    public static Map<String,Object> createMessage(HttpStatus status, String message){
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", status.value());
        errorResponse.put("Message", message);
        return errorResponse;
    }
}
