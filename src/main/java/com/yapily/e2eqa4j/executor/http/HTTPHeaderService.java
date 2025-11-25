package com.yapily.e2eqa4j.executor.http;

import io.restassured.http.Headers;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
public class HTTPHeaderService
{
    public HttpHeaders getHttpHeaders(HttpHeaders headers)
    {
        if(headers == null)
        {
            return new HttpHeaders();
        }
        return headers;
    }


    public HttpHeaders convertToHttpHeaders(Map<String, String> headers)
    {
        if(headers == null)
        {
            return new HttpHeaders();
        }
        else
        {
            HttpHeaders headersTemp = new HttpHeaders();
            headersTemp.setAll(headers);
            return headersTemp;
        }
    }


    public Map<String, String> convertToMap(Headers headers)
    {
        if(headers == null)
        {
            return new HashMap<>();
        }
        else
        {
            Map<String, String> headersMapper = new HashMap<>();
            headers.asList().forEach(header -> headersMapper.put(header.getName(), header.getValue()));
            return headersMapper;
        }
    }
}
