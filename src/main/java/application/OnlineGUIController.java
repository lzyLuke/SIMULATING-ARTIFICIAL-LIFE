package application;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;

import ast.Program;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.*;
import simulate.*;
import web.displayHex;
import web.myClient;
import web.onlineWorldInfo;

public class OnlineGUIController implements Initializable {

	private PolyHex[][] polyMap;
	//private int c;// the column of polyMap
	//private int r;// the row of polyMap
	Stage currentStage = null;
	displayHex newCritter = null;
	displayHex currentHex = null;
	//Critter currentCritter = null;
	private int speed = 50;
	private Timer timer;
	private boolean trackingCritter = false;
	private boolean advancing = false;
	private myClient client;
	private onlineWorldInfo olWorld;
	@FXML
	private JFXButton loadWorld;

	@FXML
	private JFXButton loadCritter;

	@FXML
	private JFXButton addCritter;

	@FXML
	private JFXToggleButton randomPut;

	@FXML
	private JFXTextField numCrittersAlive;
	@FXML
	private JFXTextField numTimeSteps;
	
	@FXML
	private JFXTextField UPDATE_SINCE;
	@FXML
	private JFXTextField CURRENT_VERSION;

	@FXML
	private AnchorPane hover1;
	@FXML
	private AnchorPane hover2;
	@FXML
	private AnchorPane hover3;
	@FXML
	private ImageView start;
	@FXML
	private ImageView pause;
	@FXML
	private ImageView critter;
	@FXML
	private ImageView map;
	@FXML
	private ImageView setting;
	@FXML
	private ImageView arrow1;
	@FXML
	private ImageView arrow2;
	@FXML
	private ImageView arrow3;
	@FXML
	private AnchorPane critterPane;
	@FXML
	private JFXTextField speciesText;
	@FXML
	private JFXTextField locationText;
	@FXML
	private JFXTextArea memoryText;
	@FXML
	private JFXTextArea genomeText;
	@FXML
	private JFXTextArea ruleText;

	@FXML
	private AnchorPane mapPane;
	@FXML
	private AnchorPane settingPane;
	@FXML
	private TextField column;
	@FXML
	private ScrollPane scrollPane;

	@FXML
	private ImageView loadWorldOk;
	@FXML
	private ImageView loadWorldEr;

	@FXML
	private ImageView randomWorldOk;
	@FXML
	private ImageView randomWorldEr;

	@FXML
	private ImageView loadCritterOk;
	@FXML
	private ImageView loadCritterEr;

	@FXML
	private ImageView addCritterOk;
	@FXML
	private ImageView addCritterEr;

	@FXML
	private TextField ColumnNumber;
	@FXML
	private TextField RowNumber;
	@FXML
	private TextField NumNumber;

	@FXML
	private JFXToggleButton randomToggleButton;

	@FXML
	private JFXSlider slider;

	@FXML
	private JFXButton zoomIn;
	@FXML
	private JFXButton zoomOut;
	@FXML
	private Label showWorldColumnText;
	@FXML
	private Label showWorldRowText;
	@FXML
	private Label showWorldPropertyText;
	@FXML
	private Label showWorldFoodText;

	@FXML
	private void handleHoverInAction(MouseEvent event) { // a little animation effect on the icon.
		if (event.getTarget() == critter)
			hover1.setVisible(true);

		else if (event.getTarget() == map)
			hover2.setVisible(true);

		else if (event.getTarget() == setting)
			hover3.setVisible(true);
	}

	@FXML
	private void handleHoverOutAction(MouseEvent event) { // a liitle animation effect on the icon.

		if (event.getSource() == critter)
			hover1.setVisible(false);

		else if (event.getSource() == map)
			hover2.setVisible(false);

		else if (event.getSource() == setting)
			hover3.setVisible(false);
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		
		
		
		
	}

