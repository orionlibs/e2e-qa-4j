package com.yapily.e2eqa4j.json;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

@Service
public class JSONService
{
    @Autowired JsonMapper jsonMapper;


    public String convertObjectToJSON(Object objectToConvert)
    {
        try
        {
            return jsonMapper.writeValueAsString(objectToConvert);
        }
        catch(JacksonException e)
        {
            return "";
        }
    }


    public Object convertJSONToObject(String jsonData, Class<?> classToConvertTo) throws JacksonException
    {
        return jsonMapper.readValue(jsonData, classToConvertTo);
    }
}
