package application;

import java.util.Random;

import application.timepane.TimeValueKey;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
//import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class TreeTableExample extends Application{

	public static void main(String args[]){
		launch(args);
	}
	
	 @Override
	  public void start(Stage stage) {
	    final Scene scene = new Scene(new Group(), 400, 400);
	    Group sceneRoot = (Group) scene.getRoot();

	    TreeItem<ElementModal> childNode1 = new TreeItem<>(new ElementModal());
	    TreeItem<ElementModal> childNode2 = new TreeItem<>(new ElementModal());
	    TreeItem<ElementModal> childNode3 = new TreeItem<>(new ElementModal());
	    TreeItem<ElementModal> grandChild1 = new TreeItem<>(new ElementModal());
	    TreeItem<ElementModal> grandChild2 = new TreeItem<>(new ElementModal());
	    TreeItem<ElementModal> grandChild3 = new TreeItem<>(new ElementModal());

	    TreeItem<ElementModal> root = new TreeItem<>(new ElementModal());
	    root.setExpanded(true);

	    root.getChildren().setAll(childNode1, childNode2, childNode3);
	    childNode2.getChildren().setAll(grandChild1, grandChild2, grandChild3);
	    
	    TreeTableColumn<ElementModal, String> column = new TreeTableColumn<>("Column");
	    column.setPrefWidth(150);
	    
	    column.setCellValueFactory(new Callback<CellDataFeatures<ElementModal, String>,ObservableValue<String>>(){

			@Override
			public ObservableValue<String> call(
					CellDataFeatures<ElementModal, String> param) {
				return param.getValue().getValue().nameProperty();
			}
	    	
	    });
	    TreeTableColumn<ElementModal, String> leadingColumn=new TreeTableColumn<>("Element");
	    leadingColumn.setPrefWidth(150);
	    leadingColumn.setCellValueFactory(new Callback<CellDataFeatures<ElementModal, String>,ObservableValue<String>>(){	    
	    	
			@Override
			public ObservableValue<String> call(
					CellDataFeatures<ElementModal, String> param) {
				return param.getValue().getValue().nameProperty();
			}
	    	
	    });
	    TreeTableColumn<ElementModal, String> thirdColumn=new TreeTableColumn<>("Third");
	    thirdColumn.setPrefWidth(150);
	    thirdColumn.setCellFactory(new Callback<TreeTableColumn<ElementModal, String>,TreeTableCell<ElementModal,String>>(){

			@Override
			public TreeTableCell<ElementModal, String> call(
					TreeTableColumn<ElementModal, String> param) {
//				TreeTableCell cell =new TreeTableCell<String,String>();
				CustomCell cell=new CustomCell();
				return cell;
			}
	    	
	    });
	    
	    thirdColumn.setCellValueFactory(new Callback<CellDataFeatures<ElementModal, String>,ObservableValue<String>>(){	    
	    	
			@Override
			public ObservableValue<String> call(
					CellDataFeatures<ElementModal, String> param) {
				return param.getValue().getValue().typeProperty();
			}
	    	
	    });

	    TreeTableView<ElementModal> treeTableView = new TreeTableView<>(root);
	    childNode2.expandedProperty().addListener(new ChangeListener<Boolean>(){

			@Override
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean arg1, Boolean arg2) {
				System.out.println("expand changed "+arg2);
				String firstChild=childNode2.getChildren().get(0).getValue().getName();
				System.out.println("value of first child is "+firstChild);
				
			}
	    	
	    });
	    treeTableView.setShowRoot(false);
	    treeTableView.getColumns().add(leadingColumn);
	    treeTableView.getColumns().add(column);
	    treeTableView.getColumns().add(thirdColumn);
	    sceneRoot.getChildren().add(treeTableView);
	    stage.setScene(scene);
	    stage.show();
	  }

}

class ElementModal{
	
	private static Random rand=new Random();
	private StringProperty name;
	private BooleanProperty visible;
	private BooleanProperty locked;
	private BooleanProperty solo;
	private StringProperty type;

	public String getName(){
		return name.get();
	}
	
	public void setName(String name){
		this.name.set(name);
	}
	
	public String getType(){
		return type.get();
	}
	
	public void setType(String type){
		this.type.set(type);
	}
	
	public boolean isVisible(){
		return visible.get();
	}
	
	public void setVisible(boolean visible){
		this.visible.set(visible);
	}
	
	public ElementModal(){
		name=new SimpleStringProperty();
		visible=new SimpleBooleanProperty();
		type=new SimpleStringProperty();
		String randomString=generateString(rand, "abcdefghijklmnopqrstuvwxyz", 5+rand.nextInt(5));
		setName(randomString);
		setType(getStringBasis(rand.nextInt(3)));
	}
	
	private String getStringBasis(int choice){
		String typeOfElement;
		switch(choice){
		case 0:
			typeOfElement="header";
			break;
		case 1:
			typeOfElement="subheader";
			break;
		case 2:
			typeOfElement="property";
			break;
		default:
			typeOfElement="property";
				
		}
		return typeOfElement;
	}
	
	public BooleanProperty visibleProperty(){
		return visible;
	}
	
	public StringProperty nameProperty(){
		return name;
	}
	
	public StringProperty typeProperty(){
		return type;
	}
	
	public static String generateString(Random rng, String characters, int length){
	    char[] text = new char[length];
	    for (int i = 0; i < length; i++){
	        text[i] = characters.charAt(rng.nextInt(characters.length()));
	    }
	    return new String(text);
	}
}

class CustomCell extends TreeTableCell<ElementModal, String>{

	@Override
	protected void updateItem(String type, boolean empty) {
		super.updateItem(type, empty);
		ElementModal elementModal= getTreeTableRow().getTreeItem().getValue();
		if(type!=null){
			if(type.equalsIgnoreCase("header")){
//				setText("H1");
				setGraphic(getCheckBoxes());
			}else if(type.equalsIgnoreCase("subheader")){
				setText(null);
				setGraphic(getCycleButtons());
			}else if(type.equalsIgnoreCase("property")){
				setText(null);
				setGraphic(new DraggableTextValue());
			}
		}else{
			setText(null);
			setGraphic(null);
		}
	}
	
	private HBox getCheckBoxes(){
		HBox hbox=new HBox();
		CheckBox check1=new CheckBox();
		check1.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				System.out.println("action event fired"+check1.isSelected());
			}
		});
		CheckBox check2=new CheckBox();
		CheckBox check3=new CheckBox();
		hbox.getChildren().addAll(check1,check2,check3);
		return hbox;
	}
	
	private HBox getCycleButtons(){
		HBox hbox=new HBox();
		Button previous=new Button();
		previous.setPrefSize(20, 20);
		Button keyButton=new Button();
		keyButton.setPrefSize(20, 20);
		TimeValueKey key=new TimeValueKey();
		key.translateXProperty().unbind();
		key.setSelected(true);
		key.setScaleX(0.8);
		key.setScaleY(0.8);
		keyButton.setGraphic(key);
		Button next=new Button();
		next.setPrefSize(20, 20);
		hbox.getChildren().addAll(previous,keyButton,next);
		return hbox;
	}
}
