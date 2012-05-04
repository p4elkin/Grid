package org.vaadin.addon.grid.body;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.vaadin.addon.grid.GridRow;
import org.vaadin.addon.grid.LazyLoadingGrid;
import org.vaadin.addon.grid.client.ui.body.VLazyLoadBodyComposite;

import com.vaadin.data.Container.Indexed;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.ui.ClientWidget;
import com.vaadin.ui.ClientWidget.LoadStyle;
import com.vaadin.ui.Component;

@SuppressWarnings("serial")
@ClientWidget(value = VLazyLoadBodyComposite.class, loadStyle = LoadStyle.EAGER)
public class LazyLoadingBody extends GridBody<LazyLoadingGrid> {
    
    private float cacheRate = 1f;
    
    public LazyLoadingBody(LazyLoadingGrid grid) {
        super(grid);
    }

    public LazyLoadingBody(Indexed dataSource, LazyLoadingGrid grid) {
        super(dataSource, grid);
    }
    
    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        fixIndices();
        super.paintContent(target);
    }
    
    private void fixIndices() {
        final List<Integer> indices = new ArrayList<Integer>();
        for (final Component c : rows) {
            indices.add(((GridRow)c).getIndex());
        }
        renderInfo.setIndeces(indices);
    }
    
    @Override
    public void refreshRows(final Object[] params) {
        final List<GridRow> rowsToDelete = new LinkedList<GridRow>();
        final Indexed container = (Indexed)getContainerDataSource();
        for (int i = 0; i < params.length; ++i) {
            int clientSideIndex = (Integer)params[i];
            final GridRow row = (GridRow)getRowByIndex(i);
            if (clientSideIndex < 0) {
                rowsToDelete.add(row);
            } else {
                if (clientSideIndex != row.getIndex()) {
                    row.setItemDataSource(container.getItem(container.getIdByIndex(clientSideIndex)));
                    row.setIndex(clientSideIndex);
                }
            }
        }
        
        for (final GridRow row : rowsToDelete) {
            removeRow(row);
        }
        
        Collections.sort(rows, new Comparator<Component>() {
            public int compare(Component o1, Component o2) {
                Integer idx1 = Integer.valueOf(((GridRow)o1).getIndex());
                Integer idx2 = Integer.valueOf(((GridRow)o2).getIndex());
                return idx1.compareTo(idx2);
            };
        });
        
        if (isDebug) {
            System.out.println(renderInfo);
        }
        
        renderInfo.setFirstRendered(((GridRow)rows.peekFirst()).getIndex());
        renderInfo.setLastRendered(((GridRow)rows.peekLast()).getIndex());
        
        requestRepaint();
    }

    @Override
    public Object[] initRequestFromClient() {
        final List<Object> result = Arrays.asList(super.initRequestFromClient());
        result.add(getCacheRate());
        result.add(renderInfo.getFirst());
        result.add(renderInfo.getLast());
        return result.toArray();
        
    }
    
    public float getCacheRate() {
        return cacheRate;
    }
    
    public void setCacheRate(float cacheRate) {
        this.cacheRate = cacheRate;
    }
}
