package web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import ast.Program;
import exceptions.LoginError;
import exceptions.ProcessError;
import simulate.Critter;

/**
 * HTTP Client demo program. Can both GET and POST to a URL and read input back
 * from the server. Designed to be used with the demo server.
 */
public class myClient {
	public static void main(String[] args) {
		myClient test = new myClient("http://hexworld.herokuapp.com:80/hexworld");
		System.out.println(test.login("admin", "gandalf"));
		test.getStateOfWorld(0);
	}
	public void start()
	{
		myClient test = new myClient("http://hexworld.herokuapp.com:80/hexworld");
		System.out.println(test.login("admin", "gandalf"));
		test.getStateOfWorld(0);
	}
	
	private static void usage() {
		System.err.println("Usage: MyClient <URL> [<message>]");
		System.exit(1);
	}

	private String mainURLString;
	private int session_id = -1;
	
	/**
	 * If message is null, query the URL (GET) for the current message. Otherwise,
	 * use a POST request to send the message.
	 */
	public myClient(String url) {
		mainURLString = url;

		/*
		 * Gson gson = new Gson(); try { /*if (newOther == null) this.url = new
		 * URL(url); else this.url = new URL(url + "?other_param=" + newOther);
		 * 
		 * LoginJsonBundle postbundle = new LoginJsonBundle(); postbundle.level="admin";
		 * postbundle.password="gandalf"; this.url=new URL(url); HttpURLConnection
		 * connection = (HttpURLConnection) this.url.openConnection();
		 * connection.setRequestProperty("Content-Type", "application/json");
		 * 
		 * //get connection.connect(); System.out.println("Doing GET " + url);
		 * BufferedReader r = new BufferedReader(new
		 * InputStreamReader(connection.getInputStream())); dumpResponse(r);
		 * 
		 * //post /* System.out.println("Doing POST " + url);
		 * connection.setDoOutput(true); // send a POST message
		 * connection.setRequestMethod("POST"); PrintWriter w = new
		 * PrintWriter(connection.getOutputStream()); w.println(gson.toJson(postbundle,
		 * LoginJsonBundle.class)); w.flush(); BufferedReader r = new BufferedReader(new
		 * InputStreamReader(connection.getInputStream())); Gson received = new Gson();
		 * rbundle b = new rbundle(); b=received.fromJson(r, rbundle.class);
		 * System.out.println(b.session_id); System.out.println(b.author_message);
		 * 
		 * } catch (MalformedURLException e) { usage(); } catch (IOException e) {
		 * System.err.println("IO exception: " + e.getMessage()); }
		 */
	}

	/** Read back output from the server. Could change to parse JSON... */
	void dumpResponse(BufferedReader r) throws IOException {

		for (;;) {
			String l = r.readLine();
			if (l == null)
				break;
			System.out.println("Read: " + l);
		}
	}

	/**
	 * Try to login to the server with the given level and password. If successfully
	 * login, it will return a int number of session_id. If fail, return a -1.
	 * 
	 * @param level
	 *            the level is "admin","read","write"
	 * @param password
	 *            the corresponding password
	 * @return the responseCode
	 */
	public int login(String level, String password) {

		Gson gson = new Gson();
		LoginJsonBundle login = new LoginJsonBundle(level, password);
		try {
			URL loginUrl = new URL(mainURLString + "/login");

			HttpURLConnection connection = (HttpURLConnection) loginUrl.openConnection();
			connection.setRequestProperty("Content-Type", "application/json");
			System.out.println("Doing POST Tring login... " + loginUrl);
			connection.setDoOutput(true); // send a POST message
			connection.setRequestMethod("POST");

			// transfer into Json and put it into a stream.
			PrintWriter w = new PrintWriter(connection.getOutputStream());
			w.println(gson.toJson(login, LoginJsonBundle.class));
			w.flush();

			// ready to receive
			BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			LoginReceiveBundle rbundle = new LoginReceiveBundle();
			rbundle = gson.fromJson(r, LoginReceiveBundle.class);
			this.session_id = rbundle.getSession_id();
			return connection.getResponseCode();
			
		} catch (MalformedURLException e) {
			usage();
		} catch (IOException e) {
			System.err.println("IO exception: " + e.getMessage());
		}
		return -1; // error with -1
	}

