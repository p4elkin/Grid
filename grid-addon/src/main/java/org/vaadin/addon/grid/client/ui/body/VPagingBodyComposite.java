package org.vaadin.addon.grid.client.ui.body;

import org.vaadin.addon.grid.client.event.PageNavigationEvent;
import org.vaadin.addon.grid.client.ui.VGrid;
import org.vaadin.addon.grid.client.ui.css.ComputedStyle;
import org.vaadin.addon.grid.client.ui.widget.VGridBodyScrollPanel;

@SuppressWarnings("serial")
public class VPagingBodyComposite extends VGridBodyComposite<VPagingBody> {
    
    private PaginationBar bar;
    
    public VPagingBodyComposite() {
        super();
        bar = new PaginationBar(getEventBus());
    }
    
    @Override
    protected void onAttach() {
        super.onAttach();
        if (getParent() instanceof VGrid) {
            ((VGrid)getParent()).add(bar);
        }
    }
    
    @Override
    protected void onLoad() {
        super.onLoad();
        getEventBus().addHandler(PageNavigationEvent.TYPE, getBody());
    } 
    
    @Override
    protected void onDetach() {
        super.onDetach();
        if (getParent() instanceof VGrid) {
            ((VGrid)getParent()).remove(bar);
        }
    }
    
    @Override
    public void setHeight(String height) {
        super.setHeight((ComputedStyle.parseInt(height) - bar.getOffsetHeight()) + "px");
    }
    
    @Override
    protected VPagingBody createGridBody() {
        return new VPagingBody();
    }

    @Override
    protected VGridBodyScrollPanel createScrollPanel() {
        return new VGridBodyScrollPanel();
    }

    @Override
    protected VBodyProxy createGridBodyProxy() {
        return new VBodyProxy(this);
    }
}
