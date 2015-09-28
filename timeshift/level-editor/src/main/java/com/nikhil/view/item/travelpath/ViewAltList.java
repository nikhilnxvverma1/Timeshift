package com.nikhil.view.item.travelpath;

import com.nikhil.model.pathsystem.LinkPoint;
import com.nikhil.space.bezier.path.BezierPoint;

import java.util.List;

/**
 * The view alt list contains a doubly linked list of TravelPathView and LinkPointView
 * that are joined in an alternate fashion.This ensures that a TravelPathView is always
 * between two LinkPointView. The first and last node of the View alt list are always
 * LinkPointView.
 * Created by NikhilVerma on 12/09/15.
 */
public class ViewAltList {
    private LinkPointView head;
    private LinkPointView tail;

    /**
     * builds an alt list of link points and travel paths from a list of bezier points.
     * @param bezierPointList list of bezier points used in making up the alt list
     * @return the first link point view created
     */

    public static LinkPointView buildTravelPathLinkPointViewList(List<BezierPoint> bezierPointList){
        return buildTravelPathLinkPointViewList(bezierPointList, null);
    }

    /**
     * builds an alt list of link points and travel paths from a list of bezier points.
     * the list build will be appended to the last argument specified
     * @param bezierPointList list of bezier points used in making up the alt list
     * @param appendAfter the link point after which this list should be appended. if null, this
     *                    list will begin from the first node created
     * @return the first link point view created
     */
    public static LinkPointView buildTravelPathLinkPointViewList(List<BezierPoint> bezierPointList,
                                                                 LinkPointView appendAfter){
        LinkPointView firstLinkPointViewCreated=null;
        LinkPointView lastLinkPointView=appendAfter;

        //for each bezier point
        for(BezierPoint p:bezierPointList){

            //create a new link point view for this point
            LinkPointView currentLinkPointView = getLinkPointViewFromBezierPoint(p);

            //sandwich a travel path view between this link point and the last link point
            if(lastLinkPointView!=null){
                buildAndInsertTravelPathViewBetween(lastLinkPointView, currentLinkPointView);
            }else{
                lastLinkPointView=currentLinkPointView;
            }

            //keep a reference to the first link point created
            if(firstLinkPointViewCreated==null){
                firstLinkPointViewCreated=currentLinkPointView;
            }
        }

        return firstLinkPointViewCreated;
    }

    /**
     * creates and inserts a travel path view between two link point views. this helps in
     * connecting two link point views as the nodes of the list alternate between link point
     * and travel path
     * @param firstLinkPointView first link point of the travel path
     * @param secondLinkPointView second link point of the travel path
     * @return the travel path view created
     */
    public static TravelPathView buildAndInsertTravelPathViewBetween(LinkPointView firstLinkPointView, LinkPointView secondLinkPointView) {
        TravelPathView travelPathView=new TravelPathView(firstLinkPointView, secondLinkPointView);
        firstLinkPointView.setNext(travelPathView);
        secondLinkPointView.setPrevious(travelPathView);
        return travelPathView;
    }

    /**
     * creates a link point using a bezier point
     * @param p the bezier point containing information about anchor point and control point
     * @return the link point created
     */
    public static LinkPointView getLinkPointViewFromBezierPoint(BezierPoint p) {
        return new LinkPointView(p.getAnchorPoint().getX(),p.getAnchorPoint().getY(),
                p.getControlPointWithPrevious().getX(),p.getControlPointWithPrevious().getY(),
                p.getControlPointWithNext().getX(),p.getControlPointWithNext().getY());
    }


    public LinkPointView getHead() {
        return head;
    }

    public void setHead(LinkPointView head) {
        this.head = head;
    }

    public LinkPointView getTail() {
        return tail;
    }

    public void setTail(LinkPointView tail) {
        this.tail = tail;
    }

    //=============================================================================================
    //Alt list manipulation
    //=============================================================================================

    /**
     * appends a link point to the alt list by inserting a travel path view between the last
     * link point and the one that just got created
     * @param bezierPoint the bezier point containing information about anchor point and control point
     * @return the link point view created that needed to be appended.
     */
    public LinkPointView append(BezierPoint bezierPoint){
        LinkPointView linkPointViewToAppend=getLinkPointViewFromBezierPoint(bezierPoint);
        if(tail!=null){
            buildAndInsertTravelPathViewBetween(tail, linkPointViewToAppend);
        }
        if(head==null){
            head=linkPointViewToAppend;
            tail=linkPointViewToAppend;
        }
        return linkPointViewToAppend;
    }

