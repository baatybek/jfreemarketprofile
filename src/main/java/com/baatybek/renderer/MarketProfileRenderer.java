package com.baatybek.renderer;

import com.baatybek.utils.datetime.DateTimeUtility;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.data.time.DateRange;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.XYDataset;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class MarketProfileRenderer extends AbstractXYItemRenderer implements XYItemRenderer, Cloneable, PublicCloneable, Serializable {
    private double tickSize;
    private List<SeriesChartData> seriesChartData = new ArrayList<>();

    private class SeriesChartData {
        private List<ChartItem> data;
        public SeriesChartData(List<ChartItem> data) {
            this.data = data;
        }

        public List<ChartItem> getData() {
            return data;
        }
    }

    @Override
    public XYItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, XYPlot plot, XYDataset xyDataset, PlotRenderingInfo info) {
        NumberAxis rangeAxis = (NumberAxis)plot.getRangeAxis();
        tickSize = rangeAxis.getTickUnit().getSize();

        seriesChartData.clear();
        OHLCDataset dataset = (OHLCDataset) xyDataset;
        double xValMax = dataset.getXValue(0, 0);;

        for(int series = 0; series < dataset.getSeriesCount(); series++) {

            double xLowerBound = dataset.getXValue(series, 0);
            xValMax = xLowerBound;

            Map<Double, ChartItem> chartItemMap = new HashMap<>();

            for(int item = 0; item < dataset.getItemCount(series); item++) {

                double low = dataset.getLowValue(series, item);
                low = roundValuesWithTickSize(low);
                double high = dataset.getHighValue(series, item);
                high = roundValuesWithTickSize(high);

                char symbol = getSymbol(xLowerBound, dataset.getXValue(series, item));

                List<ChartItem> chartItemList = new ArrayList<>();
                for(double curr = low; curr < high; curr = curr + tickSize) {
                    chartItemMap.putIfAbsent(curr, new ChartItem(symbol, xLowerBound, curr));
                    ChartItem currItem = chartItemMap.get(curr);
                    chartItemList.add(currItem);

                    double nextXVal = currItem.xValue + 60 * 1000;
                    chartItemMap.put(curr, new ChartItem(symbol, nextXVal, curr));

                    xValMax = Math.max(xValMax, nextXVal);
                }

                seriesChartData.add(new SeriesChartData(chartItemList));
            }
        }

        double lower = dataset.getXValue(0, 0);
        plot.getDomainAxis().setRange(new DateRange(lower, xValMax));
        return new XYItemRendererState(info);
    }

    private char getSymbol(double lowerBound, double current) {
        long lower = (long) lowerBound;
        long curr = (long) current;
        long factor = ((curr - lower)/60000) % 50;

        if(factor <= 25) {
            return (char) ('A' + factor);
        }
        return (char) (factor - 26 + 'a');
    }

    private double roundValuesWithTickSize(double val) {
        return Math.round(val/tickSize) * tickSize;
    }

    @Override
    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea,
                         PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis,
                         XYDataset xyDataset, int series, int item, CrosshairState crosshairState, int pass)
    {
        RectangleEdge rangeEdge = plot.getRangeAxisEdge();
        RectangleEdge domainEdge = plot.getDomainAxisEdge();

        List<ChartItem> chartItemList = seriesChartData.get(item).getData();
        for(ChartItem chartItem : chartItemList) {
            drawChartItem(chartItem, g2, domainEdge, rangeEdge, dataArea, domainAxis, rangeAxis);
        }
    }

    private void drawChartItem(ChartItem chartItem, Graphics2D g2, RectangleEdge domainEdge, RectangleEdge rangeEdge,
                               Rectangle2D dataArea, ValueAxis domainAxis, ValueAxis rangeAxis)
    {
        double xJ2D = domainAxis.valueToJava2D(chartItem.getXValue(), dataArea, domainEdge);
        double yJ2D = rangeAxis.valueToJava2D(chartItem.getYValue(), dataArea, rangeEdge);
        String text = Character.toString(chartItem.getSymbol());

        Date date = new Date();
        date.setTime((long) chartItem.getXValue());
        String dateStr = DateTimeUtility.DEFAULT_DATE_TIME_FORMAT.format(date);

        System.out.println("Symbol = " + chartItem.getSymbol() + " dateTime = " + dateStr);
        g2.drawString(text, (float) xJ2D, (float) yJ2D);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private class ChartItem {
        private char symbol;
        private double xValue;
        private double yValue;

        public char getSymbol() {
            return symbol;
        }

        public double getXValue() {
            return xValue;
        }

        public double getYValue() {
            return yValue;
        }

        public ChartItem(char symbol, double xValue, double yValue) {
            this.symbol = symbol;
            this.xValue = xValue;
            this.yValue = yValue;
        }
    }
}