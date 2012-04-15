package org.vaadin.addon.grid.client.ui.rowexpansion;

import java.util.HashSet;
import java.util.Set;

import org.vaadin.addon.grid.client.event.RowExpanderEvent;
import org.vaadin.addon.grid.client.event.handler.RowExpanderEventHandler;
import org.vaadin.addon.grid.client.ui.widget.VGridBodyScrollPanel;

public class VExpanderCapableScrollPanel extends VGridBodyScrollPanel implements RowExpanderEventHandler {

    private Set<VExpandingGridRow> expandedRows = new HashSet<VExpandingGridRow>();
    
    private boolean expanderTotalHeightValid = false;
    
    private int totalExpanderHeight = 0;
    
    public VExpanderCapableScrollPanel() {
        super();
    }

    @Override
    public void onExpanderChange(final RowExpanderEvent event) {
        final VExpandingGridRow row = event.getRow();
        boolean expanded = event.isExpanded();
        if (expanded) {
            expandedRows.add(row);
        } else {
            expandedRows.remove(row);
        }
        invalidateExpanderTotalHeight();
    }
    
    @Override
    public int getScrollHeight() {
        return super.getScrollHeight() + getTotalExpaderHeigth();
    }
    
    private int getTotalExpaderHeigth() {
        if (expanderTotalHeightValid) {
            return totalExpanderHeight;
        } else {
            expanderTotalHeightValid = true;
            totalExpanderHeight = 0;
            for (final VExpandingGridRow row : expandedRows) {
                totalExpanderHeight += row.getExpanderWidget().getOffsetHeight();
            }
            return totalExpanderHeight;
        }
    }
    
    private void invalidateExpanderTotalHeight() {
        expanderTotalHeightValid = false;
    }
    
}
