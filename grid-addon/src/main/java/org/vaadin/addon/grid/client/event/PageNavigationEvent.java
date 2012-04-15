package org.vaadin.addon.grid.client.event;

import org.vaadin.addon.grid.client.event.handler.PageNavigationHandler;

import com.google.gwt.event.shared.GwtEvent;

public class PageNavigationEvent extends GwtEvent<PageNavigationHandler> {

    public static Type<PageNavigationHandler> TYPE = new Type<PageNavigationHandler>();

    private int pageNumber;
    
    public PageNavigationEvent(int pageNumber) {
        super();
        this.pageNumber = pageNumber;
    }
    
    public int getPageNumber() {
        return pageNumber;
    }
    
    @Override
    protected void dispatch(PageNavigationHandler handler) {
        handler.navigateToPage(this);
    }
    
    @Override
    public Type<PageNavigationHandler> getAssociatedType() {
        return TYPE;
    }
}
