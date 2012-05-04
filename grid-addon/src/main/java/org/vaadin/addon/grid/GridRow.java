package org.vaadin.addon.grid;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.vaadin.addon.grid.body.GridBody;
import org.vaadin.addon.grid.client.ui.row.VGridRow;

import com.vaadin.data.Item;
import com.vaadin.data.Item.PropertySetChangeEvent;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.ClientWidget;
import com.vaadin.ui.ClientWidget.LoadStyle;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.Reindeer;

@SuppressWarnings("serial")
@ClientWidget(value = VGridRow.class, loadStyle = LoadStyle.EAGER)
public class GridRow extends AbstractLayout implements Item.Viewer, Item.PropertySetChangeListener {
    
    private Map<Object, Component> propertyComponentMap = new HashMap<Object, Component>();
        
    private ColumnModel columnInfo;
    
    private Item item;
    
    private int index = -1;
    
    private boolean isOdd = false;

    public GridRow(final ColumnModel columnInfo) {
        super();
        this.columnInfo = columnInfo;
        setSizeUndefined();
    }

    public boolean isOdd() {
        return isOdd;
    }

    public void setOdd(boolean isOdd) {
        this.isOdd = isOdd;
        if (isOdd) {
            addStyleName("odd");
        } else {
            removeStyleName("odd");
        }
    }
    
    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);
        target.addAttribute("token", columnInfo.getStyleToken());
        paintCells(target);   
    }
    
    private void paintCells(PaintTarget target) throws PaintException {
        target.startTag("cells");
        final List<String> visibleCells = columnInfo.getVisibleColumnsKeys();
        for (final String visibleCell : visibleCells) {
            final Object id = columnInfo.getPropertyIdForKey(visibleCell);
            final String text = String.valueOf(item.getItemProperty(id).getValue());
            target.startTag("cell");
            target.addAttribute("key", visibleCell);
            if (editors.containsKey(visibleCell)) { 
                target.startTag("editor");
                editors.get(visibleCell).paint(target);
                target.endTag("editor");
            } else {
                target.addAttribute("text", text);
            }
            target.endTag("cell");            
        }
        target.endTag("cells");
        
        if (expander != null) {
            target.startTag("expander");
            expander.paint(target);
            target.endTag("expander");
        }
    }

    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {
        super.changeVariables(source, variables);
        
        if (variables.containsKey("cellClicked")) {
            handleCellClick(variables.get("cellClicked"));
        }
        
        if (variables.containsKey("editCellStart")) {
            showEditor(variables.get("editCellStart"));
        }
        
        if (variables.containsKey("editCellFinish")) {
            hideEditor(variables.get("editCellFinish"));
        }
        
        if (variables.containsKey("showExpander")) {
            showExpander();
        }
    }
    
    private Component expander = null;
    
    private void showExpander() {
        if (expander != null) {
            removeComponent(expander);
        }
        
        expander = new TextField("I AM SUPER EXPANDER");
        expander.setSizeFull();
        expander.addStyleName(Reindeer.LABEL_H2);
        addComponent(expander);
        requestRepaint();
    }

    public void handleCellClick(Object object) {
        
    }

    private Map<Object, Component> editors = new HashMap<Object, Component>();
    
    private void hideEditor(final Object columnId) {
        final Component editor = editors.get(columnId);
        if (editor != null) {
            editors.remove(columnId);
            removeComponent(editor);
            requestRepaint();
        }
    }

    private void showEditor(final Object columnId) {
        final Object propId = columnInfo.getPropertyIdForKey(String.valueOf(columnId));
        final Field c = getParent().getCellEditorFactory().createCellEditor(item, propId);
        c.setPropertyDataSource(item.getItemProperty(propId));
        editors.put(columnId, c);
        addComponent(c);
        requestRepaint();
    }

    @Override
    public Iterator<Component> getComponentIterator() {
        return propertyComponentMap.values().iterator();
    }

    @Override
    public void setItemDataSource(Item newDataSource) {
        this.item = newDataSource;
        requestRepaint();
    }

    @Override
    public Item getItemDataSource() {
        return item;
    }
    
    @Override
    public void replaceComponent(Component oldComponent, Component newComponent) {}

    @Override
    public void itemPropertySetChange(PropertySetChangeEvent event) {}
    
    @Override
    public String toString() {
        if (item != null) {
            return String.valueOf(item.getItemProperty("p1").getValue());
        }
        return super.toString();
    }
    
    @Override
    public GridBody<?> getParent() {
        final Component parent = super.getParent();
        return parent == null ? null : (GridBody<?>)super.getParent();
    }
    
    @Override
    public void setSizeFull() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setWidth(String width) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setHeight(String height) {
        throw new UnsupportedOperationException();
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
   
}
