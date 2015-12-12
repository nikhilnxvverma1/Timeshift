package com.nikhil.xml;

import com.nikhil.controller.CompositionController;
import com.nikhil.controller.RootController;
import com.nikhil.logging.Logger;
import com.nikhil.model.ModelVisitor;
import com.nikhil.model.freeform.MovablePoint;
import com.nikhil.model.pathsystem.MovableLinkPoint;
import com.nikhil.model.pathsystem.TravelPath;
import com.nikhil.model.pathsystem.TravellingLinkPoint;
import com.nikhil.model.pathsystem.traveller.TravellerConfiguration;
import com.nikhil.model.shape.*;
import com.nikhil.space.bezier.path.BezierPoint;
import com.nikhil.timeline.KeyValue;
import com.nikhil.timeline.change.spatial.SpatialKeyframeChangeNode;
import com.nikhil.timeline.change.temporal.TemporalKeyframeChangeNode;
import com.nikhil.timeline.keyframe.SpatialKeyframe;
import com.nikhil.timeline.keyframe.TemporalKeyframe;
import com.nikhil.util.modal.UtilPoint;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

/**
 * Builds XML representation of the system(using a DOM based writer).
 * TODO keyframes are not saved
 * Created by NikhilVerma on 20/08/15.
 */
public class XMLWriter implements ModelVisitor {

    protected Document document;
    protected Element root;
    protected Element compositions;

    //=============================================================================================
    //Details that are required across callbacks.
    //=============================================================================================

    /** holds the current composition under which models are being added*/
    protected Element composition;
    protected Element itemContainer;
    protected Element movablePointContainer;

    public XMLWriter() throws Exception{
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder=documentBuilderFactory.newDocumentBuilder();
        document = documentBuilder.newDocument();
    }

    public void writeToFile(File file,RootController rootController) throws TransformerException {

        buildDomTree(rootController);

        //use a transformer to convert the element tree to xml
        TransformerFactory transformerFactory=TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        //write to file specified
        DOMSource domSource=new DOMSource(document);
        StreamResult result=new StreamResult(file);
//        StreamResult result=new StreamResult(System.out);
        transformer.transform(domSource,result);
    }

    /**
     * Builds the XML element DOM tree. override this to extend and add more custom elements to the root element.
     * On overriding,just call super first.
     * @param rootController the root controller
     */
    protected void buildDomTree(RootController rootController){
        rootController.acceptVisitor(this);
    }

    @Override
    public void visit(RootController rootController) {
        //<root>
        root = XMLTag.ROOT.element(document);
        document.appendChild(root);
        Logger.log("Added root");
    }

    @Override
    public void visit(CompositionController compositionController) {

        //if a composition tag doesn't exist already, create one
        if(compositions==null){
            //<compositions>
            compositions=XMLTag.COMPOSITIONS.element(document);
            root.appendChild(compositions);
            Logger.log("Added compositions container");
        }

        //<composition>
        composition =XMLTag.COMPOSITION.element(document);
        compositions.appendChild(composition);

        //<items>
        itemContainer=XMLTag.ITEMS.element(document);
        composition.appendChild(itemContainer);
        Logger.log("Added composition");
    }

    @Override
    public void visit(TriangleModel triangleModel) {
        Element triangleTag=XMLTag.TRIANGLE.element(document);
        triangleTag.setAttribute(XMLAttribute.NAME.toString(), triangleModel.getName());
        triangleTag.appendChild(getShapeTag(triangleModel));

        //add the triangle specific triangle properties
        //<Base>
        Element baseTag=XMLTag.BASE.element(document);
        if(triangleModel.baseChange().isEmpty()){
            baseTag.appendChild(document.createTextNode(triangleModel.getBase() + ""));
        }else{
            baseTag.appendChild(getKeyframesTag(triangleModel.baseChange()));
        }

        //<Height>
        Element heightTag=XMLTag.HEIGHT.element(document);
        if (triangleModel.heightChange().isEmpty()) {
            heightTag.appendChild(document.createTextNode(triangleModel.getHeight() + ""));
        }else{
            heightTag.appendChild(getKeyframesTag(triangleModel.heightChange()));
        }

        triangleTag.appendChild(baseTag);
        triangleTag.appendChild(heightTag);
        

        //add to the last composition(this method doesn't know which composition it will  get added to)
        itemContainer.appendChild(triangleTag);
        Logger.log("Saving Triangle model "+triangleModel.getName());
    }

    @Override
    public void visit(Parallelogram parallelogram) {

    }

    @Override
    public void visit(TravelPath travelPath) {

    }

    @Override
    public void visit(MovableLinkPoint movableLinkPoint) {

    }

    @Override
    public void visit(TravellingLinkPoint travellingLinkPoint) {

    }

