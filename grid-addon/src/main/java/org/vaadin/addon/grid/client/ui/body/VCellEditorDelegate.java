package org.vaadin.addon.grid.client.ui.body;

import org.vaadin.addon.grid.client.ui.row.VGridCell;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Element;
import com.vaadin.terminal.gwt.client.Util;

public class VCellEditorDelegate {
    
    private final ScrollHandler scrollhander = new ScrollHandler() {
        @Override
        public void onScroll(ScrollEvent event) {
            if (editedCell != null) {
                stopEditing();
            }
        }
    };
    
    private final ClickHandler clickHandler = new ClickHandler() {
        
        @Override
        public void onClick(ClickEvent event) {
            final Element target = event.getNativeEvent().getEventTarget().cast();
            final VGridCell cell = Util.findWidget(target, VGridCell.class);
            if (cell != null) {
                startEditing(cell);
            }
        }
    };
    
    private VAbstractGridBody parent;
    
    private VGridCell editedCell;
    
    private HandlerRegistration scrollHandlerRegistration;
    
    private HandlerRegistration clickHandlerRegistration;
    
    public VCellEditorDelegate(final VAbstractGridBody body) {
        super();
        this.parent = body;
        bind();
    }
    
    protected void bind() {
        scrollHandlerRegistration = parent.getParent().addScrollHandler(scrollhander);
        clickHandlerRegistration = parent.addDomHandler(clickHandler, ClickEvent.getType());
    }

    public void startEditing(final VGridCell cell) {
        if (editedCell != cell) {
            stopEditing();
            editedCell = cell;
            cell.getParent().startEditing(cell);
        }
    }
    
    public void stopEditing() {
        if (editedCell != null) {
            editedCell.getParent().finishEditing(editedCell);
            editedCell = null;
        }
    }
    
    public VGridCell getCurrentlyEditedCell() {
        return editedCell;
    }

    public void unBind() {
        stopEditing();
        if (scrollHandlerRegistration != null) { 
            scrollHandlerRegistration.removeHandler();
        }
        
        if (clickHandlerRegistration != null) {
            clickHandlerRegistration.removeHandler();
        }
    }
}
