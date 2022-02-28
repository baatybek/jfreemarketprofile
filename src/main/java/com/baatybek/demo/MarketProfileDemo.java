package com.baatybek.demo;

import com.baatybek.chart.renderer.MarketProfileExp;
import com.baatybek.chart.renderer.MarketProfileRenderer;
import com.baatybek.utils.data.OHLCDataGenerator;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.chart.ui.UIUtils;
import org.jfree.data.xy.OHLCDataset;

import javax.swing.*;
import java.awt.*;

public class MarketProfileDemo extends ApplicationFrame {

    public MarketProfileDemo(String title) throws Exception {
        super(title);
        JPanel chartPanel = createDemoPanel();
        chartPanel.setPreferredSize(new Dimension(1344, 756));
        this.setContentPane(chartPanel);
    }

    public static JPanel createDemoPanel() throws Exception {
        JFreeChart chart = createMPChart();
        ChartPanel panel = new ChartPanel(chart, false);
        panel.setMouseWheelEnabled(true);
        return panel;
    }

    private static JFreeChart createMPChart() throws Exception {
        XYPlot plot = createPlotWithDatasetAndRenderer(true);
        return new JFreeChart("MP Demo", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
    }

    private static XYPlot createPlotWithDatasetAndRenderer(boolean withCandlestickRenderer) throws Exception {
        XYPlot plot = createXYPlot();
        OHLCDataset dataset = OHLCDataGenerator.generate();

        plot.setDataset(0, dataset);
        plot.setRenderer(0, new MarketProfileExp(0.05));

        if(withCandlestickRenderer) {
            plot.setDataset(1, dataset);
            plot.setRenderer(1, new CandlestickRenderer());
        }

        return plot;
    }

    private static XYPlot createXYPlot() {
        ValueAxis timeAxis = new DateAxis("Time");
        NumberAxis valueAxis = new NumberAxis("Price");
        valueAxis.setAutoRangeIncludesZero(false);
        valueAxis.setUpperMargin(0.00);
        valueAxis.setLowerMargin(0.00);
        XYPlot plot = new XYPlot(null, timeAxis, valueAxis, null);
        plot.setDomainPannable(true);
        return plot;
    }
    public static void main(String[] args) throws Exception {
        MarketProfileDemo demo = new MarketProfileDemo("demo");
        demo.pack();
        UIUtils.centerFrameOnScreen(demo);
        demo.setVisible(true);
        demo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
