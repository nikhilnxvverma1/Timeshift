package com.nikhil.editor;

import com.nikhil.controller.*;
import com.nikhil.controller.item.ItemModelController;
import com.nikhil.controller.item.PolygonModelController;
import com.nikhil.editor.workspace.Workspace;
import com.nikhil.model.freeform.MovablePoint;
import com.nikhil.model.shape.PolygonModel;
import com.nikhil.util.modal.UtilPoint;
import com.nikhil.view.item.PolygonView;
import com.nikhil.xml.XMLReader;
import org.xml.sax.SAXException;

import javax.rmi.CORBA.Util;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by NikhilVerma on 27/09/15.
 */
public class XMLLoader {

    private Workspace workspace;

    private RootController rootController;

    public XMLLoader(Workspace workspace) {
        this.workspace = workspace;
    }

    public RootController loadFile(File file) throws IOException, SAXException, ParserConfigurationException {
        XMLReader xmlReader=new XMLReader();
        rootController= xmlReader.load(file);
        //add respective  controllers to the workspace to root controller
        createCompositions();
        return rootController;
    }

    private void createCompositions(){
        //create a new composition in the workspace for each composition controller encountered
        CompositionController t=rootController.getCompositionControllerStart();
        while(t!=null){
            CompositionViewController compositionViewController=new CompositionViewController(t);
//            workspace.addComposition(compositionViewController);//it already came added from the XMLReader
            workspace.setCurrentComposition(compositionViewController);//possibly not required
            createItemsIn(compositionViewController);
            workspace.getCompositionViewControllers().add(compositionViewController);
            t=t.getNext();
        }
    }

    private void createItemsIn(CompositionViewController compositionViewController){
        CompositionController compositionController=compositionViewController.getCompositionController();

        ItemModelController t = compositionController.getItemModelControllerStart();
        while(t!=null){

            //create item view controllers and add them to the workspace
            ItemViewController itemViewController=getItemViewController(t);
            workspace.getItemViewControllers().add(itemViewController);
//            workspace.addToTimelineSystem(itemViewController);//it already came added from the XMLReader
            itemViewController.addViewsToWorksheet();

            t=t.getNext();
        }
    }

    private ItemViewController getItemViewController(ItemModelController itemModelController){
        if(itemModelController instanceof PolygonModelController){
            return new PolygonViewController(workspace,(PolygonModelController)itemModelController);
        }
        return null;
    }

}
