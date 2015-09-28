package application;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class RowCorrespond extends VBox{

	private static final String CUSTOM_CONTROL_FXML = "row_correspond.fxml";
	
	@FXML AnchorPane greenRow;
	
	public RowCorrespond(){
		
		//load the control via fxml
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(CUSTOM_CONTROL_FXML));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);               
		
		try {
			
			fxmlLoader.load();         
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	
		makeAllChildrenUnmanagedOnHidden();
	}
	
	private void makeAllChildrenUnmanagedOnHidden(){
		for(Node node: getChildren()){
			node.managedProperty().bind(node.visibleProperty());
		}
	}
	
	@FXML
	public void rowClicked(MouseEvent clickEvent){
		System.out.println("Collapsed");
		((Node)clickEvent.getTarget()).setVisible(false);
	}
	
	@FXML
	public void rowClickedPink(MouseEvent clickEvent){
		System.out.println("Expanded");
		greenRow.setVisible(true);
	}
	
}
