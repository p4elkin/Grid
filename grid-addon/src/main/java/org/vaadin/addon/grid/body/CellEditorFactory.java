package org.vaadin.addon.grid.body;

import java.io.Serializable;

import com.vaadin.data.Item;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;

@SuppressWarnings("serial")
public interface CellEditorFactory extends Serializable {
    
    Field createCellEditor(final Item item, final Object propertyId);

    CellEditorFactory DEFAULT = new CellEditorFactory() {
        
        @Override
        public Field createCellEditor(Item item, Object propertyId) {
            return new TextField();
        }
    };
}
