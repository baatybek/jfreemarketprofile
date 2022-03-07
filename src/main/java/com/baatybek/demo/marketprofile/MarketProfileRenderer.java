package com.baatybek.demo.marketprofile;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.XYDataset;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarketProfileRenderer extends AbstractXYItemRenderer implements XYItemRenderer, Cloneable, PublicCloneable, Serializable {
    private double tickSize;
    private List<ChartItem> chartItemList;

    @Override
    public XYItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, XYPlot plot, XYDataset xyDataset, PlotRenderingInfo info) {
        NumberAxis rangeAxis = (NumberAxis)plot.getRangeAxis();
        tickSize = rangeAxis.getTickUnit().getSize();

        // everytime ticksize gets changed calculate dataset again
        return super.initialise(g2, dataArea, plot, xyDataset, info);
    }

    private char getNextSymbol(char curr) {
        if(curr == '#') {
            return 'A';
        }
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

    private double roundValuesWithTickSize(double val) {
        return Math.round(val/tickSize) * tickSize;
    }

    @Override
    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea,
                         PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis,
                         XYDataset xyDataset, int series, int item, CrosshairState crosshairState, int pass)
    {

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