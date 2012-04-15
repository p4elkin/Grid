package org.vaadin.addon.grid.client.ui.body;


/*@SuppressWarnings("serial")
public class VGridBody extends ComplexPanel implements Container, ClientSideHandler, ScrollHandler {

    private static final String CLASS_NAME = "v-grid-body";

    private final Element viewPort = DOM.createDiv();

    private final Element container = DOM.createDiv();
    
    private final List<VGridRow> rows = new LinkedList<VGridRow>();

    private VGridBodyScrollPanel scrollPanel;

    private VGridBodyProxy proxy;

    private ComputedStyle style;

    private RenderInfo renderInfo;

    private VColumnModel columnInfoHolder;

    private VEditorDelegate editDelegate;
    
    private final RowRequestSender rowRequestSender = new RowRequestSender();

    private boolean rowHeightDetermined = false;

    private double cacheRate = 1;

    private double rowHeight = 0;

    private double reactionRate = 0.75;

    private int pageLength = 5;

    private int totalRows = 0;

    protected ApplicationConnection client;

    protected String paintableId;

    protected boolean isDebug = false;
    
    public VGridBody() {
        super();
        this.columnInfoHolder = new VColumnModel();
        this.renderInfo = new RenderInfo();
        this.proxy = new VGridBodyProxy(this);
        setElement(container);
        setStyleName(CLASS_NAME);
        constructDOM();
        viewPort.addClassName("v-viewport");
        DOM.sinkEvents(getElement(), Event.MOUSEEVENTS);
    }

    public VColumnModel getColumnInfo() {
        return columnInfoHolder;
    }

    private void constructDOM() {
        container.appendChild(viewPort);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        final Widget parent = getParent();
        style = new ComputedStyle(container);
        if (parent != null && parent instanceof VGridBodyScrollPanel) {
            scrollPanel = (VGridBodyScrollPanel) parent;
            scrollPanel.addScrollHandler(this);
        }
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        this.client = client;
        this.paintableId = uidl.getId();
        proxy.update(this, uidl, client);
        renderRows(uidl);
    }

    private void renderRows(UIDL uidl) {
        final UIDL rowData = uidl.getChildByTagName("rows");
        int rowCount = rowData.getChildCount();
        if (rowData != null) {
            int index = 0;
            while (index < rowCount) {
                final UIDL rowUidl = rowData.getChildUIDL(index);
                if ("row".equals(rowUidl.getTag())) {
                    renderRow(rowUidl.getChildUIDL(0), index);
                }
            }
        }
        checkAddtionalRowsNeed();
    }

    private void renderRow(UIDL uidl, int pos) {
        final Paintable rowPaintable = client.getPaintable(uidl);
        if (rowPaintable != null && rowPaintable instanceof VGridRow) {
            final VGridRow row = (VGridRow) rowPaintable;
            ensureRowAttached(row, pos);
            row.removeStyleName("v-cells-hidden");
            rowPaintable.updateFromUIDL(uidl, client);
        }
    }

    public void finalizeRowsRender(int firstRendered, int lastRendered) {
        renderInfo.setBounds(firstRendered, lastRendered);
        if (isDebug) {
            System.out.println("New scroll top " + scrollPanel.getVerticalScrollPosition());
        }
    }

    private void removeRows(int amount, boolean isFromHead) {
        if (amount > 0) {
            System.out.println("Removing " + amount + " rows from " + (isFromHead ? " head " : "tail"));
        }
        while (amount > 0) {
            final VGridRow row = (VGridRow) getWidget(isFromHead ? 0 : getChildren().size() - 1);
            client.unregisterPaintable(row);
            remove(row);
            rows.remove(row);
            --amount;
        }
    }

    private void ensureRowAttached(VGridRow row, int position) {
        if (row.getParent() == this) {
            if (getWidgetIndex(row) != position) {
                row.removeFromParent();
                getChildren().insert(row, position);
                viewPort.insertBefore(row.getElement(), viewPort.getChildNodes().getItem(position));
                adopt(row);
            } 
        } else {
            insertRow(row, position);
        }
    }

    protected void insertRow(VGridRow row, int position) {
        rows.add(row);
        getChildren().insert(row, position);
        boolean insert = true;
        if (rows.size() == position) {
            insert = false;
        }
        if (insert) {
            viewPort.insertBefore(row.getElement(), viewPort.getChildNodes().getItem(position));
        } else {
            viewPort.appendChild(row.getElement());
        }
        adopt(row);
    }

    private void checkAddtionalRowsNeed() {
        if (isDebug) {
            System.out.println("Rows: " + rows.size());
        }
        proxy.checkRowAmount((int) Math.floor(scrollPanel.getVerticalScrollPosition() / getRowHeight()));
    }

    
    public void setCellEditable(boolean isEditable) {
            if (isEditable && editDelegate == null) { 
                editDelegate = new VEditorDelegate(this);
                editDelegate.bind();
            } else  if (editDelegate != null) {
                editDelegate.unBind();
                editDelegate = null;
            }
    }
    
    public void setTotalRows(int totalRows) {
        if (this.totalRows != totalRows) {
            this.totalRows = totalRows;
        }
    }

    public void setDefaultColumnWidth(Integer width) {
        columnInfoHolder.setDefaultColumnWidth(width);
    }

    public void setColumnWidth(String columnKey, int pxWidth) {
        columnInfoHolder.setColumnWidth(columnKey, pxWidth);
    }

    public void setColumnExpandRatio(String columnKey, float ratio) {
        columnInfoHolder.setColumnExpandRatio(columnKey, ratio);
    }

    public void setColumnWidths(final Map<String, Number> widths) {
        columnInfoHolder.setColumnWidths(widths);
    }

    void setVisibleColumns(final List<String> ids) {
        columnInfoHolder.setVisibleColumns(ids);
    }

    public void setCollapsedColumns(final List<String> columns) {
        columnInfoHolder.setCollapsedColumnKeys(columns);
    }

    @Override
    public void setWidth(String width) {
        if (isAttached()) {
            super.setWidth(width);
            int formerWidth = style.getIntProperty("width");
            if (formerWidth != columnInfoHolder.getViewPortWidth()) {
                columnInfoHolder.setViewPortWidth(style.getIntProperty("width"));
            }
        }
    }

    @Override
    public void setHeight(String height) {
        super.setHeight(height);
        if (proxy.isClientInitialized()) {
            updatePageLength();
        }
    }

    private void updatePageLength() {
        final Integer estimate = (int) Math.ceil((double) scrollPanel.getClientHeight() / getRowHeight());
        if (estimate != pageLength) {
            pageLength = estimate;
            proxy.call("setPageLength", estimate);
        }
    }

    @Override
    public boolean initWidget(Object[] params) {
        pageLength = (Integer) params[0];
        totalRows = (Integer) params[1];
        cacheRate = (Float) params[2];
        renderInfo.setFirstRendered((Integer) params[4]);
        renderInfo.setLastRendered((Integer) params[5]);
        return false;
    }

    @Override
    public void handleCallFromServer(String method, Object[] params) {
    }

    public VGridBodyScrollPanel getScrollPanel() {
        return scrollPanel;
    }

    public int getRequiredHeight() {
        return (int) Math.max((pageLength * getRowHeight()), scrollPanel.getClientHeight());
    }

    public double getRowHeight() {
        return getRowHeight(false);
    }

    public double getRowHeight(boolean forceUpdate) {
        if (rowHeightDetermined && !forceUpdate) {
            return rowHeight;
        } else {
            if (viewPort.getChildCount() > 0) {
                int viewportHeight = viewPort.getOffsetHeight();
                int rowCount = viewPort.getChildCount();
                rowHeight = viewportHeight / (double) rowCount;
            } else {
                if (isAttached()) {
                    final VGridRow scrollTableRow = new VGridRow();
                    viewPort.appendChild(scrollTableRow.getElement());
                    getRowHeight(forceUpdate);
                    viewPort.removeChild(scrollTableRow.getElement());
                }
            }
            rowHeightDetermined = true;
            return rowHeight;
        }
    }

    @Override
    public void replaceChildComponent(Widget oldComponent, Widget newComponent) {

    }

    @Override
    public boolean hasChildComponent(Widget component) {
        return rows.contains(component);
    }

    @Override
    public void updateCaption(Paintable component, UIDL uidl) {
    }

    @Override
    public boolean requestLayout(Set<Paintable> children) {
        return false;
    }

    @Override
    public RenderSpace getAllocatedSpace(Widget child) {
        return new RenderSpace();
    }

    @Override
    public void onScroll(ScrollEvent event) {
        if (isAttached()) {
            rowRequestSender.cancelPendingRowRequest();
            rowRequestSender.scheduleRowFetch();
        }
    }

    public int getPageLength() {
        return pageLength;
    }

    public double getReactionRate() {
        return reactionRate;
    }

    public double getCacheRate() {
        return cacheRate;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public int transferRows(int count, boolean fromHead) {
        int spin = 0;
        int actualCount = count;//validateTransferAmount(count, fromHead);
        if (isDebug) {
            System.out.println("Transferring rows: " + count + (fromHead ? "from head" : " from tail"));
        }
        while (spin < actualCount) {
            int index = fromHead ? 0 : getWidgetCount() - 1;
            final VGridRow row = (VGridRow) getWidget(index);
            row.hideCells();
            if (fromHead) {
                row.setOdd((renderInfo.getLast() + spin) % 2 == 0);
                add(row, viewPort);
            } else {
                row.setOdd((renderInfo.getFirst() - spin) % 2 == 0);
                insert(row, viewPort, 0, true);
            }
            ++spin;
        }
        renderInfo.shiftIndices(actualCount, fromHead);
        if (isDebug) {
            System.out.println(renderInfo);
        }
        return actualCount;
    }


    private class RowRequestSender extends Timer {

        private static final int DEFAULT_DELAY = 250;

        @Override
        public void run() {
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    proxy.processRowRequest();
                }
            });
        }

        public void cancelPendingRowRequest() {
            cancel();
        }

        public void scheduleRowFetch() {
            schedule(DEFAULT_DELAY);
        }
    }

    public RenderInfo getRenderInfo() {
        return renderInfo;
    }

    public int getFirstRendered() {
        return renderInfo.getFirst();
    }

    public void setColumnAlignment(String columnId, Align align) {
        columnInfoHolder.setColumnAlignment(columnId, align);
    }

    public int getRenderedRowsCount() {
        return rows.size();
    }
}*/
