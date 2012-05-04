package org.vaadin.addon.grid;

import org.vaadin.addon.grid.body.PagingBody;

@SuppressWarnings("serial")
public class PagingGrid extends AbstractGrid<PagingBody> {

    public PagingGrid(final Indexed container) {
        super(container);
    }
    
    @Override
    protected PagingBody createBody() {
        return new PagingBody(this);
    }

}
