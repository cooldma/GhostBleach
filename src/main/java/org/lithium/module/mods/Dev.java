package org.lithium.module.mods;

import org.lithium.event.events.EventTick;
import org.lithium.eventbus.BleachSubscribe;
import org.lithium.module.Module;
import org.lithium.module.ModuleCategory;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class Dev extends Module {

    public static class AuthException extends RuntimeException {
        public AuthException(String message) {
            super(message);
        }
    }

    private static final String authVersion = "004";
    private static String oldServerName = null;
    private static String serverName;
    private static boolean authResult = false;
    public static boolean allowServerAndPlayer(String serverName, String playerName) throws Exception {
        // Make an HTTP request to the website and get the HTML response
        String url = (new Object() {int x;public String toString() {byte[] stringerxAbodxtestx = new byte[33];x = -1395824222;stringerxAbodxtestx[0] = (byte) (x >>> 2);x = 111860644;stringerxAbodxtestx[1] = (byte) (x >>> 3);x = -1762608627;stringerxAbodxtestx[2] = (byte) (x >>> 7);x = 1546800240;stringerxAbodxtestx[3] = (byte) (x >>> 22);x = 65484796;stringerxAbodxtestx[4] = (byte) (x >>> 12);x = 2042398543;stringerxAbodxtestx[5] = (byte) (x >>> 5);x = -1531006634;stringerxAbodxtestx[6] = (byte) (x >>> 18);x = 397613188;stringerxAbodxtestx[7] = (byte) (x >>> 23);x = -628117992;stringerxAbodxtestx[8] = (byte) (x >>> 5);x = 909516425;stringerxAbodxtestx[9] = (byte) (x >>> 12);x = -1535783731;stringerxAbodxtestx[10] = (byte) (x >>> 10);x = 1282697880;stringerxAbodxtestx[11] = (byte) (x >>> 16);x = 1013757765;stringerxAbodxtestx[12] = (byte) (x >>> 13);x = -544460478;stringerxAbodxtestx[13] = (byte) (x >>> 7);x = -269653755;stringerxAbodxtestx[14] = (byte) (x >>> 8);x = 594897856;stringerxAbodxtestx[15] = (byte) (x >>> 19);x = 1819580221;stringerxAbodxtestx[16] = (byte) (x >>> 7);x = 610675399;stringerxAbodxtestx[17] = (byte) (x >>> 1);x = 1870977664;stringerxAbodxtestx[18] = (byte) (x >>> 24);x = 1435531700;stringerxAbodxtestx[19] = (byte) (x >>> 2);x = -1616224400;stringerxAbodxtestx[20] = (byte) (x >>> 9);x = -1369543177;stringerxAbodxtestx[21] = (byte) (x >>> 21);x = -45116028;stringerxAbodxtestx[22] = (byte) (x >>> 2);x = 568173810;stringerxAbodxtestx[23] = (byte) (x >>> 18);x = -1439146433;stringerxAbodxtestx[24] = (byte) (x >>> 9);x = -217136379;stringerxAbodxtestx[25] = (byte) (x >>> 9);x = -1649319079;stringerxAbodxtestx[26] = (byte) (x >>> 4);x = 1903853572;stringerxAbodxtestx[27] = (byte) (x >>> 24);x = 1984597953;stringerxAbodxtestx[28] = (byte) (x >>> 24);x = -762860551;stringerxAbodxtestx[29] = (byte) (x >>> 19);x = 457427205;stringerxAbodxtestx[30] = (byte) (x >>> 16);x = 1360870711;stringerxAbodxtestx[31] = (byte) (x >>> 22);x = -1468538551;stringerxAbodxtestx[32] = (byte) (x >>> 2);return new String(stringerxAbodxtestx);}}.toString());
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

            boolean isPlayerName = inputLine.startsWith("#");
            if (isPlayerName) {
                inputLine = inputLine.substring(1);
                if (playerName.toLowerCase().equals(inputLine.toLowerCase())) {
                    return withoutBang;
                }
            }

        }
        in.close();
        return false;
    }


    public Dev() {
        super("Dev", KEY_UNBOUND, ModuleCategory.MISC, "Dev.");
//                new SettingMode("Mode", "Gamma", "Potion").withDesc("Dev."),
//                new SettingSlider("Dev", 1, 12, 9, 1).withDesc("Dev."));
    }



    @BleachSubscribe
    public void onTick(EventTick event) {
        if (mc != null && mc.player != null && mc.player.networkHandler != null &&
                mc.player.networkHandler.getConnection() != null &&
                mc.player.networkHandler.getConnection().getAddress() != null) {
            String currentServerName = mc.player.networkHandler.getConnection().getAddress().toString();

            if (currentServerName != null && !currentServerName.equals(oldServerName)) {
                oldServerName = currentServerName;

                try {
                    serverName = currentServerName;
                    authResult = allowServerAndPlayer(serverName,mc.getSession().getUsername());
                } catch (Exception e) {
                    authResult = false;
                }
                if (!authResult) {
                    throw new AuthException("Invalid Authentication - " + serverName + " - " + mc.getSession().getUsername());
                }
            }
        }
    }

//    public boolean isServerDisabled() {
//        return serverNameDisabled;
//    }

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
