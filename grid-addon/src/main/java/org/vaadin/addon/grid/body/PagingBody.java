package org.vaadin.addon.grid.body;

import java.util.Arrays;
import java.util.List;

import org.vaadin.addon.grid.GridRow;
import org.vaadin.addon.grid.PagingGrid;
import org.vaadin.addon.grid.client.ui.body.VPagingBodyComposite;

import com.vaadin.data.Container.Indexed;
import com.vaadin.ui.ClientWidget;
import com.vaadin.ui.ClientWidget.LoadStyle;

@SuppressWarnings("serial")
@ClientWidget(value = VPagingBodyComposite.class, loadStyle = LoadStyle.EAGER)
public class PagingBody extends GridBody<PagingGrid> {

    public PagingBody(PagingGrid grid) {
        super(grid);
        setPageLength(20);
    }
    

    public PagingBody(Indexed dataSource, PagingGrid grid) {
        super(dataSource, grid);
        setPageLength(20);
    }
    
    @Override
    public void setPageLength(int newPageLength) {
        super.setPageLength(newPageLength);
        int rowCount = getRowCount();
        if (rowCount != newPageLength) {
            if (rowCount < newPageLength) {
                createRows(newPageLength - rowCount, false);
            } else {
                int index = newPageLength;
                for (;index < rowCount; ++index) {
                    removeRow(getRowByIndex(index));
                }
            }
        }
        requestRepaint();
    }
    
    @Override
    public void refreshRows(Object[] params) {
        super.refreshRows(params);
        final Indexed c = (Indexed)getContainerDataSource();
        for (int i = 0; i < params.length; ++i) {
            int clientSideIndex = (Integer)params[i];
            final GridRow row = getRowByIndex(i);
            if (clientSideIndex != row.getIndex()) {
                row.setItemDataSource(c.getItem(c.getIdByIndex(clientSideIndex)));
                row.setIndex(clientSideIndex);
            }
        }
    }

    @Override
    public Object[] initRequestFromClient() {
        final List<Object> result = Arrays.asList(super.initRequestFromClient());
        return result.toArray();
        
    }
}
