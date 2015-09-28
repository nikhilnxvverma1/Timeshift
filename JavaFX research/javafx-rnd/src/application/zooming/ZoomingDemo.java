package application.zooming;

import application.MathUtil;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Scene;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/**
 * Created by NikhilVerma on 31/08/15.
 */
public class ZoomingDemo extends Application {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    public static final int RADIUS = 300;
    private Scene scene;

    public static void main(String[] args) {
        launch(args);
    }

    private double pivotX=650;
    private double pivotY=500;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Pane pane=new Pane();
        pane.setStyle("-fx-background-color: rgb(255,255,255);");
        pane.setLayoutY(0);
        pane.setLayoutY(0);
        pane.setPrefWidth(WIDTH);
        pane.setPrefHeight(HEIGHT);
//        double layoutX = pane.getPrefWidth() / 2;
//        pane.setLayoutX(layoutX);
//        double layoutY = pane.getPrefHeight() / 2;
//        System.out.println("la x,y"+layoutX+","+layoutY);
//        pane.setLayoutY(layoutY);
        Circle circle=new Circle();
        circle.setCenterX(RADIUS);
        circle.setCenterY(RADIUS);
        circle.setRadius(45);
        circle.setFill(null);
        circle.setStroke(Color.BLUE);
//        circle.getStrokeDashArray().add(2d);
        pane.getChildren().add(circle);

        ZoomableScrollPane zoomableScrollPane=new ZoomableScrollPane(pane);
        scene = new Scene(new BorderPane(zoomableScrollPane, null, null, null, null));
        primaryStage.setScene(scene);
        primaryStage.setWidth(WIDTH);
        primaryStage.setHeight(HEIGHT);
        primaryStage.show();

        zoomableScrollPane.addEventFilter(ScrollEvent.SCROLL, new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                if(event.isAltDown()){
                    //On scrolling scale up and down the workspace pane(copied,ignore)
                    //and every child element will automatically take care of itself(copied,ignore)
                    double deltaY = event.getDeltaY();
                    //convert from delta to actual percentage(copied,ignore)
//                    double newZoomPercentage=currentZoom+(MathUtil.abs(deltaY)/MAX_SCROLL_DELTA)*100;(copied,ignore)
                    double x = zoomableScrollPane.scaleTransform.getX();
                    double y = zoomableScrollPane.scaleTransform.getY();
                    double sceneX = event.getSceneX();
                    double sceneY = event.getSceneY();
//                    Point2D local=zoomableScrollPane.sceneToLocal(sceneX,sceneY);
                    Point2D local=zoomableScrollPane.sceneToLocal(sceneX,sceneY);
                    Point2D contentLocal=pane.parentToLocal(local.getX(),local.getY());
                    System.out.print("local point ");
                    print(local.getX(),local.getY());
                    System.out.print("content point ");
                    print(contentLocal.getX(), contentLocal.getY());
//                    System.out.println("sceneX,sceneY = " + sceneX + "," + sceneY);
                    double beforeZoomWidth = zoomableScrollPane.getWidth() * zoomableScrollPane.scaleTransform.getX();
                    if(deltaY>=0){
                        zoomableScrollPane.scaleTransform.setX(x + 0.1);
                        zoomableScrollPane.scaleTransform.setY(y + 0.1);
                    }else{
                        zoomableScrollPane.scaleTransform.setX(x - 0.1);
                        zoomableScrollPane.scaleTransform.setY(y - 0.1);

                    }
                    Point2D contentLocal2=pane.parentToLocal(local.getX(),local.getY());

                    System.out.print("content point 2");
                    print(contentLocal2.getX(), contentLocal2.getY());
                    double ox=0;
//                    double ww=zoomableScrollPane.getWidth();
                    double ww=pane.getWidth();
                    double sw = zoomableScrollPane.getWidth() * zoomableScrollPane.scaleTransform.getX();
//                    double wx=local.getX();
                    double wx=contentLocal.getX();
                    double hx=0;
                    hx = (((ox * ww) + (wx * sw)) - (wx * ww)) / ((sw-ww)*ww);

                    double oy=0;
//                    double wh=zoomableScrollPane.getHeight();
                    double wh=pane.getHeight();
                    double sh = zoomableScrollPane.getWidth() * zoomableScrollPane.scaleTransform.getX();
//                    double wy=local.getY();
                    double wy=contentLocal.getY();
                    double hy=0;
                    hy = (((oy * wh) + (wy * sh)) - (wy * wh))/((sh-wh)*wh);
                    System.out.println("ww,wh"+ww+","+wh+"+hx,hy="+hx+","+hy);

                    hx=wx/ww;
                    hy=wy/wh;

                    //zoom relative to the point
                    zoomableScrollPane.setHvalue(hx);
                    zoomableScrollPane.setVvalue(hy);

                }
            }
        });
    }
    public void print(double x,double y){
        System.out.println("("+x+","+y+")");
    }
}
