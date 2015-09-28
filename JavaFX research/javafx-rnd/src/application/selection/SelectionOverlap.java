package application.selection;

import javafx.geometry.Bounds;

public interface SelectionOverlap {

	public void selectOverlappingItems(Bounds sceneBounds);
	public void resetSelection();
}
