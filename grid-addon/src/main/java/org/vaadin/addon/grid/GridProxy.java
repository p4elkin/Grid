package org.vaadin.addon.grid;

import org.vaadin.addon.grid.rpc.ServerSideProxy;

@SuppressWarnings({"serial", "unused"})
public class GridProxy extends ServerSideProxy {

    private Grid grid;
    
    public GridProxy(final Grid grid) {
        super(grid);
    }

    public void registerCallbacks() {
        
    }
}
