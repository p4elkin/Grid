package org.vaadin.addon.grid.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.vaadin.addon.grid.client.common.GridConst;
import org.vaadin.addon.grid.client.ui.css.CSSRule;
import org.vaadin.addon.grid.client.ui.css.ComputedStyle;
import org.vaadin.addon.grid.client.ui.header.VGridHeader;

public class VColumnModel {

    public static enum Align {
        LEFT("left"),
        RIGHT("right"),
        CENTER("center");

        private String cssValue;
        
        private Align(final String value) {
            this.cssValue = value;
        }
        public String getCssValue() {
            return cssValue;
        }
    }
    
    private final List<String> visibleColumnsKeys = new ArrayList<String>();

    private final List<String> collapsedColumnsKeys = new ArrayList<String>();

    private final Map<String, Number> columnWidths = new HashMap<String, Number>();
    
    private final Map<String, CSSRule> columnWidthStyleMap = new HashMap<String, CSSRule>();
    
    private final Map<String, Align> columnAligns = new HashMap<String, Align>();

    private final CSSRule rowWidthControlRule;
    
    private int viewPortWidth = 0;
   
    private int defaultColumnWidth = GridConst.DEFAULT_COL_WIDTH;

    private VGridHeader header;
    
    public VColumnModel() {
        super();
        rowWidthControlRule = CSSRule.create(".v-grid-row");
    }

    private void initRuleMap() {
        columnAligns.clear();
        columnWidthStyleMap.clear();
        columnWidths.clear();
    }
    
    public int getColumnWidth(final String columnKey) {
        final Integer value = ComputedStyle.parseInt(getCssRule(columnKey).getProperty("width"));
        return value == null ? 0 : value;
    }
    
    public int getDefaultColumnWidth() {
        return defaultColumnWidth;
    }
    
    public int getViewPortWidth() {
        return viewPortWidth;
    }
    
    public void setVisibleColumns(final List<String> columnKeys) {
        visibleColumnsKeys.clear();
        visibleColumnsKeys.addAll(columnKeys);
        initRuleMap();
    }

    public void setCollapsedColumnKeys(final List<String> columnKeys) {
        collapsedColumnsKeys.clear();
        columnKeys.retainAll(visibleColumnsKeys);
        collapsedColumnsKeys.addAll(columnKeys);
    }

    public void setColumnAlignment(final String columnId, Align align) {
        columnAligns.put(columnId, align);
        getCssRule(columnId).setProperty("textAlign", align.getCssValue());
    }

    public void setColumnExpandRatio(final String columnKey, float ratio) {
        if (visibleColumnsKeys.contains(columnKey)) {
        }
    }
    
    public void setColumnWidth(final String columnKey, int pxWidth) {
        if (visibleColumnsKeys.contains(columnKey)) {
            columnWidths.put(columnKey, pxWidth);
            doSetWidth(columnKey, pxWidth);
            recalculateRowWidth();
            fireColumnWidthChanged(columnKey, pxWidth);
        }
    }

    private void doSetWidth(final String columnKey, int pxWidth) {
        final CSSRule rule = getCssRule(columnKey);
        int actualWidth = pxWidth - getPadding(rule);
        rule.setProperty("width", actualWidth + "px");
    }

    private int getPadding(final CSSRule rule) {
        int result = 0;
        if (ComputedStyle.parseInt(rule.getProperty("paddingLeft")) != null) {
            result += ComputedStyle.parseInt(rule.getProperty("paddingLeft"));
        }
        if (ComputedStyle.parseInt(rule.getProperty("paddingRight")) != null) {
            result += ComputedStyle.parseInt(rule.getProperty("paddingRight"));
        }
        return 0;
    }

    public void setDefaultColumnWidth(int width) {
        this.defaultColumnWidth = width;
    }

    public void setColumnWidths(final Map<String, Number> widths) {
        final Iterator<Entry<String, Number>> it = widths.entrySet().iterator();
        while (it.hasNext()) {
            final Entry<String, Number> entry = it.next();
            final String key = entry.getKey();
            if (visibleColumnsKeys.contains(key)) {
                final Number value = entry.getValue();
                columnWidths.put(key, value);
                if (value instanceof Integer) {
                    doSetWidth(key, value.intValue());
                }
            }
        }
        recalculateRowWidth();
    }

