package org.vaadin.addon.grid;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.vaadin.addon.grid.client.common.GridConst;
import org.vaadin.addon.grid.client.ui.VColumnModel;

import com.vaadin.terminal.KeyMapper;

@SuppressWarnings("serial")
public class ColumnModel implements Serializable {
    
    private final Map<String, Number> columnWidths = new HashMap<String, Number>();
    
    private final Map<String, VColumnModel.Align> columnAligns = new HashMap<String, VColumnModel.Align>();
    
    private final List<String> collapsedColumnsKeys = new LinkedList<String>();
    
    private final List<String> visibleColumnKeys = new LinkedList<String>();
    
    private final KeyMapper mapper = new KeyMapper();
    
    private int defaultColummnWidth = GridConst.DEFAULT_COL_WIDTH;
    
    private final String token;
   
    public ColumnModel() {
        super();
        token = "test";
    }

    public void setVisibleColumns(final Collection<?> ids) {
        mapper.removeAll();
        columnWidths.clear();
        columnAligns.clear();
        visibleColumnKeys.clear();
        collapsedColumnsKeys.clear();
        for (final Object id : ids) {
            visibleColumnKeys.add(mapper.key(id));
        }
    }
    
    public Map<?,?> getColumnWidths() {
        return columnWidths;
    }
    
    public List<String> getCollapsedColumnsKeys() {
        return Collections.<String>unmodifiableList(collapsedColumnsKeys);
    }
    
    public List<String> getVisibleColumnsKeys() {
        return Collections.<String>unmodifiableList(visibleColumnKeys);
    }
    
    public Object getPropertyIdForKey(String columnKey) {
        return mapper.get(columnKey);
    }
    
    public int getDefaultColummnWidth() {
        return defaultColummnWidth;
    }
    
    public List<String> setCollapsedColumns(final List<?> collapsedColumns) {
        this.collapsedColumnsKeys.clear();
        for (final Object id : collapsedColumns) {
            final String key = mapper.key(id);
            if (visibleColumnKeys.contains(key)) {
                this.collapsedColumnsKeys.add(key);
            }
        }
        return Collections.<String>unmodifiableList(this.collapsedColumnsKeys);
    }
    
    public void setColumnAlign(final Object propertyId, VColumnModel.Align align) {
        columnAligns.put(mapper.key(propertyId), align);
    }
    
    public void setColumnWidth(final Object propertyId, int width) {
        doSetWidth(propertyId, width);
    }
    
    public void setColumnExpandRatio(final Object propertyId, float width) {
        doSetWidth(propertyId, width);
    }
   
    public void setDefaultColummnWidth(int defaultColummnWidth) {
        this.defaultColummnWidth = defaultColummnWidth;
    }

    
    private <T extends Number> void doSetWidth(final Object propertyId, T width) {
        final String propertyKey = mapper.key(propertyId);
        if (visibleColumnKeys != null && visibleColumnKeys.contains(propertyKey)) {
            if (width.floatValue() < 0) {
                columnWidths.remove(propertyKey);
            }
            columnWidths.put(propertyKey, width);
        }
    }

    public String getKey(Object columnId) {
        return mapper.key(columnId);
    }

    public String getStyleToken() {
        return token;
    }

}
