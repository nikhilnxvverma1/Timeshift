package application.element;

import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Skin;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import application.ruler.Ruler;
import application.timepane.KeyValue;

public class ItemTableExample extends Application implements EventHandler<WindowEvent>,InvalidationListener{

	private static final int HEIGHT = 600-300;
	private static final int WIDTH = 1000;

	private TreeTableView<ItemMetadata> treeTableView ;
	private Scene mainScene;
	public static void main(String args[]){
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {

//		ScrollPane scrollPane = new ScrollPane();
		Group scrollPane=new Group();
		final Scene scene = new Scene(scrollPane, WIDTH, HEIGHT);
		stage.addEventHandler(WindowEvent.WINDOW_SHOWING, this);
		stage.addEventHandler(WindowEvent.WINDOW_SHOWN, this);
		TreeItem<ItemMetadata> root=new TreeItem<>(new ItemMetadata(ItemType.HEADER, "Root")) ;
		root.getChildren().add(buildSampleCircleTree());
		
		TreeTableColumn<ItemMetadata, NameComponent> nameColumn = buildNameColumn();
		
		TreeTableColumn<ItemMetadata, ValueComponent> valueColumn = buildValueColumn();
		
		TreeTableColumn<ItemMetadata, OptionComponent> optionColumn = buildOptionColumn();
		
		TreeTableColumn<ItemMetadata, OptionComponent> keyColumn = buildKeyColumn();
		
		Ruler ruler = new Ruler(30,keyColumn.getPrefWidth());
		ruler.setLayoutX(0);
		keyColumn.setGraphic(ruler);
		
		keyColumn.setSortable(false);
		keyColumn.setSortNode(null);
		
		keyColumn.widthProperty().addListener(new ChangeListener<Number>(){

			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				System.out.println("key column width changed to +"+newValue);
				
			}
			
		});
		
//		TreeTableView<ItemMetadata> treeTableView = new TreeTableView<>(root);
		treeTableView = new TreeTableView<>(root);
		treeTableView.setPrefSize(WIDTH, HEIGHT);
		treeTableView.getColumns().addAll(nameColumn,valueColumn,optionColumn,keyColumn);
		treeTableView.setShowRoot(false);
		treeTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		treeTableView.setSelectionModel(new NullTableViewSelectionModel(treeTableView));
		treeTableView.getColumns().addListener(new ListChangeListener<TreeTableColumn>() {
	        @Override
	        public void onChanged(Change change) {
	          change.next();
	          if(change.wasReplaced()) {
	        	  treeTableView.getColumns().clear();
	        	  treeTableView.getColumns().addAll(nameColumn,valueColumn,optionColumn,keyColumn);
	          }
	        }
	    });		
		treeTableView.skinProperty().addListener(this);
		treeTableView.getChildrenUnmodifiable().addListener(this);
	    scrollPane.getChildren().add(treeTableView);	
//		scrollPane.setPrefSize(WIDTH, HEIGHT);
//		scrollPane.setContent(treeTableView);
	    mainScene=scene;
	    stage.setScene(scene);
	    stage.show();
		  
	}

	private TreeTableColumn<ItemMetadata, OptionComponent> buildKeyColumn() {
		TreeTableColumn<ItemMetadata, OptionComponent> keyColumn = new TreeTableColumn<>();
		keyColumn.setPrefWidth(600);
		keyColumn.setCellFactory(new Callback<TreeTableColumn<ItemMetadata,OptionComponent>,TreeTableCell<ItemMetadata,OptionComponent>>(){
			@Override
			public TreeTableCell<ItemMetadata, OptionComponent> call(
					TreeTableColumn<ItemMetadata, OptionComponent> nameColumn) {
				return new KeyTableCell();
			}
		});
		
		keyColumn.setCellValueFactory(new Callback<CellDataFeatures<ItemMetadata,OptionComponent>,ObservableValue<OptionComponent>>(){

			@Override
			public ObservableValue<OptionComponent> call(
					CellDataFeatures<ItemMetadata, OptionComponent> param) {
				return new SimpleObjectProperty<OptionComponent>(param.getValue().getValue().getOption());
			}
			
		});
		keyColumn.setText(null);
		return keyColumn;
	}

	private TreeTableColumn<ItemMetadata, OptionComponent> buildOptionColumn() {
		TreeTableColumn<ItemMetadata, OptionComponent> optionColumn = new TreeTableColumn<>("Option");
		optionColumn.setPrefWidth(100);
		optionColumn.setCellFactory(new Callback<TreeTableColumn<ItemMetadata,OptionComponent>,TreeTableCell<ItemMetadata,OptionComponent>>(){
			@Override
			public TreeTableCell<ItemMetadata, OptionComponent> call(
					TreeTableColumn<ItemMetadata, OptionComponent> nameColumn) {
				return new ItemOptionTableCell();
			}
		});
		
		optionColumn.setCellValueFactory(new Callback<CellDataFeatures<ItemMetadata,OptionComponent>,ObservableValue<OptionComponent>>(){

			@Override
			public ObservableValue<OptionComponent> call(
					CellDataFeatures<ItemMetadata, OptionComponent> param) {
				return new SimpleObjectProperty<OptionComponent>(param.getValue().getValue().getOption());
			}
			
		});
		return optionColumn;
	}

