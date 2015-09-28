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
        root = document.createElement(XMLTag.ROOT.toString());
        document.appendChild(root);
        Logger.log("Added root");
    }

    @Override
    public void visit(CompositionController compositionController) {

        //if a composition tag doesn't exist already, create one
        if(compositions==null){
            //<compositions>
            compositions=document.createElement(XMLTag.COMPOSITIONS.toString());
            root.appendChild(compositions);
            Logger.log("Added compositions container");
        }

        //<composition>
        composition =document.createElement(XMLTag.COMPOSITION.toString());
        compositions.appendChild(composition);

        //<items>
        itemContainer=document.createElement(XMLTag.ITEMS.toString());
        composition.appendChild(itemContainer);
        Logger.log("Added composition");
    }

    @Override
    public void visit(Circle circle) {

    }

    @Override
    public void visit(IsoscelesTriangle isoscelesTriangle) {

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
    public void visit(PolygonModel polygonModel) {

        Element polygon=document.createElement(XMLTag.POLYGON.toString());
        polygon.appendChild(getShapeProperties(polygonModel));

        movablePointContainer =document.createElement(XMLTag.VERTICES.toString());
        polygon.appendChild(movablePointContainer);

        //add to the last composition(this method doesn't know which composition it will  get added to)
        itemContainer.appendChild(polygon);
        Logger.log("Added polygon model");
    }

    @Override
    public void visit(MovablePoint movablePoint) {
        //<MovablePoint>
        Element header=document.createElement(XMLTag.MOVABLE_POINT.toString());

        //<Position>
        Element position=document.createElement(XMLTag.POSITION.toString());

        header.appendChild(position);
        position.appendChild(getX(movablePoint.getPoint()));
        position.appendChild(getY(movablePoint.getPoint()));

        //append this to the whatever the container is(this method doesn't know where it will get added to)
        movablePointContainer.appendChild(header);

        Logger.log("Added movable point");
    }

    @Override
    public void visit(TravellerConfiguration travellerConfiguration) {

    }

    protected Element getShapeProperties(ShapeModel shapeModel){
        //<shape>
        Element shape=document.createElement(XMLTag.SHAPE.toString());

        //<scale>
        Element scale=document.createElement(XMLTag.SCALE.toString());
        scale.appendChild(document.createTextNode(shapeModel.getScale()+""));

        //<rotation>
        Element rotation=document.createElement(XMLTag.ROTATION.toString());
        rotation.appendChild(document.createTextNode(shapeModel.getRotation()+""));

        //<translation>
        Element translation=document.createElement(XMLTag.TRANSLATION.toString());
        translation.appendChild(getX(shapeModel.getTranslation()));
        translation.appendChild(getY(shapeModel.getTranslation()));

        //<anchor point>
        Element anchorPoint=document.createElement(XMLTag.ANCHOR_POINT.toString());
        anchorPoint.appendChild(getX(shapeModel.getAnchorPoint()));
        anchorPoint.appendChild(getY(shapeModel.getAnchorPoint()));

        //append all properties to shape element
        shape.appendChild(scale);
        shape.appendChild(rotation);
        shape.appendChild(translation);
        shape.appendChild(anchorPoint);

        return shape;
    }

    protected Element getX(UtilPoint utilPoint){
        Element x=document.createElement(XMLTag.X.toString());
        x.appendChild(document.createTextNode(utilPoint.getX()+""));
        return x;
    }

    protected Element getY(UtilPoint utilPoint){
        Element y = document.createElement(XMLTag.Y.toString());
        y.appendChild(document.createTextNode(utilPoint.getY() + ""));
        return y;
    }

}
