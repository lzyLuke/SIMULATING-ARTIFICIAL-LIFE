package application;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class OnlineClientMain extends Application {

    @Override
    public void start(Stage stage) throws Exception {
    		FXMLLoader LoginfxmlLoader = new FXMLLoader(getClass().getResource("Login.fxml"));
    		FXMLLoader GUIfxmlLoader = new FXMLLoader(getClass().getResource("OnlineGUI.fxml"));
    		Parent loginRoot = LoginfxmlLoader.load();
    		Parent GUIRoot = GUIfxmlLoader.load();
    		 Scene loginScene = new Scene(loginRoot);
    		 Scene GUIScene = new Scene(GUIRoot);
    		((LoginController)(LoginfxmlLoader.getController())).setDisplayControllerAndStage(
    				(OnlineGUIController)(GUIfxmlLoader.getController()), stage, GUIScene);
    		
        
        stage.setTitle("Critter World");
        stage.setScene(loginScene);
        stage.setMinHeight(346);
        stage.setMinWidth(427);
		stage.setWidth(346);//850px
		stage.setHeight(346);//515px
		stage.setResizable(false);

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
    public void run(String[] args)
    {
    	 launch(args);
    }
}