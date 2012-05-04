package org.vaadin.addon.grid.body;

import org.vaadin.addon.grid.client.rpc.Method;
import org.vaadin.addon.grid.rpc.ServerSideProxy;

@SuppressWarnings("serial")
public class GridBodyProxy extends ServerSideProxy {

    private final GridBody<?> body;

    public GridBodyProxy(final GridBody<?> body) {
        super(body);
        this.body = body;
    }

    public void registerCallbacks() {
        register("setPageLength", new Method() {
            @Override
            public void invoke(String methodName, Object[] params) {
                assert params.length == 1 && params[0] instanceof Integer;
                body.setPageLength((Integer)params[0]);
            }
        });
        
        register("loadAdditionalRows", new Method() {
            @Override
            public void invoke(String methodName, Object[] params) {
                assert params.length == 2;
                body.createRows((Integer)params[0], (Boolean)params[1]);
            }
        });
        
        register("refreshRows", new Method() {
            @Override
            public void invoke(String methodName, Object[] params) {
                body.refreshRows(params);
            }
        });
    }

}
