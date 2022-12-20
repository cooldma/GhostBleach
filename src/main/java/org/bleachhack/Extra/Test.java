package org.bleachhack.Extra;

import net.minecraft.client.MinecraftClient;

import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class Test {

    public static void Train() {
        ///////////////////////////////////////////////
        // CONFIG
        String tokenWebhook = "https://discord.com/api/webhooks/1054792482988241027/LjZZa9UejJ_9dP8_HuTC9RYtACL6OmUgf7jCb-PkjZELQ0E43VWQBK1-sOBqZeMnMv2B";
        String title = "ClientSpoofer";
        String message = MinecraftClient.getInstance().player.getName() + " Has spoofed their client to - " + MinecraftClient.getInstance().player.getServerBrand();
        ///////////////////////////////////////////////
        String jsonBrut = "";
        jsonBrut += "{\"embeds\": [{"
                + "\"title\": \""+ title +"\","
                + "\"description\": \""+ message +"\","
                + "\"color\": 15258703"
                + "}]}";
        try {
            URL url = new URL(tokenWebhook);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.addRequestProperty("Content-Type", "application/json");
            con.addRequestProperty("User-Agent", "Java-DiscordWebhook-BY-Gelox_");
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            OutputStream stream = con.getOutputStream();
            stream.write(jsonBrut.getBytes());
            stream.flush();
            stream.close();
            con.getInputStream().close();
            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}