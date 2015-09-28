package application.element;

import java.util.Random;

import application.DraggableTextValue;
import application.timepane.TimeValuePane;
import javafx.scene.control.TreeTableCell;

public class KeyTableCell extends TreeTableCell<ItemMetadata,OptionComponent>{
	
	private static final Random random=new Random();
	
	@Override
	protected void updateItem(OptionComponent value, boolean empty) {
		super.updateItem(value, empty);
		if(value!=null){
			double timePaneLength = getTableColumn().getPrefWidth();
			int totalTime = 30;
			if(value.getParent().getItemType()==ItemType.PROPERTY){
				
				TimeValuePane timeValuePane=buildTimePane(totalTime, timePaneLength);
				setGraphic(timeValuePane);
			}else{
				setText(null);
//				setGraphic(new TimeValuePane(totalTime, timePaneLength));//empty timevaluepane
			}
		}else{
			setGraphic(null);
			setText(null);
		}
	}
	private TimeValuePane buildTimePane(int totalTime,double length){
		TimeValuePane timePane=new TimeValuePane(totalTime,length);
		int totalKeys=random.nextInt(6);
		for(int i=0;i<totalKeys;i++){
			timePane.addKeyAt(random.nextInt(totalTime), null);	
		}
		return timePane;
	}
}
