import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Parser;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.JSONObject;
import java.util.concurrent.TimeUnit;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class i2pm {

	private JSONRPC2Session session;
	private final String token;

	public i2pm(URL url, String password) throws Exception {
		session = new JSONRPC2Session(url);

		String method = "Authenticate";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("API", 1);
		params.put("Password", password);
		String id = "1";
		
		JSONRPC2Request req = new JSONRPC2Request(method, params, id);

		JSONRPC2Response response = this.getSession().send(req);

		JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);

		JSONObject result = (JSONObject) parser.parse(response.getResult().toString());
		token = (String)result.get("Token");
	}

	public JSONRPC2Session getSession() {
		return session;
	}

	public String getToken() {
		return token;
	}

	public static void printHelp() {
		System.out.println("arg 1 is url (https://localhost:7650)\narg 2 is password");
		System.exit(0);
	}
	
	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.out.println("please put in args");
			i2pm.printHelp();
		}
		String[] potentialURL = args[0].split("://", 2);
		i2pm main = new i2pm(new URI(potentialURL[0], "//" + potentialURL[1], "").toURL(), args[1]);

		String method = "RouterInfo";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("Token", main.getToken());
		params.put("i2p.router.uptime", null);
		String id = "1";
		
		JSONRPC2Request req = new JSONRPC2Request(method, params, id);

		JSONRPC2Response response = main.getSession().send(req);

		JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
		JSONObject result = (JSONObject) parser.parse(response.getResult().toString());

		System.out.println(String.format("Your i2p router has been up for: %02d min, %02d sec", 
			TimeUnit.MILLISECONDS.toMinutes((int)result.get("i2p.router.uptime")),
			TimeUnit.MILLISECONDS.toSeconds((int)result.get("i2p.router.uptime")) - 
			TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((int)result.get("i2p.router.uptime")))));
	}
}
