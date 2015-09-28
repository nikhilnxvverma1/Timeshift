package com.nikhil.model;

/**
 * Used in allowing visitors traverse the data model.
 * The visitation order is "In-traversal" in cases where children models for a model exist.
 * Every model and controller is supposed to implement this interface.
 * Controllers only facilitate traversal to each model
 * Created by NikhilVerma on 20/08/15.
 */
public interface ModelElement {
    /**
     * Visitation is done in an "In traversal" fashion,
     * therefore implementing classes should always allow
     * visitors to visit themselves first
     * and then have the visitor visit the children.
     */
    public void acceptVisitor(ModelVisitor visitor);
}
