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
    private static final String API_KEY = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXx";

    public static void main(String[] args) throws Exception {
        BusInfo http = new BusInfo();

        String[] paradasAm = {"tuzsa-853"};
        String[] paradasPm = {"tuzsa-22", "tuzsa-863"};

        String[] busesAm = {"CI2"};
        String[] busesPm = {"CI1", "42"};

        String message = http.getDataFromAPI(paradasPm, busesPm);

        PushbulletClient client = new PushbulletClient(API_KEY);

        client.deleteAllPushes();
        client.sendNotePush("Bus", message);
    }


    private String getDataFromAPI(String[] stops, String[] buses) throws Exception {
        String message = "";
        StringBuilder sb = new StringBuilder();
        URL obj = null;
        HttpURLConnection con = null;
        JSONObject bus;

        for (String stop : stops) {
            String url = "http://www.zaragoza.es/sede/servicio/urbanismo-infraestructuras/transporte-urbano/poste-autobus/" + stop + ".json";
            obj = new URL(url);

            con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);
            //TODO:try catch
            InputStream response = con.getInputStream();

            JSONTokener tokener = new JSONTokener(response);
            JSONObject object = new JSONObject(tokener);
            JSONArray destinos = object.getJSONArray("destinos");

            for (int i = 0; i < destinos.length(); i++) {
                for (String actualBus : buses) {
                    if (actualBus.equals(destinos.getJSONObject(i).get("linea"))) {
                        bus = (JSONObject) destinos.get(i);
                        String busStop = bus.getString("linea");
                        String firstBus = bus.get("primero").toString();
                        String secondBus = bus.get("segundo").toString();
                        sb.append(busStop)
                                .append(": ")
                                .append(getDataInMinutes(firstBus))
                                .append(" y ")
                                .append(getDataInMinutes(secondBus));
                        if (buses.length > 1) {
                            sb.append("\n");
                        }
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
}