package org.vaadin.addon.grid.client.ui.body;

import org.vaadin.addon.grid.client.ui.row.VGridRow;

import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.vaadin.terminal.gwt.client.UIDL;

public class VLazyLoadBody extends VAbstractGridBody implements ScrollHandler {
    
    private double cacheRate = 1;
    
    private double reactionRate = 0.75;
    
    private boolean isDebug = true;

    private HandlerRegistration scrollRegistration = null;

    public double getReactionRate() {
        return reactionRate;
    }

    public double getCacheRate() {
        return cacheRate;
    }
    
    @Override
    public void renderRows(UIDL uidl) {
        super.renderRows(uidl);
        checkAddtionalRowsNeed();
        System.out.println("Rows: " + getRenderedRowsCount());
    }
    
    @Override
    protected void onLoad() {
        super.onLoad();
        scrollRegistration = getParent().addScrollHandler(this);
    }
    
    @Override
    protected void onUnload() {
        super.onUnload();
        if (scrollRegistration != null) {
            scrollRegistration.removeHandler();
        }
    }
    
    @Override
    public void onScroll(ScrollEvent event) {
        if (isAttached()) {
            loadRowsDeferred(renderInfo.getIndeces());
        }    
    }
    
    @Override
    public void setHeight(String height) {
        super.setHeight(height);
        if (getComposite().getProxy().isClientInitialized()) {
            updatePageLength();
        }
    }
    
    @Override
    public void initialize(Object[] args) {
        super.initialize(args);
        cacheRate = (Float) args[2];
        renderInfo.setFirstRendered((Integer) args[4]);
        renderInfo.setLastRendered((Integer) args[5]);
    }

    @Override
    public void setPageLength(int pageLength) {
        super.setPageLength(pageLength);
        updatePageLength();
    }
    
    protected void updatePageLength() {
        int estimate = (int) Math.ceil((double) getParent().getClientHeight() / getRowHeight());
        if (estimate != pageLength) {
            pageLength = estimate;
            getComposite().getProxy().call("setPageLength", estimate);
        }
    }
    
    public int beforeScrollTopSet(int scrollTop) {
        double rowHeight = (int)getRowHeight();
        int viewPortOffset = renderInfo.getFirst() * (int)getRowHeight();
        int result = scrollTop - viewPortOffset;
        int upperOffset = (int)Math.floor(result / rowHeight);
        int lowerOffset = getRenderedRowsCount() - upperOffset - getPageLength();
        int cacheRows = (int)(getPageLength() * getCacheRate());
        if (lowerOffset < cacheRows) {
            int rowsTransferred = transferRows(upperOffset - cacheRows, true);
            result -= rowsTransferred * rowHeight;
            if (result < 0) {
                result = 0;
            }
            System.out.println("ScrollTop: " + result);
        } else if (upperOffset < Math.min(cacheRows, (int)(scrollTop / rowHeight))) {
            int rowsTransferred = transferRows(lowerOffset - cacheRows, false);
            result += rowsTransferred * rowHeight;
            if (result > getParent().getScrollHeight()) {
                result = getParent().getClientHeight();
            }
            System.out.println("ScrollTop: " + result);
        }
        return result;
    }
    
    private int transferRows(int count, boolean fromHead) {
        int spin = 0;
        int actualCount = count;
        while (spin < actualCount) {
            int index = fromHead ? 0 : getWidgetCount() - 1;
            final VGridRow row = (VGridRow) getWidget(index);
            row.hideCells();
            if (fromHead) {
                row.setOdd((renderInfo.getLast() + spin) % 2 == 0);
                add(row, viewPort);
            } else {
                row.setOdd((renderInfo.getFirst() - spin) % 2 == 0);
                insert(row, viewPort, 0, true);
            }
            ++spin;
        }
        renderInfo.shiftIndices(actualCount, fromHead);
        if (isDebug) {
            System.out.println(renderInfo);
        }
        return actualCount;
    }

    
    private void checkAddtionalRowsNeed() {
        checkRowAmount(getComposite().getFirstVisibleRow());
    }
    
    private void checkRowAmount(int position) {
        if (getRenderedRowsCount() < (1 + 2 * cacheRate) * pageLength) {
            int firstRendered = renderInfo.getFirst();
            int lastRendered = renderInfo.getLast();
            
            int firstRequested = Math.max(position - (int)(cacheRate * pageLength), 0); 
            int lastRequested =  Math.min(position + (int)(1 + cacheRate) * pageLength - 1, getTotalRows() - 1);
            if (firstRequested < (int)(cacheRate * pageLength)) {
                lastRequested += (int)(cacheRate * pageLength) - firstRequested;
            }
            
            if (firstRendered > firstRequested) {
                System.out.println("Querying rows in head " + (firstRendered - firstRequested));
                getComposite().getProxy().requestAdditionalRows(firstRendered - firstRequested, true);
            }
            if (lastRendered < lastRequested) {
                System.out.println("Querying rows in tail " + (lastRequested - lastRendered));
                getComposite().getProxy().requestAdditionalRows(lastRequested - lastRendered, false);
            } 
        }
    }
}
