package application;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
    		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GUI.fxml"));
    		Parent root = fxmlLoader.load();
    		((GUIController)(fxmlLoader.getController())).setStage(stage);
    		
        
        Scene scene = new Scene(root);
        stage.setTitle("Critter World");
        stage.setScene(scene);
        stage.setMinHeight(540);
        stage.setMinWidth(850);
		stage.setWidth(850);//850px
		stage.setHeight(540);//515px
		stage.setResizable(true);

		Image icon = new Image(this.getClass().getResourceAsStream("image/CritterWorld.png"));	//set the icon
		stage.getIcons().add(icon);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main (String[] args) {
        launch(args);
    }

}