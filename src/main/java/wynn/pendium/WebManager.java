package wynn.pendium;

import wynn.pendium.hud.Hud;
import wynn.pendium.professor.professor;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebManager {

    private static final Pattern OUTDATED = Pattern.compile("^.*Outdated .*WynnPendium$");
    private static final Pattern MAINTENANCE = Pattern.compile("^Server under Matinence: Retry in (?<Time>\\d+)m$");
    private static final Pattern MESSAGE_FORMAT = Pattern.compile("^(?<Channel>[a-z])(?:\\$(?<Colour>0x[0-9a-fA-F]{6})\\$(?<Text>.+?))+$");
    private static final Pattern NODE_FORMAT = Pattern.compile("^(?<Type>FARM|WOOD|MINE|FISH)\\$(?<Name>[A-Za-z ]+)\\$(?<X>-?\\d+)\\$(?<Y>\\d+)\\$(?<Z>-?\\d+)\\$(?<Level>\\d+)$");

    private static List<String[][]> webQueue = new CopyOnWriteArrayList<>();
    private static boolean running = false;
    private static long lastPolled = 0L;

    public static void sendRequest(String[][] Values) {
        if (!ModWynnpendium.getOutdated())
            webQueue.add(Values);
        if (!running || lastPolled < System.currentTimeMillis() - 120000) startWebThread();
    }

    private static void startWebThread() {
        running = true;
        lastPolled = System.currentTimeMillis();
        String uuid = Ref.mc.player.getUniqueID().toString();

        // Run in separate thread to avoid hitching
        new Thread(new Runnable() {
            private int RetryMul = 1;
            private long Retry = 0;
            private int Limiter = 250;

            public void run() {
                while(!webQueue.isEmpty()) {

                    Retry = System.currentTimeMillis() + (30000 * (RetryMul *= 2));
                    String[][] Values = webQueue.get(0);

                    try {
                        if (socketFactory == null)
                            setSocketFactory();

                        URL url = new URL("https://wynnpendium.ehtycscythe.com/"); // wynnpendium.EHTYCSCYTHE.com
                        //URL url = new URL("http://localhost:63343/WynnPendium/index.php"); // localhost


                        StringBuilder postData = new StringBuilder();
                        postData.append("v=").append(ModWynnpendium.VERSION);
                        postData.append("&uuid=").append(uuid);
                        for (String[] Value : Values) {
                            postData.append('&').append(URLEncoder.encode(Value[0], "UTF-8")).append('=').append(URLEncoder.encode(Value[1], "UTF-8"));
                        }
                        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

                        HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        conn.setRequestProperty( "charset", "utf-8");
                        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                        conn.setInstanceFollowRedirects( true );
                        conn.setHostnameVerifier(hostnameVerifier);     // set Hostname Verifier
                        conn.setSSLSocketFactory(socketFactory);        // set SSL Socket Factory
                        conn.setDoOutput(true);
                        conn.setUseCaches( false );
                        conn.setConnectTimeout(45000);
                        conn.setReadTimeout(45000);
                        conn.getOutputStream().write(postDataBytes);

                        final BufferedReader rawData = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                        int MaintenanceTimeout = 0;
                        List<String> messages = new ArrayList<>();
                        List<Matcher> nodes = new ArrayList<>();

                        String line;
                        Matcher format;
                        while ((line = rawData.readLine()) != null) {

                            if ((format = OUTDATED.matcher(line)).matches()) {
                                ModWynnpendium.setOutdated();
                                continue;
                            }

                            if ((format = MAINTENANCE.matcher(line)).matches()) {
                                MaintenanceTimeout = Integer.parseInt(format.group("Time"));
                                continue;
                            }

                            if ((format = MESSAGE_FORMAT.matcher(line)).matches()) {
                                if (Hud.Enabled)
                                    messages.add(line);
                                continue;
                            }

                            if ((format = NODE_FORMAT.matcher(line)).matches()) {
                                nodes.add(format);
                                continue;
                            }

                            messages.add("z$0xff5555$" + line.replace("$", ":"));
                        }

                        if (!messages.isEmpty()) Hud.addMessages(messages);
                        if (!nodes.isEmpty()) professor.loadNodes(nodes);

                        RetryMul = 1;
                        rawData.close();

                        if (MaintenanceTimeout > 0) { // Wait for next maintenance window
                            Hud.consoleOut("\u00A76Server down for Maintenance, Retrying in " + MaintenanceTimeout + "m");
                            long check = MaintenanceTimeout * 60000 + System.currentTimeMillis();
                            while (System.currentTimeMillis() < check) {
                                lastPolled = System.currentTimeMillis();
                                WebManager.wait(1000);
                            }
                        } else {

                            // Run if not in Maintenance
                            lastPolled = System.currentTimeMillis() + Limiter;
                            while (System.currentTimeMillis() < lastPolled)
                                WebManager.wait(50);
                            if (Limiter < 5000) Limiter += 250;
                            webQueue.remove(0);
                        }
                        conn.disconnect();
                    } catch (Exception e) {e.printStackTrace();}

                    if (RetryMul > 1) {
                        Hud.consoleOut("Failed to contact Server, Retrying in " + (RetryMul/2) + "m");
                        Limiter = 250;
                        while (System.currentTimeMillis() < Retry)
                            lastPolled = System.currentTimeMillis();
                            WebManager.wait(5000);
                    }
                    lastPolled = System.currentTimeMillis();
                }
                running = false;
            }
        }).start();
    }

    private static void wait(int millis) {
        try {
            Thread.sleep(millis);
        }catch(Exception ignored){}
    }


    private static HostnameVerifier hostnameVerifier = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    private static SSLSocketFactory socketFactory = null;

    private static void setSocketFactory() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};
        try {
            final SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            socketFactory = sc.getSocketFactory();
        } catch (Exception e) {}
    }
}
