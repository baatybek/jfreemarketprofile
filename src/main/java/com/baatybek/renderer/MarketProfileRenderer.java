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
import org.jfree.chart.util.Args;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.XYDataset;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.List;

public class MarketProfileRenderer extends AbstractXYItemRenderer implements XYItemRenderer, Cloneable, PublicCloneable, Serializable {
    private BigDecimal tickSize;
    private final int timeFrame;
    private final double minInMilliSec = 60000D;
    private double offsetDomainAxis = 1 * minInMilliSec;
    private List<ChartItemsCollection> chartItemsCollections;
    private boolean drawVolume;
    private transient Paint volumePaint;
    private transient double maxVolume = 0.0D;

    public MarketProfileRenderer(int timeFrame) {
        this(timeFrame, true);
    }
    public MarketProfileRenderer(int timeFrame, boolean drawVolume) {
        this.timeFrame = timeFrame;
        this.drawVolume = drawVolume;
        this.volumePaint = Color.GRAY;
    }

    public boolean getDrawVolume() {
        return this.drawVolume;
    }

    public void setDrawVolume(boolean drawVolume) {
        if (this.drawVolume != drawVolume) {
            this.drawVolume = drawVolume;
            this.fireChangeEvent();
        }
    }

    public Paint getVolumePaint() {
        return this.volumePaint;
    }

    public void setVolumePaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.volumePaint = paint;
        this.fireChangeEvent();
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

    private void setUpFont(Graphics2D g2) {
        String fontName = g2.getFont().toString();
        int fontSize = g2.getFont().getSize();
        g2.setFont(new Font(fontName, Font.BOLD, fontSize));
        g2.setColor(Color.black);
    }

    @Override
    public XYItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, XYPlot plot, XYDataset xyDataset, PlotRenderingInfo info) {
        NumberAxis rangeAxis = (NumberAxis)plot.getRangeAxis();
        double tick = rangeAxis.getTickUnit().getSize();
        tickSize = new BigDecimal(Double.toString(tick));

        chartItemsCollections = new ArrayList<>();

        OHLCDataset dataset = (OHLCDataset) xyDataset;
        int series = 0;
        Map<BigDecimal, Double> xValsMap = new HashMap<>();
        double xValueLowerBound = dataset.getXValue(series, 0);
        char symbol = '#';

        for(int item = 0; item < dataset.getItemCount(series); item++) {
            double xValue = dataset.getXValue(series, item);
            Date date = new Date();
            date.setTime((long) xValue);
            symbol = getSymbol(symbol);

            if(xValue >= xValueLowerBound + timeFrame * minInMilliSec) {
                xValueLowerBound = xValue;
                symbol = 'A';
                xValsMap.clear();
            }

            double low = dataset.getLowValue(series, item);
            BigDecimal lowBD = roundValueWithTickSize(low);

            double high = dataset.getHighValue(series, item);
            BigDecimal highBD = roundValueWithTickSize(high);

            List<ChartItem> chartItemsList = new ArrayList<>();

            while (highBD.compareTo(lowBD) > 0) {
                xValsMap.putIfAbsent(lowBD, xValueLowerBound);
                double xCurr = xValsMap.get(lowBD);
                chartItemsList.add(new ChartItem(xCurr, lowBD.doubleValue(), lowBD.doubleValue() + tickSize.doubleValue(), symbol));

                double nextXVal = xCurr + minInMilliSec + offsetDomainAxis;
                xValsMap.put(lowBD, nextXVal);
                lowBD = lowBD.add(tickSize);
            }
            chartItemsCollections.add(new ChartItemsCollection(chartItemsList));

            if(drawVolume) {
                double volume = dataset.getVolumeValue(series, item);
                if (volume > this.maxVolume) {
                    this.maxVolume = volume;
                }
            }
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

        setUpFont(g2);

        List<ChartItem> chartItemList = chartItemsCollections.get(item).getData();
        for(ChartItem chartItem : chartItemList) {
            drawChartItem(chartItem, g2, domainEdge, rangeEdge, dataArea, domainAxis, rangeAxis);
        }

        if(drawVolume) {
            drawVolumeItem((OHLCDataset) xyDataset, dataArea, g2, domainEdge, domainAxis, series, item);
        }
    }

    private void drawChartItem(ChartItem chartItem, Graphics2D g2, RectangleEdge domainEdge, RectangleEdge rangeEdge,
                               Rectangle2D dataArea, ValueAxis domainAxis, ValueAxis rangeAxis)
    {
        double y = chartItem.getLow();
        double x = chartItem.getXValue();
        String text = Character.toString(chartItem.getSymbol());

        double yJ2D = rangeAxis.valueToJava2D(y, dataArea, rangeEdge);
        double xJ2D = domainAxis.valueToJava2D(x, dataArea, domainEdge);

        g2.drawString(text, (float) xJ2D, (float) yJ2D);
    }

    private void drawVolumeItem(OHLCDataset dataset, Rectangle2D dataArea, Graphics2D g2, RectangleEdge domainEdge, ValueAxis domainAxis, int series, int item) {
        double volume = dataset.getVolumeValue(series, item);
        double volumeHeight = volume/maxVolume;
        double min = dataArea.getMinY();
        double max = dataArea.getMaxY();

        double volumeY = volumeHeight * (max - min);
        double volumeYY = max - volumeY;

        double x = dataset.getXValue(series, item);
        double xJ2D = domainAxis.valueToJava2D(x, dataArea, domainEdge);

        g2.setPaint(this.getVolumePaint());
        Composite originalComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(3, 0.3F));

        double volumeWidth = 3.0D;
        g2.fill(new Rectangle2D.Double(xJ2D, volumeYY, volumeWidth, volumeY));
        g2.setComposite(originalComposite);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
