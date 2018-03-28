package web;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.delete;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;


import parse.Parser;
import parse.ParserFactory;
import simulate.Critter;
import simulate.Hex;
import simulate.World;
import simulate.diff;

/**
 * Represents: a server which responds to HTTP requests.
 */
public class myServer {

	int port;
	public int newSession_id = 0;
	String readpassword;
	String writepassword;
	String adminpassword;
	ConcurrentHashMap<Integer, String> sessionidMap = new ConcurrentHashMap<Integer, String>();

	public myServer(int port, String readpassword, String writepassword, String adminpassword) {
		this.port = port;
		this.readpassword = readpassword;
		this.writepassword = writepassword;
		this.adminpassword = adminpassword;
		serverWorld.newWorld();
		serverWorld.randomRocks();
		serverWorld.freezeWorldVersionAndSave();
		advanceWorldThread worldAdvanceThread = new advanceWorldThread(serverWorld);
		worldAdvanceThread.start();
	}

	public myServer() {
		port = 8080;
		readpassword = "bilbo";
		writepassword = "frodo";
		adminpassword = "gandalf";
		serverWorld.newWorld();
		serverWorld.randomRocks();
		serverWorld.freezeWorldVersionAndSave();
		advanceWorldThread worldAdvanceThread = new advanceWorldThread(serverWorld);
		worldAdvanceThread.start();
	}

	public synchronized int newSessionId() {
		return newSession_id++;
	}

	public static void main(String[] args) {
		myServer newServer = new myServer();
		newServer.run();
	}

	World serverWorld = new World();

