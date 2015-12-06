package com.nikhil;

import com.nikhil.controller.ui.MainWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.nikhil.logging.Logger;

import java.io.File;

public class Main extends Application {

	public static final double WIDTH = 1100;
	public static final double HEIGHT = 750;

	public static final String TITLE="Untitled";
	private static final String MAIN_VIEW_LOCATION="views/level-editor.fxml";
	private static final String STYLESHEET="views/level-editor-style.css";

	private File fileToOpen;

	public Main() {
		this(null);
	}

	/**
	 * Creates a main object with a file to open
	 * @param fileToOpen will open this file upon creating the window.
	 */
	public Main(File fileToOpen) {
		this.fileToOpen = fileToOpen;
		//testing purposes
//		/Users/NikhilVerma/Desktop/myfile.xml
		this.fileToOpen=new File("/Users/NikhilVerma/Desktop/myfile2.xml");
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		FXMLLoader fxmlLoader=new FXMLLoader(getClass().getClassLoader().getResource(MAIN_VIEW_LOCATION));
		Parent root=fxmlLoader.load();
		root.getStylesheets().add(STYLESHEET);
		Scene scene=new Scene(root,WIDTH,HEIGHT);
		if(fileToOpen==null){
			primaryStage.setTitle(TITLE);
		}else{
			primaryStage.setTitle(fileToOpen.toString());
		}

		primaryStage.setScene(scene);
		primaryStage.show();//layout containers wont be initialized until primary stage is shown
		MainWindowController controller = (MainWindowController) fxmlLoader.getController();
		controller.init(fileToOpen);
		scene.setOnKeyPressed(controller::keyPressed);
		Logger.log("Primary stage is now live");

	}
}