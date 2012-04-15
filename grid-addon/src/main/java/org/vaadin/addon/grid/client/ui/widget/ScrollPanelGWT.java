package org.vaadin.addon.grid.client.ui.widget;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasScrolling;
import com.vaadin.terminal.gwt.client.BrowserInfo;

public class ScrollPanelGWT extends FlowPanel implements HasScrolling {

    protected final static int SCROLLBAR_VISIBILY_THRESHOLD = 60;

    protected final static String CLASS_NAME = "v-scroll-pane";
    
    private ScrollBar vScrollbar = new ScrollBar(true);
    
    private ScrollBar hScrollbar = new ScrollBar(false);

    private ScrollBarStateObserver stateObserver = new ScrollBarStateObserver();
    
    private Element root;

    private DragContext context = new DragContext();
    
    private MouseDownHandler mouseDownHandler = new MouseDownHandler() {
        @Override
        public void onMouseDown(MouseDownEvent event) {
            final Element target = event.getNativeEvent().getEventTarget().cast();
            if (vScrollbar.isDrag(target)) {
                context.startDragging(vScrollbar, event.getClientY());
                DOM.setCapture(vScrollbar.getDrag());
                event.preventDefault();
            }
            
            if (hScrollbar.isDrag(target)) {
                context.startDragging(hScrollbar, event.getClientX());
                DOM.setCapture(hScrollbar.getDrag());
                event.preventDefault();
            }
        }
    };
    
    private MouseUpHandler mouseUpHandler = new MouseUpHandler() {
        @Override
        public void onMouseUp(MouseUpEvent event) {
            if (context.isDragging) {
                DOM.releaseCapture(context.dragScrollBar.getDrag());
                context.stopDragging();
            }
        }
    };
    
    private MouseWheelHandler mouseWheelHandler = new MouseWheelHandler() {
        @Override
        public void onMouseWheel(final MouseWheelEvent event) {
            final int delta = event.getDeltaY();
            final double deltaX = getDeltaX(event.getNativeEvent());
            if (deltaX != 0) {
                scrollHorizontally((int)deltaX);
            } else {
                scrollVertically(delta);
            }
        }
    };
    
    private MouseMoveHandler mouseMoveHandler = new MouseMoveHandler() {
        @Override
        public void onMouseMove(MouseMoveEvent event) {
            if (context.isDragging) {
                if (context.dragScrollBar == vScrollbar) {
                    scrollVertically(context.getDelta(event.getClientY()));
                }
                if (context.dragScrollBar == hScrollbar) {
                    scrollHorizontally(context.getDelta(event.getClientX()));
                }
                event.preventDefault();
            } else {
                int rightEdgeDistance = getElement().getAbsoluteRight() - event.getClientX();
                int bottomDistance = getElement().getAbsoluteBottom() - event.getClientY();
                boolean showVSB = rightEdgeDistance <= SCROLLBAR_VISIBILY_THRESHOLD;
                boolean showHSB = bottomDistance <= SCROLLBAR_VISIBILY_THRESHOLD;
                updateScrollBarsVisibility(showVSB, showHSB);
            }
        }
    };
    
    public ScrollPanelGWT() {
        super();
        this.root = getElement();
        setStyleName(CLASS_NAME);
        root.appendChild(vScrollbar.get());
        root.appendChild(hScrollbar.get());
        DOM.sinkEvents(getElement(), Event.MOUSEEVENTS);
        addDomHandler(mouseDownHandler, MouseDownEvent.getType());
        addDomHandler(mouseUpHandler, MouseUpEvent.getType());
        addDomHandler(mouseMoveHandler, MouseMoveEvent.getType());
        addDomHandler(mouseWheelHandler, MouseWheelEvent.getType());
    }

    @Override
    public int getMaximumHorizontalScrollPosition() {
        return getScrollWidth() - getClientWidth();
    }

    @Override
    public int getMaximumVerticalScrollPosition() {
        return getScrollHeight() - getClientHeight();
    }
    
    @Override
    public int getMinimumHorizontalScrollPosition() {
        return 0;
    }

    @Override
    public int getMinimumVerticalScrollPosition() {
        return 0;
    }

    @Override
    public int getHorizontalScrollPosition() {
        return root.getScrollLeft();
    }
    
    @Override
    public int getVerticalScrollPosition() {
        return root.getScrollTop();
    }

    @Override
    public void setHorizontalScrollPosition(int position) {
        scrollVertically(position - getHorizontalScrollPosition());
    }
    
