package application.zooming;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.transform.Scale;

public class ZoomableScrollPane extends ScrollPane {
    Group zoomGroup;
    Scale scaleTransform;
    Node content;
    public ZoomableScrollPane(Node content)
    {
        this.content = content;
        Group contentGroup = new Group();
        zoomGroup = new Group();
        contentGroup.getChildren().add(zoomGroup);
        zoomGroup.getChildren().add(content);
        setContent(contentGroup);
        scaleTransform = new Scale(1 ,1, 0, 0);
        zoomGroup.getTransforms().add(scaleTransform);
        this.setStyle("-fx-background: rgb(100,100,100);");
    }


}
