package org.vaadin.addon.grid.client.ui.body;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.vaadin.addon.grid.client.rpc.ClientSideProxy;
import org.vaadin.addon.grid.client.rpc.Method;
import org.vaadin.addon.grid.client.ui.VColumnModel;

import com.vaadin.terminal.gwt.client.ValueMap;

@SuppressWarnings("serial")
public class VBodyProxy extends ClientSideProxy {

    private final VGridBodyComposite<?> composite;

    public VBodyProxy(final VGridBodyComposite<?> body) {
        super(body);
        this.composite = body;
        registerCallbacks();
    }

    protected void registerCallbacks() {
        register("setTotalRows", new Method() {            
            @Override
            public void invoke(String methodName, Object[] params) {
                assert params.length == 1 && params[0] instanceof Integer;
                composite.getBody().setTotalRows((Integer)params[0]);
            }
        });
        
        register("rowsPainted", new Method() {            
            @Override
            public void invoke(String methodName, Object[] params) {
                assert params.length == 2;
                composite.getBody().setRenderedRowsBounds((Integer)params[0], (Integer)params[1]);
            }
        });
        
        register("setVisibleColumns", new Method() {            
            @Override
            public void invoke(String methodName, Object[] params) {
                final List<String> columns = new LinkedList<String>();
                for (final Object column : params) {
                    assert column instanceof String;
                    columns.add(String.valueOf(column));
                }
                composite.getColumnModel().setVisibleColumns(columns);
            }
        });
        
        register("setCollapsedColumns", new Method() {            
            @Override
            public void invoke(String methodName, Object[] params) {
                final List<String> columns = new LinkedList<String>();
                for (final Object column : params) {
                    assert column instanceof String;
                    columns.add(String.valueOf(column));
                }
                composite.getColumnModel().setCollapsedColumnKeys(columns);
            }
        });
        
        register("setColumnWidth", new Method() {            
            @Override
            public void invoke(String methodName, Object[] params) {
                assert params.length == 2;
                composite.getColumnModel().setColumnWidth(String.valueOf(params[0]), (Integer)(params[1]));
            }
        });
        
        register("setColumnExpandRatio", new Method() {            
            @Override
            public void invoke(String methodName, Object[] params) {
                assert params.length == 2;
                composite.getColumnModel().setColumnExpandRatio(String.valueOf(params[0]), (Integer)(params[1]));
            }
        });
        
        register("setColumnAlignment", new Method() {            
            @Override
            public void invoke(String methodName, Object[] params) {
                assert params.length == 2;
                composite.getColumnModel().setColumnAlignment(String.valueOf(params[0]), (VColumnModel.Align.valueOf(params[1].toString())));
            }
        });
        
        register("setColumnWidths", new Method() {            
            @Override
            public void invoke(String methodName, Object[] params) {
                assert params.length == 1;
                final ValueMap widths = (ValueMap)params[0];
                final Map<String, Number> castMap = new HashMap<String, Number>();
                final Iterator<String> it = widths.getKeySet().iterator();
                while (it.hasNext()) {
                    final String entry = (String)it.next();
                    Double rawWidth = widths.getRawNumber(entry);
                    Number value = rawWidth.intValue();
                    if (Math.floor(rawWidth) != rawWidth) {
                        value = rawWidth.floatValue();   
                    }        
                    castMap.put(entry, value);
                }
                composite.getColumnModel().setColumnWidths(castMap);
            }
        });
        
        register("setDefaultColummnWidth", new Method() {            
            @Override
            public void invoke(String methodName, Object[] params) {
                assert params.length == 1;
                composite.getColumnModel().setDefaultColumnWidth((Integer)params[0]);
            }
        });
        
        register("setPageLength", new Method() {            
            @Override
            public void invoke(String methodName, Object[] params) {
                assert params.length == 1;
                composite.getBody().setPageLength((Integer)params[0]);
            }
        });
        
        register("setCellEditingEnabled", new Method() {            
            @Override
            public void invoke(String methodName, Object[] params) {
                assert params.length == 1;
                composite.getBody().setCellEditable((Boolean)params[0]);
            }
        });
        
        register("setStyleToken", new Method() {            
            @Override
            public void invoke(String methodName, Object[] params) {
                assert params.length == 1;
                composite.getColumnModel().setToken(String.valueOf(params[0]));
            }
        });
    }
    
    public void processRowRequest(Object[] indeces) {
        call("refreshRows", indeces);
    }

    public void requestAdditionalRows(final int number, final boolean inHead) {
        call("loadAdditionalRows", number, inHead);
    }
    
}
