package rip.diamond.disguise.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DisguiseUtil {

    public static JsonObject readData(String uuid) {
        StringBuilder builder = new StringBuilder();

        try {
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
            connection.setConnectTimeout(3000);
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            reader.close();

            return new JsonParser().parse(builder.toString()).getAsJsonObject();
        } catch (Exception e) {
            return null;
        }
    }

    public static String readUUID(String uuid) throws IOException {
        StringBuilder builder = new StringBuilder();

        URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + uuid);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.addRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
        connection.setConnectTimeout(3000);
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));

        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        reader.close();

        JsonObject object = new JsonParser().parse(builder.toString()).getAsJsonObject();

        return object.get("id").getAsString();
    }
}
