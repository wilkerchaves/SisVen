package application;
	

import gui.MainViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;


public class Main extends Application {
	
	private static Scene mainScene;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainView.fxml"));
			ScrollPane scrollPane = loader.load();
			final MainViewController controller = loader.getController();
			controller.updateListOrders();
			
			
			scrollPane.setFitToHeight(true);
			scrollPane.setFitToWidth(true);
			
			mainScene = new Scene(scrollPane);
			
			mainScene.addEventHandler(KeyEvent.KEY_PRESSED,e->{
				if(e.getCode()==KeyCode.C) {
					controller.showModal();
				}
			});
			primaryStage.setScene(mainScene);
			primaryStage.setTitle("Sistema de Vendas - SiVen");
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Scene getMainScene() {
		return mainScene;
	}
	public static void main(String[] args) {
		launch(args);
	}
}