    @Override
    public void visit(CircleModel circleModel) {
        Element circleTag=XMLTag.CIRCLE.element(document);
        circleTag.setAttribute(XMLAttribute.NAME.toString(), circleModel.getName());
        circleTag.appendChild(getShapeTag(circleModel));
        
        //add the circle specific circle properties
        //<InnerRadius>
        Element innerRadiusTag=XMLTag.INNER_RADIUS.element(document);
        if(circleModel.innerRadiusChange().isEmpty()){
            innerRadiusTag.appendChild(document.createTextNode(circleModel.getInnerRadius() + ""));
        }else{
            innerRadiusTag.appendChild(getKeyframesTag(circleModel.innerRadiusChange()));
        }

        //<OuterRadius>
        Element outerRadiusTag=XMLTag.OUTER_RADIUS.element(document);
        if (circleModel.outerRadiusChange().isEmpty()) {
            outerRadiusTag.appendChild(document.createTextNode(circleModel.getOuterRadius() + ""));
        }else{
            outerRadiusTag.appendChild(getKeyframesTag(circleModel.outerRadiusChange()));
        }

        //<StartingAngle>
        Element startingAngleTag=XMLTag.STARTING_ANGLE.element(document);
        if (circleModel.startAngleChange().isEmpty()) {
            startingAngleTag.appendChild(document.createTextNode(circleModel.getStartAngle() + ""));
        }else{
            startingAngleTag.appendChild(getKeyframesTag(circleModel.startAngleChange()));

        }

        //<EndingAngle>
        Element endingAngleTag=XMLTag.ENDING_ANGLE.element(document);
        if (circleModel.endAngleChange().isEmpty()) {
            endingAngleTag.appendChild(document.createTextNode(circleModel.getEndAngle() + ""));
        }else{
            endingAngleTag.appendChild(getKeyframesTag(circleModel.endAngleChange()));
        }

        circleTag.appendChild(innerRadiusTag);
        circleTag.appendChild(outerRadiusTag);
        circleTag.appendChild(startingAngleTag);
        circleTag.appendChild(endingAngleTag);

        //add to the last composition(this method doesn't know which composition it will  get added to)
        itemContainer.appendChild(circleTag);
        Logger.log("Saving Circle model "+circleModel.getName());
    }

    @Override
    public void visit(PolygonModel polygonModel) {

        Element polygonTag=XMLTag.POLYGON.element(document);
        polygonTag.setAttribute(XMLAttribute.NAME.toString(),polygonModel.getName());
        polygonTag.appendChild(getShapeTag(polygonModel));

        movablePointContainer =XMLTag.VERTICES.element(document);
        polygonTag.appendChild(movablePointContainer);

        //add to the last composition(this method doesn't know which composition it will  get added to)
        itemContainer.appendChild(polygonTag);
        Logger.log("Saving Polygon model "+polygonModel.getName());
    }

    @Override
    public void visit(MovablePoint movablePoint) {
        //<MovablePoint>
        Element header=XMLTag.MOVABLE_POINT.element(document);

        //<Position>
        Element position=XMLTag.POSITION.element(document);

        header.appendChild(position);
        position.appendChild(getXTag(movablePoint.getPoint()));
        position.appendChild(getYTag(movablePoint.getPoint()));

        //add this to the whatever the container is(this method doesn't know where it will get added to)
        movablePointContainer.appendChild(header);

        Logger.log("Added movable point");
    }

    @Override
    public void visit(TravellerConfiguration travellerConfiguration) {

    }

    protected Element getShapeTag(ShapeModel shapeModel){
        //<shape>
        Element shapeTag=XMLTag.SHAPE.element(document);

        //<scale>
        Element scaleTag=XMLTag.SCALE.element(document);
        if (shapeModel.scaleChange().isEmpty()) {
            scaleTag.appendChild(document.createTextNode(shapeModel.getScale() + ""));
        } else {
            scaleTag.appendChild(getKeyframesTag(shapeModel.scaleChange()));
        }

        //<rotation>
        Element rotationTag=XMLTag.ROTATION.element(document);
        if(shapeModel.rotationChange().isEmpty()){
            rotationTag.appendChild(document.createTextNode(shapeModel.getRotation() + ""));
        }else{
            rotationTag.appendChild(getKeyframesTag(shapeModel.rotationChange()));
        }

        //<translation>
        Element translationTag=XMLTag.TRANSLATION.element(document);
        if(shapeModel.translationChange().isEmpty()){
            translationTag.appendChild(getXTag(shapeModel.getTranslation()));
            translationTag.appendChild(getYTag(shapeModel.getTranslation()));
        }else{
            translationTag.appendChild(getKeyframesTag(shapeModel.translationChange()));
        }

        //<anchor point>
        Element anchorPointTag=XMLTag.ANCHOR_POINT.element(document);
        if(shapeModel.anchorPointChange().isEmpty()){
            anchorPointTag.appendChild(getXTag(shapeModel.getAnchorPoint()));
            anchorPointTag.appendChild(getYTag(shapeModel.getAnchorPoint()));
        }else{
            anchorPointTag.appendChild(getKeyframesTag(shapeModel.anchorPointChange()));
        }

        //add all properties to shape element
        shapeTag.appendChild(scaleTag);
        shapeTag.appendChild(rotationTag);
        shapeTag.appendChild(translationTag);
        shapeTag.appendChild(anchorPointTag);

        return shapeTag;
    }

