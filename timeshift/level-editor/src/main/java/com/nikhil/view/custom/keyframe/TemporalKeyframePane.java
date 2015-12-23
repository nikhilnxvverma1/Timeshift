package com.nikhil.view.custom.keyframe;

import com.nikhil.timeline.KeyValue;
import com.nikhil.timeline.keyframe.Keyframe;
import com.nikhil.timeline.keyframe.TemporalKeyframe;
import com.nikhil.view.item.record.TemporalMetadata;

import java.util.Iterator;
import java.util.TreeSet;

/**
 * Created by NikhilVerma on 11/11/15.
 */
public class TemporalKeyframePane extends KeyframePane{

    private TemporalMetadata metadata;
    private TreeSet<TemporalGraphNode> graphNodes=new TreeSet<>((o1,o2)-> (int) (o1.getTime()-o2.getTime()));

    public TemporalKeyframePane(double totalTime, double length,TemporalMetadata metadata) {
        super(totalTime, length);
        this.metadata=metadata;
        initKeyframes();
    }

    @Override
    public TemporalMetadata getMetadata() {
        return metadata;
    }

    /**
     * Adds a new keyframe at the specified time and selects it.
     * Internally it also adds a keyframe model to the associated change node
     * @param time the time of the keyframe
     * @param keyValue the temporal value of the keyframe
     * @return the keyframe view created
     */
    public TemporalKeyframeView addKeyframe(double time, KeyValue keyValue){
        //create and add the keyframe model
        TemporalKeyframe keyframeModel=new TemporalKeyframe(time,keyValue);
        metadata.getKeyframeChangeNode().addKeyframe(keyframeModel);

        //insert a new keyframe view and select it
        TemporalKeyframeView keyframeView = insertNewKeyframeView(keyframeModel);
        keyframeView.setSelected(false);//we don want the keyframe selected
        return keyframeView;
    }

    /**
     * Create a new keyframe view which houses this keyframe model,
     * and adds it to the "keyContainer" node.
     * @param keyframeModel the keyframe model for which this keyframe view is being created
     * @return the new keyframe view
     */
    private TemporalKeyframeView insertNewKeyframeView(TemporalKeyframe keyframeModel) {

        TemporalKeyframeView keyframeView=new TemporalKeyframeView(keyframeModel,this);
        keyframeView.setLayoutX(getLayoutXFor(keyframeView));
        keyContainer.getChildren().add(keyframeView);
        addGraphNode(keyframeView.getGraphNode());
        return keyframeView;
    }

    protected void addGraphNode(TemporalGraphNode graphNode) {
        graphNodes.add(graphNode);
        TemporalGraphNode before= graphNodes.floor(graphNode);
        if (before!=null) {
            before.updateView(graphNode);
        }
        TemporalGraphNode after = graphNodes.ceiling(graphNode);
        if (after!=null) {
            graphNode.updateView(after);
        }

        //add the graph node associated with this keyframe to the graph editor
        getMetadata().getItemViewController().getCompositionViewController().
                getGraphEditor().addGraphNode(graphNode);

    }

    /**
     * Adds the supplied keyframe view to itself. Along with that the keyframe
     * model is also pushed to the temporal keyframe change nod
     * @param keyframeView the keyframe to add to the keyframe pane
     */
    public void addKeyframe(TemporalKeyframeView keyframeView){
        //add the keyframe model to the temporal change node
        metadata.getKeyframeChangeNode().addKeyframe(keyframeView.getKeyframeModel());

        //select it and add it to the "keyContainer" node.
        keyframeView.setSelected(true);
        keyframeView.setLayoutX(getLayoutXFor(keyframeView));
        keyContainer.getChildren().add(keyframeView);

        //add the graph node associated with this keyframe
        addGraphNode(keyframeView.getGraphNode());
    }

    /**
     * Removes the specified keyframe from both the pane and the model
     * @param keyframeView the keyframe view to remove(containment check included)
     * @return true if the data structure contained this keyframe(and thus it got removed),
     * false otherwise
     */
    public boolean removeKeyframe(TemporalKeyframeView keyframeView){
        boolean wasRemoved = keyContainer.getChildren().remove(keyframeView);
        if(wasRemoved){
            //remove the value from the change node too
            metadata.getKeyframeChangeNode().removeKeyframe(keyframeView.getKeyframeModel());

            //remove the graph node associated with this keyframe
            removeGraphNode(keyframeView.getGraphNode());
        }
        return wasRemoved;
    }

    private void removeGraphNode(TemporalGraphNode graphNode) {
        getMetadata().getItemViewController().getCompositionViewController().
                getGraphEditor().removeGraphNode(graphNode);
        TemporalGraphNode before= graphNodes.floor(graphNode);
        TemporalGraphNode after = graphNodes.ceiling(graphNode);
        if (before!=null) {

            before.updateView(after);

        }

        graphNodes.remove(graphNode);
    }

    @Override
    public TemporalKeyframeView findKeyframeView(Keyframe keyframe) {
        return (TemporalKeyframeView)super.findKeyframeView(keyframe);
    }

    @Override
    public void keyframesMoved(int totalKeysMoved) {
        double currentTime = getMetadata().getItemViewController().getCompositionViewController().getTime();
        getMetadata().getKeyframeChangeNode().setTime(currentTime);
    }

    @Override
    public TreeSet<TemporalGraphNode> getGraphNodes() {
        return graphNodes;
    }

    private void initKeyframes(){
        TemporalKeyframe t=metadata.getKeyframeChangeNode().getStart();
        while(t!=null){
            insertNewKeyframeView(t);
            t=t.getNext();
        }
    }

    @Override
    public void updateGraphNodes(){

        TemporalGraphNode previous=null;
        for (TemporalGraphNode graphNode : getGraphNodes()) {
            if (previous != null) {
                previous.updateView(graphNode);
            }
            previous = graphNode;
        }
        //for the last graph node we will need to explicitly set it to null
        if (previous!=null) {
            previous.updateView(null);
        }

    }
}
