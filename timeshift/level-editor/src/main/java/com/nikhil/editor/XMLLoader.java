package com.nikhil.editor;

import com.nikhil.controller.*;
import com.nikhil.controller.item.ItemModelController;
import com.nikhil.controller.item.PolygonModelController;
import com.nikhil.editor.workspace.Workspace;
import com.nikhil.xml.XMLReader;
import javafx.scene.control.TabPane;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

/**
 * Created by NikhilVerma on 27/09/15.
 */
public class XMLLoader {

    private Workspace workspace;
    private TabPane compositionTabs;
    private RootController rootController;

    public XMLLoader(Workspace workspace,TabPane compositionTabs) {
        this.workspace = workspace;
        this.compositionTabs=compositionTabs;
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
        CompositionViewController firstComposition=null;
        compositionTabs.getTabs().clear();
        while(t!=null){
            CompositionViewController compositionViewController=new CompositionViewController(t,workspace,CompositionViewController.getNewTabName());
            //keep track of the first composition view controller
            if(firstComposition==null){
                firstComposition=compositionViewController;
            }

            createItemsIn(compositionViewController);
            workspace.getCompositionViewControllers().add(compositionViewController);
            compositionTabs.getTabs().add(compositionViewController.getTab());
            t=t.getNext();
        }
        if(firstComposition!=null){
            workspace.setCurrentComposition(firstComposition);
            //select the tab in the tab pane if it exists
            compositionTabs.getSelectionModel().select(firstComposition.getTab());
        }
    }

    private void createItemsIn(CompositionViewController compositionViewController){
        CompositionController compositionController=compositionViewController.getCompositionController();

        ItemModelController t = compositionController.getItemModelControllerStart();
        while(t!=null){

            //create item view controllers and add them to the workspace
            ItemViewController itemViewController=getItemViewController(t,compositionViewController);
            compositionViewController.addItemViewController(itemViewController,true);
//            workspace.addToTimelineSystem(itemViewController);//it already came added from the XMLReader
//            itemViewController.addViewsTo();

            t=t.getNext();
        }
    }

    private ItemViewController getItemViewController(ItemModelController itemModelController,CompositionViewController compositionViewController){
        if(itemModelController instanceof PolygonModelController){
            return new PolygonViewController(compositionViewController,(PolygonModelController)itemModelController);
        }
        return null;
    }

}