	@FXML
	private void handleIconClick(MouseEvent event) {
		if (event.getSource() == critter) {
		

			arrow1.setVisible(true);
			arrow2.setVisible(false);
			arrow3.setVisible(false);

			critterPane.setVisible(true);
			mapPane.setVisible(false);
			settingPane.setVisible(false);

		} else if (event.getSource() == map) {// when the map icon is clicked, the map pane will be shown and others
												// will be hidden.
	

			arrow1.setVisible(false);
			arrow2.setVisible(true);
			arrow3.setVisible(false);

			critterPane.setVisible(false);
			mapPane.setVisible(true);
			settingPane.setVisible(false);

		} else if (event.getSource() == setting) { // setting icon is clicked
	
			arrow1.setVisible(false);
			arrow2.setVisible(false);
			arrow3.setVisible(true);

			critterPane.setVisible(false);
			mapPane.setVisible(false);
			settingPane.setVisible(true);
		} else if (event.getSource() == start) { // start icon is clicked.
			client.runTheWorld(speed);
			refreshHexMap();
			updateInfo();
			}

		else if (event.getSource() == pause) { // pause icon is clicked.
			speed=0;
			client.runTheWorld(0);
			refreshHexMap();
			updateInfo();
		}

	}

	/**
	 * draw a map of polyhex on the scroll pane, alone with the mouse clicked action
	 * for the polyHex. If a polyHex is clicked, its stroke color will change and
	 * the information will be presented in the Critter pane or the world
	 * information pane
	 * 
	 * @param column
	 *            the column number to draw
	 * @param row
	 *            the row number to draw
	 */
	public void drawHexMap(int column, int row) {
		polyMap = new PolyHex[column][row];
		Pane map = new Pane();
		for (int i = 0; i < column; i++)
			for (int j = 0; j < row; j++) {
				polyMap[i][j] = new PolyHex(i, j);
				polyMap[i][j].displayhex = olWorld.worldmap[i][j];
				polyMap[i][j].setOnMouseClicked(e -> { // when the polyHex is clicked
					if (PolyHex.lastClicked != null) {
						PolyHex.lastClicked.setStroke(Color.web("#e4d6d6")); // resume to the origin color
					}
					PolyHex currentPoly = (PolyHex) (e.getSource()); // get the current clicked PolyHex
					PolyHex.lastClicked = currentPoly;
					currentPoly.setStroke(Color.web("#EA4646"));
					ColumnNumber.setText(String.valueOf(currentPoly.column));
					RowNumber.setText(String.valueOf(currentPoly.row));
					currentHex=olWorld.worldmap[currentPoly.column][currentPoly.row];
					updateInfo();
				});

				if (!(i >= column || 2 * j - i < 0 || 2 * j - i >= 2 * row - column || i < 0)) {
					map.getChildren().add(polyMap[i][j]);
					for (int k = 0; k < 6; k++)
						map.getChildren().add(polyMap[i][j].face[k]); // add the facing
				}

			}
		if(currentHex!=null)
			updateInfo();
		scrollPane.setContent(map);
	}

	// get the reference of the origin stage.
	public void setStage(Stage stage) {
		currentStage = stage;
	}

	/**
	 * load a world from the given file and all the critter in the world will also
	 * be loaded.
	 * 
	 * @param e
	 */
	@FXML
	private void loadNewWorld(MouseEvent e) {
		FileChooser chooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TEXT files (*.txt)", "*.txt");
		chooser.getExtensionFilters().add(extFilter);
		File file = chooser.showOpenDialog(currentStage);

		if (file == null) {
			loadWorldOk.setVisible(false);
			loadWorldEr.setVisible(true);
		}
		try {
			if( client.makeNewWorld(file) )
			{
			refreshHexMap();
			loadWorldOk.setVisible(true);
			loadWorldEr.setVisible(false);
			this.randomWorldEr.setVisible(true);
			this.randomWorldOk.setVisible(false);
			this.updateInfo();
			}
			else
			{
				loadWorldOk.setVisible(false);
				loadWorldEr.setVisible(true);
			}
			
		} catch (Exception exception) {
			System.out.println("Error when loading WorldFile");
			exception.printStackTrace();
		}
	}




	/**
	 * load a new Critter from the given file.
	 * 
	 * @param e
	 */
	@FXML
	private void loadNewCritter(MouseEvent e) {
		FileChooser chooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TEXT files (*.txt)", "*.txt");
		chooser.getExtensionFilters().add(extFilter);
		File file = chooser.showOpenDialog(currentStage);
		if (file == null) {
			loadCritterOk.setVisible(false);
			loadCritterEr.setVisible(true);
		}
		try {
			FileInputStream in = new FileInputStream(file);
		
			newCritter=Critter.displayHex(in);
			loadCritterOk.setVisible(true);
			loadCritterEr.setVisible(false);
		} catch (Exception exception) {
			System.out.println("Error when loading CritterFile");
			exception.printStackTrace();
		}

	}

