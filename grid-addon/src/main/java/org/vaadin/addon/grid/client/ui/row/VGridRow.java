package org.vaadin.addon.grid.client.ui.row;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.vaadin.addon.grid.client.ui.VColumnModel;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.DomEvent.Type;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.impl.FocusImpl;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Container;
import com.vaadin.terminal.gwt.client.EventId;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.RenderSpace;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.ui.LayoutClickEventHandler;

@SuppressWarnings("unused")
public class VGridRow extends ComplexPanel implements Container, ClickHandler {
    
    private static final String CLASS_NAME = "v-grid-row";
    
    private Map<String, VGridCell> columnKeysToCell = new LinkedHashMap<String, VGridCell>();
   
    private Element container = DOM.createDiv();
    
    private VColumnModel sizeInfo;
    
    protected ApplicationConnection client;
    
    protected String paintableId;
    
    private int index = 0;
    
    public VGridRow() {
        super();
        setElement(container);
        addStyleName(CLASS_NAME);
        addStyleName("v-row-width-control");
        DOM.sinkEvents(getElement(), DOM.getEventsSunk(getElement()) | Event.MOUSEEVENTS);
        addDomHandler(this, ClickEvent.getType());
    }
    
    public int getIndex() {
        return index;
    }
    
    public void setSizeInfo(final VColumnModel sizeInfo) {
        this.sizeInfo = sizeInfo;
    }
    
    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        this.client = client;
        this.paintableId = uidl.getId();
     
        if (client.updateComponent(this, uidl, false)) {
            return;
        }
        
        if (!Util.isCached(uidl)) {
            paintCells(uidl);
        }
    }

    private void paintCells(final UIDL uidl) {
        final UIDL cellData = uidl.getChildByTagName("cells");
        if (cellData != null) {
            final Iterator<?> cellIt = cellData.getChildIterator();
            while (cellIt.hasNext()) {
                final UIDL cellUidl = (UIDL)cellIt.next();
                if ("cell".equals(cellUidl.getTag())) {
                    paintCell(cellUidl);
                }
            }
        }
    }
    
    private void paintCell(final UIDL cellUidl) {
        final String key = cellUidl.getStringAttribute("key");
        final VGridCell cell = getCellForKey(key);
        final UIDL editor = cellUidl.getChildByTagName("editor");
        if (editor != null) {
            final Paintable p = client.getPaintable(editor.getChildUIDL(0));
            p.updateFromUIDL(editor.getChildUIDL(0), client);
            cell.setWidget((Widget)p);
            FocusImpl.getFocusImplForPanel().focus(((Widget)p).getElement());
        } else if (cellUidl.hasAttribute("text")) {
            cell.setText(cellUidl.getStringAttribute("text"));
        }
    }

    private VGridCell getCellForKey(final String key) {
        VGridCell cell = columnKeysToCell.get(key);
        if (cell == null) {
            cell = createNewCell(key);
        }
        return cell;
    }
    
    @Override
    public void replaceChildComponent(Widget oldComponent, Widget newComponent) {
    }

    @Override
    public boolean hasChildComponent(Widget component) {
        return false;
    }

    @Override
    public void updateCaption(Paintable component, UIDL uidl) {/* NOP */}

    @Override
    public boolean requestLayout(Set<Paintable> children) {
        return false;
    }

    @Override
    public RenderSpace getAllocatedSpace(Widget child) {
        return new RenderSpace();
    }
    
    private VGridCell createNewCell(final String columnKey) {
        final VGridCell cell = new VGridCell(columnKey, this);
        columnKeysToCell.put(columnKey, cell);
        add(cell);
        return cell;
    }

    @Override
    public void add(Widget child) {
        container.appendChild(child.getElement());
        adopt(child);
    }
    
    public void hideCells() {
        addStyleName("v-cells-hidden");
    }
    
    public void setOdd(boolean isOdd) {
        if (isOdd) {
            addStyleName("v-grid-row-odd");
        } else {
            removeStyleName("v-grid-row-odd");
        }
    }
    
    public ApplicationConnection getConnection() {
        return client;
    }
    
    private Paintable getComponent(Element element) {
        return Util.getPaintableForElement(client, this, element);
    }

    @Override
    public void onClick(ClickEvent event) {
    }

    public void finishEditing(final VGridCell cell) {
        client.updateVariable(paintableId, "editCellFinish", cell.getColumnKey(), true);
    }

    public void startEditing(VGridCell cell) {
        client.updateVariable(paintableId, "editCellStart", cell.getColumnKey(), true);
    }
}
