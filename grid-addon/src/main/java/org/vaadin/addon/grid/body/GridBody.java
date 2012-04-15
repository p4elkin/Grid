package org.vaadin.addon.grid.body;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.vaadin.addon.grid.ColumnModel;
import org.vaadin.addon.grid.Grid;
import org.vaadin.addon.grid.GridRow;
import org.vaadin.addon.grid.client.ui.RenderInfo;
import org.vaadin.addon.grid.client.ui.VColumnModel.Align;
import org.vaadin.addon.grid.client.ui.body.VLazyLoadBodyComposite;
import org.vaadin.addon.grid.rpc.ServerSideHandler;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Indexed;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeNotifier;
import com.vaadin.data.Container.PropertySetChangeEvent;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.ClientWidget;
import com.vaadin.ui.ClientWidget.LoadStyle;
import com.vaadin.ui.Component;

@SuppressWarnings({"serial"})
@ClientWidget(value = VLazyLoadBodyComposite.class, loadStyle = LoadStyle.EAGER)
public class GridBody extends AbstractLayout implements ServerSideHandler, Container.Viewer,
        Container.ItemSetChangeListener, Container.PropertySetChangeListener {

    private Grid grid;
    
    private Indexed container;
    
    private GridBodyProxy proxy;

    protected RenderInfo renderInfo;
    
    protected LinkedList<Component> rows = new LinkedList<Component>();
    
    private CellEditorFactory cellEditorFactory = CellEditorFactory.DEFAULT;

    private int totalRows = 0;

    private int pageLength = 5;
    
    private boolean cellEditingEnabled = false;
    
    protected boolean isDebug = false;
    
    public GridBody(Grid grid) {
        this(new IndexedContainer(), grid);
    }

    public GridBody(Container.Indexed dataSource, Grid grid) {
        super();
        this.grid = grid;
        this.renderInfo = new RenderInfo();
        this.proxy = new GridBodyProxy(this);
        proxy.registerCallbacks();
        setImmediate(true);
        setSizeFull();
        setContainerDataSource(dataSource);
    }

    public void setColumnAlignment(String key, Align align) {
        proxy.call("setColumnAlignment", key, align.name());
    }
    
    public void setColumnWidth(final Object columnId, int width) {
        proxy.call("setColumnWidth", columnId, width);
    }
    
    public void setDefaultColumnWidth(int width) {
        proxy.callOnce("setDefaultColummnWidth", width);
    }

    public void setColumnExpandRatio(String columnId, float ratio) {
        proxy.call("setColumnWidth", columnId, ratio);
    }
    
    private void sendRepaintInfo() {
        proxy.call("rowsPainted", renderInfo.getFirst(), renderInfo.getLast());
    }
    
    public void setVisibleColumns(final Collection<String> ids) {
        proxy.callOnce("setVisibleColumns", ids.toArray());
    }
    
    public void setPageLength(int newPageLength) {
        if (pageLength != newPageLength) {
            pageLength = newPageLength;
            proxy.callOnce("setPageLength", newPageLength);
        }
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        sendRepaintInfo();
        proxy.paintContent(target);
        super.paintContent(target);
        final Iterator<Component> rowIt = rows.iterator();
        target.startTag("rows");
        boolean isOdd = renderInfo.getFirst() % 2 != 0;
        while (rowIt.hasNext()) {
            final GridRow row = (GridRow) rowIt.next();
            target.startTag("row");
            row.setOdd(isOdd);
            row.paint(target);
            target.endTag("row");
            isOdd = !isOdd;
        }
        target.endTag("rows");
    }

    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {
        proxy.changeVariables(source, variables);
        super.changeVariables(source, variables);
    }

    @Override
    public Object[] initRequestFromClient() {
        final ColumnModel info = grid.getColumnModel();
        proxy.callOnce("setVisibleColumns", info.getVisibleColumnsKeys().toArray());
        proxy.callOnce("setCollapsedColumns", info.getCollapsedColumnsKeys().toArray());
        proxy.callOnce("setColumnWidths", info.getColumnWidths());
        return new Object[] { 
                pageLength, 
                totalRows,};
    }

    @Override
    public void setContainerDataSource(final Container newDataSource) {
        assert newDataSource instanceof Indexed;
        container = (Indexed) newDataSource;
        totalRows = container.size();
        proxy.callOnce("setTotalRows", totalRows);
        discardPageInfo();
        createRows(Math.min(totalRows, pageLength), false);
        if (container instanceof ItemSetChangeNotifier) {
            ((ItemSetChangeNotifier) container).addListener(this);
        }
    }
    
    @Override
    public Container getContainerDataSource() {
        return container;
    }

    @Override
    public Iterator<Component> getComponentIterator() {
        return rows.iterator();
    }

    public void createRows(int number, boolean inHead) {
        if (isDebug) {
            System.out.println("Creating rows: " + number + (inHead ? " in head." : " in tail."));
        }
        if (inHead && renderInfo.getFirst() - number < 0) {
            number = number - renderInfo.getFirst();
        } else if (renderInfo.getLast() + number > totalRows) {
            number = totalRows - renderInfo.getLast() + 1;
        }
        int offset = inHead ? renderInfo.getFirst() : (renderInfo.getLast() + 1);
        for (int i = 0; i < number; ++i) {
            final GridRow row = addNewRow(inHead);
            int index = inHead ? offset - i - 1 : offset + i;
            final Item item = container.getItem(container.getIdByIndex(index));
            row.setIndex(index);
            row.setItemDataSource(item);
        }
        if (inHead) {
            renderInfo.setFirstRendered(renderInfo.getFirst() - number);
        } else {
            renderInfo.setLastRendered(renderInfo.getLast() + number);
        }
    }

    public void refreshRows(final Object[] params) {

    }

    protected void removeRow(GridRow row) {
        rows.remove(row);
        removeComponent(row);
    }
    
    protected void addRow(final GridRow row, boolean inHead) {
        super.addComponent(row);
        if (inHead) {
            rows.offerFirst(row);
        } else {
            rows.offerLast(row);
        }
    }

    protected GridRow addNewRow(boolean inHead) {
        final GridRow result = new GridRow(grid.getColumnModel());
        addRow(result, inHead);
        return result;
    }

    @Override
    public void removeComponent(Component c) {
        if (c != null) {
            super.removeComponent(c);
            rows.remove(c);
        }
    }

    @Override
    public void containerItemSetChange(ItemSetChangeEvent event) {
        resetRows();
    }
    
    protected void resetRows() {
        discardPageInfo();
        totalRows = container.size();
        int renderedRowsAmount = Math.min(rows.size(), totalRows);
        if (rows.size() > renderedRowsAmount) {
            final List<Component> rowsToDelete = rows.subList(renderedRowsAmount, rows.size());
            final Iterator<Component> it = rowsToDelete.iterator();
            while (it.hasNext()) {
                final Component c = it.next();
                super.removeComponent(c);
                it.remove();
            }
        }
        renderInfo.setBounds(0, renderedRowsAmount - 1);
        
        int idx = 0;
        Object id = container.getIdByIndex(0);
        for (;idx < renderedRowsAmount; ++idx) {
            final GridRow row = (GridRow)rows.get(idx);
            row.setItemDataSource(container.getItem(id));
            row.setIndex(idx);
            id = container.nextItemId(id);
        }
        
        proxy.call("setTotalRows", totalRows);
        requestRepaint();
    }

    @Override
    public void containerPropertySetChange(PropertySetChangeEvent event) {
    }

    protected void discardPageInfo() {
     
    }

    @Override
    public void addComponent(Component c) {
        throw new UnsupportedOperationException("Use addRow() method instead");
    }

    @Override
    public void replaceComponent(Component oldComponent, Component newComponent) {
        throw new UnsupportedOperationException("Not supported");
    }
    
    @Override
    public void callFromClient(String method, Object[] params) {
        System.out.println("Unhandled method " + method + "with " + params.length + " params");
    }
    
    public interface CellStyler extends Serializable {
        String getCellStyle();
    }
    
    public void setCelEditingEnabled(boolean isEnabled) {
        if (cellEditingEnabled != isEnabled) {
            cellEditingEnabled = isEnabled;
            proxy.call("setCellEditingEnabled", isEnabled);
        }
    }
    
    public CellEditorFactory getCellEditorFactory() {
        return cellEditorFactory;
    }
    
    public void setCellEditorFactory(final CellEditorFactory factory) {
        this.cellEditorFactory = factory;
        if (cellEditingEnabled) {
            requestRepaint();
        }
    }
    
    protected int getRowCount() {
        return rows.size();
    }
    
    protected GridRow getRowByIndex(int index) {
        return (GridRow)rows.get(index);
    }
}