	/**
	 * Effect: starts running a server
	 */
	public void run() {
		// Make the server run on port 8080
		port(8080);
		Gson gson = new Gson();

		// login ===================================
		post("/login", (request, response) -> {
			int session_id = -1;
			response.header("Content-Type", "application/json");
			LoginJsonBundle received = gson.fromJson(request.body(), LoginJsonBundle.class);
			if (received.level.equals("admin") && received.password.equals(adminpassword)) {
				session_id = newSessionId();
				sessionidMap.put(session_id, "admin");
				response.status(200);
			} else if (received.level.equals("read") && received.password.equals(readpassword)) {
				session_id = newSessionId();
				sessionidMap.put(session_id, "read");
				response.status(200);

			} else if (received.level.equals("write") && received.password.equals(writepassword)) {
				session_id = newSessionId();
				sessionidMap.put(session_id, "write");
				response.status(200);
			} else {
				response.status(401);
			}
			LoginReceiveBundle newSend = new LoginReceiveBundle();
			newSend.session_id = session_id;
			return newSend;

		}, gson::toJson);

		// List all Critters. ===================================
		get("/critters", (request, response) -> {
			Integer thisSession_id = Integer.parseInt(request.queryParams("session_id"));
			response.header("Content-Type", "application/json");

			Object[] allCritter = serverWorld.allCritter();
			Critter thisCritter;
			retrievedCritter[] returnBundle = new retrievedCritter[allCritter.length];

			if (sessionidMap.get(thisSession_id).equals("admin")) {
				// Can see all the genome of this critter
				for (int i = 0; i < allCritter.length; i++) {
					thisCritter = (Critter) allCritter[i];
					returnBundle[i] = new retrievedCritter(thisCritter.id, thisCritter.species,
							thisCritter.genome.toString(), thisCritter.r, thisCritter.c, thisCritter.dir,
							thisCritter.mem, thisCritter.getLastExcutedRuleIndex());

				}

			} else {
				// Can only see with the same session_id
				for (int i = 0; i < allCritter.length; i++) {
					thisCritter = (Critter) allCritter[i];
					if (thisCritter.session_id == thisSession_id) {
						returnBundle[i] = new retrievedCritter(thisCritter.id, thisCritter.species,
								thisCritter.genome.toString(), thisCritter.r, thisCritter.c, thisCritter.dir,
								thisCritter.mem, thisCritter.getLastExcutedRuleIndex());
					} else {
						returnBundle[i] = new retrievedCritter(thisCritter.id, thisCritter.species, null, thisCritter.r,
								thisCritter.c, thisCritter.dir, thisCritter.mem, -1);
					}

				}

			}
			response.status(200);
			return returnBundle;
		}, gson::toJson);

		// Create Critter ===================================
		post("/critters", (request, response) -> {
			Integer thisSession_id = Integer.parseInt(request.queryParams("session_id"));
			response.header("Content-Type", "application/json");
			int[] ids;
			if (sessionidMap.get(thisSession_id).equals("read")) {
				// reject this request
				response.status(401);
			} else {
				sendRandomCritterBundle newRandomCritter = gson.fromJson(request.body(), sendRandomCritterBundle.class);
				sendPositionCritterBundle newPositionCritter = gson.fromJson(request.body(),
						sendPositionCritterBundle.class);
				// positions are given
				if (newPositionCritter.positions != null) {
					ids = this.putPositionCritter(newPositionCritter, thisSession_id);
					serverWorld.freezeWorldVersionAndSave();
					response.status(201);
					return new returnCreateCritter(ids, newPositionCritter.species_id);
				}
				// random create
				else if (newRandomCritter.num != 0) {
					ids = this.putRandomCritter(newRandomCritter.num, newRandomCritter, thisSession_id);
					serverWorld.freezeWorldVersionAndSave();
					response.status(201);
					return new returnCreateCritter(ids, newRandomCritter.species_id);
				}

			}

			return "Fail";
		}, gson::toJson);

		// Make a new world on the server ===================================
		post("/world", (request, response) -> {
			Integer thisSession_id = Integer.parseInt(request.queryParams("session_id"));
			response.header("Content-Type", "application/json");
			if (!sessionidMap.get(thisSession_id).equals("admin")) {
				response.status(401);
				return "Unauthorized";
			}
			worldDescription received = gson.fromJson(request.body(), worldDescription.class);
			
			// Since we only have a method that take a "File" variable to make a new world,
			// thus we need to transform String -> file.
			File worldFile = new File("newWorld" + thisSession_id);
			if (worldFile.exists()) {
				worldFile.delete();
				worldFile.createNewFile();
			} else
				worldFile.createNewFile();
			try {
				FileWriter fw = new FileWriter(worldFile.getName());
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(received.description);
				bw.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
			serverWorld.readFile(worldFile);
			response.status(201);
			return "OK";
		}, gson::toJson);

		// get the state of the world ===================================
		get("/world", (request, response) -> {
			Integer thisSession_id = Integer.parseInt(request.queryParams("session_id"));
			response.header("Content-Type", "application/json");
			Integer update_since=0;
			if(request.queryParams("update_since")!=null)
				update_since=Integer.parseInt(request.queryParams("update_since"));
			else
			update_since=0;
			
			diff computedDiff = serverWorld.computeDiff(update_since,serverWorld.getWorldByVersion(update_since),
					serverWorld.lastFreezedWorld);
			
			if (sessionidMap.get(thisSession_id).equals("read") || sessionidMap.get(thisSession_id).equals("write")) {
				computedDiff.filter(thisSession_id);
			}
			
			if (request.queryParams("from_row") == null || request.queryParams("to_row") == null
					|| request.queryParams("from_col") == null || request.queryParams("to_col") == null) {
				response.status(200);
				return computedDiff;
			}

			int from_row = Integer.parseInt(request.queryParams("from_row"));
			int to_row = Integer.parseInt(request.queryParams("to_row"));
			int from_col = Integer.parseInt(request.queryParams("from_col"));
			int to_col = Integer.parseInt(request.queryParams("to_col"));

			Iterator<displayHex> iterator = computedDiff.state.iterator();
			while (iterator.hasNext()) {
				displayHex entry = iterator.next();
				if (entry.col > to_col || entry.col < from_col || entry.row > to_row || entry.row < from_row) {
					iterator.remove();
				}
			}
			response.status(200);
			return computedDiff;

		}, gson::toJson);

		// retrieve a critter with id ===================================
		get("/critter/*", (request, response) -> {
			Integer thisSession_id = Integer.parseInt(request.queryParams("session_id"));
			response.header("Content-Type", "application/json");
			int id = Integer.parseInt(request.splat()[0]);
			if (sessionidMap.get(thisSession_id).equals("admin")) {
				response.status(200);
				return serverWorld.retrieveCritterAdmin(id);
			} else {
				response.status(200);
				return serverWorld.retrieveCritterWithCheck(id, thisSession_id);
			}
		}, gson::toJson);

		// delete a critter with a specific id
		delete("/critter/*", (request, response) -> {

			Integer thisSession_id = Integer.parseInt(request.queryParams("session_id"));
			response.header("Content-Type", "application/json");
			int id = Integer.parseInt(request.splat()[0]);
			if (sessionidMap.get(thisSession_id) != null && sessionidMap.get(thisSession_id).equals("admin")) {
				if (this.serverWorld.deleteCritterAdmin(id)) {
					response.status(204);
					return "OK";
				} else {
					response.status(401);
					return "401Fail";
				}
			} else {
				if (this.serverWorld.deleteCritterWithCheck(id, thisSession_id)) {
					response.status(204);
					return "OK";
				} else {
					response.status(401);
					return "401Fail";
				}
			}

		});

		// create_entity in the world.
		post("/create_entity", (request, response) -> {
			Integer thisSession_id = Integer.parseInt(request.queryParams("session_id"));
			if (sessionidMap.get(thisSession_id).equals("read")) {
				response.status(401);
				return "Reject";
			}
			response.header("Content-Type", "application/json");
			displayHex received = gson.fromJson(request.body(), displayHex.class);
			if (received.type.equals("food"))
				serverWorld.world[received.col][received.row].setFood(received.value);
			else if (received.type.equals("rock"))
				serverWorld.world[received.col][received.row].setRock();

			serverWorld.freezeWorldVersionAndSave();
			response.status(201);
			return "OK";
		}, gson::toJson);

		// Advance the world by steps
		post("/step", (request, response) -> {
			Integer thisSession_id = Integer.parseInt(request.queryParams("session_id"));
			if (sessionidMap.get(thisSession_id).equals("read")) {
				response.status(401);
				return "Reject";
			}
			response.header("Content-Type", "application/json");

			PostAdvanceSteps received = gson.fromJson(request.body(), PostAdvanceSteps.class);

			if (received.count == 0)
				received.count = 1;

			if (serverWorld.rate != 0) {
				response.status(406);
				return "Reject";
			}

			for (int i = 0; i < received.count; i++) {
				serverWorld.advanceATimeStep();
			}
			response.status(201);
			return "OK";
		}, gson::toJson);

		// Run the world continuously/change the rate
		post("/run", (request, response) -> {
			Integer thisSession_id = Integer.parseInt(request.queryParams("session_id"));
			if (sessionidMap.get(thisSession_id).equals("read")) {
				response.status(401);
				return "Reject";
			}
			response.header("Content-Type", "application/json");

			PostRunRate received = gson.fromJson(request.body(), PostRunRate.class);
			if (received.rate < 0) {
				response.status(406);
				return "Not Acceptable";
			}

			serverWorld.rate = received.rate;
			serverWorld.freezeWorldVersionAndSave();
			response.status(200);
			return "Ok";

		}, gson::toJson);
	}
	/**
	 * To randomly put critter into our server world.
	 * @param num number of critter to put
	 * @param critter the received critter bundle
	 * @param session_id current session_id
	 * @return id of each critter, if the critter's id is not equal to -1, it has been successfully put, else not.
	 */
	private int[] putRandomCritter(int num, sendRandomCritterBundle critter, int session_id) {
		Critter newCritterToPut = null;
		int[] ids = new int[num];
		for (int i = 0; i < ids.length; i++) {
			ids[i] = -1;
		}

		if (critter == null)
			return null;

		Random rand = new Random();
		int pick = 0;
		LinkedList<Hex> emptyList = new LinkedList<Hex>(); // all the polyHex that is empty will be put into // this
															// list
		LinkedList<Hex> putList = new LinkedList<Hex>(); // the putlist is selected from emptyList randomly and
															// will be use to refresh the map.
		for (int i = 0; i < serverWorld.columns; i++)
			for (int j = 0; j < serverWorld.rows; j++) {
				if (!(serverWorld.isOutofBounds(i, j)) && serverWorld.world[i][j].isEmpty()) {
					emptyList.add(serverWorld.world[i][j]);
				}
			}

		while ((!emptyList.isEmpty()) && putList.size() < num) { // chose putlist from emptyList with no repeat
																	// coordinates
			pick = rand.nextInt(emptyList.size());
			putList.add(emptyList.get(pick));
			emptyList.remove(pick);
		}
		int count = 0;
		for (Hex hex : putList) {
			newCritterToPut = new Critter(hex.c, hex.r, serverWorld);
			newCritterToPut.mem = critter.mem.clone(); // set memmory
			try {
				InputStream in = new ByteArrayInputStream(critter.program.getBytes(StandardCharsets.UTF_8.name()));

				Parser parser = ParserFactory.getParser();
				BufferedReader genomeReader = new BufferedReader(new InputStreamReader(in));
				newCritterToPut.genome = parser.parse(genomeReader);
				newCritterToPut.species = critter.species_id;
				newCritterToPut.session_id = session_id;
				serverWorld.addCritter(newCritterToPut, hex.c, hex.r); // put the critter.
				ids[count] = newCritterToPut.id;
				count++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ids;

	}

	/**
	 * To  put critter into our server world with specific coordinates.
	 * @param critter the received critter bundle
	 * @param session_id current session_id
	 * @return id of each critter, if the critter's id is not equal to -1, it has been successfully put, else not.
	 */
	private int[] putPositionCritter(sendPositionCritterBundle critter, int session_id) {
		if (critter == null)
			return null;

		Critter newCritterToPut = null;
		int num = critter.positions.length;
		Random rand = new Random();
		int[] ids = new int[num];
		for (int i = 0; i < ids.length; i++) {
			ids[i] = -1;
		}

		LinkedList<Hex> putList = new LinkedList<Hex>(); // the putlist is selected from positions in our bundle.
		for (position p : critter.positions) {
			if (!(serverWorld.isOutofBounds(p.col, p.row)) && serverWorld.world[p.col][p.row].isEmpty())
				putList.add(serverWorld.world[p.col][p.row]);
		}

		int count = 0;
		for (Hex hex : putList) {
			newCritterToPut = new Critter(hex.c, hex.r, serverWorld);
			newCritterToPut.mem = critter.mem.clone(); // set memmory
			try {
				InputStream in = new ByteArrayInputStream(critter.program.getBytes(StandardCharsets.UTF_8.name()));

				Parser parser = ParserFactory.getParser();
				BufferedReader genomeReader = new BufferedReader(new InputStreamReader(in));
				newCritterToPut.genome = parser.parse(genomeReader);
				newCritterToPut.species = critter.species_id;
				newCritterToPut.session_id = session_id;
				serverWorld.addCritter(newCritterToPut, hex.c, hex.r); // put the critter.
				ids[count] = newCritterToPut.id;
				count++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ids;

	}

}

class returnCreateCritter {
	int[] ids;
	String species_id;

	public returnCreateCritter(int[] ids, String species_id) {
		this.ids = ids;
		this.species_id = species_id;
	}
}

class worldDescription {
	String description;
}
/**
 * This thread is used to call advanceOneStep repeatedly with a certain interval time according to current world rate
 *
 */
class advanceWorldThread extends Thread {
	World world;
	double rate;
	public advanceWorldThread(World world){
		this.world=world;
	}

	public void run() {
		while (true) {
			rate=world.rate;
			if(rate==0)
				{try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}continue;
				}
			else {
				long delay = new Double(1000.0 / rate).intValue();
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				world.advanceATimeStep();
			}
		}
	}
}

