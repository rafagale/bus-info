import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONTokener;
import org.json.JSONArray;
import org.json.JSONObject;

import com.github.silk8192.jpushbullet.PushbulletClient;

public class BusInfo {

    private final String USER_AGENT = "Mozilla/5.0";
    private static final String API_KEY = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";

    public static void main(String[] args) throws Exception {
        BusInfo http = new BusInfo();

        String[] paradasAm = {"tuzsa-853"};
        String[] paradasPm = {"tuzsa-22", "tuzsa-863"};

        String[] busesAm = {"CI2"};
        String[] busesPm = {"CI1", "42"};

        String message = http.getDataFromAPI(paradasPm, busesPm);

        PushbulletClient client = new PushbulletClient(API_KEY);

        System.out.println(message);
//		client.deleteAllPushes();
//		client.sendNotePush("Bus", message);
    }


    private String getDataFromAPI(String[] stops, String[] buses) throws Exception {
        String message = "";
        StringBuilder sb = new StringBuilder("");
        URL obj = null;
        HttpURLConnection con = null;
        JSONObject actualBus;

        for (String stop : stops) {
            String url = "http://www.zaragoza.es/sede/servicio/urbanismo-infraestructuras/transporte-urbano/poste-autobus/" + stop + ".json";
            obj = new URL(url);

            con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);

            InputStream response = con.getInputStream();

            JSONTokener tokener = new JSONTokener(response);
            JSONObject object = new JSONObject(tokener);
            JSONArray destinos = object.getJSONArray("destinos");

            for (int i = 0; i < destinos.length(); i++) {
                for (String bus : buses) {
                    if (bus.equals(destinos.getJSONObject(i).get("linea"))) {
                        actualBus = (JSONObject) destinos.get(i);
                        String firstBus = actualBus.get("primero").toString();
                        String secondBus = actualBus.get("segundo").toString();
                        if (buses.length > 1) {
                            sb.append("\n");
                        }
                        sb.append(actualBus.getString("linea")).append(": ").append(getDataInMinutes(firstBus));
                        sb.append(" y ").append(getDataInMinutes(secondBus));
                        message = sb.toString();
                    }
                }
            }
        }
        return message;
    }

    private String calculateEstimatedTimeOfArrival(Integer waitingTime) {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.add(Calendar.MINUTE, waitingTime);
        date = cal.getTime();

        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String strTime = dateFormat.format(date);

        return strTime;
    }

    private String getDataInMinutes(String strBusTime) {
        int busTime;
        String[] parts = strBusTime.split(" ");
        String timeInMinutes = parts[0];

        try {
            busTime = Integer.parseInt(timeInMinutes);
        } catch (NumberFormatException e) {
            busTime = -1;
        }

        if (busTime != -1) {
            return calculateEstimatedTimeOfArrival(busTime);
        } else {
            return "nunca";
        }
    }

    private String getDataFromAPIOld_v2(String[] stops) throws Exception {
        String mensaje = "Error";

        if (stops.length == 1) {
            String url = "http://www.zaragoza.es/sede/servicio/urbanismo-infraestructuras/transporte-urbano/poste-autobus/"
                    + stops[0] + ".json";
            URL obj = new URL(url);
            HttpURLConnection con = null;

            try {
                con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", USER_AGENT);
            } catch (Exception e) {
                return con.getResponseCode() + "";
            }

            InputStream response = con.getInputStream();

            JSONTokener tokener = new JSONTokener(response);
            JSONObject object = new JSONObject(tokener);

            JSONArray destinos = object.getJSONArray("destinos");

            JSONObject bus;
            StringBuilder sb = new StringBuilder("");

            for (int i = 0; i < destinos.length(); i++) {
                if ("CI2".equals(destinos.getJSONObject(i).get("linea"))) {

                    bus = (JSONObject) destinos.get(i);
                    String firstBus = bus.get("primero").toString();
                    String secondBus = bus.get("segundo").toString();

                    sb.append(bus.getString("linea") + ": " + getDataInMinutes(firstBus));
                    sb.append(" y " + getDataInMinutes(secondBus));
                    mensaje = sb.toString();
                }
            }
        } else if (stops.length == 2) {
            String url = "http://www.zaragoza.es/sede/servicio/urbanismo-infraestructuras/transporte-urbano/poste-autobus/"
                    + stops[0] + ".json";
            URL obj = new URL(url);
            HttpURLConnection con = null;

            try {
                con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", USER_AGENT);
            } catch (Exception e) {
                return con.getResponseCode() + "";
            }

            InputStream response = con.getInputStream();

            JSONTokener tokener = new JSONTokener(response);
            JSONObject object = new JSONObject(tokener);

            JSONArray destinos = object.getJSONArray("destinos");

            JSONObject bus;
            StringBuilder sb = new StringBuilder("");

            for (int i = 0; i < destinos.length(); i++) {
                if ("42".equals(destinos.getJSONObject(i).get("linea"))) {

                    bus = (JSONObject) destinos.get(i);
                    String firstBus = bus.get("primero").toString();
                    String secondBus = bus.get("segundo").toString();

                    sb.append(bus.getString("linea") + ": " + getDataInMinutes(firstBus));
                    sb.append(" y " + getDataInMinutes(secondBus));
                    mensaje = sb.toString() + "\n";
                }
            }
            url = "http://www.zaragoza.es/sede/servicio/urbanismo-infraestructuras/transporte-urbano/poste-autobus/"
                    + stops[1] + ".json";
            obj = new URL(url);

            try {
                con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", USER_AGENT);
            } catch (Exception e) {
                return con.getResponseCode() + "";
            }

            response = con.getInputStream();
            tokener = new JSONTokener(response);
            object = new JSONObject(tokener);
            destinos = object.getJSONArray("destinos");
            bus = null;
            sb = new StringBuilder("");
            for (int i = 0; i < destinos.length(); i++) {
                if ("CI1".equals(destinos.getJSONObject(i).get("linea"))) {

                    bus = (JSONObject) destinos.get(i);
                    String firstBus = bus.get("primero").toString();
                    String secondBus = bus.get("segundo").toString();

                    sb.append(bus.getString("linea") + ": " + getDataInMinutes(firstBus));
                    sb.append(" y " + getDataInMinutes(secondBus));
                    mensaje += sb.toString();
                }
            }
        } else {
            mensaje = "Error";
        }
        return mensaje;
    }
}