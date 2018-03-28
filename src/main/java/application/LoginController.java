package application;
import com.jfoenix.controls.JFXTextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.fxml.Initializable;
import simulate.World;
import javafx.fxml.FXML;
import web.myClient;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.stage.*;
import javafx.util.StringConverter;
import javafx.scene.control.Label;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.scene.Scene;
public class LoginController implements Initializable{
	OnlineGUIController controller;
	Stage mainStage;
	Scene mainScene;
	myClient newClient;
	@FXML
	private JFXButton login;
	@FXML
	private JFXComboBox<Label> level;
	@FXML
	private JFXTextField password;
	@FXML
	private Label log;
	@FXML
	private JFXTextField url;

	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		Label check1 = new Label("admin");
		Label check2 = new Label("read");

		Label check3 = new Label("write");

	        level.getItems().add(check1);
	        level.getItems().add(check2);
	        level.getItems().add(check3);
	        level.setPromptText("Select A Level");
	        level.setConverter(new StringConverter<Label>() {
	            @Override
	            public String toString(Label object) {
	                return object==null? "" : object.getText();
	            }

	            @Override
	            public Label fromString(String string) {
	                return new Label(string);
	            }
	        });
	}
	
	public void setDisplayControllerAndStage(OnlineGUIController controller, Stage stage, Scene mainScene)
	{
		this.controller=controller;
		this.mainStage=stage;
		this.mainScene=mainScene;
		this.password.setText("gandalf");
		this.url.setText("http://localhost:8080");
	}
	
	/**
	 * trying to send the level and password to server.
	 * @param e
	 */
	@FXML
	public void connect(MouseEvent e)
	{
		newClient=new myClient(url.getText());
		int responseCode;
		if(level.getSelectionModel().getSelectedItem().getText() != null && password.getText()!=null)
			{
				responseCode=newClient.login(level.getSelectionModel().getSelectedItem().getText(), password.getText());
				if(responseCode==200)
				{
					controller.setClient(newClient);
					mainStage.setMinHeight(540);
					mainStage.setMinWidth(850);
					mainStage.setWidth(850);//850px
					mainStage.setHeight(540);//515px
					mainStage.setResizable(true);
					mainStage.setScene(mainScene);
					
				}
				else if(responseCode==401)
				{
					log.setText("Error PassWord CODE 401");
					
					
				}
				else 
				{
					log.setText("Other ERROR CODE"+responseCode);
				}
				
				
				
			}
	}


}
