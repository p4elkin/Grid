package org.vaadin.addon.grid;

import org.vaadin.addon.grid.body.GridBody;
import org.vaadin.addon.grid.body.TreeGridBody;

import com.vaadin.data.Container;

@SuppressWarnings("serial")
public class TreeGrid extends AbstractGrid<TreeGrid> {

    @Override
    protected TreeGrid get() {
        return this;
    }

    @Override
    protected GridBody<TreeGrid> createBody(Container container) {
        return new TreeGridBody(container, this);
    }

}
