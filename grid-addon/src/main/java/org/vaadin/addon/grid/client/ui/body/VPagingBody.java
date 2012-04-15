package org.vaadin.addon.grid.client.ui.body;

import org.vaadin.addon.grid.client.event.PageNavigationEvent;
import org.vaadin.addon.grid.client.event.PagingContextChangedEvent;
import org.vaadin.addon.grid.client.event.handler.PageNavigationHandler;

public class VPagingBody extends VAbstractGridBody implements PageNavigationHandler {

    private int currentPage = 0;

    public VPagingBody() {
        super();
    }
    
    @Override
    public void navigateToPage(PageNavigationEvent event) {
        currentPage = event.getPageNumber();
        Object[] idxs = new Object[getPageLength()];
        for (int i = 0; i < getPageLength(); ++i) {
            idxs[i] = (Integer)(currentPage * pageLength + i); 
        }
        loadRowsEager(idxs);
    }

    @Override
    public void initialize(Object[] args) {
        super.initialize(args);
        firePagingContextChanged();
    }

    @Override
    public void setPageLength(int pageLength) {
        super.setPageLength(pageLength);
        if (getComposite().getProxy().isClientInitialized()) {
            firePagingContextChanged();
        }
    }
    
    @Override
    public void setTotalRows(int totalRows) {
        super.setTotalRows(totalRows);
        this.currentPage = 0;
        if (getComposite().getProxy().isClientInitialized()) {
            firePagingContextChanged();
        }
    }
    
    public int getCurrentPage() {
        return currentPage;
    }

    private void firePagingContextChanged() {
        getComposite().getEventBus().fireEvent(
                new PagingContextChangedEvent(getTotalRows(), getPageLength(), currentPage));
    }
    
}