	/**
	 * Return an ArrayList of Critter, fail if it returns null A get method with a
	 * seesion_id on the URL is implemented.
	 * 
	 * @return
	 */
	public displayHex[] getCritterList() {
		displayHex[] newCritterList;
		Gson gson = new Gson();
		try {
			if (this.session_id == -1) // Check if it is valid call under an existed session_id.
			{
				throw new LoginError();
			}

			URL loginUrl = new URL(mainURLString + "/critters?session_id=" + String.valueOf(this.session_id));
			HttpURLConnection connection = (HttpURLConnection) loginUrl.openConnection();
			connection.setRequestMethod("GET");
			System.out.println("Tring to getCritterList... " + loginUrl);

			BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			dumpResponse(r); /////////////// test
			if (connection.getResponseCode() == 200) {
				newCritterList = gson.fromJson(r, displayHex[].class);
				return newCritterList;
			} else {
				throw new ProcessError();
			}
		} catch (MalformedURLException e) {
			usage();
		} catch (IOException e) {
			System.err.println("IO exception: " + e.getMessage());
		} catch (LoginError e) {
			System.err.println("LoginError:" + e.getMessage());
		} catch (ProcessError e) {
			System.err.println("ProcessError:" + e.getMessage());
		}
		return null; // error with null
	}

	/**
	 * 
	 * @param c
	 *            the critter program that is going to be copied.
	 * @param num
	 *            the number of critter that is going to be placed.
	 */
	public int[] randomlyCreateCritter(displayHex c, int num) {
		Gson gson = new Gson();
		sendRandomCritterBundle bundle = new sendRandomCritterBundle(c.species_id, c.program, c.mem, num);

		try {
			if (this.session_id == -1) // Check if it is valid call under an existed session_id.
			{
				throw new LoginError();
			}
			URL loginUrl = new URL(mainURLString + "/critters?session_id=" + String.valueOf(this.session_id));

			HttpURLConnection connection = (HttpURLConnection) loginUrl.openConnection();
			connection.setRequestProperty("Content-Type", "application/json");
			System.out.println("Doing Post RandomlyCreateCritter... " + loginUrl);
			connection.setDoOutput(true); // send a POST message
			connection.setRequestMethod("POST");

			// transfer into Json and put it into a stream.
			PrintWriter w = new PrintWriter(connection.getOutputStream());
			w.println(gson.toJson(bundle, sendRandomCritterBundle.class));
			w.flush();

			// ready to receive
			BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			critterIdsBundle rbundle = new critterIdsBundle();
			rbundle = gson.fromJson(r, critterIdsBundle.class);
			return rbundle.ids;

		} catch (MalformedURLException e) {
			usage();
		} catch (IOException e) {
			System.err.println("IO exception: " + e.getMessage());
		} catch (LoginError e) {
			System.err.println("LoginError:" + e.getMessage());
		}

		return null; // error with null

	}