	/**
	 * add a New Critter into the world. If the toggleButton RANDOMPUT is not on,
	 * then it will place the loaded Critter according to the column number and the
	 * row number in the text field. If the toggleButton is on, then it will
	 * randomly place {@code NUM} critters into the world.
	 * 
	 * @param e
	 *            click event
	 */
	@FXML
	private void addNewCritter(MouseEvent e) {
		boolean success = false;
		int column = 0;
		int row = 0;
		int num = 0;
		if (randomToggleButton.isSelected() && NumNumber.getText() != null) {
			try {
				num = Integer.parseInt(NumNumber.getText());
				if (num < 0)
					success = false;

			} catch (Exception excpetion) {
				success = false;
			}

			if (putRandomCritter(num))
				success = true;
			else
				success = false;

		} else if (ColumnNumber.getText() != null && RowNumber.getText() != null)
			;
		{
			try {
				column = Integer.parseInt(ColumnNumber.getText());
				row = Integer.parseInt(RowNumber.getText());
				int ids[] = client.positionCreateCritter(newCritter, row, column);
				success=true;
				for(int i:ids)
				{
					if(i==-1) {success=false;break;}
				}
			} catch (Exception excpetion) {
				success = false;
			}
		}

		if (success == false) { // use the small icon to represent successful or not.
			addCritterOk.setVisible(false);
			addCritterEr.setVisible(true);
		} else {
			addCritterOk.setVisible(true);
			addCritterEr.setVisible(false);
			refreshHexMap();
			return;
		}
	}

	/**
	 * Refresh the polygon Hex graphic of GUI according to the simulation world's
	 * map.
	 */
	public void refreshHexMap() {
		displayHex dispHex;
		olWorld.updateWorldState(client.getStateOfWorld(olWorld.current_version_number),this);
		numTimeSteps.setText(String.valueOf(olWorld.current_timestep));
		numCrittersAlive.setText(String.valueOf(olWorld.population));
		UPDATE_SINCE.setText(String.valueOf(olWorld.old_update_since));
		CURRENT_VERSION.setText(String.valueOf(olWorld.current_version_number));
		if(currentHex!=null)
		currentHex=olWorld.worldmap[currentHex.col][currentHex.row];
		for (int i = 0; i < olWorld.cols; i++)
			for (int j = 0; j < olWorld.rows; j++) {
				if ( olWorld.inBounds(i, j) ) {
					dispHex = olWorld.worldmap[i][j];
					if (dispHex.type.equals("nothing")) { // fill the hex with different color indicating the thing on the hex.
						polyMap[i][j].setFill(Color.TRANSPARENT);
						polyMap[i][j].blockFace();
					} else if (dispHex.type.equals("critter")) {
						polyMap[i][j].setFill(Color.web("#FF8500"));
						polyMap[i][j].setFace(dispHex.direction);
					} else if (dispHex.type.equals("food")) {
						polyMap[i][j].setFill(Color.web("#A2C56C"));
						polyMap[i][j].blockFace();
					} else if (dispHex.type.equals("rock")) {
						polyMap[i][j].setFill(Color.web("#AA6A39"));
						polyMap[i][j].blockFace();
					}
				}
			}
	}

	/**
	 * randomly locate the loaded Critter to the world with {@code num} numbers.
	 * 
	 * @param num
	 * @return If successfully locate such many critter, return true; else return
	 *         false;
	 */
	private boolean putRandomCritter(int num) {
		
		if (newCritter == null)
			return false;
		int[] ids = client.randomlyCreateCritter(newCritter, num);
		for(int i:ids)
		{
			if(i==-1) return false;
		}
		return true;
	}

	/**
	 * alter the radius of the polygon and redraw them on the graphics. There is a
	 * maximum radius of 100 of the polygon
	 */
	@FXML
	public void zoomIn() {
		if (PolyHex.radius < 100)
			PolyHex.radius += 5;
		drawHexMap(olWorld.cols, olWorld.rows);
		refreshHexMap();
	}

