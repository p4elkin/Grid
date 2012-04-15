package org.vaadin.addon.grid.client.ui.widget;

import org.vaadin.csstools.client.ComputedStyle;

public class VGridBodyScrollPanel extends ScrollPanelGWT {

    private ComputedStyle style;
    
    @Override
    protected void onLoad() {
        super.onLoad();
        style = new ComputedStyle(getElement());
        addStyleName("v-body-scrollpanel");
    }

    public int getDecorationsWidth() {
        return style.getBorder()[1] + style.getBorder()[3];
    }

    public int getDecorationsHeight() {
        return style.getBorder()[0] + style.getBorder()[2];
    }
}
