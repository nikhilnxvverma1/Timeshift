package application.element;

import application.DraggableTextValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TreeTableCell;

public class ItemValueTableCell extends TreeTableCell<ItemMetadata,ValueComponent>{

	@Override
	protected void updateItem(ValueComponent value, boolean empty) {
		super.updateItem(value, empty);
		if(value!=null){
			if(value.getParent().getItemType()==ItemType.PROPERTY){
				DraggableTextValue draggableValue=new DraggableTextValue();
				setGraphic(draggableValue);
			}else{
				setText("Reset");
			}
		}else{
			setGraphic(null);
			setText(null);
		}
	}
	
}