	/**
	 * alter the radius of the polygon and redraw them on the graphics. There is a
	 * minimum radius of 10 of the polygon
	 */
	@FXML
	public void zoomOut() {
		if (PolyHex.radius > 10)
			PolyHex.radius -= 5;
		drawHexMap(olWorld.cols, olWorld.rows);
		refreshHexMap();
	}

	/**
	 * update the simulation by one step and all the info on the graphics will be
	 * updated.
	 */
	@FXML
	public void advanceOnce() {
		if (olWorld.population == 0) {
			System.out.println("Can't advance world since there're no critters alive.");
			return;
		}
		client.advanceWorldBySteps();
		refreshHexMap();
		this.updateInfo();
		System.out.println("advanced a time step");
	}

	/**
	 * alter the speed using the slideBar, the speed ranges from 0~100
	 * 
	 * @param event
	 */
	@FXML
	public void handleDragAction(MouseEvent event) {
		speed = (int) Math.round(slider.getValue());
		if (advancing)
			System.out.println("Can't change speed while advancing.");
	}

	/**
	 * update the world hex (Rock, Food, Empty) info to user
	 */
	private void updateWorldInfo() {
		if(currentHex==null)
			return;
		try {
			showWorldColumnText.setText(String.valueOf(currentHex.col));
			showWorldRowText.setText(String.valueOf(currentHex.row));
			if (currentHex.type.equals("critter")) {
				showWorldPropertyText.setText("Critter");
				showWorldFoodText.setText("N/A");
			} else if (currentHex.type.equals("nothing")) {
				showWorldPropertyText.setText("Empty");
				showWorldFoodText.setText("N/A");
			} else if (currentHex.type.equals("food")) {
				showWorldPropertyText.setText("Food");
				showWorldFoodText.setText(String.valueOf(currentHex.value));
			} else if (currentHex.type.equals("rock")) {
				showWorldPropertyText.setText("Rock");
				showWorldFoodText.setText("N/A");
			}
		} catch (NullPointerException e) {
			// when world is not initialized
		}
	}

	/**
	 * Updates the critter info displayed to user.
	 */
	private void updateCritterInfo() {
		
		
		
		// changes in critter pane
		if(currentHex==null)
			return;
		
		if (!(currentHex.type.equals("critter") ) ){
			speciesText.setText("N/A");
			locationText.setText("N/A");
			memoryText.setText("N/A");
			genomeText.setText("N/A");
			ruleText.setText("N/A");
		}
		else {
			speciesText.setText(currentHex.species_id);
			locationText.setText("(" + currentHex.col + "," + currentHex.row + ")");
			memoryText.setText("MEMSIZE: " + currentHex.mem[0] + "\n" + "DEFENSE: " + currentHex.mem[1]
					+ "\n" + "OFFENSE: " + currentHex.mem[2] + "\n" + "SIZE:    " + currentHex.mem[3]
					+ "\n" + "ENERGY:  " + currentHex.mem[4] + "\n" + "PASS:    " + currentHex.mem[5]
					+ "\n" + "TAG:     " + currentHex.mem[6] + "\n" + "POSTURE: " + currentHex.mem[7]);
			genomeText.setText(currentHex.program);
			if (currentHex.recently_executed_rule != -1)
				ruleText.setText(String.valueOf(currentHex.recently_executed_rule));
			else
				ruleText.setText("N/A");
		}


	}

	/**
	 * Update all info.
	 */
	public synchronized void updateInfo() {
		this.updateCritterInfo();
		this.updateWorldInfo();
	}
	/**
	 * set the client that will be used to update
	 * @param client the given client
	 */
	public void setClient(myClient client)
	{
		this.client=client;
		olWorld = new onlineWorldInfo();
		olWorld.updateWorldState(client.getStateOfWorld(),this);
		advanceOnlineWorldThread speedThread = new advanceOnlineWorldThread(olWorld,this);
		speedThread.start();
		drawHexMap(olWorld.cols,olWorld.rows);
		refreshHexMap();
	}



}
class advanceOnlineWorldThread extends Thread {
	onlineWorldInfo world;
	double rate;
	OnlineGUIController controller;
	public advanceOnlineWorldThread(onlineWorldInfo world,OnlineGUIController controller){
		this.world=world;
		this.controller=controller;
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
				controller.refreshHexMap();
				controller.updateInfo();
			}
		}
	}
}