package com.nikhil.editor.tool;

import com.nikhil.command.AddLinkPoint;
import com.nikhil.command.Command;
import com.nikhil.editor.gizmo.TravelPathGizmo;
import com.nikhil.logging.Logger;
import com.nikhil.view.item.travelpath.LinkPointView;
import com.nikhil.view.item.travelpath.TravelPathView;
import com.nikhil.view.item.travelpath.ViewAltList;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

/**
 * Created by NikhilVerma on 12/09/15.
 */
public class TravelPathTool extends BaseTool {

    private Pane pane;
    private AddLinkPoint lastCommand;
    private ViewAltList viewAltList;

    public TravelPathTool(Pane pane) {
        this.pane = pane;
    }

    @Override
    public void toolAppointed(Tool lastSelectedTool) {
        viewAltList=new ViewAltList();
        lastCommand=null;
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        //Instantiate a new link point
        double x=mouseEvent.getX();
        double y=mouseEvent.getY();
        LinkPointView linkPointView=new LinkPointView(x,y);

        if(viewAltList.getHead()==null){
            viewAltList.setHead(linkPointView);
        }

//        viewAltList.add(linkPointView);//TODO fix this and use this one
        if(viewAltList.getTail()!=null){
            viewAltList.getTail().append(linkPointView);
        }
        viewAltList.setTail(linkPointView);

        //add the link point and the travel path to the pane
        TravelPathView travelPathView = viewAltList.getTail().getPrevious();
        //prevent NPE in case this is the first Link Point View added
        if(travelPathView!=null){
            pane.getChildren().add(travelPathView);
            //make sure to move the travel path at the back
            viewAltList.getTail().getPrevious().toBack();
        }
        pane.getChildren().add(viewAltList.getTail());
        lastCommand=new AddLinkPoint(pane,linkPointView,viewAltList,true);

    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        double x= mouseEvent.getX() - viewAltList.getTail().getX();
        double y= mouseEvent.getY() - viewAltList.getTail().getY();
        Logger.log("setting points "+x+" , "+y);
        viewAltList.getTail().extendNextControlPoints(x, y);
    }

    @Override
    public Command mouseReleased(MouseEvent mouseEvent) {
        return lastCommand;
    }

    @Override
    public void toolDismissed(Tool newToolSelected) {
        viewAltList.showControlPointGraphics(false);

        //experiment only for now
//        LinkPointView t=viewAltList.getHead();
//        while (t != null) {
//            if (t.getNext() != null) {
//                TravelPathGizmo travelPathGizmo=new TravelPathGizmo(pane,t.getNext());//let it loose,event handlers are configured
//                t=t.getNext().getNext();
//            }else{
//                t=null;
//            }
//        }

    }

    @Override
    public ToolType getToolType() {
        return ToolType.TRAVEL_PATH;
    }
}