	public int[] positionCreateCritter(displayHex c, int row, int column) {
		Gson gson = new Gson();
		position positions[] = new position[1];
		positions[0] = new position(row, column);
		sendPositionCritterBundle bundle = new sendPositionCritterBundle(c.species_id, c.program, c.mem,
				positions);

		try {
			if (this.session_id == -1) // Check if it is valid call under an existed session_id.
			{
				throw new LoginError();
			}
			URL loginUrl = new URL(mainURLString + "/critters?session_id=" + String.valueOf(this.session_id));

			HttpURLConnection connection = (HttpURLConnection) loginUrl.openConnection();
			connection.setRequestProperty("Content-Type", "application/json");
			System.out.println("Doing Post RandomlyCreateCritter... " + loginUrl);
			connection.setDoOutput(true); // send a POST message
			connection.setRequestMethod("POST");
			System.out.println(gson.toJson(bundle, sendPositionCritterBundle.class));
			// transfer into Json and put it into a stream.
			PrintWriter w = new PrintWriter(connection.getOutputStream());
			w.println(gson.toJson(bundle, sendPositionCritterBundle.class));
			w.flush();

			// ready to receive
			BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			System.out.println(connection.getResponseCode());
			critterIdsBundle rbundle = new critterIdsBundle();
			rbundle = gson.fromJson(r, critterIdsBundle.class);
			return rbundle.ids;

		} catch (MalformedURLException e) {
			usage();
		} catch (IOException e) {
			System.err.println("IO exception: " + e.getMessage());
		} catch (LoginError e) {
			System.err.println("LoginError:" + e.getMessage());
		}

		return null; // error with null

	}

	/**
	 * 
	 * @param id
	 *            the critter's unique id
	 * @return the retrievedCritter. If not found, return a null
	 */
	public retrievedCritter retrieveCritter(int id) {
		Gson gson = new Gson();
		try {
			if (this.session_id == -1) // Check if it is valid call under an existed session_id.
			{
				throw new LoginError();
			}
			URL loginUrl = new URL(mainURLString + "/critter/" + String.valueOf(id) + "?session_id="
					+ String.valueOf(this.session_id));

			HttpURLConnection connection = (HttpURLConnection) loginUrl.openConnection();
			connection.setRequestMethod("GET");
			// ready to receive
			BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			if (connection.getResponseCode() == 200) {
				retrievedCritter rbundle = new retrievedCritter();
				rbundle = gson.fromJson(r, retrievedCritter.class);
				return rbundle;
			} else {
				throw new ProcessError();
			}
		} catch (MalformedURLException e) {
			usage();
		} catch (IOException e) {
			System.err.println("IO exception: " + e.getMessage());
		} catch (LoginError e) {
			System.err.println("LoginError:" + e.getMessage());
		} catch (ProcessError e) {
			System.err.println("ProcessError:" + e.getMessage());
		}

		return null; // error with null

	}
	//Remove a Critter based on its id.
	public boolean removeCritter(int id) {
		try {
			if (this.session_id == -1) // Check if it is valid call under an existed session_id.
			{
				throw new LoginError();
			}
			URL loginUrl = new URL(
					mainURLString + "/" + String.valueOf(id) + "?session_id=" + String.valueOf(this.session_id));

			HttpURLConnection connection = (HttpURLConnection) loginUrl.openConnection();
			connection.setRequestMethod("DELETE");
			// ready to receive
			connection.getInputStream();
			if (connection.getResponseCode() == 204)
				return true;
			else
				return false;
		} catch (MalformedURLException e) {
			usage();
		} catch (IOException e) {
			System.err.println("IO exception: " + e.getMessage());
		} catch (LoginError e) {
			System.err.println("LoginError:" + e.getMessage());
		}
		return false;
	}

