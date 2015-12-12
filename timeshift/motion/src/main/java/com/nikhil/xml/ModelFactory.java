package com.nikhil.xml;

import com.nikhil.model.freeform.MovablePoint;
import com.nikhil.model.shape.CircleModel;
import com.nikhil.model.shape.PolygonModel;
import com.nikhil.model.shape.ShapeModel;
import com.nikhil.model.shape.TriangleModel;
import com.nikhil.space.bezier.path.BezierPoint;
import com.nikhil.timeline.KeyValue;
import com.nikhil.timeline.change.spatial.SpatialKeyframeChangeNode;
import com.nikhil.timeline.change.temporal.TemporalKeyframeChangeNode;
import com.nikhil.timeline.keyframe.SpatialKeyframe;
import com.nikhil.timeline.keyframe.TemporalKeyframe;
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

    public TriangleModel parseTriangleModel(Element triangleElement){
        TriangleModel triangleModel=new TriangleModel();
        triangleModel.setName(triangleElement.getAttribute(XMLAttribute.NAME.toString()));

        //traverse each child of the polygon tag
        int length=triangleElement.getChildNodes().getLength();
        for (int i = 0; i < length; i++) {

            //each direct child can either be Shape or Vertices tag
            Node node=triangleElement.getChildNodes().item(i);
            if(node instanceof Element){
                Element child=(Element)node;
                XMLTag tag=XMLTag.toTag(child.getTagName());
                if (tag == XMLTag.SHAPE) {
                    extractShapeProperties(child, triangleModel);
                } else if (tag == XMLTag.BASE) {
                    if (keyframesPresent(child)) {
                        addKeyframes(child, triangleModel.baseChange());
                    } else {
                        triangleModel.setBase(Double.parseDouble(child.getTextContent()));
                    }
                }else if (tag == XMLTag.HEIGHT) {
                    if (keyframesPresent(child)) {
                        addKeyframes(child, triangleModel.heightChange());
                    } else {
                        triangleModel.setHeight(Double.parseDouble(child.getTextContent()));
                    }
                }
            }
        }

        return triangleModel;
    }
    
    public CircleModel parseCircleModel(Element circleElement){

        CircleModel circleModel=new CircleModel();
        circleModel.setName(circleElement.getAttribute(XMLAttribute.NAME.toString()));

        //traverse each child of the polygon tag
        int length=circleElement.getChildNodes().getLength();
        for (int i = 0; i < length; i++) {

            //each direct child can either be Shape or Vertices tag
            Node node=circleElement.getChildNodes().item(i);
            if(node instanceof Element){
                Element child=(Element)node;
                XMLTag tag=XMLTag.toTag(child.getTagName());
                if (tag == XMLTag.SHAPE) {
                    extractShapeProperties(child, circleModel);
                } else if (tag == XMLTag.INNER_RADIUS) {
                    if (keyframesPresent(child)) {
                        addKeyframes(child, circleModel.innerRadiusChange());
                    } else {
                        circleModel.setInnerRadius(Double.parseDouble(child.getTextContent()));
                    }
                }else if (tag == XMLTag.OUTER_RADIUS) {
                    if (keyframesPresent(child)) {
                        addKeyframes(child, circleModel.outerRadiusChange());
                    } else {
                        circleModel.setOuterRadius(Double.parseDouble(child.getTextContent()));
                    }
                }else if (tag == XMLTag.STARTING_ANGLE) {
                    if (keyframesPresent(child)) {
                        addKeyframes(child, circleModel.startAngleChange());
                    } else {
                        circleModel.setStartAngle(Double.parseDouble(child.getTextContent()));
                    }
                }else if (tag == XMLTag.ENDING_ANGLE) {
                    if (keyframesPresent(child)) {
                        addKeyframes(child, circleModel.endAngleChange());
                    } else {
                        circleModel.setEndAngle(Double.parseDouble(child.getTextContent()));
                    }
                }
            }
        }

        return circleModel;
    }

    public PolygonModel parsePolygonModel(Element polygonElement){
        PolygonModel polygonModel=new PolygonModel();
        polygonModel.setName(polygonElement.getAttribute(XMLAttribute.NAME.toString()));

        //traverse each child of the polygon tag
        int length=polygonElement.getChildNodes().getLength();
        for (int i = 0; i < length; i++) {

            //each direct child can either be Shape or Vertices tag
            Node node=polygonElement.getChildNodes().item(i);
            if(node instanceof Element){
                Element element=(Element)node;
                XMLTag tag=XMLTag.toTag(element.getTagName());
                if (tag == XMLTag.SHAPE) {
                    extractShapeProperties(element, polygonModel);
                } else if (tag == XMLTag.VERTICES) {
                    addPolygonVertices(element, polygonModel);
                }
            }
        }
        return polygonModel;
    }

    protected void addPolygonVertices(Element verticesElement,PolygonModel polygonModel){

        //traverse each node in this element
        int totalNodes=verticesElement.getChildNodes().getLength();
        for (int i = 0; i < totalNodes; i++) {

            //each vertex is of the type MovablePoint
            Node node=verticesElement.getChildNodes().item(i);
            if(node instanceof Element){

                //extract the movable point
                Element element=(Element)node;
                XMLTag tag=XMLTag.toTag(element.getTagName());

                if (tag == XMLTag.MOVABLE_POINT) {//add movable point to the polygonModel
                    MovablePoint movablePoint = getMovablePoint(element);
                    polygonModel.addPoint(movablePoint);
                }

            }
        }
    }

    protected void extractShapeProperties(Element shapeElement,ShapeModel shapeModel){

        //initialize properties with default values
        shapeModel.setScale(ShapeModel.DEFAULT_SCALE);
        shapeModel.setRotation(ShapeModel.DEFAULT_ROTATION);
        shapeModel.setTranslation(new UtilPoint(ShapeModel.DEFAULT_TRANSLATION_X,ShapeModel.DEFAULT_TRANSLATION_Y));
        shapeModel.setAnchorPoint(new UtilPoint());
        
        //traverse each shape property
        int length=shapeElement.getChildNodes().getLength();
        for (int i = 0; i < length; i++) {

            Node node=shapeElement.getChildNodes().item(i);
            if(node instanceof Element){
                Element child=(Element)node;
                XMLTag tag=XMLTag.toTag(child.getTagName());

                //each element can either be :scale rotation translation or anchor point
                if (tag == XMLTag.SCALE) {
                    if (keyframesPresent(child)) {
                        addKeyframes(child, shapeModel.scaleChange());
                    } else {
                        shapeModel.setScale(Double.parseDouble(child.getTextContent()));
                    }
                } else if (tag == XMLTag.ROTATION) {
                    if (keyframesPresent(child)) {
                        addKeyframes(child, shapeModel.rotationChange());
                    } else {
                        shapeModel.setRotation(Double.parseDouble(child.getTextContent()));
                    }

                } else if (tag == XMLTag.TRANSLATION) {
                    if (keyframesPresent(child)) {
                        addKeyframes(child, shapeModel.translationChange());
                    } else {
                        shapeModel.setTranslation(getPoint(child));
                    }

                } else if (tag == XMLTag.ANCHOR_POINT) {
                    if (keyframesPresent(child)) {
                        addKeyframes(child, shapeModel.anchorPointChange());
                    } else {
                        shapeModel.setAnchorPoint(getPoint(child));
                    }

                }
            }
        }
    }

    protected boolean keyframesPresent(Element propertyElement){
        //traverse each child of this property
        int length=propertyElement.getChildNodes().getLength();
        for (int i = 0; i < length; i++) {

            Node node=propertyElement.getChildNodes().item(i);
            if(node instanceof Element){
                Element child=(Element)node;
                XMLTag tag=XMLTag.toTag(child.getTagName());

                //if this child element is a "keyframes" tag,return true
                if (tag == XMLTag.SPATIAL_KEYFRAMES||tag == XMLTag.TEMPORAL_KEYFRAMES) {
                    return true;
                }
            }
        }
        return false;
    }

    protected void addKeyframes(Element propertyElement,TemporalKeyframeChangeNode keyframeChangeNode){

        //traverse each node of the property element
        int totalNodes=propertyElement.getChildNodes().getLength();
        for (int i = 0; i < totalNodes; i++) {

            //each vertex is of the type MovablePoint
            Node node=propertyElement.getChildNodes().item(i);
            if(node instanceof Element){
                Element child=(Element)node;
                //extract the movable point
                XMLTag tag=XMLTag.toTag(child.getTagName());

                if (tag != null && (tag==XMLTag.TEMPORAL_KEYFRAMES)) {
                    extractKeyframes(child,keyframeChangeNode);
                }
            }
        }
    }

    protected void addKeyframes(Element propertyElement,SpatialKeyframeChangeNode keyframeChangeNode){

        //traverse each node of the property element
        int totalNodes=propertyElement.getChildNodes().getLength();
        for (int i = 0; i < totalNodes; i++) {

            //each vertex is of the type MovablePoint
            Node node=propertyElement.getChildNodes().item(i);
            if(node instanceof Element){
                Element child=(Element)node;
                //extract the movable point
                XMLTag tag=XMLTag.toTag(child.getTagName());

                if (tag != null && (tag==XMLTag.SPATIAL_KEYFRAMES)) {
                    extractKeyframes(child,keyframeChangeNode);
                }
            }
        }
    }

    protected void extractKeyframes(Element keyframeElement,TemporalKeyframeChangeNode temporalKeyframeChangeNode){
        TemporalKeyframe start=null;
        TemporalKeyframe last=null;
        
        //traverse each temporal keyframe 
        int length=keyframeElement.getChildNodes().getLength();
        for (int i = 0; i < length; i++) {

            Node node=keyframeElement.getChildNodes().item(i);
            if(node instanceof Element){
                Element child=(Element)node;
                XMLTag tag=XMLTag.toTag(child.getTagName());

                //each element has to be a temporal keyframe
                if (tag == XMLTag.TEMPORAL_KEYFRAME) {
                    double time = Double.parseDouble(child.getAttribute(XMLAttribute.TIME.toString()));
                    
                    TemporalKeyframe temporalKeyframe = new TemporalKeyframe(time, new KeyValue());
                    extractKeyframe(child, temporalKeyframe);
                    
                    //add this keyframe to list
                    if(start==null){
                        start=temporalKeyframe;
                        last=start;
                    }else{
                        temporalKeyframe.setPrevious(last);
                        last.setNext(temporalKeyframe);
                        last=temporalKeyframe;
                    }
                }
            }
        }
        temporalKeyframeChangeNode.setKeyframes(start,last);
    }

    protected void extractKeyframe(Element temporalKeyframeElement,TemporalKeyframe temporalKeyframe){
        //traverse each component of the key value
        int length=temporalKeyframeElement.getChildNodes().getLength();
        for (int i = 0; i < length; i++) {

            Node node=temporalKeyframeElement.getChildNodes().item(i);
            if(node instanceof Element){
                Element child=(Element)node;
                XMLTag tag=XMLTag.toTag(child.getTagName());

                //each element has to be a KeyValue(apparently)
                if (tag == XMLTag.KEY_VALUE) {
                    KeyValue keyValue=temporalKeyframe.getKeyValue();
                    keyValue.setDimension(Integer.parseInt(child.getAttribute(XMLAttribute.SIZE.toString())));
                    extractKeyValue(child, keyValue);
                }
            }
        }
    }

    protected void extractKeyValue(Element keyValueElement,KeyValue keyValue){
        //traverse each component of the key value
        int length=keyValueElement.getChildNodes().getLength();
        for (int i = 0; i < length; i++) {

            Node node=keyValueElement.getChildNodes().item(i);
            if(node instanceof Element){
                Element child=(Element)node;
                XMLTag tag=XMLTag.toTag(child.getTagName());

                //each element has to be a Component
                if (tag == XMLTag.COMPONENT) {
                    int index = Integer.parseInt(child.getAttribute(XMLAttribute.INDEX.toString()));
                    double value = Double.parseDouble(child.getAttribute(XMLAttribute.VALUE.toString()));
                    keyValue.set(index, value);
                }
            }
        }
    }

    protected void extractKeyframes(Element keyframeElement,SpatialKeyframeChangeNode spatialKeyframeChangeNode){
        SpatialKeyframe start=null;
        SpatialKeyframe last=null;

        //traverse each spatial keyframe 
        int length=keyframeElement.getChildNodes().getLength();
        for (int i = 0; i < length; i++) {

            Node node=keyframeElement.getChildNodes().item(i);
            if(node instanceof Element){
                Element child=(Element)node;
                XMLTag tag=XMLTag.toTag(child.getTagName());

                //each element has to be a spatial keyframe
                if (tag == XMLTag.SPATIAL_KEYFRAME) {
                    double time = Double.parseDouble(child.getAttribute(XMLAttribute.TIME.toString()));
                    SpatialKeyframe spatialKeyframe=new SpatialKeyframe(time,new UtilPoint());
                    //extract full spatial keyfram definition
                    extractKeyframe(child,spatialKeyframe);
                    //add this keyframe to list
                    if(start==null){
                        start=spatialKeyframe;
                        last=start;
                    }else{
                        spatialKeyframe.setPrevious(last);
                        last.setNext(spatialKeyframe);
                        last=spatialKeyframe;
                    }
                }
            }
        }
        spatialKeyframeChangeNode.setKeyframes(start,last);
    }

    protected void extractKeyframe(Element spatialKeyframeElement,SpatialKeyframe spatialKeyframe){
        //traverse each bezier point of the key value(note only one is expected to be there)
        int length=spatialKeyframeElement.getChildNodes().getLength();
        for (int i = 0; i < length; i++) {

            Node node=spatialKeyframeElement.getChildNodes().item(i);
            if(node instanceof Element){
                Element child=(Element)node;
                XMLTag tag=XMLTag.toTag(child.getTagName());

                //each element has to be a bezier point
                if (tag == XMLTag.BEZIER_POINT) {
                    extractBezierPoint(child,spatialKeyframe.getBezierPoint());
                }
            }
        }
    }
    
    protected void extractBezierPoint(Element bezierPointElement,BezierPoint bezierPoint){
        //traverse each component of the key value
        int length=bezierPointElement.getChildNodes().getLength();
        for (int i = 0; i < length; i++) {

            Node node=bezierPointElement.getChildNodes().item(i);
            if(node instanceof Element){
                Element child=(Element)node;
                XMLTag tag=XMLTag.toTag(child.getTagName());

                //each element has to be a bezier anchor point, previous control point or next control point
                if (tag == XMLTag.BEZIER_ANCHOR_POINT) {
                    double x = Double.parseDouble(child.getAttribute(XMLAttribute.X.toString()));
                    double y = Double.parseDouble(child.getAttribute(XMLAttribute.Y.toString()));
                    bezierPoint.getAnchorPoint().set(x,y);
                }else if (tag == XMLTag.PREVIOUS_CONTROL_POINT) {
                    double x = Double.parseDouble(child.getAttribute(XMLAttribute.X.toString()));
                    double y = Double.parseDouble(child.getAttribute(XMLAttribute.Y.toString()));
                    bezierPoint.getControlPointWithPrevious().set(x,y);
                }else if (tag == XMLTag.NEXT_CONTROL_POINT) {
                    double x = Double.parseDouble(child.getAttribute(XMLAttribute.X.toString()));
                    double y = Double.parseDouble(child.getAttribute(XMLAttribute.Y.toString()));
                    bezierPoint.getControlPointWithNext().set(x, y);
                }
            }
        }
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
