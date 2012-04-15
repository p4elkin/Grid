package org.vaadin.addon.grid.client.ui.header;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class VGridHeaderCell extends SimplePanel {

    private final static String CLASS_NAME = "v-header-cell";
    
    public VGridHeaderCell() {
        super();
        addStyleName(CLASS_NAME);
    }

    public Widget getContent() {
        return getWidget();
    }
    
    public void setContent(final Widget w) {
        setWidget(w);
    }
}