	/**
	 * The client will ignore any critter lines in the world definition. The only
	 * way to add critters to the world is through other API calls later. This
	 * method may still place rocks and food into the new world, but the world will
	 * initially start out with no critters.
	 * 
	 * @param worldfile
	 *            the file of the description of the file
	 * @return true if succeed, else fail.
	 */
	public boolean makeNewWorld(File worldfile) {
		Gson gson = new Gson();
		StringBuilder sb = new StringBuilder();
		String line;
		String trimed[];

		try {
			BufferedReader br = new BufferedReader(new FileReader(worldfile));
			while (true) // ignore any critter files....
			{
				line = br.readLine();
				if(line==null)
					break;
				trimed = line.trim().split("\\s+");
				if (trimed[0].equals("critter")) {
					br.close();
					break;
				} else
					sb.append(line+"\n");
			}
			worldMessage bundle = new worldMessage();
			bundle.description = sb.toString();

			if (this.session_id == -1) // Check if it is valid call under an existed session_id.
			{
				throw new LoginError();
			}

			// connect to the URL
			URL loginUrl = new URL(mainURLString + "/world?session_id=" + String.valueOf(this.session_id));

			HttpURLConnection connection = (HttpURLConnection) loginUrl.openConnection();
			connection.setRequestProperty("Content-Type", "application/json");
			System.out.println("Making a new world... " + loginUrl);
			connection.setDoOutput(true); // send a POST message
			connection.setRequestMethod("POST");

			// transfer into Json and put it into a stream.
			PrintWriter w = new PrintWriter(connection.getOutputStream());
			w.println(gson.toJson(bundle, worldMessage.class));
			w.flush();

			// ready to receive
			BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			if (connection.getResponseCode() == 201)
				return true;
			else
				return false;

		} catch (MalformedURLException e) {
			usage();
		} catch (IOException e) {
			System.err.println("IO exception: " + e.getMessage());
		} catch (LoginError e) {
			System.err.println("LoginError:" + e.getMessage());
		}
		return false;
	}

	/**
	 * Gets information about the world such as the current timestep, current
	 * version, size, population, information about each hex, and more. The world's
	 * timestep is only affected by advancing whole turns, whereas the current
	 * version is affected by calls that add objects and critters into the world.
	 * Each state of the world a client could potentially see should gets a version
	 * number. Each atomic operation on the world - changing from state A to state
	 * B, with no intermidiate states that a client could detect - should increment
	 * the version number by 1. Note that changes to critter memory, while the may
	 * not affect the appearance of the world, are still state changes. The version
	 * number must increment for any state change.
	 * 
	 * @param update_since
	 *            update_since number
	 * @return
	 */
	public worldChangeReceivedBundle getStateOfWorld(int update_since) {
		Gson gson = new Gson();
		try {
			if (this.session_id == -1) // Check if it is valid call under an existed session_id.
			{
				throw new LoginError();
			}
			URL loginUrl = new URL(mainURLString + "/world?session_id=" + String.valueOf(this.session_id)
					+ "&update_since=" + String.valueOf(update_since));

			HttpURLConnection connection = (HttpURLConnection) loginUrl.openConnection();
			System.out.println("getStateOfWorld... " + loginUrl);
			connection.setRequestMethod("GET");
			// ready to receive
			BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			System.out.println(connection.getResponseCode());
			if (connection.getResponseCode() == 200) {
				worldChangeReceivedBundle rbundle = new worldChangeReceivedBundle();
				rbundle = gson.fromJson(r, worldChangeReceivedBundle.class);
				return rbundle;
			} else {
				throw new ProcessError();
			}
		} catch (MalformedURLException e) {
			usage();
		} catch (IOException e) {
			System.err.println("IO exception: " + e.getMessage());
		} catch (LoginError e) {
			System.err.println("LoginError:" + e.getMessage());
		} catch (ProcessError e) {
			System.err.println("ProcessError:" + e.getMessage());
		}

		return null; // error with null

	}
	
