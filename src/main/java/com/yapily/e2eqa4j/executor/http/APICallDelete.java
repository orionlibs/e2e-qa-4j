package com.yapily.e2eqa4j.executor.http;

import static io.restassured.RestAssured.given;

import com.yapily.e2eqa4j.Logger;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
class APICallDelete
{
    @Autowired HTTPHeaderService httpHeaderService;


    Response makeDeleteAPICall(String url, HttpHeaders headers)
    {
        Logger.info("[E2EQA4J] making DELETE call");
        RestAssured.baseURI = url;
        RestAssured.defaultParser = Parser.JSON;
        headers = httpHeaderService.getHttpHeaders(headers);
        return given()
                        .contentType(ContentType.JSON)
                        .headers(headers.toSingleValueMap())
                        .accept(ContentType.JSON)
                        .when()
                        .delete()
                        .then()
                        .extract()
                        .response();
    }
}
