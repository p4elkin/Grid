package org.vaadin.addon.grid.client.ui.body;

import java.util.LinkedList;
import java.util.List;

import org.vaadin.addon.grid.client.ui.RenderInfo;
import org.vaadin.addon.grid.client.ui.row.VGridRow;
import org.vaadin.addon.grid.client.ui.widget.VGridBodyScrollPanel;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;

public abstract class VAbstractGridBody extends ComplexPanel {

    private static final String CLASS_NAME = "v-grid-body";
    
    private final List<VGridRow> rows = new LinkedList<VGridRow>();
    
    private final RowRequestSender rowRequestSender = new RowRequestSender();

    private VCellEditorDelegate cellEditorDelegate;
    
    private boolean rowHeightDetermined = false;

    private double rowHeight = 0;

    private int totalRows = 0;
    
    protected int pageLength = 5;
    
    protected final Element viewPort = DOM.createDiv();
    
    protected RenderInfo renderInfo;
    
    public VAbstractGridBody() {
        this.renderInfo = new RenderInfo();
        setElement(DOM.createDiv());
        setStyleName(CLASS_NAME);
        getElement().appendChild(viewPort);
        viewPort.addClassName("v-viewport");
        DOM.sinkEvents(getElement(), Event.MOUSEEVENTS);
    }
    
    public void renderRows(UIDL uidl) {
        final ApplicationConnection appConnection = getComposite().getAppConnection();
        final UIDL rowData = uidl.getChildByTagName("rows");
        int rowCount = rowData.getChildCount();
        if (rowData != null) {
            int index = 0;
            final List<VGridRow> rowList = new LinkedList<VGridRow>(rows);
            while (index < rowCount) {
                final UIDL rowUidl = rowData.getChildUIDL(index);
                if ("row".equals(rowUidl.getTag())) {
                    final Paintable rowPaintable = appConnection.getPaintable(rowUidl.getChildUIDL(0));
                    if (rowPaintable instanceof VGridRow) {
                        renderRow((VGridRow) rowPaintable, index++);
                        rowPaintable.updateFromUIDL(rowUidl.getChildUIDL(0), appConnection);
                        rowList.remove(rowPaintable);
                    }
                }
            }
            for (final VGridRow row : rowList) {
                removeRow(row);
            }
        }
    }

    private void removeRow(final VGridRow row) {
        getComposite().getAppConnection().unregisterPaintable(row);
        remove(row);
        rows.remove(row);
    }

    protected void renderRow(final VGridRow row, int pos) {
        ensureRowAttached(row, pos);
        row.removeStyleName("v-cells-hidden");
    }

    public void setRenderedRowsBounds(int firstRendered, int lastRendered) {
        renderInfo.setBounds(firstRendered, lastRendered);
    }

    protected void removeRows(int amount, boolean isFromHead) {
        while (amount > 0) {
            final VGridRow row = (VGridRow) getWidget(isFromHead ? 0 : getChildren().size() - 1);
            removeRow(row);
            --amount;
        }
    }

    private void ensureRowAttached(VGridRow row, int position) {
        if (row.getParent() == this) {
            if (getWidgetIndex(row) != position) {
                row.removeFromParent();
                getChildren().insert(row, position);
                viewPort.insertBefore(row.getElement(), viewPort.getChildNodes().getItem(position));
                adopt(row);
            } 
        } else {
            insertRow(row, position);
        }
    }

    protected void insertRow(VGridRow row, int position) {
        rows.add(row);
        getChildren().insert(row, position);
        if (rows.size() != position) {
            viewPort.insertBefore(row.getElement(), viewPort.getChildNodes().getItem(position));
        } else {
            viewPort.appendChild(row.getElement());
        }
        adopt(row);
    }
   
    
    public void setCellEditable(boolean isEditable) {
        if (isEditable && cellEditorDelegate == null) { 
            cellEditorDelegate = new VCellEditorDelegate(this);
            cellEditorDelegate.bind();
        } else  if (cellEditorDelegate != null) {
            cellEditorDelegate.unBind();
            cellEditorDelegate = null;
        }
}
    
    public int getPageLength() {
        return pageLength;
    }

    public int getTotalRows() {
        return totalRows;
    }
    
    public double getRowHeight() {
        return getRowHeight(false);
    }
   
    @Override
    public VGridBodyScrollPanel getParent() {
        return (VGridBodyScrollPanel)super.getParent();
    }
    
    public VGridBodyComposite<?> getComposite() {
        return (VGridBodyComposite<?>)getParent().getParent();
    }

    public double getRowHeight(boolean forceUpdate) {
        if (rowHeightDetermined && !forceUpdate) {
            return rowHeight;
        } else {
            if (viewPort.getChildCount() > 0) {
                int viewportHeight = viewPort.getOffsetHeight();
                int rowCount = viewPort.getChildCount();
                rowHeight = viewportHeight / (double) rowCount;
            } else {
                if (isAttached()) {
                    final VGridRow row = new VGridRow();
                    viewPort.appendChild(row.getElement());
                    getRowHeight(forceUpdate);
                    viewPort.removeChild(row.getElement());
                }
            }
            rowHeightDetermined = true;
            return rowHeight;
        }
    }
    
    public void setTotalRows(int totalRows) {
        if (this.totalRows != totalRows) {
            this.totalRows = totalRows;
        }
    }
    
    public void initialize(Object[] args) {
        setPageLength((Integer) args[0]);
        setTotalRows((Integer) args[1]);
    }
    
    public void setPageLength(int pageLength) {
        if (this.pageLength != pageLength) {
            this.pageLength = pageLength;
        }
    }
    
    public int getRenderedRowsCount() {
        return rows.size();
    }
    
    @Override
    public void setHeight(String height) {
        super.setHeight(height);
    }
    
    public RowRequestSender getRowRequestSender() {
        return rowRequestSender;
    }
    
    protected void loadRowsDeferred(final Object[] indeces) {
        getRowRequestSender().cancelPendingRowRequest();
        getRowRequestSender().scheduleRowFetch(indeces);
    }
    
    protected void loadRowsEager(final Object[] indeces) {
        getComposite().getProxy().processRowRequest(indeces);
    }
    
    protected class RowRequestSender extends Timer {

        private static final int DEFAULT_DELAY = 250;

        private Object[] indeces;
        
        @Override
        public void run() {
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    if (!getComposite().getAppConnection().hasActiveRequest()) {
                        loadRowsEager(indeces);
                    }
                }
            });
        }

        public void cancelPendingRowRequest() {
            cancel();
        }

        public void scheduleRowFetch(Object[] indeces) {
            this.indeces = indeces;
            schedule(DEFAULT_DELAY);
        }
    }
}
