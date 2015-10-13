package com.nikhil.view.control;

import java.io.IOException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class DraggableTextValue extends HBox{

	private static final String CUSTOM_CONTROL_FXML = "Draggable_text_value.fxml";
	
	private boolean upperLimitExists=false;
	private boolean lowerLimitExists=true;
	private double step=0.5;
	private double upperLimit=100;
	private double lowerLimit=0;
	
	private double value=50;
	private ValueFormatter valueFormatter;	

	//for internal uses 
	private double lastX,lastY;
	private boolean justGotDragged=false;
	
	@FXML private TextField textfield;
	@FXML private Label label;
	
	public DraggableTextValue() {
		super();

		//load the control via fxml
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(CUSTOM_CONTROL_FXML));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);               
        
        try {
            fxmlLoader.load();         
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        textfield.managedProperty().bind(textfield.visibleProperty());
        label.setFocusTraversable(true);
        label.setLabelFor(textfield);
        valueFormatter=Util.getFormatterFor(ValueType.PERCENTAGE);
        
        label.focusedProperty().addListener(new ChangeListener<Boolean>(){
        	@Override
        	public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue){
        		if (newPropertyValue){
        			System.out.println("label on focus");
        			editValueInTextfield();
        		}
        	}
        });
        
        textfield.focusedProperty().addListener(new ChangeListener<Boolean>(){
        	@Override
        	public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue){
        		if (newPropertyValue){
        			System.out.println("Textfield on focus");
        		}else{
        			System.out.println("Textfield out focus");
        			doneEditingValueInTextfield();
        		}
        	}
        });
        
        refreshLabelText();
	}


	private void refreshLabelText() {
		label.setText(valueFormatter.formatWith(value));
	}

	
	private void editValueInTextfield(){
		if(textfield.isFocused()){
			return;
		}
		textfield.setVisible(true);
		textfield.setText(value+"");
		label.setText(valueFormatter.getUnit());
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
	
	@FXML
	private void labelPressed(MouseEvent mousePressEvent){
		lastX=mousePressEvent.getX();
		lastY=mousePressEvent.getY();
	}
	
	@FXML 
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
		refreshLabelText();
		lastX=x;
		lastY=y;
		justGotDragged=true;
	}
	
	@FXML 
	private void labelReleased(MouseEvent releaseEvent){
		if(!justGotDragged){
			editValueInTextfield();
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
