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

public class i2pm { //main class, entry point in program

	private JSONRPC2Session session; //jsonrpc session with server
	private final String token; //the token is used for authentication with the server
				    //the password is given to the server, and THEN the token is returned
				    //from there, token is used for all other authentication

	public i2pm(URL url, String password) throws Exception { //constructor with lazy exception handling
								 //TODO fix error handling
		session = new JSONRPC2Session(url);
		JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);

		//initializes and creates the request to get the token
		String method = "Authenticate";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("API", 1);
		params.put("Password", password);
		String id = "1";
		JSONRPC2Request req = new JSONRPC2Request(method, params, id);

		JSONRPC2Response response = this.getSession().send(req); // gets response from server
		JSONObject result = (JSONObject) parser.parse(response.getResult().toString()); //gets json result field from response
		token = (String)result.get("Token"); //sets token to value from result map
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
		if (args.length < 2) { //makes sure there are two args
			System.out.println("please put in args");
			i2pm.printHelp();
		}
		String[] potentialURL = args[0].split("://", 2);
		i2pm main = new i2pm(new URI(potentialURL[0], "//" + potentialURL[1], "").toURL(), args[1]); //makes i2pm object from url passed and password

		//HACK copied code and made redundant, just a test function to get uptime
		//prepares request for uptime
		String method = "RouterInfo";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("Token", main.getToken());
		params.put("i2p.router.uptime", null);
		String id = "1";
		
		JSONRPC2Request req = new JSONRPC2Request(method, params, id);
		JSONRPC2Response response = main.getSession().send(req); //gets from server

		JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE); //TODO make a universal parser? there was one in the constructor
		JSONObject result = (JSONObject) parser.parse(response.getResult().toString());

		System.out.println(String.format("Your i2p router has been up for: %02d min, %02d sec", //formats server result to human readable time
			TimeUnit.MILLISECONDS.toMinutes((int)result.get("i2p.router.uptime")),
			TimeUnit.MILLISECONDS.toSeconds((int)result.get("i2p.router.uptime")) - 
			TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((int)result.get("i2p.router.uptime")))));
	}
}
