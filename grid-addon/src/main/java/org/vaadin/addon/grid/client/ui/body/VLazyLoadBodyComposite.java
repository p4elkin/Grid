package org.vaadin.addon.grid.client.ui.body;

import org.vaadin.addon.grid.client.ui.widget.VGridBodyScrollPanel;

@SuppressWarnings("serial")
public class VLazyLoadBodyComposite extends VGridBodyComposite<VLazyLoadBody> {

    @Override
    protected VGridBodyScrollPanel createScrollPanel() {
        return new VGridBodyScrollPanel() {
            private int scrollTop = 0;
            
            @Override
            public int getVerticalScrollPosition() {
                return scrollTop;
            }
            
            @Override
            public int getScrollHeight() {
                int result = 0;
                final VAbstractGridBody body = getBody();
                if (body != null) {
                    result = body.getTotalRows() * (int)body.getRowHeight();
                }
                return result;
            }
            
            @Override
            public void setScrollTop(int scrollTop) {
                final VLazyLoadBody body = getBody();
                if (body != null) {
                    this.scrollTop = scrollTop;
                    super.setScrollTop(body.beforeScrollTopSet(scrollTop));
                }
            }
        };
    }
    
    @Override
    protected VLazyLoadBody createGridBody() {
        return new VLazyLoadBody();
    }
    
    @Override
    protected VBodyProxy createGridBodyProxy() {
        return new VBodyProxy(this);
    }

}
