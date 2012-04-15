package org.vaadin.addon.grid.client.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public final class RenderInfo implements Serializable {
    
    private int currentFirst = 0;
    
    private int targetFirst = 0;
    
    private int currentLast = -1;
    
    private int targetLast = -1;
    
    private List<Integer> indices = new ArrayList<Integer>();
    
    public void shiftIndices(int count, boolean fromHead) {
        int N = indices.size();
        int targetIndex = fromHead ? targetLast + 1 : targetFirst - count;
        int index = fromHead ? 
                (N - (currentFirst - targetFirst)) % N : 
                 N - ((currentLast - targetLast + count) % N);
        for (int i = 0; i < count; ++i) {
            indices.set((i + index) % N, targetIndex);
            ++targetIndex;
        }
        shiftBounds(count, fromHead);
    }
    
    private void shiftBounds(int count, boolean fromHead) {
        targetFirst += count * (fromHead ? 1 : -1);
        targetLast += count * (fromHead ? 1 : -1);
    }

    public int getFirst() {
        return targetFirst;
    }
    
    public int getLast() {
        return targetLast;
    }


    public void setFirstRendered(int firstRendered) {
        this.currentFirst = this.targetFirst = firstRendered;
    }

    public void setLastRendered(int lastRendered) {
        this.currentLast = this.targetLast = lastRendered;
    }

    public void setBounds(int firstRendered, int lastRendered) {
        currentFirst = targetFirst = firstRendered;
        currentLast = targetLast = lastRendered;
        resetIndeces();
    }

    private void resetIndeces() {
        indices.clear();
        for (int i = currentFirst; i <= currentLast; ++i) {
            indices.add(i);
        }
    }

    public Integer[] getIndeces() {
        return indices.toArray(new Integer[indices.size()]);
    }
    
    @Override
    public String toString() {
        StringBuilder result =  new StringBuilder(currentFirst + "").
                append(" - " + currentLast + "\n").append(targetFirst).append(" - " + targetLast + "\n").append("[");
        for (int i = 0; i < indices.size(); ++i) {
            if (i > 0 && Math.abs(indices.get(i) - indices.get(i - 1)) > 1) {
                result.append(" | ");
            }
            if (i < indices.size() - 1) {
                result.append(indices.get(i) + ", ");
            } else {
                result.append(indices.get(i) + "]");
            }
        }
        return result.append("\n").toString();
    }

    public void setIndeces(final List<Integer> indices) {
        this.indices = indices;
    }
}

