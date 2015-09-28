package com.nikhil.xml;

import com.nikhil.model.freeform.MovablePoint;
import com.nikhil.model.shape.PolygonModel;
import com.nikhil.model.shape.ShapeModel;
import com.nikhil.util.modal.UtilPoint;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This model factory is used in supplying model classes to XML Reader class.
 * This class can be extended to override methods so custom models can be created
 * and supplied (or default models could be manipulated).Combined with custom controller
 * factory, both class can provide good extensibility to default supported data models
 * Created by NikhilVerma on 20/08/15.
 */
public class ModelFactory {

    public PolygonModel parsePolygonModel(Element polygonElement){
        PolygonModel polygonModel=new PolygonModel();

        //traverse each child of the polygon tag
        int length=polygonElement.getChildNodes().getLength();
        for (int i = 0; i < length; i++) {

            //each direct child can either be Shape or Vertices tag
            Node node=polygonElement.getChildNodes().item(i);
            if(node instanceof Element){
                Element element=(Element)node;
                XMLTag tag=XMLTag.toTag(element.getTagName());
                switch (tag){
                    case SHAPE:
                        extractShapeProperties(element,polygonModel);
                        break;
                    case VERTICES:
                        addPolygonVertices(element,polygonModel);
                        break;
                }
            }
        }
        return polygonModel;
    }

    protected int addPolygonVertices(Element verticesElement,PolygonModel polygonModel){

        //traverse each vertex element
        int totalVertices=verticesElement.getChildNodes().getLength();
        for (int i = 0; i < totalVertices; i++) {

            //each vertex is of the type MovablePoint
            Node node=verticesElement.getChildNodes().item(i);
            if(node instanceof Element){

                //extract the movable point
                Element element=(Element)node;
                XMLTag tag=XMLTag.toTag(element.getTagName());

                switch (tag){
                    case MOVABLE_POINT:
                        //add movable point to the polygonModel
                        MovablePoint movablePoint=getMovablePoint(element);
                        polygonModel.addPoint(movablePoint);
                        break;
                }

            }
        }
        return totalVertices;
    }

    protected void extractShapeProperties(Element shapeElement,ShapeModel shapeModel){

        //initialize properties with default values
        double scale=ShapeModel.DEFAULT_SCALE;
        double rotation=ShapeModel.DEFAULT_ROTATION;
        UtilPoint translation=new UtilPoint(ShapeModel.DEFAULT_TRANSLATION_X,ShapeModel.DEFAULT_TRANSLATION_Y);
        UtilPoint anchorPoint=new UtilPoint();

        //traverse each shape property
        int length=shapeElement.getChildNodes().getLength();
        for (int i = 0; i < length; i++) {

            Node node=shapeElement.getChildNodes().item(i);
            if(node instanceof Element){
                Element child=(Element)node;
                XMLTag tag=XMLTag.toTag(child.getTagName());

                //each element can either be :scale rotation translation or anchor point
                switch (tag){
                    case SCALE:
                        scale= Double.parseDouble(child.getTextContent());
                        break;
                    case ROTATION:
                        rotation= Double.parseDouble(child.getTextContent());
                        break;
                    case TRANSLATION:
                        translation=getPoint(child);
                        break;
                    case ANCHOR_POINT:
                        anchorPoint=getPoint(child);
                        break;
                }
            }
        }
        shapeModel.setScale((float) scale);
        shapeModel.setRotation((float) rotation);
        shapeModel.setTranslation(translation);
        shapeModel.setAnchorPoint(anchorPoint);
    }

    public MovablePoint getMovablePoint(Element movablePointElement){
        MovablePoint movablePoint=null;

        //we proceed only if this tag is movable point
        if(XMLTag.toTag(movablePointElement.getTagName())==XMLTag.MOVABLE_POINT){

            //create a movable point from the parsed doubles
            movablePoint=new MovablePoint();
            movablePoint.setPoint(getPoint(movablePointElement));
        }
        return movablePoint;
    }

    /**
     * gets the point if the element passed has x,and y
     * @param positionElement element that contains x,y
     * @return the respective UtilPoint which stores  x, and y
     */
    public UtilPoint getPoint(Element positionElement) throws NumberFormatException{

        //we make a strong assumption that the first two children are :x,y(in that order)
        NodeList allXElements = positionElement.getElementsByTagName(XMLTag.X.toString());
        Element xChild=(Element)allXElements.item(0);
        double x=Double.parseDouble(xChild.getTextContent());

        NodeList allYElements = positionElement.getElementsByTagName(XMLTag.Y.toString());
        Element yChild= (Element) allYElements.item(0);
        double y=Double.parseDouble(yChild.getTextContent());

        return new UtilPoint(x,y);
    }
}
