package com.nikhil.view.item.delegate;

import javafx.scene.input.MouseEvent;

/**
 * Created by NikhilVerma on 19/09/15.
 */
public interface ItemViewDelegate {
    void mousePressed(MouseEvent mouseEvent);
    void mouseDragged(MouseEvent mouseEvent);
    void mouseReleased(MouseEvent mouseEvent);
    void propertyCurrentlyGettingTweaked();
}
