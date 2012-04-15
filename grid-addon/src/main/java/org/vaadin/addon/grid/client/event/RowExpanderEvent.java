package org.vaadin.addon.grid.client.event;

import org.vaadin.addon.grid.client.event.handler.RowExpanderEventHandler;
import org.vaadin.addon.grid.client.ui.rowexpansion.VExpandingGridRow;

import com.google.gwt.event.shared.GwtEvent;

public class RowExpanderEvent extends GwtEvent<RowExpanderEventHandler> {

    public static Type<RowExpanderEventHandler> TYPE = new Type<RowExpanderEventHandler>();
    
    private VExpandingGridRow row;
    
    private boolean isExpanded;
    
    public RowExpanderEvent(final VExpandingGridRow row, boolean isExpanded) {
        this.row = row;
        this.isExpanded = isExpanded;
    }
    
    @Override
    public GwtEvent.Type<RowExpanderEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(RowExpanderEventHandler handler) {
        handler.onExpanderChange(this);
    }
    
    public boolean isExpanded() {
        return isExpanded;
    }
    
    public VExpandingGridRow getRow() {
        return row;
    }

}