	/**
	 * Get the entire state of the world
	 * @return all the information that is included in the world
	 */
	public worldChangeReceivedBundle getStateOfWorld() {
		Gson gson = new Gson();
		try {
			if (this.session_id == -1) // Check if it is valid call under an existed session_id.
			{
				throw new LoginError();
			}
			URL loginUrl = new URL(mainURLString + "/world?session_id=" + String.valueOf(this.session_id)
					);

			HttpURLConnection connection = (HttpURLConnection) loginUrl.openConnection();
			System.out.println("getStateOfWorld... " + loginUrl);
			connection.setRequestMethod("GET");
			// ready to receive
			BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			if (connection.getResponseCode() == 200) {
				worldChangeReceivedBundle rbundle = new worldChangeReceivedBundle();
				rbundle = gson.fromJson(r, worldChangeReceivedBundle.class);
				return rbundle;
			} else {
				throw new ProcessError();
			}
		} catch (MalformedURLException e) {
			usage();
		} catch (IOException e) {
			System.err.println("IO exception: " + e.getMessage());
		} catch (LoginError e) {
			System.err.println("LoginError:" + e.getMessage());
		} catch (ProcessError e) {
			System.err.println("ProcessError:" + e.getMessage());
		}

		return null; // error with null

	}
	/**
	 * the sub version of the {@code getStateOfWorld} function, starting from a 4
	 * parameters area, and only return the diff in this area.
	 * 
	 * @param from_row
	 * @param to_row
	 * @param from_col
	 * @param to_col
	 * @param update_since
	 * @return
	 */
	public worldChangeReceivedBundle getSubWorld(int from_row, int to_row, int from_col, int to_col, int update_since) {
		Gson gson = new Gson();
		try {
			if (this.session_id == -1) // Check if it is valid call under an existed session_id.
			{
				throw new LoginError();
			}
			URL loginUrl = new URL(mainURLString + "/world?session_id=" + String.valueOf(this.session_id)
					+ "&update_since=" + String.valueOf(update_since) + "&from_row=" + String.valueOf(from_row)
					+ "&to_row=" + String.valueOf(to_row) + "&from_col=" + String.valueOf(from_col) + "&to_col="
					+ String.valueOf(to_col));

			HttpURLConnection connection = (HttpURLConnection) loginUrl.openConnection();
			System.out.println("getSubWorld... " + loginUrl);
			connection.setRequestMethod("GET");
			// ready to receive
			BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			if (connection.getResponseCode() == 200) {
				worldChangeReceivedBundle rbundle = new worldChangeReceivedBundle();
				rbundle = gson.fromJson(r, worldChangeReceivedBundle.class);
				return rbundle;
			} else {
				throw new ProcessError();
			}
		} catch (MalformedURLException e) {
			usage();
		} catch (IOException e) {
			System.err.println("IO exception: " + e.getMessage());
		} catch (LoginError e) {
			System.err.println("LoginError:" + e.getMessage());
		} catch (ProcessError e) {
			System.err.println("ProcessError:" + e.getMessage());
		}

		return null; // error with null
	}

	/**
	 * the default version with parameter update_since=1
	 * 
	 * @param from_row
	 * @param to_row
	 * @param from_col
	 * @param to_col
	 * @return
	 */
	public worldChangeReceivedBundle getSubWorld(int from_row, int to_row, int from_col, int to_col) {
		return getSubWorld(from_row, to_row, from_col, to_col, 0);
	}

	/**
	 * If the entity could not be created at the specified location, the server
	 * should respond with the status code 406, meaning "Not Acceptable". If the
	 * session id does not have write or admin access, the server should respond
	 * with the status code 401, meaning "Unauthorized".
	 * 
	 * @param row
	 *            the row number
	 * @param col
	 *            the col number
	 * @param type
	 *            the type string
	 * @param amount
	 *            food amount number
	 * @return true if succeed, else if fail
	 */
	public boolean CreateFoodOrRockObject(int row, int col, String type, int amount) {
		Gson gson = new Gson();
		PostCreateFoodOrRockObject bundle = new PostCreateFoodOrRockObject(row, col, type, amount);

		try {

			if (this.session_id == -1) // Check if it is valid call under an existed session_id.
			{
				throw new LoginError();
			}

			// connect to the URL
			URL loginUrl = new URL(mainURLString + "/create_entity?session_id=" + String.valueOf(this.session_id));

			HttpURLConnection connection = (HttpURLConnection) loginUrl.openConnection();
			connection.setRequestProperty("Content-Type", "application/json");
			System.out.println("Create a food or rock object... " + loginUrl);
			connection.setDoOutput(true); // send a POST message
			connection.setRequestMethod("POST");

			// transfer into Json and put it into a stream.
			PrintWriter w = new PrintWriter(connection.getOutputStream());
			w.println(gson.toJson(bundle, PostCreateFoodOrRockObject.class));
			w.flush();

			// ready to receive
			BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			if (connection.getResponseCode() == 201)
				return true;
			else {
				if (connection.getResponseCode() == 401)
					System.out.println("Unauthorized Code 401");
				else if (connection.getResponseCode() == 406)
					System.out.println("Not Acceptable Response Code 406");
				else
					System.out.println("Unknown Exception:" + connection.getResponseCode());
			}
		} catch (MalformedURLException e) {
			usage();
		} catch (IOException e) {
			System.err.println("IO exception: " + e.getMessage());
		} catch (LoginError e) {
			System.err.println("LoginError:" + e.getMessage());
		}
		return false;
	}

