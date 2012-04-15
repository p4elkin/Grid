package org.vaadin.addon.grid.client.ui.body;


/*@SuppressWarnings("serial")
public class VGridBodyProxy extends ClientSideProxy {
    
    private final VGridBody gridBody;

    public VGridBodyProxy(final VGridBody body) {
        super(body);
        this.gridBody = body;
        registerCallbacks();
    }

    protected void registerCallbacks() {
        register("setTotalRows", new Method() {            
            @Override
            public void invoke(String methodName, Object[] params) {
                assert params.length == 1 && params[0] instanceof Integer;
                gridBody.setTotalRows((Integer)params[0]);
            }
        });
        
        register("rowsPainted", new Method() {            
            @Override
            public void invoke(String methodName, Object[] params) {
                assert params.length == 2;
                gridBody.finalizeRowsRender((Integer)params[0], (Integer)params[1]);
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
                gridBody.setVisibleColumns(columns);
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
                gridBody.setCollapsedColumns(columns);
            }
        });
        
        register("setColumnWidth", new Method() {            
            @Override
            public void invoke(String methodName, Object[] params) {
                assert params.length == 2;
                gridBody.setColumnWidth(String.valueOf(params[0]), (Integer)(params[1]));
            }
        });
        
        register("setColumnExpandRatio", new Method() {            
            @Override
            public void invoke(String methodName, Object[] params) {
                assert params.length == 2;
                gridBody.setColumnExpandRatio(String.valueOf(params[0]), (Integer)(params[1]));
            }
        });
        
        register("setColumnAlignment", new Method() {            
            @Override
            public void invoke(String methodName, Object[] params) {
                assert params.length == 2;
                gridBody.setColumnAlignment(String.valueOf(params[0]), (VColumnModel.Align.valueOf(params[1].toString())));
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
                gridBody.setColumnWidths(castMap);
            }
        });
        
        register("setDefaultColummnWidth", new Method() {            
            @Override
            public void invoke(String methodName, Object[] params) {
                assert params.length == 1;
                gridBody.setDefaultColumnWidth((Integer)params[0]);
            }
        });
        
        register("setCellEditingEnabled", new Method() {            
            @Override
            public void invoke(String methodName, Object[] params) {
                assert params.length == 1;
                gridBody.setCellEditable((Boolean)params[0]);
            }
        });
        
        
    }

    
    public void processRowRequest() {
        System.out.println("REQUEST: \n" + gridBody.getRenderInfo());
        call("refreshRows", (Object[])gridBody.getRenderInfo().getIndeces());
    }

    public void checkRowAmount(int position) {
        
        int pageLength = gridBody.getPageLength();
        double cacheRate = gridBody.getCacheRate();
        
        if (gridBody.getRenderedRowsCount() >= (1 + 2 * cacheRate) * pageLength) {
            return;
        }
        
        int firstRendered = gridBody.getRenderInfo().getFirst();
        int lastRendered = gridBody.getRenderInfo().getLast();
        
        int firstRequested = Math.max(position - (int)(cacheRate * pageLength), 0); 
        int lastRequested =  Math.min(position + (int)(1 + cacheRate) * pageLength - 1, gridBody.getTotalRows() - 1);
        if (firstRequested < (int)(cacheRate * pageLength)) {
            lastRequested += (int)(cacheRate * pageLength) - firstRequested;
        }
        
        if (firstRendered > firstRequested) {
            System.out.println("Querying rows in head " + (firstRendered - firstRequested));
            requestAdditionalRows(firstRendered - firstRequested, true);
        }
        if (lastRendered < lastRequested) {
            System.out.println("Querying rows in tail " + (lastRequested - lastRendered));
            requestAdditionalRows(lastRequested - lastRendered, false);
        } 
    }

    public void requestAdditionalRows(final int number, final boolean inHead) {
        call("loadAdditionalRows", number, inHead);
    }
}*/
