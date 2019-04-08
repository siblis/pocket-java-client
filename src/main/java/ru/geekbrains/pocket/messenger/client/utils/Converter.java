package ru.geekbrains.pocket.messenger.client.utils;


import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

public class Converter {

    private final static String baseFile = "user.json";

    public static <T> T toJavaObject(String json, Class<T> valueType) throws JsonSyntaxException {
        return new Gson().fromJson(json, valueType);
    }

    public static String toJson(Object object) {
        return new Gson().toJson(object);
    }

    public static String toJson2(Object object){

        Gson mapper = new Gson();
        StringWriter writer = new StringWriter();
        try {
            mapper.toJson(object, writer);
        } catch (JsonIOException e) {
            e.printStackTrace();
        }
        return writer.toString();

    }

    public static void toJSONinFile(Object object) throws IOException {
        Gson mapper = new Gson();
        mapper.toJson(object, new FileWriter(new File(baseFile)));
        System.out.println("json created!");
    }

    public static <T> T toJavaObject(Class<T> valueType) throws IOException {
        Gson mapper = new Gson();
        return mapper.fromJson(new FileReader(new File(baseFile)), valueType);
    }

}