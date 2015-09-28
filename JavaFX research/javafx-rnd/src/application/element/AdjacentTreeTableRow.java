package application.element;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeTableRow;

public class AdjacentTreeTableRow extends TreeTableRow<ItemMetadata>{

	
	
	public AdjacentTreeTableRow(){
		super();
//		getTreeItem().expandedProperty().addListener((listener)->{
//			
//		});
		
		treeItemProperty().addListener((listener)->{
			System.out.println("tree item has been set");
		});
		
//		treeItemProperty().addListener(new ChangeListener<ItemMetadata>() {
//
//			@Override
//			public void changed(
//					ObservableValue<? extends ItemMetadata> observable,
//					ItemMetadata oldValue, ItemMetadata newValue) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
	}
	
	
	
	
}