    @Override
    public void setVerticalScrollPosition(int position) {
        scrollVertically(position - getVerticalScrollPosition());
    }

    @Override
    public HandlerRegistration addScrollHandler(ScrollHandler handler) {
        return addDomHandler(handler, ScrollEvent.getType());
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        stateObserver.scheduleRepeating(100);
    }
    
    @Override
    protected void onUnload() {
        super.onUnload();
        stateObserver.cancel();
    }
    
    public int getScrollHeight() {
        return root.getScrollHeight();
    }

    public int getScrollWidth() {
        return root.getScrollWidth();
    }
    
    public int getClientHeight() {
        return root.getClientHeight();
    }

    public int getClientWidth() {
        return root.getClientWidth();
    }
    
    public void setScrollTop(int scrollTop) {
        root.setScrollTop(scrollTop);
        vScrollbar.get().getStyle().setTop(scrollTop, Unit.PX);
        hScrollbar.get().getStyle().setBottom(-scrollTop, Unit.PX);
    }
    
    private void setScrollLeft(int scrollLeft) {
        root.setScrollLeft(scrollLeft);
        hScrollbar.get().getStyle().setLeft(scrollLeft, Unit.PX);
        vScrollbar.get().getStyle().setRight(-scrollLeft, Unit.PX);
    }
    
    protected void scrollVertically(int deltaY) {
        int clientHeight = getClientHeight();
        int scrollHeight = getScrollHeight();
        int scrollTop = getVerticalScrollPosition();
        double ratio = clientHeight / (double) scrollHeight;
        if (scrollHeight > clientHeight && ratio != 0) {
            int scrollBarSize = Math.max((int) (clientHeight * ratio), 20);
            int newScrollTop = validateScrollTop((int) (scrollTop + deltaY / (ratio))); 
            setScrollTop(newScrollTop);
            vScrollbar.show(scrollBarSize, (int) (newScrollTop * ratio));
            if (deltaY != 0) {
                DomEvent.fireNativeEvent(Document.get().createScrollEvent(), this);
            }
        }
    }
    
    private void scrollHorizontally(int deltaX) {
        int clientWidth = root.getClientWidth();
        double scrollWidth = root.getScrollWidth();
        int scrollLeft = getHorizontalScrollPosition();
        double ratio = clientWidth / scrollWidth;
        if (scrollWidth > clientWidth && ratio != 0) {
            int scrollBarSize = Math.max((int) (clientWidth * ratio), 10);
            int newScrollLeft = validateScrollLeft((int) (scrollLeft + deltaX / ratio)); 
            setScrollLeft(newScrollLeft);
            hScrollbar.show(scrollBarSize, (int) (newScrollLeft * ratio));
            if (deltaX != 0) {
                DomEvent.fireNativeEvent(Document.get().createScrollEvent(), this);
            }
        }        
    }

    protected void updateScrollbars() {
        scrollVertically(0);
        scrollHorizontally(0);
    }
    
    protected int validateScrollLeft(int newScrollLeft) {
        if (newScrollLeft < getMinimumHorizontalScrollPosition()) {
            newScrollLeft = getMinimumHorizontalScrollPosition();
        } else if (newScrollLeft > getMaximumHorizontalScrollPosition()) {
            newScrollLeft = getMaximumHorizontalScrollPosition();
        }
        return newScrollLeft;
    }
    
    protected int validateScrollTop(int newScrollTop) {
        if (newScrollTop < getMinimumVerticalScrollPosition()) {
            newScrollTop = getMinimumVerticalScrollPosition();
        } else if (newScrollTop > getMaximumVerticalScrollPosition()) {
            newScrollTop = getMaximumVerticalScrollPosition();
        }
        return newScrollTop;
    }
    
    protected void updateScrollBarsVisibility(boolean showVSB, boolean showHSB) {
        if (showHSB) {
            scrollHorizontally(0);
        }
        if (showVSB) {
            scrollVertically(0);
        }
    }
    
    protected native double getDeltaX(NativeEvent event) /*-{
        var delta = 0;
        var deltaX = 0;
        if (event.wheelDelta) { 
            delta = event.wheelDelta / 120; 
        }
        
        if (event.detail) { 
            delta = -event.detail / 3; 
        }
        
        if (event.axis !== undefined && event.axis === event.HORIZONTAL_AXIS ) {
            deltaX = -delta;
        }
        
        if (event.wheelDeltaX !== undefined) { 
            deltaX = -event.wheelDeltaX / 120; 
        }
        
        return deltaX;
    }-*/;
    
