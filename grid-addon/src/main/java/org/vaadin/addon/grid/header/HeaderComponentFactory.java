package org.vaadin.addon.grid.header;

import org.vaadin.addon.grid.AbstractGrid;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

public interface HeaderComponentFactory {

    Component createComponent(final AbstractGrid<?> grid, final Object propertyId);
    
    public static class Default implements HeaderComponentFactory {
        @Override
        public Component createComponent(AbstractGrid<?> grid, Object propertyId) {
            return new Label(propertyId.toString());
        }
    }
}
