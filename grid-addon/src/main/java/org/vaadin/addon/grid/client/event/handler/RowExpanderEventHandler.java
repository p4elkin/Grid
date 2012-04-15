package org.vaadin.addon.grid.client.event.handler;

import org.vaadin.addon.grid.client.event.RowExpanderEvent;

import com.google.gwt.event.shared.EventHandler;

public interface RowExpanderEventHandler extends EventHandler {
    
    void onExpanderChange(final RowExpanderEvent event);
    
}