    protected native double getDeltaY(NativeEvent event) /*-{
        var delta = 0;
        var deltaY = 0;
        if (event.wheelDelta) { 
            delta = event.wheelDelta / 120; 
        }
    
        if (event.detail) { 
            delta = -event.detail / 3; 
        }
    
        if (event.axis !== undefined && event.axis === event.HORIZONTAL_AXIS ) {
            deltaY = 0;
        }
    
        if (event.wheelDeltaY !== undefined) { 
            deltaY = event.wheelDeltaY / 120; 
        }
    
        return deltaX;
    }-*/;
    
    private class ScrollBarStateObserver extends Timer {
        
        @Override
        public void run() {
            if (vScrollbar.expired()) {
                vScrollbar.hide();
            }
            if (hScrollbar.expired()) {
                hScrollbar.hide();
            }
        }
    }
    
    private static class DragContext {
        
        boolean isDragging;
        
        int currentPosition;
        
        ScrollBar dragScrollBar;
    
        void startDragging(ScrollBar scrollBar, int position) {
            this.isDragging = true;
            this.dragScrollBar = scrollBar;
            this.currentPosition = position;
        }
        
        void stopDragging() {
            dragScrollBar = null;
            isDragging = false;
            currentPosition = -1;
        }
        
        int getDelta(int newPosition) {
            int result = newPosition - currentPosition;
            currentPosition = newPosition;
            return result;
        }
    }
   
    
    public static class ScrollBar {
        
        private final static String CLASS_NAME = "v-scrollbar";
        
        private final static String V_SUFFIX = "-vertical";
        
        private final static String H_SUFFIX = "-horizontal";
        
        private Element trackElement = DOM.createDiv();
        
        private Element dragElement = DOM.createDiv();
        
        private final boolean isVertical;
        
        private long lastUpdate = 0;
        
        private final FadeAnimation fadeAnimation = new FadeAnimation();
        
        public ScrollBar(boolean isVertical) {
            super();
            this.isVertical = isVertical;
            trackElement.appendChild(dragElement);
            trackElement.setClassName(CLASS_NAME + (isVertical ? V_SUFFIX : H_SUFFIX));
            dragElement.setClassName(CLASS_NAME + (isVertical ? V_SUFFIX : H_SUFFIX) + "-drag");
        }
        
        public Element getDrag() {
            return dragElement;
        }

        public boolean isDrag(final Element target) {
            return DOM.isOrHasChild(dragElement, target);
        }

        public Element get() {
            return trackElement;
        }

        public boolean isVertical() {
            return isVertical;
        }
 
        public boolean isVisible() {
            return "visible".equals(dragElement.getStyle().getVisibility());
        }
        
        public void show(int size, int position) {
            update(size, position);
            if (!isVisible()) {
                fadeAnimation.start(true);
            }
        }
        
        public void hide() {
            if (isVisible()) {
                fadeAnimation.start(false);
            }
        }
        
        public boolean expired() {
            return System.currentTimeMillis() - lastUpdate > 200000; 
        }
        
        private void update(int size, int position) {
            if (isVertical) {
                dragElement.getStyle().setTop(position, Unit.PX);
                dragElement.getStyle().setHeight(size, Unit.PX);
            } else {
                dragElement.getStyle().setLeft(position, Unit.PX);
                dragElement.getStyle().setWidth(size, Unit.PX);            
            }
            lastUpdate = System.currentTimeMillis();
        }
        
        private class FadeAnimation extends Animation {
            
            private boolean fadeIn;

            public void start(boolean isShowing) {
                cancel();
                this.fadeIn = isShowing;
                run(300);
            }

            @Override
            protected void onStart() {
                super.onStart();
                if (fadeIn) {
                    dragElement.getStyle().setVisibility(Visibility.VISIBLE);
                }
            }
            
            @Override
            protected void onUpdate(double progress) {
                final String msOpacityPrpertyValue = "alpha(opacity=";
                double value = fadeIn ? progress : 1d - progress;
                if (BrowserInfo.get().isIE8()) {
                    dragElement.getStyle().setProperty("filter", msOpacityPrpertyValue + (int) (value * 100) + ")");
                } else {
                    dragElement.getStyle().setOpacity(value);
                }
            }
            
            @Override
            protected void onComplete() {
                super.onComplete();
                if (!fadeIn) {
                    dragElement.getStyle().setVisibility(Visibility.HIDDEN);
                }
            }
        }
    }
}
