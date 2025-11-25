package com.yapily.e2eqa4j.executor.http;

import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

@Component
public class HTTPExecutor
{
    @Autowired APICallGet apiCallGet;
    @Autowired APICallPost apiCallPost;
    @Autowired APICallPut apiCallPut;
    @Autowired APICallPatch apiCallPatch;
    @Autowired APICallDelete apiCallDelete;
    @Autowired HTTPHeaderService httpHeaderService;


    public APICallResult run(String method, String url, HttpHeaders headers, String requestBody)
    {
        Response response = null;
        if(HttpMethod.GET.name().equalsIgnoreCase(method))
        {
            response = makeGetAPICall(url, headers);
        }
        else if(HttpMethod.POST.name().equalsIgnoreCase(method))
        {
            response = makePostAPICall(url, requestBody, headers);
        }
        else if(HttpMethod.PUT.name().equalsIgnoreCase(method))
        {
            response = makePutAPICall(url, requestBody, headers);
        }
        else if(HttpMethod.PATCH.name().equalsIgnoreCase(method))
        {
            response = makePatchAPICall(url, requestBody, headers);
        }
        else if(HttpMethod.DELETE.name().equalsIgnoreCase(method))
        {
            response = makeDeleteAPICall(url, headers);
        }
        APICallResult apiCallResult = new APICallResult();
        apiCallResult.jsonResponseBody = response.body().asString();
        apiCallResult.jsonResponseBodyFormatted = response.body().asPrettyString();
        apiCallResult.headers = httpHeaderService.convertToMap(response.getHeaders());
        apiCallResult.statusCode = response.getStatusCode();
        return apiCallResult;
    }


    private Response makeGetAPICall(String url, HttpHeaders headers)
    {
        return apiCallGet.makeGetAPICall(url, headers);
    }


    private Response makePostAPICall(String url, String requestBody, HttpHeaders headers)
    {
        return apiCallPost.makePostAPICall(url, requestBody, headers);
    }


    private Response makePutAPICall(String url, String requestBody, HttpHeaders headers)
    {
        return apiCallPut.makePutAPICall(url, requestBody, headers);
    }


    private Response makePatchAPICall(String url, String requestBody, HttpHeaders headers)
    {
        return apiCallPatch.makePatchAPICall(url, requestBody, headers);
    }


    private Response makeDeleteAPICall(String url, HttpHeaders headers)
    {
        return apiCallDelete.makeDeleteAPICall(url, headers);
    }
}
