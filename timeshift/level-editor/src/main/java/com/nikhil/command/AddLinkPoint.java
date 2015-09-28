package com.nikhil.command;

import com.nikhil.view.item.travelpath.LinkPointView;
import com.nikhil.view.item.travelpath.ViewAltList;
import javafx.scene.layout.Pane;

/**
 * Created by NikhilVerma on 12/09/15.
 */
public class AddLinkPoint implements Command {

    private Pane pane;
    private LinkPointView linkPointView;
    private ViewAltList viewAltList;
    private boolean executed=false;

    public AddLinkPoint(Pane pane, LinkPointView linkPointView, ViewAltList viewAltList, boolean executed) {
        this.pane = pane;
        this.linkPointView = linkPointView;
        this.viewAltList = viewAltList;
        this.executed = executed;
    }

    @Override
    public void execute() {

        if (!executed) {
            pane.getChildren().add(linkPointView);

            //non first nodes
            if(linkPointView.getPrevious()!=null){
                pane.getChildren().add(linkPointView.getPrevious());
                linkPointView.getPrevious().toBack();
                viewAltList.append(linkPointView.getPrevious());
            }
            //first node
            else{
                viewAltList.append(linkPointView);
            }
        }

        //TODO add controller
        executed=true;
    }

    @Override
    public void unexecute() {

        pane.getChildren().remove(linkPointView);
        pane.getChildren().remove(linkPointView.getPrevious());
        viewAltList.removeLinkPoint(linkPointView);
        //TODO remove controller
        executed=false;
    }
}
