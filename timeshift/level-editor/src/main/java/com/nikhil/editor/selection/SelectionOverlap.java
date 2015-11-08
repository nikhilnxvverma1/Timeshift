package com.nikhil.editor.selection;

import javafx.geometry.Bounds;

/**
 * Delegate for receiving all the callbacks from {@link SelectionArea} as part
 * of selecting(and deselecting) something
 */
public interface SelectionOverlap {

	/**
	 * Callback by the seleciton rectangle for the delegate to "select" elements that
	 * lie inside the selection area
	 * @param selectionArea the SelectionArea that made this call
	 * @param sceneBounds the bounds in scene coordinates denoting the current selection rect
	 */
	void selectOverlappingItems(SelectionArea selectionArea, Bounds sceneBounds);

	/** Callback by SelectionArea which suggests the delegate to reset the selection
	 * of all elements, as a result of deselection */
	void resetSelection();
}