	/**
	 * When (and only when) the simulation rate is 0, a client may use the step
	 * command to advance the simulation world. The optional count parameter
	 * specifies a number of timesteps to advance by. If omitted, it defaults to 1.
	 * If a client tries to step the simulation while the simulation is running
	 * continuously (i.e., while the rate is not zero), the server should respond
	 * with the status code 406, meaning "Not Acceptable".If the session id does not
	 * have write or admin access, the server should respond with the status code
	 * 401, meaning "Unauthorized".
	 * 
	 * @param count
	 *            count step that want to advance
	 * @return true if succeed, false if fail
	 */
	public boolean advanceWorldBySteps(int count) {
		Gson gson = new Gson();
		PostAdvanceSteps bundle = new PostAdvanceSteps(count);

		try {

			if (this.session_id == -1) // Check if it is valid call under an existed session_id.
			{
				throw new LoginError();
			}

			// connect to the URL
			URL loginUrl = new URL(mainURLString + "/step?session_id=" + String.valueOf(this.session_id));

			HttpURLConnection connection = (HttpURLConnection) loginUrl.openConnection();
			connection.setRequestProperty("Content-Type", "application/json");
			System.out.println("advance World By Steps... " + loginUrl);
			connection.setDoOutput(true); // send a POST message
			connection.setRequestMethod("POST");

			// transfer into Json and put it into a stream.
			PrintWriter w = new PrintWriter(connection.getOutputStream());
			w.println(gson.toJson(bundle, PostAdvanceSteps.class));
			w.flush();

			// ready to receive
			BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			if (connection.getResponseCode() == 200)
				return true;
			else {
				if (connection.getResponseCode() == 401)
					System.out.println("Unauthorized Code 401");
				else if (connection.getResponseCode() == 406)
					System.out.println("Not Acceptable Response Code 406");
				else
					System.out.println("Unknown Exception:" + connection.getResponseCode());
				return false;
			}
		} catch (MalformedURLException e) {
			usage();
		} catch (IOException e) {
			System.err.println("IO exception: " + e.getMessage());
		} catch (LoginError e) {
			System.err.println("LoginError:" + e.getMessage());
		}
		return false;
	}

	/**
	 * default count = 1
	 * 
	 * @return
	 */
	public boolean advanceWorldBySteps() {
		return advanceWorldBySteps(1);
	}
	/**
	 * rate is the number of timesteps for the simulation to perform per second. 
	 * When the rate is specified as 0, the simulation stops. 
	 * If a negative rate is specified, the server should respond with the status code 406, meaning "Not Acceptable".
	 * If the session id does not have write or admin access, 
	 * the server should respond with the status code 401, meaning "Unauthorized".
	 * @param rate the rate that going to adjust
	 * @return if succeed return true, else false
	 */
	public boolean runTheWorld(double rate) {
		Gson gson = new Gson();
		PostRunRate bundle = new PostRunRate(rate);

		try {

			if (this.session_id == -1) // Check if it is valid call under an existed session_id.
			{
				throw new LoginError();
			}

			// connect to the URL
			URL loginUrl = new URL(mainURLString + "/run?session_id=" + String.valueOf(this.session_id));

			HttpURLConnection connection = (HttpURLConnection) loginUrl.openConnection();
			connection.setRequestProperty("Content-Type", "application/json");
			System.out.println("runTheWorld... " + loginUrl);
			connection.setDoOutput(true); // send a POST message
			connection.setRequestMethod("POST");

			// transfer into Json and put it into a stream.
			PrintWriter w = new PrintWriter(connection.getOutputStream());
			w.println(gson.toJson(bundle, PostRunRate.class));
			w.flush();

			// ready to receive
			BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			if (connection.getResponseCode() == 200)
				return true;
			else {
				if (connection.getResponseCode() == 401)
					System.out.println("Unauthorized Code 401");
				else if (connection.getResponseCode() == 406)
					System.out.println("Not Acceptable Response Code 406");
				else
					System.out.println("Unknown Exception:" + connection.getResponseCode());
				return false;
			}
		} catch (MalformedURLException e) {
			usage();
		} catch (IOException e) {
			System.err.println("IO exception: " + e.getMessage());
		} catch (LoginError e) {
			System.err.println("LoginError:" + e.getMessage());
		}
		return false;
	}

}