	private TreeTableColumn<ItemMetadata, ValueComponent> buildValueColumn() {
		TreeTableColumn<ItemMetadata, ValueComponent> valueColumn = new TreeTableColumn<>("Value");
		valueColumn.setPrefWidth(100);
		//TODO: fix lambda expressions in eclipse
//		valueColumn.setCellFactory((TreeTableColumn<ItemMetadata,ValueComponent> valueC)->{new ItemValueTableCell();});
//		
//		valueColumn.setCellValueFactory(
//				(TreeTableColumn.CellDataFeatures<ItemMetadata, ValueComponent> param)->
//		{new SimpleObjectProperty<ValueComponent>(param.getValue().getValue().getValue());}
//		);
		
		valueColumn.setCellFactory(new Callback<TreeTableColumn<ItemMetadata,ValueComponent>,TreeTableCell<ItemMetadata,ValueComponent>>(){
			@Override
			public TreeTableCell<ItemMetadata, ValueComponent> call(
					TreeTableColumn<ItemMetadata, ValueComponent> valueColumn) {
				return new ItemValueTableCell();
			}
		});
		
		valueColumn.setCellValueFactory(new Callback<CellDataFeatures<ItemMetadata,ValueComponent>,ObservableValue<ValueComponent>>(){

			@Override
			public ObservableValue<ValueComponent> call(
					CellDataFeatures<ItemMetadata, ValueComponent> param) {
				return new SimpleObjectProperty<ValueComponent>(param.getValue().getValue().getValue());
			}
			
		});
		return valueColumn;
	}

	private TreeTableColumn<ItemMetadata, NameComponent> buildNameColumn() {
		TreeTableColumn<ItemMetadata, NameComponent> nameColumn = new TreeTableColumn<>("Name");
		nameColumn.setPrefWidth(200);
		nameColumn.setCellFactory(new Callback<TreeTableColumn<ItemMetadata,NameComponent>,TreeTableCell<ItemMetadata,NameComponent>>(){
			@Override
			public TreeTableCell<ItemMetadata, NameComponent> call(
					TreeTableColumn<ItemMetadata, NameComponent> nameColumn) {
				return new ItemNameTableCell();
			}
		});
		
		nameColumn.setCellValueFactory(new Callback<CellDataFeatures<ItemMetadata,NameComponent>,ObservableValue<NameComponent>>(){

			@Override
			public ObservableValue<NameComponent> call(
					CellDataFeatures<ItemMetadata, NameComponent> param) {
				return new SimpleObjectProperty<NameComponent>(param.getValue().getValue().getName());
			}
			
		});
		return nameColumn;
	}

	private TreeItem<ItemMetadata> buildSampleCircleTree() {
		TreeItem<ItemMetadata> root = new TreeItem<>(new ItemMetadata(ItemType.HEADER, "Circle"));

		TreeItem<ItemMetadata> transform= new TreeItem<>(new ItemMetadata(ItemType.SUB_HEADER, "Transform"));
		TreeItem<ItemMetadata> scale= new TreeItem<>(new ItemMetadata(ItemType.PROPERTY, "Scale",new KeyValue(100)));
		TreeItem<ItemMetadata> rotation= new TreeItem<>(new ItemMetadata(ItemType.PROPERTY, "Rotation",new KeyValue(130)));
		TreeItem<ItemMetadata> position= new TreeItem<>(new ItemMetadata(ItemType.PROPERTY, "Position",new KeyValue(421,234)));
		TreeItem<ItemMetadata> anchorPoint= new TreeItem<>(new ItemMetadata(ItemType.PROPERTY, "AnchorPoint",new KeyValue(0,0)));
		transform.getChildren().addAll(scale,rotation,position,anchorPoint);
		
		TreeItem<ItemMetadata> special= new TreeItem<>(new ItemMetadata(ItemType.SUB_HEADER, "Special"));
		TreeItem<ItemMetadata> innerRadius= new TreeItem<>(new ItemMetadata(ItemType.PROPERTY, "Inner Radus",new KeyValue(10)));
		TreeItem<ItemMetadata> outerRadius= new TreeItem<>(new ItemMetadata(ItemType.PROPERTY, "Outer Radius",new KeyValue(30)));
		TreeItem<ItemMetadata> startingAngle= new TreeItem<>(new ItemMetadata(ItemType.PROPERTY, "Starting Angle",new KeyValue(0)));
		TreeItem<ItemMetadata> endingAngle= new TreeItem<>(new ItemMetadata(ItemType.PROPERTY, "Ending Angle",new KeyValue(45)));
		special.getChildren().addAll(innerRadius,outerRadius,startingAngle,endingAngle);
		
		root.getChildren().addAll(transform,special);
		
		return root;
	}
	
	@Override
	public void handle(WindowEvent event) {
		if(event.getEventType()==WindowEvent.WINDOW_SHOWING){
			System.out.println("window event showing");
		}else if(event.getEventType()==WindowEvent.WINDOW_SHOWN){
			System.out.println("window event shown");
		}
	}
	
	@Override
	public void invalidated(Observable observable) {

		String simpleName = observable.getClass().getSimpleName();
		System.out.println("skin property changing "+simpleName);
		
	}
}
