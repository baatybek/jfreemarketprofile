package com.baatybek.renderer;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarketProfileRenderer extends AbstractXYItemRenderer implements XYItemRenderer, Cloneable, PublicCloneable, Serializable {
    private BigDecimal tickSize;
    private final int timeFrame;
    private final double minInMilliSec = 60000D;
    private double offsetDomainAxis = 1 * minInMilliSec;
    private List<ChartItemsCollection> chartItemsCollections;

    public MarketProfileRenderer(int timeFrame) {
        this.timeFrame = timeFrame;
    }

    private BigDecimal roundValueWithTickSize(double val) {
        double round = Math.round(val/tickSize.doubleValue()) * tickSize.doubleValue();
        BigDecimal bd = new BigDecimal(round);
        bd = bd.setScale(tickSize.scale(), RoundingMode.HALF_UP);
        return bd;
    }

    private class ChartItem {
        private double xValue;
        private double low;
        private double high;
        private char symbol;

        public ChartItem(double xValue, double low, double high, char symbol) {
            this.xValue = xValue;
            this.low = low;
            this.high = high;
            this.symbol = symbol;
        }

        public char getSymbol() {
            return symbol;
        }

        public double getXValue() {
            return xValue;
        }

        public double getLow() {
            return low;
        }

        public double getHigh() {
            return high;
        }
    }

    private class ChartItemsCollection {
        private java.util.List<ChartItem> data;
        public ChartItemsCollection(java.util.List<ChartItem> data) {
            this.data = data;
        }
        public List<ChartItem> getData() {
            return data;
        }
    }

    private char getSymbol(char symbol) {
        if(symbol == '#') {
            return 'A';
        }

        if(symbol >= 'A' && symbol < 'Z') {
            return (char) (symbol + 1);
        } else if(symbol >= 'a' && symbol < 'z') {
            return (char) (symbol + 1);
        }

        if(symbol == 'Z') {
            return 'a';
        } else if(symbol == 'z') {
            return 'A';
        }

        return '#';
    }

    @Override
    public XYItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, XYPlot plot, XYDataset xyDataset, PlotRenderingInfo info) {
        NumberAxis rangeAxis = (NumberAxis)plot.getRangeAxis();
        double tick = rangeAxis.getTickUnit().getSize();
        tickSize = new BigDecimal(Double.toString(tick));

        chartItemsCollections = new ArrayList<>();

        OHLCDataset dataset = (OHLCDataset) xyDataset;
        int series = 0;
        Map<BigDecimal, ChartItem> chartItemMap = new HashMap<>();
        double xValueLowerBound = dataset.getXValue(series, 0);
        char symbol = '#';

        for(int item = 0; item < dataset.getItemCount(series); item++) {
            double xValue = dataset.getXValue(series, item);
            symbol = getSymbol(symbol);

            if(xValue >= xValueLowerBound + timeFrame * minInMilliSec) {
                xValueLowerBound = xValue;
                symbol = 'A';
                chartItemMap.clear();
            }

            double low = dataset.getLowValue(series, item);
            BigDecimal lowBD = roundValueWithTickSize(low);

            double high = dataset.getHighValue(series, item);
            BigDecimal highBD = roundValueWithTickSize(high);

            List<ChartItem> chartItemsList = new ArrayList<>();

            while (highBD.compareTo(lowBD) > 0) {
                chartItemMap.putIfAbsent(lowBD, new ChartItem(xValueLowerBound, lowBD.doubleValue(), lowBD.doubleValue() + tickSize.doubleValue(), symbol));
                ChartItem currItem = chartItemMap.get(lowBD);
                chartItemsList.add(currItem);

                double nextXVal = currItem.getXValue() + minInMilliSec + offsetDomainAxis;
                chartItemMap.put(lowBD, new ChartItem(nextXVal, lowBD.doubleValue(), lowBD.doubleValue() + tickSize.doubleValue(), symbol));
                lowBD = lowBD.add(tickSize);
            }
            chartItemsCollections.add(new ChartItemsCollection(chartItemsList));
        }

        return new XYItemRendererState(info);
    }

    @Override
    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea,
                         PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis,
                         XYDataset xyDataset, int series, int item, CrosshairState crosshairState, int pass)
    {
        RectangleEdge rangeEdge = plot.getRangeAxisEdge();
        RectangleEdge domainEdge = plot.getDomainAxisEdge();

        List<ChartItem> chartItemList = chartItemsCollections.get(item).getData();
        for(ChartItem chartItem : chartItemList) {
            drawChartItem(chartItem, g2, domainEdge, rangeEdge, dataArea, domainAxis, rangeAxis);
        }
    }
    private void drawChartItem(ChartItem chartItem, Graphics2D g2, RectangleEdge domainEdge, RectangleEdge rangeEdge,
                               Rectangle2D dataArea, ValueAxis domainAxis, ValueAxis rangeAxis)
    {
        double y = chartItem.getHigh();
        double x = chartItem.getXValue();
        String text = Character.toString(chartItem.getSymbol());

        double yJ2D = rangeAxis.valueToJava2D(y, dataArea, rangeEdge);
        double xJ2D = domainAxis.valueToJava2D(x, dataArea, domainEdge);

        g2.drawString(text, (float) xJ2D, (float) yJ2D);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
