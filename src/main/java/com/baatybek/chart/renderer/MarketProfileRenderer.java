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
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.XYDataset;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MarketProfileRenderer  extends AbstractXYItemRenderer implements XYItemRenderer, Cloneable, PublicCloneable, Serializable {
    private static final long serialVersionUID = 914108561834089247L;
    private final int minInMilliSec = 60000;
    private double componentHeight = 1.0D;
    Map<Double, Integer> priceTimeCountMap = new HashMap<>();

    public MarketProfileRenderer() {
        XYToolTipGenerator toolTipGenerator = new HighLowItemLabelGenerator();
        this.setDefaultToolTipGenerator(toolTipGenerator);
    }

    private double roundValue(double value) {
        return Math.round(value * 2)/2.0;
    }

    @Override
    public XYItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, XYPlot plot, XYDataset dataset, PlotRenderingInfo info) {
        componentHeight = 1/Math.pow(10, 1);
        OHLCDataset ohlcDataset = (OHLCDataset) dataset;
        priceTimeCountMap = new HashMap<>();
        for(int i = 0; i < ohlcDataset.getItemCount(0); i++) {
            double close = roundValue(ohlcDataset.getCloseValue(0, i));
            priceTimeCountMap.computeIfPresent(close, (k, v) -> v + 1);
            priceTimeCountMap.putIfAbsent(close, 1);
        }
        return super.initialise(g2, dataArea, plot, dataset, info);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea,
                         PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis,
                         XYDataset xyDataset, int series, int item, CrosshairState crosshairState, int pass)
    {
        OHLCDataset dataset = (OHLCDataset) xyDataset;

        double closePrice = roundValue(dataset.getCloseValue(series, item));
        RectangleEdge rangeEdge = plot.getRangeAxisEdge();
        double yValJava2D = rangeAxis.valueToJava2D(closePrice, dataArea, rangeEdge);
        double heightJava2D = rangeAxis.lengthToJava2D(componentHeight, dataArea, rangeEdge);

        int order = priceTimeCountMap.get(closePrice);
        priceTimeCountMap.computeIfPresent(closePrice, (k, v) -> v - 1);
        double lowerbound = dataset.getXValue(0, 0);

        double timeMillis = lowerbound + minInMilliSec * 1 * (order - 1);
        RectangleEdge domainEdge = plot.getDomainAxisEdge();
        double xValeJava2D = domainAxis.valueToJava2D(timeMillis, dataArea, domainEdge);
        double widthJava2D = domainAxis.lengthToJava2D(minInMilliSec * 1 , dataArea, domainEdge);

        Number dateTimeNum = timeMillis;
        Date dateTime = new Date();
        dateTime.setTime(dateTimeNum.longValue());
        //System.out.println(DateTimeUtility.DEFAULT_DATE_TIME_FORMAT.format(dateTime) + "=" + closePrice);

        Paint paint = this.getItemPaint(series, item);
        g2.setPaint(paint);

        Rectangle2D.Double body = null;
        if(plot.getOrientation() == PlotOrientation.HORIZONTAL) {
            body = new Rectangle2D.Double(yValJava2D, xValeJava2D, heightJava2D, widthJava2D);
        }
        if(plot.getOrientation() == PlotOrientation.VERTICAL) {
            body = new Rectangle2D.Double(xValeJava2D, yValJava2D, widthJava2D, heightJava2D);
        }

        g2.fill(body);
        g2.draw(body);
    }
}
