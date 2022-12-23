package org.nohurtcam.module;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.nohurtcam.eventbus.BleachSubscribe;

import javax.net.ssl.HttpsURLConnection;
import java.io.OutputStream;
import java.net.URL;

public class DiscordMessage {



    public static final MinecraftClient mc = MinecraftClient.getInstance();


//    public static void main(String[] args) {
//        sendMessage("My Title","This is my test");
//    }
    public void init() {
//        BleachLogger.info("1");
        ServerPlayConnectionEvents.JOIN.register(this::discordMessage);
//        BleachLogger.info("2");
    }

    @BleachSubscribe
    public void discordMessage(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
//        BleachLogger.info("3");
        String title = "Alert" + "  -   " + mc.getSession().getUsername();
        String message = "Has joined with Tessssstttting";

        String serverName = mc.player.networkHandler.getConnection().getAddress().toString().toLowerCase();
        if (serverName.contains("fewer") || serverName.contains("box")) {
            message = "This user is on a blocked server: " + serverName;
        } else {
            message = "Ok server: " + serverName;
        }

        sendMessage(title, message);
    }

    public static void sendMessage(String title, String message) {
        ///////////////////////////////////////////////
        String tokenWebhook = "https://discord.com/api/webhooks/1054792482988241027/LjZZa9UejJ_9dP8_HuTC9RYtACL6OmUgf7jCb-PkjZELQ0E43VWQBK1-sOBqZeMnMv2B";
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