package org.vaadin.addon.grid.client.ui.body;

import java.util.Set;

import org.vaadin.addon.grid.client.rpc.ClientSideHandler;
import org.vaadin.addon.grid.client.ui.VColumnModel;
import org.vaadin.addon.grid.client.ui.widget.VGridBodyScrollPanel;
import org.vaadin.csstools.client.ComputedStyle;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Container;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.RenderSpace;
import com.vaadin.terminal.gwt.client.UIDL;

@SuppressWarnings("serial")
public abstract class VGridBodyComposite<T extends VAbstractGridBody> extends Composite implements Container,
        ClientSideHandler {

    private VColumnModel columnModel = new VColumnModel();

    private EventBus eventBus = new SimpleEventBus();

    private T body = createGridBody();
    
    private VGridBodyScrollPanel scrollPanel = createScrollPanel();
    
    private VBodyProxy proxy = createGridBodyProxy();;

    protected ApplicationConnection client;

    protected String paintableId;

    private ComputedStyle style;

    public VGridBodyComposite() {
        super();
        scrollPanel.insert(body, 0);
        initWidget(scrollPanel);
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        this.client = client;
        this.paintableId = uidl.getId();
        proxy.update(this, uidl, client);
        body.renderRows(uidl);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        style = new ComputedStyle(body.getElement());
    }

    @Override
    public boolean initWidget(Object[] params) {
        body.initialize(params);
        return false;
    }

    @Override
    public void setHeight(String height) {
        super.setHeight(height);
        body.setHeight(height);
    }

    @Override
    public void setWidth(String width) {
        super.setWidth(width);
        body.setWidth(width);
        if (isAttached()) {
            super.setWidth(width);
            int formerWidth = style.getIntProperty("width");
            if (formerWidth != columnModel.getViewPortWidth()) {
                columnModel.setViewPortWidth(style.getIntProperty("width"));
            }
        }
    }

    @Override
    public void handleCallFromServer(String method, Object[] params) {

    }

    @Override
    public void replaceChildComponent(Widget oldComponent, Widget newComponent) {/* NOP */
    }

    @Override
    public boolean hasChildComponent(Widget component) {
        return body.getWidgetIndex(component) >= 0;
    }

    @Override
    public void updateCaption(Paintable component, UIDL uidl) {/* NOP */
    }

    @Override
    public boolean requestLayout(Set<Paintable> children) {
        return false;
    }

    @Override
    public RenderSpace getAllocatedSpace(Widget child) {
        return new RenderSpace();
    }

    public VColumnModel getColumnModel() {
        return columnModel;
    }
    
    protected abstract VGridBodyScrollPanel createScrollPanel();

    protected abstract T createGridBody();

    protected abstract VBodyProxy createGridBodyProxy();

    public Integer getFirstVisibleRow() {
        return (int) Math.ceil((double) scrollPanel.getClientHeight() / body.getRowHeight());
    }

    public final T getBody() {
        return body;
    }

    public final VBodyProxy getProxy() {
        return proxy;
    }

    public final VGridBodyScrollPanel getScrollPanel() {
        return scrollPanel;
    }
    
    public final ApplicationConnection getAppConnection() {
        return client;
    }

    public final VColumnModel getColumnInfo() {
        return columnModel;
    }

    protected EventBus getEventBus() {
        return eventBus;
    }
}
