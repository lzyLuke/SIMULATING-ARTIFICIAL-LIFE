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

public class GUIController implements Initializable {

	private PolyHex[][] polyMap;
	private int c;// the column of polyMap
	private int r;// the row of polyMap
	private World myWorld = null;
	Stage currentStage = null;
	Critter newCritter = null;
	Hex currentHex = null;
	Critter currentCritter = null;
	private int speed = 50;
	private Timer timer;
	private boolean trackingCritter = false;
	private boolean advancing = false;


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
		myWorld = new World();
		myWorld.newWorld();
		myWorld.randomRocks();
		drawHexMap(myWorld.columns, myWorld.rows);
		refreshHexMap();
		this.randomWorldOk.setVisible(true);
		this.randomWorldEr.setVisible(false);
	}

	@FXML
	private void handleIconClick(MouseEvent event) {
		if (event.getSource() == critter) {
			updateInfo();

			arrow1.setVisible(true);
			arrow2.setVisible(false);
			arrow3.setVisible(false);

			critterPane.setVisible(true);
			mapPane.setVisible(false);
			settingPane.setVisible(false);

		} else if (event.getSource() == map) {// when the map icon is clicked, the map pane will be shown and others
												// will be hidden.
			updateInfo();

			arrow1.setVisible(false);
			arrow2.setVisible(true);
			arrow3.setVisible(false);

			critterPane.setVisible(false);
			mapPane.setVisible(true);
			settingPane.setVisible(false);

		} else if (event.getSource() == setting) { // setting icon is clicked
			updateInfo();
			arrow1.setVisible(false);
			arrow2.setVisible(false);
			arrow3.setVisible(true);

			critterPane.setVisible(false);
			mapPane.setVisible(false);
			settingPane.setVisible(true);
		} else if (event.getSource() == start) { // start icon is clicked.
			if (myWorld.world == null) {
				System.out.println("Can't advance world since world is not initialized.");
				return;
			}
			if (myWorld.getNumCrittersAlive() == 0) {
				System.out.println("Can't advance world since there're no critters alive.");
				return;
			}
			this.start.setVisible(false);
			this.pause.setVisible(true);
			System.out.println("start pressed");
			System.out.println("simulation speed: " + speed);
			this.advancing = true;
			timer = new Timer();
			if (speed == 0)
				return;
			long delay = 1000 / speed;
			try {
				if (speed <= 30) {
					timer.schedule(new TimerTask() {
						@Override
						public void run() {
							myWorld.advanceATimeStep();
							refreshHexMap();
							updateInfo();
						}
					}, 0, delay);
				} else {
					timer.schedule(new TimerTask() {
						@Override
						public void run() {
							myWorld.advanceATimeStep();
						}
					}, 0, delay);
					timer.schedule(new TimerTask() {
						@Override
						public void run() {
							refreshHexMap();
							updateInfo();
						}
					}, 0, 34);
				}
				refreshHexMap();
				updateInfo();
			} catch (Exception e) {
				System.out.println("Exception in timer.");
				// e.printStackTrace();
			}

		} else if (event.getSource() == pause) { // pause icon is clicked.
			System.out.println("pause pressed");
			this.start.setVisible(true);
			this.pause.setVisible(false);
			if (timer != null)
				timer.cancel();
			this.advancing = false;
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
	private void drawHexMap(int column, int row) {
		polyMap = new PolyHex[column][row];
		Pane map = new Pane();
		c = column;
		r = row;
		for (int i = 0; i < column; i++)
			for (int j = 0; j < row; j++) {
				polyMap[i][j] = new PolyHex(i, j);
				polyMap[i][j].hex = myWorld.world[i][j];
				polyMap[i][j].setOnMouseClicked(e -> { // when the polyHex is clicked
					if (PolyHex.lastClicked != null) {
						PolyHex.lastClicked.setStroke(Color.web("#e4d6d6")); // resume to the origin color
					}
					PolyHex currentPoly = (PolyHex) (e.getSource()); // get the current clicked PolyHex
					PolyHex.lastClicked = currentPoly;
					currentPoly.setStroke(Color.web("#EA4646"));
					ColumnNumber.setText(String.valueOf(currentPoly.column));
					RowNumber.setText(String.valueOf(currentPoly.row));

					currentHex = myWorld.getHex(currentPoly.column, currentPoly.row);
					if (currentHex.isCritter()) {
						trackingCritter = true; // the tracking critter flag.
						currentCritter = currentHex.getCritter();
						System.out.println(currentCritter);
						this.updateInfo();
					} else if (currentHex.isFood() || currentHex.isEmpty() || currentHex.isRock()) {
						trackingCritter = false;
					}
					this.updateInfo();
				});

				if (!(i >= column || 2 * j - i < 0 || 2 * j - i >= 2 * row - column || i < 0)) {
					map.getChildren().add(polyMap[i][j]);
					for (int k = 0; k < 6; k++)
						map.getChildren().add(polyMap[i][j].face[k]); // add the facing
				}

			}
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
			myWorld = new World();
			myWorld.readFile(file);
			drawHexMap(myWorld.columns, myWorld.rows);
			refreshHexMap();
			currentHex = null;
			currentCritter = null;
			loadWorldOk.setVisible(true);
			loadWorldEr.setVisible(false);
			this.randomWorldEr.setVisible(true);
			this.randomWorldOk.setVisible(false);
			this.updateInfo();
		} catch (Exception exception) {
			System.out.println("Error when loading WorldFile");
			exception.printStackTrace();
		}
	}

	/**
	 * initialize the world with default map size and generate random rocks on the
	 * map.
	 * 
	 * @param e
	 */
	@FXML
	public void initializeRandomWorld(MouseEvent e) {
		myWorld = new World();
		myWorld.newWorld();
		myWorld.randomRocks();
		drawHexMap(myWorld.columns, myWorld.rows);
		refreshHexMap();
		currentHex = null;
		currentCritter = null;
		this.trackingCritter = false;
		this.updateInfo();
		this.randomWorldEr.setVisible(false);
		this.randomWorldOk.setVisible(true);
		this.loadWorldEr.setVisible(true);
		this.loadWorldOk.setVisible(false);
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
			newCritter = new Critter(0, 0, 0, myWorld);
			newCritter.setCritter(in);
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
				if (myWorld.isOutofBounds(column, row) && myWorld.world[column][row].isEmpty()) {
					success = false;
				} else { // place a new critter.
					Random rand = new Random();
					Critter newCritterToPut = new Critter(column, row, rand.nextInt(6), myWorld);
					newCritterToPut.mem = newCritter.mem.clone(); // set memmory
					newCritterToPut.genome = (Program) (newCritter.genome.copy()); // set genome
					newCritter.species = newCritter.species; // set species
					myWorld.addCritter(newCritterToPut, column, row);
					success = true;
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
		Hex hex;

		for (int i = 0; i < c; i++)
			for (int j = 0; j < r; j++) {
				if (!(myWorld.isOutofBounds(i, j))) {
					hex = myWorld.world[i][j];
					if (hex.isEmpty()) { // fill the hex with different color indicating the thing on the hex.
						polyMap[i][j].setFill(Color.TRANSPARENT);
						polyMap[i][j].blockFace();
					} else if (hex.isCritter()) {
						polyMap[i][j].setFill(Color.web("#FF8500"));
						polyMap[i][j].setFace(hex.getCritter().dir);
					} else if (hex.isFood()) {
						polyMap[i][j].setFill(Color.web("#A2C56C"));
						polyMap[i][j].blockFace();
					} else if (hex.isRock()) {
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
		Critter newCritterToPut = null;
		if (newCritter == null)
			return false;

		Random rand = new Random();
		int pick = 0;
		LinkedList<PolyHex> emptyList = new LinkedList<PolyHex>(); // all the polyHex that is empty will be put into
																	// this list
		LinkedList<PolyHex> putList = new LinkedList<PolyHex>(); // the putlist is selected from emptyList randomly and
																	// will be use to refresh the map.
		for (int i = 0; i < c; i++)
			for (int j = 0; j < r; j++) {
				if (!(myWorld.isOutofBounds(i, j)) && polyMap[i][j].hex.isEmpty()) {
					emptyList.add(polyMap[i][j]);
				}
			}
		if (emptyList.size() < num) // not enough space to put such a number of critter.
			return false;
		else {
			while (putList.size() <= num) {
				pick = rand.nextInt(emptyList.size());
				putList.add(emptyList.get(pick));
				emptyList.remove(pick);
			}
		}
		for (PolyHex hex : putList) {
			newCritterToPut = new Critter(hex.column, hex.row, rand.nextInt(6), myWorld);
			newCritterToPut.mem = newCritter.mem.clone(); // set memmory
			newCritterToPut.genome = (Program) (newCritter.genome.copy()); // set genome
			newCritter.species = newCritter.species;
			myWorld.addCritter(newCritterToPut, hex.column, hex.row); // put the critter.
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
		drawHexMap(myWorld.columns, myWorld.rows);
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
		drawHexMap(myWorld.columns, myWorld.rows);
		refreshHexMap();
	}

	/**
	 * update the simulation by one step and all the info on the graphics will be
	 * updated.
	 */
	@FXML
	public void advanceOnce() {
		if (myWorld.getNumCrittersAlive() == 0) {
			System.out.println("Can't advance world since there're no critters alive.");
			return;
		}
		myWorld.advanceATimeStep();
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
		try {
			showWorldColumnText.setText(String.valueOf(currentHex.getColumn()));
			showWorldRowText.setText(String.valueOf(currentHex.getRow()));
			if (currentHex.isCritter()) {
				showWorldPropertyText.setText("Critter");
				showWorldFoodText.setText("N/A");
			} else if (currentHex.isEmpty()) {
				showWorldPropertyText.setText("Empty");
				showWorldFoodText.setText("N/A");
			} else if (currentHex.isFood()) {
				showWorldPropertyText.setText("Food");
				showWorldFoodText.setText(String.valueOf(currentHex.getFood()));
			} else if (currentHex.isRock()) {
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
		if (trackingCritter) {
			if (this.currentCritter.c != this.currentHex.getColumn()
					|| this.currentCritter.r != this.currentHex.getRow()) {
				polyMap[currentHex.getColumn()][currentHex.getRow()].setStroke(Color.web("#e4d6d6"));
				polyMap[currentCritter.c][currentCritter.r].setStroke(Color.web("#EA4646"));
				currentHex = myWorld.getHex(currentCritter.c, currentCritter.r);
			}
		}
		// changes in critter pane
		if (currentCritter == null) {
			speciesText.setText("N/A");
			locationText.setText("N/A");
			memoryText.setText("N/A");
			genomeText.setText("N/A");
			ruleText.setText("N/A");
		}
		if (currentHex != null && currentHex.isCritter()) {
			currentCritter = currentHex.getCritter();
			speciesText.setText(currentCritter.species);
			locationText.setText("(" + currentCritter.c + "," + currentCritter.r + ")");
			memoryText.setText("MEMSIZE: " + currentCritter.getMem(0) + "\n" + "DEFENSE: " + currentCritter.getMem(1)
					+ "\n" + "OFFENSE: " + currentCritter.getMem(2) + "\n" + "SIZE:    " + currentCritter.getMem(3)
					+ "\n" + "ENERGY:  " + currentCritter.getMem(4) + "\n" + "PASS:    " + currentCritter.getMem(5)
					+ "\n" + "TAG:     " + currentCritter.getMem(6) + "\n" + "POSTURE: " + currentCritter.getMem(7));
			genomeText.setText(currentCritter.genome.toString().trim());
			if (currentCritter.lastExe != null)
				ruleText.setText(currentCritter.lastExe.toString().trim());
			else
				ruleText.setText("N/A");
		}

		// changes in setting
		this.addCritterOk.setVisible(false);
		this.addCritterEr.setVisible(true);
		this.loadCritterEr.setVisible(true);
		this.loadCritterOk.setVisible(false);

		// changes in world pane
		this.numCrittersAlive.setText(Integer.toString(myWorld.getNumCrittersAlive()));
		this.numTimeSteps.setText(Integer.toString(this.myWorld.getNumTimeSteps()));
	}

	/**
	 * Update all info.
	 */
	public synchronized void updateInfo() {
		this.updateCritterInfo();
		this.updateWorldInfo();
	}
}
