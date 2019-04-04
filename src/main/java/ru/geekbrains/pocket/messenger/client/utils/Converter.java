package ru.geekbrains.pocket.messenger.client.utils;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.geekbrains.pocket.messenger.client.controller.ClientController;
import ru.geekbrains.pocket.messenger.client.model.pub.UserProfilePub;
import ru.geekbrains.pocket.messenger.client.view.customFX.CFXListElement;
import ru.geekbrains.pocket.messenger.database.entity.User;
import ru.geekbrains.pocket.messenger.database.entity.UserProfile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Converter {
    private static final Logger log = LogManager.getLogger(Converter.class);
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

    public static User convertUserProfileJSONToUser(String jsonText) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return new User(null, gson.fromJson(jsonText, UserProfile.class));
    }

    public static List<CFXListElement> convertJSONToCFXListElements(String jsonText) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        List<CFXListElement> res = new ArrayList<>();
        try {
            List<UserProfile> finded = gson.fromJson(jsonText,
                    new TypeToken<List<UserProfile>>(){}.getType());
            finded.forEach(user -> res.add(new CFXListElement(new User(null, user))));
        } catch (Exception e) {
            log.error("HTTPSRequest.getUserByNameOrEmail_JsonParsError", e);
        }
        return res.isEmpty() ? null : res;
    }

    public static CFXListElement convertJSONToCFXListElement(String jsonText) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        CFXListElement res = null;
        try {
            UserProfilePub finded = gson.fromJson(jsonText, UserProfilePub.class);
            res = new CFXListElement(new User(null, new UserProfile(finded)));
        } catch (Exception e) {
            log.error("HTTPSRequest.getUserByNameOrEmail_JsonParsError", e);
        }
        return res;
    }
}