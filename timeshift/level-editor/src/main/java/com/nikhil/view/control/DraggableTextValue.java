package com.nikhil.view.control;

import com.nikhil.view.ValueFormatter;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class DraggableTextValue extends HBox{

	private static final String LABEL_STYLE="-fx-text-fill:rgb(0, 0, 255);";

	private DraggableTextValueDelegate delegate;
	private boolean upperLimitExists=false;
	private boolean lowerLimitExists=true;
	private double step=0.5;
	private double upperLimit=100;
	private double lowerLimit=0;
	
	private double value=50;//TODO make this an Observable Value
	private ValueFormatter valueFormatter;
	private String postfix;

	//for internal uses 
	private double lastX,lastY;
	private boolean justGotDragged=false;
	
	private TextField textfield;
	private Label label;
	
	public DraggableTextValue(DraggableTextValueDelegate delegate) {
		super();
		this.delegate=delegate;
		valueFormatter=v-> String.format("%.2f",v);
		initView();
	}

	private void initView() {
		this.setAlignment(Pos.CENTER);
		textfield=new TextField();
		textfield.setVisible(false);
		textfield.textProperty().addListener((observable, oldValue, newValue) -> {
			int letterWidth = 13;// number is just a guess

			int letters = textfield.getText().length();
			if(letters<3){
				letters=3;//at least 3 letters worth of space should be there
			}
			textfield.setPrefWidth(letters * letterWidth);
        });

		label=new Label();
		label.setStyle(LABEL_STYLE);
		label.setCursor(Cursor.W_RESIZE);
		label.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
			labelPressed(e);
		});
		label.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
			labelDragged(e);
		});
		label.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
			labelReleased(e);
		});
		this.getChildren().addAll(textfield, label);

		textfield.managedProperty().bind(textfield.visibleProperty());
		label.setFocusTraversable(true);
		label.setLabelFor(textfield);
		label.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue){
                editValueInTextfield();
            }
        });
		textfield.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (!newPropertyValue){
				doneEditingValueInTextfield();
				delegate.valueFinishedChanging(this, value);
			}
        });
		refreshLabelText();
	}


	private void refreshLabelText() {
		label.setText(valueFormatter.format(value)+(postfix!=null?postfix:""));
	}

	public String getPostfix() {
		return postfix;
	}

	public void setPostfix(String postfix) {
		this.postfix = postfix;
	}

	private void editValueInTextfield(){
		if(textfield.isFocused()){
			return;
		}
		textfield.setVisible(true);
		textfield.setText(value+"");
		label.setText(postfix);
		textfield.requestFocus();
		label.setFocusTraversable(false);
	}
	
	private void doneEditingValueInTextfield(){
		value=Double.parseDouble(textfield.getText());
		textfield.setVisible(false);
//		label.setText(textfield.getText());
		refreshLabelText();
		label.setFocusTraversable(true);
	}

	private void labelPressed(MouseEvent mousePressEvent){
		lastX=mousePressEvent.getX();
		lastY=mousePressEvent.getY();
	}

	private void labelDragged(MouseEvent dragEvent){
		double x=dragEvent.getX();
		double y=dragEvent.getY();
		
		if(x>=lastX){
			if((upperLimitExists)&&(value+step>upperLimit)){
				value=upperLimit;//never overshoot upper limit ,just in case 'step' is large enough
			}else{
				value+=step;
			}
		}else{
			if((lowerLimitExists)&&(value-step<lowerLimit)){
				value=lowerLimit;//never undershoot lower limit ,just in case 'step' is large enough
			}else{
				value-=step;
				
			}
		}
		delegate.valueBeingDragged(this, value);
		refreshLabelText();
		lastX=x;
		lastY=y;
		justGotDragged=true;
	}
	
	@FXML 
	private void labelReleased(MouseEvent releaseEvent){
		if(!justGotDragged){
			editValueInTextfield();
		}else{
			delegate.valueFinishedChanging(this,value);
		}
		justGotDragged=false;
	}


	public boolean isUpperLimitExists() {
		return upperLimitExists;
	}


	public void setUpperLimitExists(boolean upperLimitExists) {
		this.upperLimitExists = upperLimitExists;
	}


	public boolean isLowerLimitExists() {
		return lowerLimitExists;
	}


	public void setLowerLimitExists(boolean lowerLimitExists) {
		this.lowerLimitExists = lowerLimitExists;
	}


	public double getUpperLimit() {
		return upperLimit;
	}


	public void setUpperLimit(double upperLimit) {
		this.upperLimit = upperLimit;
	}


	public double getLowerLimit() {
		return lowerLimit;
	}


	public void setLowerLimit(double lowerLimit) {
		this.lowerLimit = lowerLimit;
	}


	public double getValue() {
		return value;
	}


	public void setValue(double value) {
		this.value = value;
		refreshLabelText();
	}


	public ValueFormatter getValueFormatter() {
		return valueFormatter;
	}


	public void setValueFormatter(ValueFormatter valueFormatter) {
		this.valueFormatter = valueFormatter;
	}

	
}
