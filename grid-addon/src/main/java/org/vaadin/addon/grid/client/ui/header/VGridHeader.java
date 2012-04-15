package org.vaadin.addon.grid.client.ui.header;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vaadin.addon.grid.client.ui.VColumnModel;
import org.vaadin.csstools.client.ComputedStyle;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Container;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.RenderSpace;
import com.vaadin.terminal.gwt.client.UIDL;

public class VGridHeader extends FlowPanel implements Container {

    private static final String CLASS_NAME = "v-grid-header";
    
    private final Map<String, VGridHeaderCell> columnKeyToCell = new LinkedHashMap<String, VGridHeaderCell>();
    
    private final Map<Widget, String> widgetToColumnKey = new HashMap<Widget, String>();
    
    private ComputedStyle style;
    
    private VColumnModel columnInfo; 
    
    protected ApplicationConnection client;
    
    protected String paintableId;
    
    public VGridHeader() {
        super();
        setStyleName(CLASS_NAME);
    }
    
    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        this.client = client;
        this.paintableId = uidl.getId();
        if (client.updateComponent(this, uidl, false)) {
            return;
        }
        updateColumnHeaders(uidl);
    }

    @Override
    public void replaceChildComponent(Widget oldComponent, Widget newComponent) {
    }

    @Override
    public boolean hasChildComponent(Widget component) {
        return widgetToColumnKey.keySet().contains(component);
    }

    @Override
    public void updateCaption(Paintable component, UIDL uidl) {
        // TODO!
    }

    @Override
    public boolean requestLayout(Set<Paintable> children) {
        return true;
    }

    @Override
    public RenderSpace getAllocatedSpace(Widget child) {
        final String columnKey = widgetToColumnKey.get(child);
        return new RenderSpace(columnInfo.getColumnWidth(columnKey), style.getIntProperty("height"));
    }

    public int getDecorationsWidth() {
        int result = 0;
        if (style != null) {
            result = style.getBorder()[1] + style.getBorder()[3]; 
        }
        return result;
    }

    public void setColumnInfo(VColumnModel columnInfo) {
        this.columnInfo = columnInfo;
    }

    public VColumnModel getColumnInfo() {
        return columnInfo;
    }
    
    private void updateColumnHeaders(final UIDL uidl) {
        int pos = 0;
        final List<Widget> widgets = new LinkedList<Widget>();
        final Iterator<?> it = uidl.getChildIterator();
        while (it.hasNext()) {
            final UIDL columnHeader = (UIDL) it.next();
            final String key = columnHeader.getStringAttribute("key");
            final Paintable paintable = client.getPaintable(columnHeader.getChildUIDL(0));
            final Widget widget = (Widget) paintable;
            widgetToColumnKey.put(widget, key);
            final VGridHeaderCell cell = getCellForWidget(key, widget);
            insertHeader(cell, pos);
            widgets.add(widget);
            paintable.updateFromUIDL(columnHeader.getChildUIDL(0), client);
            ++pos;
        }
        final Set<Widget> setToDelete = new HashSet<Widget>(widgetToColumnKey.keySet());
        setToDelete.removeAll(widgets);
        removeHeaders(setToDelete);
    }
    
    private void removeHeaders(Set<Widget> setToDelete) {
        for (final Widget w : setToDelete) {
            final  String key = widgetToColumnKey.get(w);
            remove(columnKeyToCell.get(key));
        }
    }

    private VGridHeaderCell getCellForWidget(final String key, final Widget widget) {
        VGridHeaderCell cell = columnKeyToCell.get(key);
        if (cell == null) {
            cell = new VGridHeaderCell();
            cell.setContent(widget);
        } else if (cell.getContent() != widget) {
            cell.setContent(widget);
        }
        cell.addStyleName(columnInfo.getColumnControlStyleName(key));
        columnKeyToCell.put(key, cell);
        return cell;
    }

    private void insertHeader(final Widget widget, int pos) {
        int currentPosition = getWidgetIndex(widget);
        if (pos != currentPosition) {
            widget.removeFromParent();
            insert(widget, pos);
        }
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        style = new ComputedStyle(getElement());
    }

    /**
     * Replace with the Event handler.
     */
    public void recalculateColumnWidths() {
        for (final Widget w : widgetToColumnKey.keySet())
        client.handleComponentRelativeSize(w);
    }
}