/**
 * This class contains twos string with level and password, they will be used to
 * construct JSON string and will be send to server.
 *
 */
class LoginJsonBundle {
	public String level;
	public String password;

	public LoginJsonBundle(String level, String password) {
		this.level = level;
		this.password = password;
	}
	public LoginJsonBundle() {

	}
}

/**
 * 
 * The class that will be fit into Json. There will be a deserialization so we
 * can read session_id from server
 */
class LoginReceiveBundle {
	public int session_id;

	public int getSession_id() {
		return session_id;
	}
}

class position {
	int row;
	int col;

	public position(int row, int col) {
		this.row = row;
		this.col = col;
	}
}

/**
 * bundle of create a critter with a key word "num".
 */
class sendRandomCritterBundle {
	public String species_id;
	public String program; // it is just a string by pretty print
	public int mem[];
	public int num;

	public sendRandomCritterBundle(String species_id, String program, int mem[], int num) {
		this.species_id = species_id;
		this.program = program;
		this.mem = mem;
		this.num = num;
	}
}

/**
 * bundle of create a critter with a key word "positions".
 */
class sendPositionCritterBundle {
	public String species_id;
	public String program;
	public int mem[];
	public position positions[];

	public sendPositionCritterBundle(String species_id, String program, int mem[], position positions[]) {
		this.species_id = species_id;
		this.program = program;
		this.mem = mem;
		this.positions = positions;
	}
}

/**
 * the ids that are returned by server
 */
class critterIdsBundle {
	public String species_id;
	public int ids[];
}

class retrievedCritter {
	public int id;
	public String species_id;
	public String program; // also a string
	public int row;
	public int col;
	public int direction;
	public int mem[];
	public int recently_executed_rule; // the index of lines of the program
	public retrievedCritter()
	{
		
	}
	public retrievedCritter(int id,String species_id,String program, int row, int col, int direction, int[] mem, int recently_executed_rule)
	{
		this.id=id;
		this.species_id=species_id;
		this.program=program;
		this.row=row;
		this.col=col;
		this.direction=direction;
		this.mem=mem;
		this.recently_executed_rule=recently_executed_rule;
	}
}

class worldMessage {
	public String description;
}

class worldChangeReceivedBundle {
	public int current_timestep;
	public int current_version_number;
	public int update_since;
	public double rate;
	public String name;
	public int population;
	public int rows;
	public int cols;
	public int[] dead_critters;
	public displayHex state[];
}


class PostCreateFoodOrRockObject {
	public int row;
	public int col;
	public String type;
	public int amount;

	public PostCreateFoodOrRockObject(int row, int col, String type, int amount) {
		this.row = row;
		this.col = col;
		this.type = type;
		this.amount = amount;
	}
}

class PostAdvanceSteps {
	public int count=0;

	public PostAdvanceSteps(int count) {
		this.count = count;
	}
}

class PostRunRate{
	public double rate=0;
	public PostRunRate(double rate) {
		this.rate=rate;

}

}
