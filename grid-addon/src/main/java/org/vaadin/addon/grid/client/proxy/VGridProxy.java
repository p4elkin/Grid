package org.vaadin.addon.grid.client.proxy;

import org.vaadin.addon.grid.client.rpc.ClientSideProxy;
import org.vaadin.addon.grid.client.ui.VGrid;

@SuppressWarnings({"serial", "unused"})
public class VGridProxy extends ClientSideProxy {

    private VGrid grid;
    
    public VGridProxy(VGrid clientWidget) {
        super(clientWidget);
        this.grid = clientWidget;
        registerCallbacks();
    }

    protected void registerCallbacks() {
    }
}
