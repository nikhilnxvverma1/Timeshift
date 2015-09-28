package application;
	
import java.util.Iterator;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;


public class BasicRulerExample extends Application {
	
	public static final double WIDTH=800;
	public static final double HEIGHT=40;
	
	private AnchorPane ruler;
	
	private double offset=10;
	private double gap=40;
	private int numberOfMarking;
	private float currentZoom=0;
	
	@Override
	public void start(Stage primaryStage) {

		
		try {
			AnchorPane root = new AnchorPane();
			Scene scene = new Scene(root,WIDTH,HEIGHT);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
					
			ruler=root;
			
			addTextMarkings(ruler, 30);
			
			ruler.setOnScroll(new EventHandler<ScrollEvent>(){

				@Override
				public void handle(ScrollEvent event) {
					
					double deltaY = event.getDeltaY();
					float stepZoom = 0.1f;
					if(deltaY>0){
						System.out.println("zoom in "+deltaY);
						zoomByFactor(currentZoom+stepZoom);
						if(currentZoom<1){
						}
					}else if(deltaY<0){
						System.out.println("zoom out "+deltaY);
						zoomByFactor(currentZoom-stepZoom);
						if(currentZoom>-1){
						}
					}
				}
				
			});
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * zooms in by factor defined in floats
	 * @param factor between -1.0 to 1.0 .default is 0
	 */
	private void zoomByFactor(float factor){
		double defaultWidth=offset+gap*numberOfMarking;
		double currentWidth=defaultWidth+defaultWidth*factor;
		Iterator<Node> iterator=ruler.getChildren().iterator();
		while(iterator.hasNext()){
			Node marking=iterator.next();
			
			int markingNumber=Integer.parseInt(marking.getId().substring(0, marking.getId().length()-1));
			double newLayoutX=offset+(double)markingNumber/numberOfMarking*currentWidth;
			System.out.println("setting new x for maring number "+markingNumber+" x="+newLayoutX);
			marking.setLayoutX(newLayoutX);
		}
		currentZoom=factor;
	}
	
	
	public void addTextMarkings(AnchorPane ruler,int howMany){
		
		double sameY=5;
		
		for(int i=0;i<howMany;i++){
			double goingRight=offset+i*gap;		
			
			VBox marking=new VBox();
			marking.setLayoutY(sameY);
			marking.setLayoutX(goingRight);			
			marking.setAlignment(Pos.TOP_CENTER);
			marking.setId(i+"s");
			
			String text=String.format("%02d",i);
			Label mark=new Label();
			mark.setText(text);
			marking.getChildren().add(mark);
			
			double lineHeight=10;
			Line line=new Line(0,0,0,lineHeight);
			marking.getChildren().add(line);
			
			ruler.getChildren().add(marking);
		}
		numberOfMarking=howMany;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
