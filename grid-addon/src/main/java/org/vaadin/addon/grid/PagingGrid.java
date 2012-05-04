package org.vaadin.addon.grid;

import org.vaadin.addon.grid.body.PagingBody;

import com.vaadin.data.Container;

@SuppressWarnings("serial")
public class PagingGrid extends AbstractGrid<PagingGrid> {

    public PagingGrid(final Indexed container) {
        super(container);
    }
    
    @Override
    protected PagingBody createBody(final Container container) {
        return new PagingBody(container, get());
    }

    @Override
    protected PagingGrid get() {
        return this;
    }

}
