package org.vaadin.addon.grid;

import org.vaadin.addon.grid.body.GridBody;
import org.vaadin.addon.grid.body.LazyLoadingBody;

import com.vaadin.data.Container;

@SuppressWarnings("serial")
public class LazyLoadingGrid extends AbstractGrid<LazyLoadingGrid> {

    @Override
    protected LazyLoadingGrid get() {
        return this;
    }

    @Override
    protected GridBody<LazyLoadingGrid> createBody(final Container container) {
        return new LazyLoadingBody(container, get());
    }

}
