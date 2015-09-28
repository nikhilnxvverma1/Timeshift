package application.element;

import javafx.scene.control.CheckBox;
import javafx.scene.control.TreeTableCell;

public class ItemNameTableCell extends TreeTableCell<ItemMetadata,NameComponent>{

	@Override
	protected void updateItem(NameComponent name, boolean empty) {
		super.updateItem(name, empty);
		if(name!=null){
			setText(name.getName());
			if(name.getParent().getItemType()==ItemType.PROPERTY){
				CheckBox checkBox = new CheckBox();
				if(name.getParent().isKeyframed()){
					checkBox.setSelected(true);
				}else{
					checkBox.setSelected(false);
				}
				setGraphic(checkBox);
			}
		}else{
			setText(null);
			setGraphic(null);
		}
	}
	
}
