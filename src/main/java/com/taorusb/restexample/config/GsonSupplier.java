package com.taorusb.restexample.config;

import com.google.gson.*;
import com.taorusb.restexample.model.Event;
import com.taorusb.restexample.model.File;
import com.taorusb.restexample.model.User;
import com.taorusb.restexample.model.UserStatus;

import java.lang.reflect.Type;

public class GsonSupplier {

    private static Gson instance;

    private GsonSupplier() {
    }

    public static Gson getGson() {
        if (instance == null) {
            instance = new GsonBuilder()
                    .registerTypeAdapter(User.class, new UserSerializer())
                    .registerTypeAdapter(File.class, new FileSerializer())
                    .registerTypeAdapter(Event.class, new EventSerializer())
                    .registerTypeAdapter(User.class, new UserDeserializer())
                    .registerTypeAdapter(File.class, new FileDeserializer())
                    .registerTypeAdapter(Event.class, new EventDeserializer())
                    .serializeNulls()
                    .setPrettyPrinting()
                    .create();
        }
        return instance;
    }

    private static class UserSerializer implements JsonSerializer<User> {

        @Override
        public JsonElement serialize(User src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            result.addProperty("id", src.getId());
            result.addProperty("username", src.getUsername());
            result.addProperty("status", src.getStatus() == null ? UserStatus.ACTIVE.name() : src.getStatus().name());
            return result;
        }
    }

    private static class FileSerializer implements JsonSerializer<File> {

        @Override
        public JsonElement serialize(File src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            result.addProperty("id", src.getId());
            result.addProperty("path", src.getPath());
            result.addProperty("userId", src.getUser().getId());
            return result;
        }
    }

    private static class EventSerializer implements JsonSerializer<Event> {

        @Override
        public JsonElement serialize(Event src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            result.addProperty("id", src.getId());
            result.addProperty("uploadDate", src.getUploadDate());
            result.addProperty("fileId", src.getFile().getId());
            result.addProperty("userId", src.getUser().getId());
            return result;
        }
    }

    private static class UserDeserializer implements JsonDeserializer<User> {

        @Override
        public User deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            jsonCheckerForUser(jsonObject);
            idChecker(jsonObject.get("id").getAsString());
            emailChecker(jsonObject.get("username").getAsString());
            statusChecker(jsonObject.get("status").getAsString());
            User user = new User();
            user.setId(jsonObject.get("id").getAsLong());
            user.setUsername(jsonObject.get("username").getAsString());
            user.setStatus(UserStatus.valueOf(jsonObject.get("status").getAsString()));
            return user;
        }
    }

    private static class FileDeserializer implements JsonDeserializer<File> {

        @Override
        public File deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            jsonCheckerForFile(jsonObject);
            idChecker(jsonObject.get("id").getAsString());
            pathChecker(jsonObject.get("path").getAsString());
            idChecker(jsonObject.get("userId").getAsString());
            File file = new File();
            User user = new User();
            file.setId(jsonObject.get("id").getAsLong());
            file.setPath(jsonObject.get("path").getAsString());
            user.setId(jsonObject.get("userId").getAsLong());
            file.setUser(user);
            return file;
        }
    }

    private static class EventDeserializer implements JsonDeserializer<Event> {

        @Override
        public Event deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            jsonCheckerForEvent(jsonObject);
            idChecker(jsonObject.get("id").getAsString());
            dateChecker(jsonObject.get("uploadDate").getAsString());
            idChecker(jsonObject.get("userId").getAsString());
            idChecker(jsonObject.get("fileId").getAsString());
            Event event = new Event();
            File file = new File();
            User user = new User();
            event.setId(jsonObject.get("id").getAsLong());
            event.setUploadDate(jsonObject.get("uploadDate").getAsString());
            file.setId(jsonObject.get("fileId").getAsLong());
            user.setId(jsonObject.get("userId").getAsLong());
            event.setFile(file);
            event.setUser(user);
            return event;
        }
    }

    private static void jsonCheckerForUser(JsonObject jsonObject) {
        if (!jsonObject.has("id")
                || !jsonObject.has("username")
                || !jsonObject.has("status")) {
            throw new JsonSyntaxException("Invalid argument name.");
        }
        if (jsonObject.keySet().size() < 3 || jsonObject.keySet().size() > 3) {
            throw new JsonSyntaxException("Invalid argument count.");
        }
    }

    private static void jsonCheckerForFile(JsonObject jsonObject) {
        if (!jsonObject.has("id")
                || !jsonObject.has("path")
                || !jsonObject.has("userId")) {
            throw new JsonSyntaxException("Invalid argument name.");
        }
        if (jsonObject.keySet().size() < 3 || jsonObject.keySet().size() > 3) {
            throw new JsonSyntaxException("Invalid argument count.");
        }
    }

    private static void jsonCheckerForEvent(JsonObject jsonObject) {
        if (!jsonObject.has("id")
                || !jsonObject.has("uploadDate")
                || !jsonObject.has("userId")
                || !jsonObject.has("fileId")) {
            throw new JsonSyntaxException("Invalid argument name.");
        }
        if (jsonObject.keySet().size() < 4 || jsonObject.keySet().size() > 4) {
            throw new JsonSyntaxException("Invalid argument count.");
        }
    }

    private static void emailChecker(String s) {
        if (!s.matches("^\\w+@[a-zA-Z_]+?\\.[a-zA-Z]{2,3}$")) {
            throw new IllegalArgumentException("Not valid email");
        }
    }

    private static void statusChecker(String s) {
        try {
            UserStatus.valueOf(s);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Not valid user status.");
        }
    }

    private static void pathChecker(String s) {
        if (!s.matches("^/|(/[\\w-]+)+$")) {
            throw new IllegalArgumentException("Not valid path");
        }
    }

    private static void idChecker(String s) {
        if (!s.matches("\\d+")) {
            throw new IllegalArgumentException("Not valid id");
        }
    }

    private static void dateChecker(String s) {
        if (!s.matches("^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))" +
                "\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[13-9]|1[0-2])\\2))(?" +
                ":(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\/|-|\\.)0?2\\" +
                "3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[1357" +
                "9][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0" +
                "?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]" +
                "))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$")) {
            throw new IllegalArgumentException("Not valid id");
        }
    }
}