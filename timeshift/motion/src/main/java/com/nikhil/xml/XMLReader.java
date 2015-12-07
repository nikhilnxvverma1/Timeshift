package com.nikhil.xml;

import com.nikhil.controller.CompositionController;
import com.nikhil.controller.RootController;
import com.nikhil.controller.item.CircleModelController;
import com.nikhil.controller.item.ItemModelController;
import com.nikhil.logging.Logger;
import com.nikhil.model.shape.CircleModel;
import com.nikhil.model.shape.PolygonModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

/**
 * DOM parser that uses configurable model and controller factories to build entire
 * model controller system.This class can be extended to accept custom XML tags.
 * Created by NikhilVerma on 20/08/15.
 */
public class XMLReader {

    private ModelFactory modelFactory;
    private ControllerFactory controllerFactory;
    private RootController rootController;
//    private CompositionController lastCompositionController;

    public XMLReader() {
        this(new ModelFactory(),new ControllerFactory());
    }

    public XMLReader(ModelFactory modelFactory, ControllerFactory controllerFactory) {
        this.modelFactory = modelFactory;
        this.controllerFactory = controllerFactory;
    }

    public ModelFactory getModelFactory() {
        return modelFactory;
    }

    public void setModelFactory(ModelFactory modelFactory) {
        this.modelFactory = modelFactory;
    }

    public ControllerFactory getControllerFactory() {
        return controllerFactory;
    }

    public void setControllerFactory(ControllerFactory controllerFactory) {
        this.controllerFactory = controllerFactory;
    }

    /**
     * loads the timeshift file ,and creates the controller tree
     * @param file the timeshift file
     * @return the same custom root controller passed in now containing the entire controller tree
     */
    final public RootController load(File file) throws ParserConfigurationException, IOException, SAXException {

        rootController=new RootController();
//        lastCompositionController =new CompositionController();
//        rootController.addCompositionController(lastCompositionController);

        DocumentBuilderFactory documentBuilderFactory=DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        //load and parse the XML Document,document contains the entire parsed tree
//        InputStream file = ClassLoader.getSystemResourceAsStream(filename);
//        File file=new File(filename);
        Document document=documentBuilder.parse(file);

        int length=document.getChildNodes().getLength();
        for (int i = 0; i < length; i++) {

            Node node=document.getChildNodes().item(i);

            if(node instanceof Element){
//                parseDirectChildOfRoot((Element) node);
                parseRoot((Element)node);
            }
        }

        return rootController;
    }

    private void parseRoot(Element rootElement){
        int length=rootElement.getChildNodes().getLength();
        for (int i = 0; i < length; i++) {

            Node node=rootElement.getChildNodes().item(i);

            if(node instanceof Element){
                parseDirectChildOfRoot((Element) node);
            }
        }
    }

    /**
     * callback for an encountered element from the root node
     * @param element the element from the root
     */
    protected void parseDirectChildOfRoot(Element element){
        XMLTag tag=XMLTag.toTag(element.getTagName());
        if (tag == XMLTag.ROOT) {
            Logger.log("Came to root");
        } else if (tag == XMLTag.COMPOSITIONS) {
            parseAllCompositions(element);
        } else {
            Logger.log("Unknown element '" + element.getTagName() + "', will be processed by subclass");
        }
    }

    protected void parseAllCompositions(Element compositionList){

        //traverse each composition
        int length = compositionList.getChildNodes().getLength();
        for (int i = 0; i < length; i++) {
            Node node = compositionList.getChildNodes().item(i);

            //each composition itself has a list of items and certain other properties
            if(node instanceof Element){

                //a child can be either an item list, TODO or playback control
                Element child=(Element)node;
                XMLTag tag=XMLTag.toTag(child.getTagName());
                if (tag == XMLTag.COMPOSITION) {
                    CompositionController compositionController = new CompositionController();
                    rootController.addCompositionController(compositionController);
                    parseComposition(child, compositionController);

                    //extend here
                } else {
                    Logger.log("Unknown element in the composition list");
                }
            }
        }
    }

    protected void parseComposition(Element compositionElement, CompositionController compositionController) {
        //traverse each element in composition
        int length = compositionElement.getChildNodes().getLength();
        for (int i = 0; i < length; i++) {
            Node node = compositionElement.getChildNodes().item(i);

            //each composition itself has a list of items and certain other properties
            if (node instanceof Element) {
                Element child=(Element)node;
                XMLTag tag=XMLTag.toTag(child.getTagName());
                if (tag == XMLTag.ITEMS) {
                    parseAllItems(child, compositionController);

                    //extend here for posssibly playback controls
                }
            }
        }
    }

    protected void parseAllItems(Element itemList, CompositionController compositionController){
        //traverse each item
        int length = itemList.getChildNodes().getLength();
        for (int i = 0; i < length; i++) {

            //each item tag
            Node node = itemList.getChildNodes().item(i);
            if(node instanceof Element){

                //each item cane be one of the following
                Element element=(Element)node;
                XMLTag tag=XMLTag.toTag(element.getTagName());
                if (tag == XMLTag.POLYGON) {
                    PolygonModel polygonModel = modelFactory.parsePolygonModel(element);
                    ItemModelController polygonModelController = controllerFactory.getItemControllerFor(polygonModel);
                    compositionController.addItemController(polygonModelController);

                    //extend here
                }else if (tag == XMLTag.CIRCLE) {
                    CircleModel circleModel = modelFactory.parseCircleModel(element);
                    CircleModelController circleModelController = controllerFactory.getItemControllerFor(circleModel);
                    compositionController.addItemController(circleModelController);

                    //extend here
                }
                else {
                    Logger.log("Unknown item in the composition");
                }
            }
        }
    }
}
