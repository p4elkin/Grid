package org.vaadin.addon.grid.header;

import org.vaadin.addon.grid.Grid;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

public interface HeaderComponentFactory {

    Component createComponent(final Grid grid, final Object propertyId);
    
    public static class Default implements HeaderComponentFactory {
        @Override
        public Component createComponent(Grid grid, Object propertyId) {
            return new Label(propertyId.toString());
        }
    }
}
