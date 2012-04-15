package org.vaadin.addon.grid.client.event.handler;

import org.vaadin.addon.grid.client.event.PagingContextChangedEvent;

import com.google.gwt.event.shared.EventHandler;

public interface PagingContextChangeHandler extends EventHandler {

    void onContextChanged(PagingContextChangedEvent event);
    
}
