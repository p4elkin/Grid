package org.vaadin.addon.grid.client.ui.row;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.ui.SimpleFocusablePanel;

public class VGridCell extends SimpleFocusablePanel {
    
    private static final String CONTENT_STYLE_NAME = "v-grid-cell-wrapper";
    
    private static final String STYLE_NAME = "v-grid-cell";
    
    private final Element wrapper = DOM.createDiv();
    
    private final Element container;
    
    private final String columnKey; 
    
    private Widget widget;
    
    private String text;
    
    private VGridRow parentRow;
    
    public VGridCell(final String columnKey, final VGridRow row) {
        super();
        this.parentRow = row;
        this.container = getElement();
        this.columnKey = columnKey;
        container.addClassName(STYLE_NAME);
        wrapper.addClassName(CONTENT_STYLE_NAME);
        container.addClassName("v-td-" + columnKey);
        container.appendChild(wrapper);
        DOM.sinkEvents(getElement(), DOM.getEventsSunk(getElement()) | Event.FOCUSEVENTS);
    }
    
    @Override
    protected void onLoad() {
        super.onLoad();
    }
    
    public String getColumnKey() {
        return columnKey;
    }

    @Override
    public Widget getWidget() {
        return widget;
    }

    @Override
    public void setWidget(final Widget widget) {
        if (widget != this.widget) {
            if (widget != null) {
                widget.removeFromParent();
            }
            if (this.widget != null) {
              remove(this.widget);
            }
            this.widget = widget;
            if (widget != null) {
              wrapper.setInnerHTML("");
              wrapper.appendChild(widget.getElement());
              adopt(widget);
            }
        }
    }

    @Override
    public boolean remove(Widget w) {
      if (w == this.widget) {
          try {
            orphan(w);
          } finally {
            wrapper.removeChild(w.getElement());
            widget = null;
          }
          return true;
      }
      return false;
    }
    
    public String getText() {
        return text;
    }

    public void setText(String text) {
        if (getWidget() != null) {
            parentRow.getConnection().unregisterPaintable((Paintable)widget);
            remove(getWidget());
        }
        this.text = text;
        wrapper.setInnerHTML(text);
    }
    
    @Override
    public VGridRow getParent() {
        return (VGridRow)super.getParent();
    }
    
}
