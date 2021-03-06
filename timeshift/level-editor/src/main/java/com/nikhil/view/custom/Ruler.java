package com.nikhil.view.custom;

import com.nikhil.logging.Logger;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import java.util.Iterator;

public class Ruler extends Pane{

	private static final double DEFAULT_MAX_MARK_SEPERATION=80;
	private static final double DEFAULT_MARKING_STEP=5;
	
	private double unit;
	private double unitLength;
	private double length;
	private int totalUnits;
	private double xForOrigin=0;
	private double thresholdMarkSeperation;
	
	private double currentZoom;
	
	//================================================================================
	//Internal variables
	//================================================================================ 
	private int lastAddedSubunitsInSingleUnit=0;
	private Pane markingContainer;
	private Line bottomLine;

    //-1 is a sentinel value that indicates that the bounds weren't recomputed before
    private double oldWidth=-1;
	
	public Ruler(int totalUnits, double length){
		this(DEFAULT_MARKING_STEP,length*DEFAULT_MARKING_STEP/totalUnits,length);
		this.totalUnits=totalUnits;
	}
	
	public Ruler(double markingStep, double markDistance, double length) {
		this(markingStep,markDistance,length,DEFAULT_MAX_MARK_SEPERATION);
	}
	
	public Ruler(double markingStep, double markDistance, double length,double maxMarkSeperation) {
		this(markingStep,markDistance,length,maxMarkSeperation,1);
	}

	public Ruler(double markingStep, double markDistance, double length,double maxMarkSeperation,
			double currentZoom){
		this(markingStep,markDistance,length,maxMarkSeperation,1,0);
	}
	
	private Ruler(double markingStep, double markDistance, double length, double maxMarkSeperation,
				  double currentZoom, double xForOrigin) {
		super();
		this.unit = markingStep;
		this.unitLength = markDistance;
		this.length = length;
		this.currentZoom = currentZoom;
		this.thresholdMarkSeperation=maxMarkSeperation;
		this.xForOrigin=xForOrigin;
		
		markingContainer=new Pane();
		this.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
        this.setMinSize(Control.USE_COMPUTED_SIZE,Control.USE_COMPUTED_SIZE);
		getChildren().add(markingContainer);
		
		//line 
		bottomLine =new Line(0,0,0,getPrefWidth());
//		AnchorPane.setBottomAnchor(bottomLine,  0.0);
		bottomLine.layoutYProperty().bind(markingContainer.heightProperty());
		getChildren().add(bottomLine);
		
		addSubMarks(1);
        addRemoveMarkingIfNeeded();
	}
	
	private double getLayoutXFor(Marking marking){
		
		double similarMarkSeperation=getMarkSeperationFor(marking.getSubUnitsInSingleUnit());
        return marking.getOrderInThisZoom()*similarMarkSeperation;
	}
	
	private double getMarkSeperationFor(int subUnitsInSingleUnit){
		return (unitLength/subUnitsInSingleUnit)*currentZoom;
	}	

	public void zoomBy(double step){
		zoomTo(currentZoom+step);
	}
	
	public void zoomTo(double newZoom){
		if(newZoom<1){//minimum zoom is 1
			return;
		}
		
		currentZoom=newZoom;
		//shift all marking according to new zoom
		shiftMarkings();
		
		double markSeperation=getMarkSeperationFor(lastAddedSubunitsInSingleUnit);
		double markSeperationForPrevious=getMarkSeperationFor(lastAddedSubunitsInSingleUnit/2);
		if(markSeperation>thresholdMarkSeperation){
			long totalUnitsAdded=addSubMarks(lastAddedSubunitsInSingleUnit*2);
			Logger.log("new markings added " + totalUnitsAdded);
		}else if((markSeperationForPrevious<thresholdMarkSeperation)&&(lastAddedSubunitsInSingleUnit/2>=1)){
			long totalUnitsRemoved=removeLastSubMarkings(lastAddedSubunitsInSingleUnit/2);
			Logger.log("new markings removed " + totalUnitsRemoved);
		}
	}

