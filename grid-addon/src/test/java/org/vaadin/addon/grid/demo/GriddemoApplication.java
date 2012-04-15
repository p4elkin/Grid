package org.vaadin.addon.grid.demo;

import org.vaadin.addon.grid.Grid;
import org.vaadin.addon.grid.client.ui.VColumnModel.Align;
import org.vaadin.addon.grid.header.HeaderComponentFactory;

import com.vaadin.Application;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Container.Indexed;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class GriddemoApplication extends Application {
    @Override
    public void init() {
        Window mainWindow = new Window("GridDemo");
        mainWindow.getContent().setSizeFull();
        ((VerticalLayout) mainWindow.getContent()).setSpacing(true);
        Indexed c = getTestContainer();
        final Table table = new Table();
        table.setImmediate(true);
        table.setSizeUndefined();
        table.setHeight("100%");
        table.setWidth("100%");
        table.setContainerDataSource(c);
        table.setCacheRate(0.5d);

        table.setNullSelectionAllowed(false);
        final Grid grid = new Grid(c);
        grid.setWidth("100%");
        grid.setHeight("100%");

        mainWindow.addComponent(grid);
        grid.getBody().setCelEditingEnabled(true);
        grid.setColumnWidth("p1", 200);
        grid.setImmediate(true);
        grid.setColumnAlignment("p1", Align.RIGHT);
        grid.setColumnAlignment("p2", Align.CENTER);
        grid.getHeader().setHeaderFactory(new HeaderComponentFactory() {
            @Override
            public Component createComponent(final Grid grid, final Object propertyId) {
                final TextField l = new TextField("<b>" + propertyId + "<\b>");
                l.setImmediate(true);
                l.setWidth("100%");
                l.addListener(new ValueChangeListener() {
                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        ((Filterable) grid.getContainerDataSource()).addContainerFilter(new Filter() {

                            @Override
                            public boolean passesFilter(Object itemId, Item item) throws UnsupportedOperationException {
                                return item.getItemProperty(propertyId).getValue().toString()
                                        .contains(l.getValue().toString());
                            }

                            @Override
                            public boolean appliesToProperty(Object id) {
                                return id.equals(propertyId);
                            }

                        });
                    }
                });
                return l;
            }
        });
        table.setEditable(true);
        //mainWindow.addComponent(table);
        setMainWindow(mainWindow);
        //setTheme("griddemotheme");
    }

    public static Indexed getTestContainer() {
        final IndexedContainer container = new IndexedContainer();
        container.addContainerProperty("p1", Integer.class, null);
        container.addContainerProperty("p2", String.class, "p2");

        for (int i = 0; i < 1000; ++i) {
            final Item item = container.getItem(container.addItem());
            String val = "" + i;
            item.getItemProperty("p2").setValue(val);
            item.getItemProperty("p1").setValue(i);
        }

        return container;
    }

}
