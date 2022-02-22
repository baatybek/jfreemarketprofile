package com.baatybek.chart.renderer;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.HighLowItemLabelGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.data.xy.OHLCDataItem;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.XYDataset;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class MarketProfileRenderer  extends AbstractXYItemRenderer implements XYItemRenderer, Cloneable, PublicCloneable, Serializable {
    private static final long serialVersionUID = 914108561834089247L;
    private final int minInMilliSec = 60000;
    private TimeBracketType type;
    private int timeBracketUnits;
    private double tickSize;
    Map<Double, Item> mmmap = new HashMap<>();
    public enum TimeBracketType {
        MINUTE,
        HOUR,
        MONTH,
        YEAR
    }

    class Item {
        public Queue<OHLCDataItem> itemsQueue = new LinkedList<>();
        public Item add(OHLCDataItem dataItem) {
            itemsQueue.add(dataItem);
            return this;
        }
    }

    private double roundValueToTickSize(double value) {
        double factor = 1 / tickSize;
        return Math.round(value * factor) / factor;
    }

    public MarketProfileRenderer() {
        this(0.05D, TimeBracketType.MINUTE, 1);
    }

    public MarketProfileRenderer(double tickSize, TimeBracketType type, int timeBracketUnits) {
        this.tickSize = tickSize;
        this.type = type;
        this.timeBracketUnits = timeBracketUnits;
    }

    @Override
    public XYItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, XYPlot plot, XYDataset dataset, PlotRenderingInfo info) {
        OHLCDataset ohlcDataset = (OHLCDataset) dataset;
        for(int i = 0; i < ohlcDataset.getItemCount(0); i++) {
            Date date = new Date();
            date.setTime(ohlcDataset.getX(0, i).longValue());
            OHLCDataItem dataItem = new OHLCDataItem(date, ohlcDataset.getOpenValue(0, i),
                    ohlcDataset.getHighValue(0, i),
                    ohlcDataset.getLowValue(0, i),
                    ohlcDataset.getCloseValue(0, i),
                    ohlcDataset.getVolumeValue(0, i));


        }
        return super.initialise(g2, dataArea, plot, dataset, info);
    }

    @Override
    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea,
                         PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis,
                         XYDataset xyDataset, int series, int item, CrosshairState crosshairState, int pass)
    {
        OHLCDataset dataset = (OHLCDataset) xyDataset;


    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