	private void shiftMarkings() {
		double maxX=0;
		double minX=0;
		ObservableList<Node> markings=markingContainer.getChildren();
		for(Node node: markings){
			
			Marking marking=(Marking)node;
			double markingX = getLayoutXFor(marking);
			marking.setLayoutX(markingX);
			
			if(markingX>maxX){
				maxX=markingX;
			}
			
			if(markingX<minX){
				minX=markingX;
			}
		}
		
		bottomLine.setStartX(minX);
		bottomLine.setEndX(maxX);
	}
	
	private int addSubMarks(int subUnitsInSingleUnit){
		int totalMarkingsAdded=0;
		double maxX=0;
		double minX=0;
		
		long markingNumber=0;
		double subUnitLength=unitLength/(double)subUnitsInSingleUnit;
		double subUnit=unit/(double)subUnitsInSingleUnit;
		Logger.log("Sub unit length " + subUnitLength + " value " + subUnit);
		for(double i=0,currentMark=0;
				i<=length;
				i+=subUnitLength,currentMark+=subUnit){
			
			Marking mark=new Marking(currentMark, markingNumber, subUnitsInSingleUnit);
			mark.setMarkStrikeSize(1/(double)subUnitsInSingleUnit);
			double markingX = getLayoutXFor(mark);
			mark.setLayoutX(markingX);
			mark.setLayoutY(0);

			//avoid adding markings that already overlap with previous markings
			boolean markingOverlapsWithPreviousMarkings=false;
			
			if(lastAddedSubunitsInSingleUnit>0){
				//check if this marking may already be added by a super marking done before
				int k=subUnitsInSingleUnit/lastAddedSubunitsInSingleUnit;
				int n=(int) (markingNumber%subUnitsInSingleUnit);				
				if((k==0)||
						(n%k==0)){
					markingOverlapsWithPreviousMarkings=true;
				}
			}

			if(!markingOverlapsWithPreviousMarkings){
				markingContainer.getChildren().add(mark);
				totalMarkingsAdded++;
			}
			
			markingNumber++;
			
			if(markingX>maxX){
				maxX=markingX;
			}
		
			if(markingX<minX){
				minX=markingX;
			}
		}		
		
		bottomLine.setStartX(minX);
		bottomLine.setEndX(maxX);
		
		lastAddedSubunitsInSingleUnit=subUnitsInSingleUnit;
		return totalMarkingsAdded;
	}
	
	private int removeLastSubMarkings(int subUnitsInSingleUnitForPreviousMarkings){
		int totalMarkingsRemoved=0;
		ObservableList<Node> markings=markingContainer.getChildren();
		Iterator<Node> iterator=markings.iterator();
		while(iterator.hasNext()){
			
			Marking marking=(Marking)iterator.next();
			if(marking.getSubUnitsInSingleUnit()==lastAddedSubunitsInSingleUnit){
				iterator.remove();
				totalMarkingsRemoved++;
			}
		}
		lastAddedSubunitsInSingleUnit=subUnitsInSingleUnitForPreviousMarkings;
		return totalMarkingsRemoved;
	}
	
	@Override
	protected void layoutChildren() {
        super.layoutChildren();
        if(oldWidth==-1){
            oldWidth=getWidth();
            return;
        }
        double newWidth=getWidth();
        double fractionIncrease = newWidth / oldWidth;
        this.length*= fractionIncrease;
		this.unitLength= (length*DEFAULT_MARKING_STEP) /this.totalUnits;
        //shift all marking according to new zoom
        shiftMarkings();

        addRemoveMarkingIfNeeded();
        oldWidth=newWidth;

	}

    private void addRemoveMarkingIfNeeded() {
        double markSeperation=getMarkSeperationFor(lastAddedSubunitsInSingleUnit);
        double markSeperationForPrevious=getMarkSeperationFor(lastAddedSubunitsInSingleUnit/2);
        if(markSeperation>thresholdMarkSeperation){
            int totalUnitsAdded=addSubMarks(lastAddedSubunitsInSingleUnit*2);
        }else if((markSeperationForPrevious<thresholdMarkSeperation)&&(lastAddedSubunitsInSingleUnit/2>=1)){
            int totalUnitsRemoved=removeLastSubMarkings(lastAddedSubunitsInSingleUnit/2);
        }
    }
}
