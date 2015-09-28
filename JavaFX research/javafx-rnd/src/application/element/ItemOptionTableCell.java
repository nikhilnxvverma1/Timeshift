package application.element;

import application.timepane.TimeValueKey;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TreeTableCell;
import javafx.scene.layout.HBox;

public class ItemOptionTableCell extends TreeTableCell<ItemMetadata,OptionComponent>{

	@Override
	protected void updateItem(OptionComponent option, boolean empty) {
		super.updateItem(option, empty);
		if(option!=null){
			if(option.getParent().getItemType()==ItemType.HEADER){
				setGraphic(getCheckBoxes(option));
			}else if(option.getParent().getItemType()==ItemType.PROPERTY){
				setGraphic(getCycleButtons(option));
			}
		}else{
			setText(null);
			setGraphic(null);
		}
	}
	private HBox getCheckBoxes(OptionComponent option){
		HBox hbox=new HBox();
		CheckBox visiblityCheck=new CheckBox();
		visiblityCheck.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				System.out.println("visible event fired"+visiblityCheck.isSelected());
				option.setVisible(visiblityCheck.isSelected());
			}
		});
		visiblityCheck.setSelected(option.isVisible());
		
		CheckBox lockCheck=new CheckBox();
		lockCheck.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent event) {
				System.out.println("lock event fired"+lockCheck.isSelected());
				option.setVisible(lockCheck.isSelected());
			}
			
		});
		lockCheck.setSelected(option.isLocked());
		
		CheckBox soloCheck=new CheckBox();
		soloCheck.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent event) {
				System.out.println("solo event fired"+soloCheck.isSelected());
				option.setVisible(soloCheck.isSelected());
			}
			
		});
		soloCheck.setSelected(option.isSolo());
		hbox.getChildren().addAll(visiblityCheck,lockCheck,soloCheck);
		return hbox;
	}
	
	private HBox getCycleButtons(OptionComponent option){
		HBox hbox=new HBox();
		Button previous=new Button();
		previous.setText("<");
//		previous.setPrefSize(20, 20);
		Button keyButton=new Button();
//		keyButton.setPrefSize(20, 20);
		keyButton.setText("<>");
//		TimeValueKey key=new TimeValueKey();
//		key.translateXProperty().unbind();
//		key.setSelected(true);
//		key.setScaleX(0.8);
//		key.setScaleY(0.8);
//		keyButton.setGraphic(key);
		Button next=new Button();
		next.setText(">");
//		next.setPrefSize(20, 20);
		hbox.getChildren().addAll(previous,keyButton,next);
		return hbox;
	}
}