    protected Element getXTag(UtilPoint utilPoint){
        Element xTag=XMLTag.X.element(document);
        xTag.appendChild(document.createTextNode(utilPoint.getX() + ""));
        return xTag;
    }

    protected Element getYTag(UtilPoint utilPoint){
        Element yTag = XMLTag.Y.element(document);
        yTag.appendChild(document.createTextNode(utilPoint.getY() + ""));
        return yTag;
    }

    protected Element getKeyframesTag(TemporalKeyframeChangeNode temporalKeyframeChangeNode){
        //<TemporalKeyframes>
        Element temporalKeyframesTag=XMLTag.TEMPORAL_KEYFRAMES.element(document);

        //iterate through all the keyframes
        TemporalKeyframe t = temporalKeyframeChangeNode.getStart();
        while(t!=null){

            //<TemporalKeyframe>
            Element temporalKeyframeTag=XMLTag.TEMPORAL_KEYFRAME.element(document);
            temporalKeyframeTag.setAttribute(XMLAttribute.TIME.toString(), t.getTime() + "");
            temporalKeyframeTag.appendChild(getKeyValuesTag(t.getKeyValue()));

            //add each keyframe to keyframes tag
            temporalKeyframesTag.appendChild(temporalKeyframeTag);
            t=t.getNext();
        }
        return temporalKeyframesTag;
    }

    protected Element getKeyValuesTag(KeyValue keyValue){
        //<KeyValues>
        Element keyValueTag=XMLTag.KEY_VALUE.element(document);
        int dimension = keyValue.getDimension();
        keyValueTag.setAttribute(XMLAttribute.SIZE.toString(), dimension + "");

        //iterate over all the value of this KeyValue
        for (int i = 0; i < dimension; i++) {

            //<Value>
            Element componentTag=XMLTag.COMPONENT.element(document);
            componentTag.setAttribute(XMLAttribute.INDEX.toString(), i + "");
            componentTag.setAttribute(XMLAttribute.VALUE.toString(), keyValue.get(i) + "");

            //append a KeyValue as a child for this value
            keyValueTag.appendChild(componentTag);

        }
        return keyValueTag;
    }

    protected Element getKeyframesTag(SpatialKeyframeChangeNode spatialKeyframeChangeNode){
        //<SpatialKeyframes>
        Element spatialKeyframesTag=XMLTag.SPATIAL_KEYFRAMES.element(document);
        
        //iterate through all the keyframes
        SpatialKeyframe t = spatialKeyframeChangeNode.getStart();
        while(t!=null){

            //<SpatialKeyframe>
            Element spatialKeyframeTag=XMLTag.SPATIAL_KEYFRAME.element(document);
            spatialKeyframeTag.setAttribute(XMLAttribute.TIME.toString(), t.getTime() + "");
            spatialKeyframeTag.appendChild(getBezierPointTag(t.getBezierPoint()));

            //add each keyframe to keyframes tag
            spatialKeyframesTag.appendChild(spatialKeyframeTag);
            t=t.getNext();
        }
        return spatialKeyframesTag;
    }

    protected Element getBezierPointTag(BezierPoint bezierPoint){
        //<BezierPoint>
        Element bezierPointTag=XMLTag.BEZIER_POINT.element(document);
        
        //<BezierAnchorPoint>
        Element anchorPointTag=XMLTag.BEZIER_ANCHOR_POINT.element(document);
        anchorPointTag.setAttribute(XMLAttribute.X.toString(),bezierPoint.getAnchorPoint().getX()+"");
        anchorPointTag.setAttribute(XMLAttribute.Y.toString(),bezierPoint.getAnchorPoint().getY()+"");
        
        //<PreviousControlPoint>
        Element previousControlPointTag=XMLTag.PREVIOUS_CONTROL_POINT.element(document);
        previousControlPointTag.setAttribute(XMLAttribute.X.toString(),bezierPoint.getControlPointWithPrevious().getX()+"");
        previousControlPointTag.setAttribute(XMLAttribute.Y.toString(),bezierPoint.getControlPointWithPrevious().getY()+"");
        
        //<NextControlPoint>
        Element nextControlPointTag=XMLTag.NEXT_CONTROL_POINT.element(document);
        nextControlPointTag.setAttribute(XMLAttribute.X.toString(),bezierPoint.getControlPointWithNext().getX()+"");
        nextControlPointTag.setAttribute(XMLAttribute.Y.toString(),bezierPoint.getControlPointWithNext().getY()+"");

        bezierPointTag.appendChild(anchorPointTag);
        bezierPointTag.appendChild(previousControlPointTag);
        bezierPointTag.appendChild(nextControlPointTag);

        return bezierPointTag;
    }
}
