package org.vaadin.addon.grid;

import org.vaadin.addon.grid.rpc.ServerSideProxy;

@SuppressWarnings({"serial", "unused"})
public class GridProxy extends ServerSideProxy {

    private AbstractGrid<?> grid;
    
    public GridProxy(final AbstractGrid<?> grid) {
        super(grid);
    }

    public void registerCallbacks() {
        
    }
}
