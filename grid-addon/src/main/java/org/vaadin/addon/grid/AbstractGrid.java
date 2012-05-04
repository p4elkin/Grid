package org.vaadin.addon.grid;

import java.util.Arrays;
import java.util.Map;

import org.vaadin.addon.grid.body.GridBody;
import org.vaadin.addon.grid.client.ui.VColumnModel;
import org.vaadin.addon.grid.client.ui.VGrid;
import org.vaadin.addon.grid.header.GridHeader;
import org.vaadin.addon.grid.header.HeaderComponentFactory;
import org.vaadin.addon.grid.rpc.ServerSideHandler;

import com.vaadin.data.Container;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.ClientWidget;
import com.vaadin.ui.ClientWidget.LoadStyle;

@SuppressWarnings("serial")
@ClientWidget(value = VGrid.class, loadStyle=LoadStyle.EAGER)
public abstract class AbstractGrid<T extends GridBody<?>> extends AbstractSelect implements ServerSideHandler {
 
    private T body = createBody();
    
    private GridHeader header;
    
    private GridProxy proxy;
    
    private final ColumnModel columnModel;
    
    public AbstractGrid() {
        this(new IndexedContainer());
    }
    
    public AbstractGrid(final Container container) {
        super();
        this.columnModel = new ColumnModel();
        setContainerDataSource(container);
        setImmediate(true);
        initializeProxy();
        createHeader();
        createBody();
        setVisibleColumns(container.getContainerPropertyIds().toArray());
    }
    
    public void setColumnAlignment(final Object columnId, final VColumnModel.Align align) {
        columnModel.setColumnAlign(columnId, align);
        body.setColumnAlignment(columnModel.getKey(columnId), align);
    }
    
    public void setVisibleColumns(final Object[] columnIds) {
        columnModel.setVisibleColumns(Arrays.asList(columnIds));
        body.setVisibleColumns(columnModel.getVisibleColumnsKeys());
        header.setVisibleColumns(columnModel.getVisibleColumnsKeys());
    }
    
    private void initializeProxy() {
        proxy = instantiateProxy();
        proxy.registerCallbacks();
    }

    protected GridProxy instantiateProxy() {
        return new GridProxy(this);
    }
    
    @Override
    public void setSizeUndefined() {
        super.setSizeUndefined();
        body.setSizeUndefined();
        header.setSizeUndefined();
    }
    
    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        proxy.paintContent(target);
        paintHeader(target);
        paintBody(target);
    }

    private void paintBody(PaintTarget target) throws PaintException {
        if (body != null) {
            target.startTag("gridBody");
            body.paint(target);
            target.endTag("gridBody");
        }
    }

    private void paintHeader(PaintTarget target) throws PaintException {
        if (header != null) {
            target.startTag("gridHeader");
            header.paint(target);
            target.endTag("gridHeader");
        }
    }

    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {
        proxy.changeVariables(source, variables);
        super.changeVariables(source, variables);
    }

    @Override
    public Object[] initRequestFromClient() {
        return new Object[] {};
    }

    @Override
    public void callFromClient(String method, Object[] params) {
        System.out.println("Received call from client: " + method);
    }

    @Override
    public void attach() {
        super.attach();
        body.attach();
        header.attach();
    }
    
    @Override
    public void detach() {
        super.detach();
        body.detach();
        header.detach();
    }
    
    @Override
    public void setContainerDataSource(Container newDataSource) {
        super.setContainerDataSource(newDataSource);
        if (body != null) {
            columnModel.setVisibleColumns(newDataSource.getContainerPropertyIds());
            body.setContainerDataSource(newDataSource);
        }
    }
    
    @Override
    public Container getContainerDataSource() {
        return body.getContainerDataSource();
    }
    
    protected void createHeader() {
        this.header = new GridHeader(this);
        header.setParent(this);
        requestRepaint();        
    }
    
    protected abstract T createBody();
    
    @Override
    public void requestRepaint() {
        super.requestRepaint();
        if (header != null && body != null) {
            header.requestRepaint();
            body.requestRepaint();
        }
    }

    public void setDefaultColumnWidth(int width) {
        columnModel.setDefaultColummnWidth(width);
        body.setDefaultColumnWidth(width);
    }

    public void setColumnWidth(Object columnId, int width) {
        columnModel.setColumnWidth(columnId, width);
        body.setColumnWidth(columnModel.getKey(columnId), width);
    }
    
    public void setColumnExpandRatio(String columnId, float ratio) {
        columnModel.setColumnExpandRatio(columnId, ratio);
        body.setColumnExpandRatio(columnModel.getKey(columnId), ratio);
    }

    public ColumnModel getColumnModel() {
        return columnModel;
    }

    public void setHeaderFactory(final HeaderComponentFactory headerComponentFactory) {
        header.setHeaderFactory(headerComponentFactory);
        requestRepaint();
    }
    
    public T getBody() {
        return body;
    }
    
    public GridHeader getHeader() {
        return header;
    }
}
