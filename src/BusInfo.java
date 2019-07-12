import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONTokener;
import org.json.JSONArray;
import org.json.JSONObject;

import com.github.silk8192.jpushbullet.PushbulletClient;

public class BusInfo {

	private final String USER_AGENT = "Mozilla/5.0";
	private static final String API_KEY = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";

	public static void main(String[] args) throws Exception {
		BusInfo http = new BusInfo();

		String paradasAm[] = { "tuzsa-853" };
		String paradasPm[] = { "tuzsa-22", "tuzsa-863" };

		String mensaje = http.getDataFromAPI(paradasAm);

		PushbulletClient client = new PushbulletClient(API_KEY);
		client.sendNotePush("Bus", mensaje);

	}

	private String getDataFromAPI(String[] stops) throws Exception {
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

					sb.append(bus.getString("linea") + ": " + firstBus.replace(".", ""));
					sb.append(" y " + bus.get("segundo"));
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

					sb.append(bus.getString("linea") + ": " + firstBus.replace(".", ""));
					sb.append(" y " + bus.get("segundo"));
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

					sb.append(bus.getString("linea") + ": " + firstBus.replace(".", ""));
					sb.append(" y " + bus.get("segundo"));
					mensaje += sb.toString();
				}
			}
		} else {
			mensaje = "Error";
		}
		return mensaje;
	}
}