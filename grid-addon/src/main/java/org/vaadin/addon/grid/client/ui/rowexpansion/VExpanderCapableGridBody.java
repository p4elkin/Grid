package org.vaadin.addon.grid.client.ui.rowexpansion;

import org.vaadin.addon.grid.client.event.RowExpanderEvent;
import org.vaadin.addon.grid.client.ui.body.VAbstractGridBody;
import org.vaadin.addon.grid.client.ui.row.VGridRow;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.SimpleEventBus;

public class VExpanderCapableGridBody extends VAbstractGridBody {
    
    private EventBus eventBus = new SimpleEventBus();
    
    private HandlerRegistration registration;
    
    public VExpanderCapableGridBody() {
        super();
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        registration = eventBus.addHandler(RowExpanderEvent.TYPE, getScrollPanel());
    }
    
    @Override
    protected void onUnload() {
        super.onUnload();
        registration.removeHandler();
    }
    
    public VExpanderCapableScrollPanel getScrollPanel() {
        return (VExpanderCapableScrollPanel)getScrollPanel();
    }
    
    @Override
    protected void insertRow(VGridRow row, int position) {
        super.insertRow(row, position);
        if (row instanceof VExpandingGridRow) {
            ((VExpandingGridRow)row).setEventBus(eventBus);
        }
    }
    
}
