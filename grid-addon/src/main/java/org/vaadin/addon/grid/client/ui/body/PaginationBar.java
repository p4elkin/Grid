package org.vaadin.addon.grid.client.ui.body;

import org.vaadin.addon.grid.client.event.PageNavigationEvent;
import org.vaadin.addon.grid.client.event.PagingContextChangedEvent;
import org.vaadin.addon.grid.client.event.handler.PagingContextChangeHandler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.web.bindery.event.shared.EventBus;

public class PaginationBar extends FlowPanel implements PagingContextChangeHandler {
    
    private Element controlWrapper = DOM.createDiv();
    
    private Button nextButton = new Button("->");
    
    private Button previousButton = new Button("<-");
    
    private TextBox pageBox = new TextBox(); 
    
    private Label pageDetails = new Label();
    
    private int totalRows;
    
    private int pageLength = 50;
    
    private int currentPage;
    
    public PaginationBar(final EventBus bus) {
        super();
        bus.addHandler(PagingContextChangedEvent.TYPE, this);
        getElement().appendChild(controlWrapper);
        construct();
        addStyleName("v-paging-bar");
        previousButton.addStyleName("v-prevpage-button");
        pageBox.addStyleName("v-page-indicator-textbox");
        nextButton.addStyleName("v-nextpage-button");
        controlWrapper.setClassName("v-control-wrapper");
        pageDetails.addStyleName("v-paging-details");
        nextButton.addClickHandler(new ClickHandler() { 
            @Override
            public void onClick(ClickEvent event) {
                ++currentPage;
                pageBox.setValue(String.valueOf(currentPage), true);
            }
        });
        
        previousButton.addClickHandler(new ClickHandler() { 
            @Override
            public void onClick(ClickEvent event) {
                --currentPage;
                pageBox.setValue(String.valueOf(currentPage), true);
            }
        });
        
        pageBox.addValueChangeHandler(new ValueChangeHandler<String>() {
            
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                try {
                    currentPage = Integer.parseInt(event.getValue());
                    updateControls();
                    bus.fireEvent(new PageNavigationEvent(currentPage));
                } catch(NumberFormatException e) {
                    pageBox.setValue(String.valueOf(currentPage));
                    return;
                }
            }
        });
        
    }

    private void construct() {
        add(previousButton, controlWrapper);
        add(pageBox, controlWrapper);
        add(nextButton, controlWrapper);
        add(pageDetails);
    }

    @Override
    public void onContextChanged(PagingContextChangedEvent event) {
        totalRows = event.getTotalRows();
        currentPage = event.getCurrentPage();
        pageLength = event.getPageLength();
        updateControls();
    }

    private void updateControls() {
        previousButton.setEnabled(currentPage > 0);
        nextButton.setEnabled((int)Math.ceil(totalRows / (double)pageLength) > currentPage);
        int first = pageLength * currentPage;
        int last = ( currentPage + 1) * pageLength;
        pageDetails.setText("Showing: " + first + " - " + last + " of " + totalRows);
        pageBox.setValue(String.valueOf(currentPage), false);
    }
}
