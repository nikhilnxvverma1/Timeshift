package com.nikhil.view.custom;

import com.nikhil.view.ValueFormatter;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

/**
 * Custom draggable text field whose value can be changed by dragging the text left and right.
 * By default ,lower and upper limits don't exist ,but they can be set.
 */
public class DraggableTextValue extends HBox{ //TODO textfield listeners may leak memory

	private static final String LABEL_STYLE="-fx-text-fill:rgb(0, 0, 255);";

	private DraggableTextValueDelegate delegate;
	private boolean upperLimitExists=false;
	private boolean lowerLimitExists=false;
	private double step=0.5;
	private double upperLimit=100;
	private double lowerLimit=0;
	
	private double value=50;
	private ValueFormatter valueFormatter;
	private String postfix;

	//=============================================================================================
	//For internal use
	//=============================================================================================

	private double lastX;
	private boolean justGotDragged=false;
	private double valueBeforeChange;
	
	private TextField textfield;
	private Label label;

	/**
	 * creates a new DraggableTextValue with an associated delegate
	 * @param delegate the listener for each change event. Cannot be null
	 */
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
		label.focusedProperty().addListener((arg0, wasFocused, nowFocused) -> {
            if (nowFocused && delegate.isEnabled()){
                editValueInTextfield();
            }
        });
		textfield.focusedProperty().addListener((arg0, wasFocused, nowFocused) -> {
            if (!nowFocused){
				doneEditingValueInTextfield();
			}
        });
		refreshLabelText();
	}

	public double getStep() {
		return step;
	}

	public void setStep(double step) {
		this.step = step;
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
		valueBeforeChange=value;
	}
	
	private void doneEditingValueInTextfield(){
		try {
			value=Double.parseDouble(textfield.getText());
		} catch (NumberFormatException e) {
			//revert back to original and return to disregard invalid input
			textfield.setText(value+"");
			textfield.setVisible(false);
			refreshLabelText();
			label.setFocusTraversable(true);
			return;
		}
		textfield.setVisible(false);
		refreshLabelText();
		label.setFocusTraversable(true);
		delegate.valueFinishedChanging(this, valueBeforeChange, value, false);

	}

	private void labelPressed(MouseEvent mousePressEvent){
		if(!delegate.isEnabled()){
			return;
		}
		lastX=mousePressEvent.getX();
		valueBeforeChange=value;
	}

	private void labelDragged(MouseEvent dragEvent){
		if(!delegate.isEnabled()){
			return;
		}
		double x=dragEvent.getX();
		double oldValue=value;
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
		delegate.valueBeingDragged(this,valueBeforeChange,oldValue, value);
		refreshLabelText();
		lastX=x;
		justGotDragged=true;
	}
	
	@FXML 
	private void labelReleased(MouseEvent releaseEvent){
		if(!delegate.isEnabled()){
			return;
		}
		if(!justGotDragged){
			editValueInTextfield();
		}else{
			delegate.valueFinishedChanging(this,valueBeforeChange , value, true);
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