    public void setViewPortWidth(int viewPortWidth) {
        this.viewPortWidth = viewPortWidth;
        recalculateRowWidth();
    }

    private void recalculateRowWidth() {
        int consumedWidth = 0;
        float expandRatioSum = 0f;
        final List<String> columnsWithExpRatio = new LinkedList<String>();
        final Iterator<Entry<String, Number>> it = columnWidths.entrySet().iterator();
        while (it.hasNext()) {
            final Entry<String, Number> entry = it.next();
            final Number value = entry.getValue();
            if (value instanceof Float) {
                expandRatioSum += value.floatValue();
                columnsWithExpRatio.add(entry.getKey());
            } else {
                consumedWidth += value.intValue();
            }
        }
        final List<String> columnsWithUndefWidth = getColumnsWidthUndefined();
        
        int rowWidth = viewPortWidth - 2;//TODO FIX!!!
        int excessSpace = Math.max(rowWidth - consumedWidth, 0);
        
        expandRatioSum += viewPortWidth != 0 ? columnsWithUndefWidth.size() : 0;

        consumedWidth += expandRatioColumnsWidth(expandRatioSum, columnsWithExpRatio, excessSpace);
        consumedWidth += undefWidthsColumnsWidth(expandRatioSum, columnsWithUndefWidth, excessSpace);
        
        rowWidth = Math.max(rowWidth, consumedWidth);
        rowWidthControlRule.setProperty("width", rowWidth + "px");
        if (header != null)
            header.recalculateColumnWidths();
    }

    public int undefWidthsColumnsWidth(float expandRatioSum, final List<String> columnsWithUndefWidth, int excessSpace) {
        int undefinedWidthColumnsSummaryWidth = 0;
        for (final String columnKey : columnsWithUndefWidth) {
            int width = viewPortWidth != 0 ? relativeColumnWidth(expandRatioSum, excessSpace, 1.0f) : defaultColumnWidth;
            undefinedWidthColumnsSummaryWidth += width;
            doSetWidth(columnKey, width);
        }
        return undefinedWidthColumnsSummaryWidth;
    }

    public int expandRatioColumnsWidth(float expandRatioSum, final List<String> columnsWithExpRatio, int excessSpace) {
        int expandRatioColumnsSummaryWidth = 0;
        for (final String columnKey : columnsWithExpRatio) {
            float expRatio = columnWidths.get(columnKey).floatValue();
            int width = relativeColumnWidth(expandRatioSum, excessSpace, expRatio);
            expandRatioColumnsSummaryWidth += width;
            doSetWidth(columnKey, width);
        }
        return expandRatioColumnsSummaryWidth;
    }

    public int relativeColumnWidth(float expandRatioSum, int excessSpace, float expRatio) {
        return Math.max((int)((expRatio / expandRatioSum) * (double)excessSpace), GridConst.MIN_COL_WIDTH);
    }
    
    private List<String> getColumnsWidthUndefined() {
        final List<String> result = new ArrayList<String>(visibleColumnsKeys);
        result.removeAll(collapsedColumnsKeys);
        result.removeAll(columnWidths.keySet());
        return result;
    }

    private void fireColumnWidthChanged(String columnKey, int pxWidth) {
        // TODO let all the rows that have components know that they should
        // reLayout those.
    }
    
    private CSSRule getCssRule(String key) {
        CSSRule result = columnWidthStyleMap.get(key);
        if (result == null) {
            result = CSSRule.create(".v-td-" + key);
            result.setProperty("width", "100px");
            columnWidthStyleMap.put(key, result);
        }
        return result;
    }

    public String getColumnControlStyleName(String key) {
        return "v-td-" + key;
    }

    public void setHeader(VGridHeader header) {
        this.header = header;
    }

    public VGridHeader getHeader() {
        return header;
    }
}
