package org.vaadin.addon.grid.header;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.vaadin.addon.grid.ColumnModel;
import org.vaadin.addon.grid.AbstractGrid;
import org.vaadin.addon.grid.client.ui.header.VGridHeader;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.ClientWidget;
import com.vaadin.ui.ClientWidget.LoadStyle;
import com.vaadin.ui.Component;

@SuppressWarnings("serial")
@ClientWidget(value = VGridHeader.class, loadStyle = LoadStyle.EAGER)
public class GridHeader extends AbstractLayout {

    private final Map<String, Component> headerComponents = new HashMap<String, Component>();
    
    private final AbstractGrid grid;
    
    private HeaderComponentFactory factory = new HeaderComponentFactory.Default();
    
    public GridHeader(AbstractGrid grid) {
        super();
        this.grid = grid;
        setImmediate(true);
        setSizeFull();
    }
    
    @Override
    public void replaceComponent(Component oldComponent, Component newComponent) {}

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);
        final List<String> visibleColumnKeys = grid.getColumnModel().getVisibleColumnsKeys();
        for (final String key : visibleColumnKeys) {
            final Component component = headerComponents.get(key);
            assert component != null;
            target.startTag("header");
            target.addAttribute("key", key);
            component.paint(target);
            target.endTag("header");
        }
        
    }
    
    @Override
    public Iterator<Component> getComponentIterator() {
        return headerComponents.values().iterator();
    }

    @Override
    public void removeAllComponents() {
        super.removeAllComponents();
        headerComponents.clear();
    }
    
    public void setVisibleColumns(final List<String> visibleColumnsKeys) {
        recreateComponents(visibleColumnsKeys);
    }

    public void setHeaderFactory(final HeaderComponentFactory headerComponentFactory) {
        this.factory = headerComponentFactory;
        recreateComponents(grid.getColumnModel().getVisibleColumnsKeys());
    }
    
    private void recreateComponents(final List<String> visibleColumnsKeys) {
        removeAllComponents();
        final ColumnModel columnInfo = grid.getColumnModel();
        for (final String key : visibleColumnsKeys) {
            final Component c = factory.createComponent(grid, columnInfo.getPropertyIdForKey(key));
            headerComponents.put(key, c);
            addComponent(c);
        }
    }
}
