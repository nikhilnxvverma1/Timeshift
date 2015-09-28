package application.element;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import application.timepane.KeyValue;

enum ItemType{
	HEADER,
	SUB_HEADER,
	PROPERTY
}

public class ItemMetadata {

	private ItemType itemType;
	private BooleanProperty keyframed;//only valid for "property" item type
	
	private NameComponent name;
	private ValueComponent value;
	private OptionComponent option;
	
	public ItemMetadata(ItemType itemType,String name){
		this(itemType,name,null);
	}
	
	public ItemMetadata(ItemType itemType,String name,KeyValue keyValue) {
		super();
		this.itemType = itemType;
		this.keyframed=new SimpleBooleanProperty();
		this.name=new NameComponent(name,this);
		this.value=new ValueComponent(keyValue,this);
		this.option=new OptionComponent(this);
	}
	public BooleanProperty keyframeProperty() {
		return keyframed;
	}
	public boolean isKeyframed(){
		return keyframed.get();
	}
	public void setKeyframed(boolean keyframed) {
		this.keyframed.set(keyframed);
	}
	public ItemType getItemType() {
		return itemType;
	}
	public NameComponent getName() {
		return name;
	}
	public ValueComponent getValue() {
		return value;
	}
	public OptionComponent getOption() {
		return option;
	}
	
}

class NameComponent{
	private String name;
	private ItemMetadata parent;
	public NameComponent(String name, ItemMetadata parent) {
		super();
		this.name = name;
		this.parent = parent;
	}
	public String getName() {
		return name;
	}
	public ItemMetadata getParent() {
		return parent;
	}
	 
}

class ValueComponent{
	private KeyValue keyValue;
	private ItemMetadata parent;
	public ValueComponent(KeyValue keyValue, ItemMetadata parent){
		this.keyValue=keyValue;
		this.parent=parent;
	}
	public KeyValue getKeyValue() {
		return keyValue;
	}
	public ItemMetadata getParent() {
		return parent;
	}
	
}

class OptionComponent{
	private boolean visible=true;
	private boolean locked=false;
	private boolean solo=false;
	
	private ItemMetadata parent;
	
	public OptionComponent(ItemMetadata parent) {
		super();
		this.parent = parent;
	}
	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	public boolean isLocked() {
		return locked;
	}
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	public boolean isSolo() {
		return solo;
	}
	public void setSolo(boolean solo) {
		this.solo = solo;
	}
	public ItemMetadata getParent() {
		return parent;
	}
}