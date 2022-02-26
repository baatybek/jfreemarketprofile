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
    private int timeBracketUnits;
    private double tickSize;
    private List<Double> startXList = new ArrayList<>();
    private Map<Double, Double> xValMap = new HashMap<>();
    private Double startDate;

    private double roundValueToTickSize(double value) {
        return Math.round(value/tickSize) * tickSize;
    }

    public MarketProfileRenderer() {
        this(0.05);
    }

    public MarketProfileRenderer(double tickSize) {
        this.tickSize = tickSize;
    }


    @Override
    public XYItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, XYPlot plot, XYDataset xyDataset, PlotRenderingInfo info) {
        startXList.clear();
        xValMap.clear();
        startDate = xyDataset.getXValue(0, 0);
        int series = xyDataset.getSeriesCount();
        for(int i = 0; i < series; i++) {
            startXList.add(xyDataset.getXValue(0, 0));
        }
        return super.initialise(g2, dataArea, plot, xyDataset, info);
    }


    @Override
    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea,
                         PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis,
                         XYDataset xyDataset, int series, int item, CrosshairState crosshairState, int pass)
    {
        OHLCDataset dataset = (OHLCDataset) xyDataset;

        double low = dataset.getLowValue(series, item);
        double high = dataset.getHighValue(series, item);

        low = roundValueToTickSize(low);
        high = roundValueToTickSize(high);
        char symbol = 'A';
        symbol += (long)(dataset.getXValue(series, item) - startDate)/minInMilliSec;
        System.out.println(symbol);
        for(double currLow = low; currLow < high; currLow = currLow + tickSize) {
            xValMap.putIfAbsent(currLow, startXList.get(series));
            double xVal = xValMap.get(currLow);
            xValMap.put(currLow, xVal + minInMilliSec);
            drawSingleItem(currLow, xVal, symbol, g2, dataArea, plot, domainAxis, rangeAxis);
        }

    }

    private void drawSingleItem(double yVal, double xVal, char symbol, Graphics2D g2,
                                Rectangle2D dataArea, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis) {

        RectangleEdge rangeEdge = plot.getRangeAxisEdge();
        double yValJava2D = rangeAxis.valueToJava2D(yVal, dataArea, rangeEdge);

        RectangleEdge domainEdge = plot.getDomainAxisEdge();
        double xValJava2D = domainAxis.valueToJava2D(xVal, dataArea, domainEdge);
        g2.drawString(Character.toString(symbol), (float) xValJava2D,(float) yValJava2D);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
