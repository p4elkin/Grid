package org.vaadin.addon.grid.client.event;

import org.vaadin.addon.grid.client.event.handler.PagingContextChangeHandler;

import com.google.gwt.event.shared.GwtEvent;

public class PagingContextChangedEvent extends GwtEvent<PagingContextChangeHandler> {

    public static Type<PagingContextChangeHandler> TYPE = new Type<PagingContextChangeHandler>();
    
    private int totalRows;
    
    private int pageLength;
    
    private int  currentPage;
    
    public PagingContextChangedEvent(int totalRows, int pageLength, int currentPage) {
        this.totalRows = totalRows;
        this.currentPage = currentPage;
        this.pageLength = pageLength;
    }
    
    public int getCurrentPage() {
        return currentPage;
    }
    
    public int getPageLength() {
        return pageLength;
    }
    
    public int getTotalRows() {
        return totalRows;
    }
    
    @Override
    public Type<PagingContextChangeHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(PagingContextChangeHandler handler) {
        handler.onContextChanged(this);
    }
}
