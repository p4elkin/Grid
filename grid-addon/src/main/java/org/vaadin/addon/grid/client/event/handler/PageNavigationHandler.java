package org.vaadin.addon.grid.client.event.handler;

import org.vaadin.addon.grid.client.event.PageNavigationEvent;

import com.google.gwt.event.shared.EventHandler;

public interface PageNavigationHandler extends EventHandler {
   
   void navigateToPage(PageNavigationEvent event);
}
