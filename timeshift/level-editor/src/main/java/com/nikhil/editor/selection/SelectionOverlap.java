package com.nikhil.editor.selection;

import javafx.geometry.Bounds;

public interface SelectionOverlap {

	public void selectOverlappingItems(SelectionArea selectionArea, Bounds sceneBounds);
	public void resetSelection();
}
