package application.element;

import java.util.Random;

import application.SelectionBar;
import application.ruler.Ruler;
import application.timepane.KeyValue;
import application.timepane.TimeValuePane;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class ItemTableSplitPaneExample extends Application{

	private static final double HEIGHT = 400;
	private static final double WIDTH = 1000;
	
	private static final double TABLE_HEIGHT=0.8*HEIGHT;

	private TreeTableView<ItemMetadata> treeTableView ;
	private SplitPane splitPane;
	private Random random=new Random();
	
	public static void main (String []args){
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		splitPane=new SplitPane();
		final Scene scene = new Scene(splitPane, WIDTH, HEIGHT);
		
		//left side
		VBox leftVbox=new VBox();
		HBox timeControlsContainer=new HBox();
		timeControlsContainer.setAlignment(Pos.CENTER_LEFT);
		timeControlsContainer.getChildren().addAll(new TextField(),buildControlToolbar());		
		leftVbox.getChildren().add(timeControlsContainer);
		
		buildTreeTableView();		
		leftVbox.getChildren().add(treeTableView);

		splitPane.getItems().add(leftVbox);
		
		
		//right side
		VBox rightVbox=new VBox();
		Ruler ruler=new Ruler(10,30,WIDTH/2);
		ruler.prefHeightProperty().bind(timeControlsContainer.heightProperty());
		rightVbox.getChildren().add(ruler);
		
		SelectionBar selectionBar=new SelectionBar(WIDTH/2);
		rightVbox.getChildren().add(selectionBar);
		
		ScrollPane keyScroll=new ScrollPane();
		keyScroll.setPrefHeight(TABLE_HEIGHT);
		VBox keyList=new VBox();
		keyScroll.setContent(keyList);		
		for(int i=0;i<11;i++){
			keyList.getChildren().add(buildTimePane(30	,WIDTH/2));
		}
		rightVbox.getChildren().add(keyScroll);
		
		splitPane.getItems().add(rightVbox);
		
		stage.setScene(scene);
		stage.show();
	}

	private void buildTreeTableView() {
		//model
		TreeItem<ItemMetadata> root=new TreeItem<>(new ItemMetadata(ItemType.HEADER, "Root")) ;
		root.getChildren().add(buildSampleCircleTree());

		//columns
		TreeTableColumn<ItemMetadata, NameComponent> nameColumn = buildNameColumn();
		TreeTableColumn<ItemMetadata, ValueComponent> valueColumn = buildValueColumn();
		TreeTableColumn<ItemMetadata, OptionComponent> optionColumn = buildOptionColumn();
//		TreeTableColumn<ItemMetadata, OptionComponent> keyColumn = buildKeyColumn();
		
		treeTableView = new TreeTableView<>(root);
		treeTableView.setPrefSize(WIDTH/2, TABLE_HEIGHT);
		treeTableView.getColumns().addAll(nameColumn,valueColumn,optionColumn);
		treeTableView.setShowRoot(false);
//		treeTableView.setSelectionModel(new NullTableViewSelectionModel(treeTableView));
		treeTableView.getColumns().addListener((ListChangeListener<TreeTableColumn>) change -> {
		  change.next();
		  if(change.wasReplaced()) {
			  treeTableView.getColumns().clear();
			  treeTableView.getColumns().addAll(nameColumn,valueColumn,optionColumn);
		  }
		});
		nameColumn.prefWidthProperty().bind(treeTableView.widthProperty().multiply(0.50));
		valueColumn.prefWidthProperty().bind(treeTableView.widthProperty().multiply(0.25));
		optionColumn.prefWidthProperty().bind(treeTableView.widthProperty().multiply(0.23));
		//0.2 left so that horizontal scroll bar doesnt appear
		treeTableView.setRowFactory(new Callback<TreeTableView<ItemMetadata>,TreeTableRow<ItemMetadata>>(){

			@Override
			public TreeTableRow<ItemMetadata> call(
					TreeTableView<ItemMetadata> param) {
				TreeTableRow<ItemMetadata> slightlyModifiedRow=new AdjacentTreeTableRow();
				return slightlyModifiedRow;
			}
			
		});
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
		
		root.expandedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				System.out.println("tree is expanded? "+newValue+" ");
			}
		});
		
		return root;
	}

	private ToolBar buildControlToolbar(){
		ToolBar toolbar=new ToolBar();
		Button gotoStart=new Button();
		gotoStart.setText("<||");
		Button play=new Button();
		play.setText(">");
		Button gotoEnd=new Button();
		gotoEnd.setText("||>");
		Button pause=new Button();
		pause.setText("||");
		Button stop=new Button();
		stop.setText("[]");
		toolbar.getItems().addAll(gotoStart,play,gotoEnd,pause,stop);
		return toolbar;
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