    /**
     * appends a link point to the alt list by inserting a travel path view between the last
     * link point and the one that needs to be appended
     * @return the same link point view that got appended
     */
    public LinkPointView append(LinkPointView linkPointViewToAppend){
        if(tail!=null){
            buildAndInsertTravelPathViewBetween(tail, linkPointViewToAppend);
        }
        if(head==null){
            head=linkPointViewToAppend;
        }
        if(tail==null){
            tail=linkPointViewToAppend;
        }
        return linkPointViewToAppend;
    }

    /**
     * appends a travel path connected link point to the alt list.This does not create
     * a new travel path.This method expects a tail to already exist
     * @return the travel path just inserted,if possible to insert,else null
     */
    public TravelPathView append(TravelPathView travelPath){
        if(travelPath==null||travelPath.getNext()==null){
            return null;
        }
        if(tail!=null){
            tail.setNext(travelPath);
            travelPath.setPrevious(tail);
            //go to the end of this list
            LinkPointView t=travelPath.getNext();
            LinkPointView last=null;
            while(t!=null){
                last=t;
                if(t.getNext()!=null){
                    t=t.getNext().getNext();
                }else{
                    t=null;
                }
            }
            tail=last;
            return travelPath;
        }else{
            return null;
        }
    }

    /**
     * removes the last link point view from the alt list
     * @return the link point view just removed(old tail), null if list is empty
     */
    public LinkPointView removeLastLinkPoint(){
       return removeLinkPoint(tail);
    }

    /**
     * removes a link point view from the alt list.
     * This method does not check for containment
     * @return the link point view just removed, null if list is empty
     * TODO this method is splitting the list, consider returning the second alt list
     */
    public LinkPointView removeLinkPoint(LinkPointView linkPointView){
        if(linkPointView==null||!this.contains(linkPointView)){
            return null;
        }
        if(linkPointView == tail){ //last node
            LinkPointView oldTail=tail;
            if(tail.getPrevious()!=null){//not the only node
                LinkPointView newTail=tail.getPrevious().getPrevious();
                tail.removeFromList();
                if(newTail!=null){
                    tail=newTail;
                }else{//loner node
                    tail=null;
                    head=null;
                }
            }else{
                tail=null;
                head=null;
            }
            return oldTail;
        }else if(linkPointView==head){//first node
            LinkPointView oldHead=head;
            if (head.getNext()!=null) {//not the only node
                LinkPointView newHead=head.removeFromList();
                if(newHead!=null){
                    head=newHead;
                }else{
                    head=null;
                    tail=null;
                }
            }else{//loner node
                head=null;
                tail=null;
            }
            return oldHead;
        }else{
            //somewhere in the middle, safe to remove.
            linkPointView.removeFromList();
            return linkPointView;
        }
    }

    /**
     * checks if a LinkPointView exists in this alt list or not
     * @param linkPointView node to search for
     * @return true if link point view exists,false otherwise
     */

    public boolean contains(LinkPointView linkPointView){
        boolean doesExist=false;
        LinkPointView t=head;
        while(t!=null){
            if(linkPointView==t){
                doesExist=true;
                break;
            }
            if(t.getNext()!=null){
                t=t.getNext().getNext();
            }else{
                t=null;
            }
        }
        return doesExist;
    }

    /**
     * checks if a travel path exists in this alt list or not
     * @param travelPathView node to search for
     * @return true if travel path exists,false otherwise
     */
    public boolean contains(TravelPathView travelPathView){
        boolean doesExist=false;
        LinkPointView t=head;
        while(t!=null){
            if(travelPathView==t.getNext()){
                doesExist=true;
                break;
            }
            if(t.getNext()!=null){
                t=t.getNext().getNext();
            }else{
                t=null;
            }
        }
        return doesExist;
    }

    /**
     * sets the visibility of the control point graphics for the
     * entire bezier travel path
     * @param visible sets the visibility of the control point graphics
     */
    public void showControlPointGraphics(boolean visible){
        LinkPointView t=head;
        while(t!=null){
            t.showControlPointGraphics(visible);
            if(t.getNext()!=null){
                t=t.getNext().getNext();
            }else{
                t=null;
            }
        }
    }
}
