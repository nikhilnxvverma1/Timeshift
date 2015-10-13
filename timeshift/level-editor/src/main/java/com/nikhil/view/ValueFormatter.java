package com.nikhil.view;

/**
 * Simple functional interface used by views to display a formatted value for a double value.
 * Created by NikhilVerma on 11/10/15.
 */
public interface ValueFormatter {
    String format(double value);
}
