package com.nikhil.view.util;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.*;

public class ConfirmBox {

	// Create variable
	static boolean answer;

	public static boolean display(String title, String message) {
		final Stage window = new Stage();// final so that anonymous innner
											// classes can use this
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle(title);
		window.setMinWidth(250);
		Label label = new Label();
		label.setText(message);

		// Create two buttons
		Button yesButton = new Button("Yes");
		Button noButton = new Button("No");

		// Clicking will set answer and close window
		yesButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				answer = true;
				window.close();
			}
		});
		noButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				answer = false;
				window.close();
			}
		});

		VBox layout = new VBox(10);

		// Add buttons
		layout.getChildren().addAll(label, yesButton, noButton);
		layout.setAlignment(Pos.CENTER);
		Scene scene = new Scene(layout);
		window.setScene(scene);
		window.showAndWait();

		// Make sure to return answer
		return answer;
	}

}