package com.baatybek.chart.renderer;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarketProfileExp extends AbstractXYItemRenderer implements XYItemRenderer, Cloneable, PublicCloneable, Serializable {
    private double tickSize;
    private double startDate;
    private final int minInMilliSec = 60000;
    private int itemsCounter;
    private List<SeriesChartData> seriesChartData = new ArrayList<>();

    public MarketProfileExp(double tickSize) {
        this.tickSize = tickSize;
    }

    @Override
    public XYItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, XYPlot plot, XYDataset xyDataset, PlotRenderingInfo info) {
        seriesChartData.clear();
        OHLCDataset dataset = (OHLCDataset) xyDataset;
        startDate = dataset.getXValue(0, 0);
        int series = 0;
        itemsCounter = 0;
        Map<Double, ChartItem> chartItemsMap = new HashMap<>();

        for(int item = 0; item < dataset.getItemCount(series); item++) {
            double low = dataset.getLowValue(series, item);
            low = roundValuesWithTickSize(low);
            double high = dataset.getHighValue(series, item);
            high = roundValuesWithTickSize(high);

            List<ChartItem> chartItemList = new ArrayList<>();
            for(double curr = low; curr < high; curr += tickSize) {
                chartItemsMap.putIfAbsent(curr, new ChartItem('A', startDate, curr));
                ChartItem currItem = chartItemsMap.get(curr);
                chartItemList.add(currItem);

                char nextSymbol = getNextSymbol(currItem.getSymbol());
                double nextXVal = currItem.xValue + minInMilliSec;
                chartItemsMap.put(curr, new ChartItem(nextSymbol, nextXVal, curr));
            }

            seriesChartData.add(new SeriesChartData(chartItemList));
        }
        return super.initialise(g2, dataArea, plot, xyDataset, info);
    }

    private char getNextSymbol(char curr) {
        if(curr >= (int) 'A' && curr < (int) 'Y' ||
           curr >= (int) 'a' && curr < (int) 'y') {
            curr += 1;
        } else if(curr == 'Z') {
            curr = 'a';
        } else if(curr == 'z') {
            curr = 'A';
        }
        return curr;
    }

    @Override
    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea,
                         PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis,
                         XYDataset xyDataset, int series, int item, CrosshairState crosshairState, int pass)
    {
        OHLCDataset dataset = (OHLCDataset) xyDataset;
        double low = dataset.getLowValue(series, item);
        low = roundValuesWithTickSize(low);
        double high = dataset.getHighValue(series, item);
        high = roundValuesWithTickSize(high);

        RectangleEdge rangeEdge = plot.getRangeAxisEdge();
        RectangleEdge domainEdge = plot.getDomainAxisEdge();

        List<ChartItem> chartItemList = seriesChartData.get(item).getData();
        for(ChartItem chartItem : chartItemList) {
            drawChartItem(chartItem, g2, domainEdge, rangeEdge, dataArea, domainAxis, rangeAxis);
        }
    }

    private void drawChartItem(ChartItem chartItem, Graphics2D g2, RectangleEdge domainEdge, RectangleEdge rangeEdge,
                               Rectangle2D dataArea, ValueAxis domainAxis, ValueAxis rangeAxis) {
        double xJ2D = domainAxis.valueToJava2D(chartItem.getxValue(), dataArea, domainEdge);
        double yJ2D = rangeAxis.valueToJava2D(chartItem.getyValue(), dataArea, rangeEdge);
        String text = Character.toString(chartItem.getSymbol());

        g2.drawString(text, (float) xJ2D, (float) yJ2D);
    }

    private double roundValuesWithTickSize(double val) {
        return Math.round(val/tickSize) * tickSize;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private class SeriesChartData {
        private List<ChartItem> data;

        public SeriesChartData(List<ChartItem> data) {
            this.data = data;
        }

        public List<ChartItem> getData() {
            return data;
        }
    }

    private class ChartItem {
        private char symbol;
        private double xValue;
        private double yValue;

        public ChartItem(char symbol, double xValue, double yValue) {
            this.symbol = symbol;
            this.xValue = xValue;
            this.yValue = yValue;
        }

        public char getSymbol() {
            return symbol;
        }

        public double getxValue() {
            return xValue;
        }

        public double getyValue() {
            return yValue;
        }
    }
}
