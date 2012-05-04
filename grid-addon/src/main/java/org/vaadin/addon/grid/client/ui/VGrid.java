package org.vaadin.addon.grid.client.ui;

import java.util.Set;

import org.vaadin.addon.grid.client.proxy.VGridProxy;
import org.vaadin.addon.grid.client.rpc.ClientSideHandler;
import org.vaadin.addon.grid.client.ui.body.VGridBodyComposite;
import org.vaadin.addon.grid.client.ui.header.VGridHeader;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Container;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.RenderSpace;
import com.vaadin.terminal.gwt.client.UIDL;

@SuppressWarnings("serial")
public class VGrid extends FlowPanel implements Paintable, ClientSideHandler, Container {

	public static final String CLASSNAME = "v-grid";

	private VGridBodyComposite<?> body;
	
	private VGridHeader header;
	
	private VGridProxy proxy = new VGridProxy(this);
	
	protected String paintableId;

	protected String width = ""; 
	
	protected String height = ""; 
	        
	protected ApplicationConnection client;
	
	public VGrid() {
	    super();
		setStyleName(CLASSNAME);
	}
	
	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
	    proxy.update(this, uidl, client);
		this.client = client;
		paintableId = uidl.getId();
	    updateBody(uidl);
		updateHeader(uidl);
	}

	private void updateHeader(final UIDL uidl) {
	    final UIDL headerUidl = uidl.getChildByTagName("gridHeader");
        if (headerUidl != null) {
            final Paintable newHeaderPaintable = client.getPaintable(headerUidl.getChildUIDL(0));
            if (newHeaderPaintable instanceof VGridHeader) {
                final VGridHeader newHeader = (VGridHeader)newHeaderPaintable;
                if (!newHeader.equals(header)) {
                    if (header != null) {
                        remove(header);
                    }
                    this.header = (VGridHeader)newHeaderPaintable;
                    insert((VGridHeader)newHeaderPaintable, 0);
                }
                if (body != null) {
                    this.header.setColumnInfo(body.getColumnModel());
                    body.getColumnModel().setHeader(header);
                }
                newHeaderPaintable.updateFromUIDL(headerUidl.getChildUIDL(0), client);
            }
        }
    }

    private void updateBody(final UIDL uidl) {
        final UIDL bodyUidl = uidl.getChildByTagName("gridBody");
        if (bodyUidl != null) {
            final Paintable newBodyPaintable = client.getPaintable(bodyUidl.getChildUIDL(0));
            if (newBodyPaintable instanceof VGridBodyComposite) {
                final VGridBodyComposite<?> newBody = (VGridBodyComposite<?>)newBodyPaintable;
                if (!newBody.equals(body)) {
                    if (body != null) {
                        remove(body);
                    }
                    this.body = (VGridBodyComposite<?>)newBody;
                    add(body);
                }
                newBody.updateFromUIDL(bodyUidl.getChildUIDL(0), client);
            }
        }        
    }

    @Override
	public void setWidth(String width) {
        super.setWidth(width);
        if (this.width != width) {
            this.width = width;
            if (header != null) {
                 //int viewportWidth = getOffsetWidth()/* - scrollPanel.getDecorationsWidth()*/; 
                 //scrollPanel.setWidth((viewportWidth > 0 ? viewportWidth : 0) + "px");
             }
        }
	}
	
	@Override
	public void setHeight(String height) {
        super.setHeight(height);
	    if (this.height != height) {
	        if (header != null) {
	            //int viewportHeight = getOffsetHeight() - scrollPanel.getDecorationsHeight();
	            //viewportHeight -= header.getOffsetHeight();
	            //scrollPanel.setHeight((viewportHeight > 0 ? viewportHeight : 0) + "px");
	        }
	    }
	}

    @Override
    public boolean initWidget(Object[] params) {
        return false;
    }

    @Override
    public void handleCallFromServer(String method, Object[] params) {
        System.out.println("Method invoke attempt: " + method + " with " + params.length + " params");
    }

    @Override
    public void replaceChildComponent(Widget oldComponent, Widget newComponent) {
        
    }

    @Override
    public boolean hasChildComponent(Widget component) {
        return component == header || component == body;
    }

    @Override
    public void updateCaption(Paintable component, UIDL uidl) {}

    @Override
    public boolean requestLayout(Set<Paintable> children) {
        return false;
    }

    @Override
    public RenderSpace getAllocatedSpace(Widget child) {
        int width = 0;
        int height = 0;
        if (child == header) {
            width = getOffsetWidth() - header.getDecorationsWidth();
            height = 25;
        } else if (child == body) {
            width = getOffsetWidth();
            height = getOffsetHeight() - 25;
        }
        return new RenderSpace(width, height);
    }

    
}
