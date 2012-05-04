package org.vaadin.addon.grid.body;

import org.vaadin.addon.grid.AbstractGrid;
import org.vaadin.addon.grid.TreeGrid;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Hierarchical;

@SuppressWarnings("serial")
public class TreeGridBody extends GridBody<TreeGrid>{

    public TreeGridBody(Container dataSource, AbstractGrid<TreeGrid> grid) {
        super(dataSource, grid);
        if (!(dataSource instanceof Hierarchical)) {
            throw new RuntimeException();
        }
    }

}
