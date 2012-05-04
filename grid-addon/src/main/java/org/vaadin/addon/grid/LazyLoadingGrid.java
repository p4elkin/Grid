package org.vaadin.addon.grid;

import org.vaadin.addon.grid.body.LazyLoadingBody;

@SuppressWarnings("serial")
public class LazyLoadingGrid extends AbstractGrid<LazyLoadingBody>{

    @Override
    protected LazyLoadingBody createBody() {
        return new LazyLoadingBody(this);
    }

}
