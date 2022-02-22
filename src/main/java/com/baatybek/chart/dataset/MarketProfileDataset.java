package com.baatybek.chart.dataset;

import org.jfree.chart.util.PublicCloneable;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.OHLCDataItem;
import org.jfree.data.xy.OHLCDataset;

import java.util.*;

public class MarketProfileDataset extends AbstractXYDataset implements OHLCDataset, PublicCloneable {
    private final Comparable seriesKey;
    private List<OHLCDataItem> data = new ArrayList<>();

    public MarketProfileDataset(Comparable seriesKey) {
        this.seriesKey = seriesKey;
    }

    public void add(Date date, double open, double high, double low, double close, double volume) {
        data.add(new OHLCDataItem(date, open, high, low, close, volume));
    }

    public void restructure(double tickSize) {
        for(OHLCDataItem dataItem : data) {

        }
    }

    private double roundWithTickSize(double val, double tickSize) {
        double factor = 1 / tickSize;
        return Math.round(val * factor) / factor;
    }

    @Override
    public int getSeriesCount() {
        return 1;
    }

    @Override
    public Comparable getSeriesKey(int i) {
        return this.seriesKey;
    }

    @Override
    public Number getHigh(int i, int i1) {
        return null;
    }

    @Override
    public double getHighValue(int i, int i1) {
        return 0;
    }

    @Override
    public Number getLow(int i, int i1) {
        return null;
    }

    @Override
    public double getLowValue(int i, int i1) {
        return 0;
    }

    @Override
    public Number getOpen(int i, int i1) {
        return null;
    }

    @Override
    public double getOpenValue(int i, int i1) {
        return 0;
    }

    @Override
    public Number getClose(int i, int i1) {
        return null;
    }

    @Override
    public double getCloseValue(int i, int i1) {
        return 0;
    }

    @Override
    public Number getVolume(int i, int i1) {
        return null;
    }

    @Override
    public double getVolumeValue(int i, int i1) {
        return 0;
    }

    @Override
    public int getItemCount(int i) {
        return 0;
    }

    @Override
    public Number getX(int i, int i1) {
        return null;
    }

    @Override
    public Number getY(int i, int i1) {
        return null;
    }
}
