package org.vaadin.addon.grid.client.ui.rowexpansion;

import org.vaadin.addon.grid.client.ui.row.VGridRow;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.RenderSpace;
import com.vaadin.terminal.gwt.client.UIDL;

public class VExpandingGridRow extends VGridRow {

    private final Element expander = DOM.createDiv();
    
    private Widget expanderWidget = null;
    
    private EventBus eventBus;
    
    public VExpandingGridRow() {
        super();
        addDomHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                if (!DOM.isOrHasChild(getElement(), expander)) {
                    expander.getStyle().setHeight(30, Unit.PX);
                    expander.getStyle().setWidth(100, Unit.PCT);
                    getElement().appendChild(expander);
                    client.updateVariable(paintableId, "showExpander", "1", true);
                } else {
                    getElement().removeChild(expander);
                }
            }
        }, DoubleClickEvent.getType());
    }
    
    
    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        super.updateFromUIDL(uidl, client);
        final UIDL expanderUidl = uidl.getChildByTagName("expander");
        if (expanderUidl != null) {
            setExpander(expanderUidl);
        } 
    }
    
    
    private void setExpander(final UIDL expanderUidl) {
        final Paintable expanderPaintable = client.getPaintable(expanderUidl.getChildUIDL(0));
        final Widget newExpaner = (Widget)expanderPaintable; 
        if (expanderWidget != null && expanderWidget != newExpaner) {
            remove(expanderWidget);
            client.unregisterPaintable((Paintable)expanderWidget);
        }
        this.expanderWidget = newExpaner;
        add(newExpaner, expander);
        expanderPaintable.updateFromUIDL(expanderUidl.getChildUIDL(0), client);
    }


    @Override
    public RenderSpace getAllocatedSpace(Widget child) {
        if (child == expanderWidget) {
            return new RenderSpace(expander.getOffsetWidth(), expander.getOffsetHeight());
        }
        return super.getAllocatedSpace(child);
    }
    
    @Override
    public boolean hasChildComponent(Widget component) {
        return super.hasChildComponent(component) || component == expanderWidget;
    }
    
    public Widget getExpanderWidget() {
        return expanderWidget;
    }
    
    @Override
    protected void add(Widget child, Element container) {
        super.add(child, container);
    }
    
    @Override
    public void add(Widget child) {
        if (!DOM.isOrHasChild(getElement(), expander)) {
            super.add(child);
        } else {
            getElement().insertBefore(child.getElement(), expander);
            adopt(child);
        }
    }


    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }
}
