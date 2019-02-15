package ru.geekbrains.pocket.messenger.client.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

public class Converter {

    private final static String baseFile = "user.json";

    public static <T> T toJavaObject(String json, Class<T> valueType) throws IOException {
        return new ObjectMapper().readValue(json, valueType);
    }

    public static String toJson(Object object) throws IOException {
        return new ObjectMapper().writeValueAsString(object);
    }

    public static String toJson2(Object object){

        ObjectMapper mapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        try {
            mapper.writeValue(writer, object);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer.toString();

    }

    public static void toJSONinFile(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File(baseFile), object);
        System.out.println("json created!");
    }

    public static <T> T toJavaObject(Class<T> valueType) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(baseFile), valueType);
    }

}