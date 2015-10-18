package application;

import java.net.URL;

import application.curveeditor.CurveEditor;
import application.ruler.Ruler;
import application.selection.SelectionArea;
import application.timepane.TimeValuePane;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ClientUI extends Application{

	private static final double WIDTH = 500;
	private static final double HEIGHT = 300;
	private static final String MAIN_VIEW_LOCATION = "table_tree_experiment.fxml";

	public static void main(String args[]){
		launch(args);
	}

	private Parent root;

	@Override
	public void start(Stage primaryStage) throws Exception {
	
		URL fileLocation =getClass().getResource(MAIN_VIEW_LOCATION);
		root=FXMLLoader.load(fileLocation);
		
		SelectionBar selectionBar=new SelectionBar(WIDTH);
		RowCorrespond rowCorrespond=new RowCorrespond();
		
		VBox vbox=new VBox();
		DraggableTextValue draggableTextValue=new DraggableTextValue();		
		vbox.getChildren().addAll(new TextField(),draggableTextValue,new TextField());
		
		CurveEditor curveEditor=new CurveEditor();
		
		
		AnchorPane timeControl=new AnchorPane();
		Ruler ruler=new Ruler(30,WIDTH);
		timeControl.getChildren().add(ruler);
		TimeValuePane timePane=buildTimePane(30,WIDTH);
		timePane.setLayoutY(50);
		timeControl.getChildren().add(timePane);
		
		ScrollPane scrollPane =new ScrollPane();
		scrollPane.setPrefSize(WIDTH, 300);
		scrollPane.setContent(timeControl);
		scrollPane.setOnScroll(new EventHandler<ScrollEvent>(){
			
			@Override
			public void handle(ScrollEvent event) {
				
				double xInScrollPane=event.getX();
				double ratio=xInScrollPane/scrollPane.getWidth();
				double relativeHValue=ratio*((scrollPane.getHmax()-scrollPane.getHmin())/scrollPane.getHmin());
				double deltaY = event.getDeltaY();
				float stepZoom = 0.1f;
				if(deltaY>0){
					ruler.zoomBy(stepZoom);
					timePane.zoomBy(stepZoom);
				}else if(deltaY<0){
					ruler.zoomBy(-stepZoom);
					timePane.zoomBy(-stepZoom);
				}
//				scrollPane.setHvalue((scrollPane.getHmin()+scrollPane.getHmax())/2);
//				scrollPane.setHvalue(relativeHValue);
			}
			
		});
		
		AnchorPane anchorPane=new AnchorPane();
		anchorPane.getChildren().add(scrollPane);
		selectionAreaPlay(rowCorrespond);
		Scene scene=new Scene(timeControl,WIDTH,HEIGHT);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
		
	private void selectionAreaPlay(RowCorrespond rowCorrespond){
		SelectionArea selectionArea=new SelectionArea();
		selectionArea.getSelectRect().setManaged(false);
		rowCorrespond.getChildren().add(selectionArea.getSelectRect());
		rowCorrespond.addEventHandler(MouseEvent.ANY, selectionArea);
		
		selectionArea.getSelectRect().localToScene(selectionArea.getSelectRect().getBoundsInLocal());
		
	}
	
	private TimeValuePane buildTimePane(int totalTime,double length){
		TimeValuePane timePane=new TimeValuePane(totalTime,length);
		timePane.addKeyAt(7, null);
		timePane.addKeyAt(10, null);
		timePane.addKeyAt(12, null);
		timePane.addKeyAt(23, null);
		timePane.addKeyAt(26, null);
		timePane.addKeyAt(19, null);
		return timePane;
	}
	
}
