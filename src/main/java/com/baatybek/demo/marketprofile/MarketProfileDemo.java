package com.baatybek.demo.marketprofile;

import com.baatybek.dataset.DataGenerator;
import com.baatybek.renderer.MarketProfileRenderer;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.chart.ui.UIUtils;
import org.jfree.data.xy.OHLCDataset;

import javax.swing.*;
import java.awt.*;

public class MarketProfileDemo extends ApplicationFrame {
    public MarketProfileDemo(String title) throws Exception {
        super(title);
        JPanel chartPanel = createDemoPanel();
        chartPanel.setPreferredSize(new Dimension(1920, 1080));
        this.setContentPane(chartPanel);
    }

    public static JPanel createDemoPanel() throws Exception {
        JFreeChart chart = createMPChart();
        ChartPanel panel = new ChartPanel(chart, false);
        panel.setMouseWheelEnabled(true);
        return panel;
    }

    private static JFreeChart createMPChart() throws Exception {
        // dataset
        OHLCDataset dataset = DataGenerator.generateDefaultHighLowDataset();

        // renderer
        MarketProfileRenderer renderer = new MarketProfileRenderer(30);

        // plot
        ValueAxis timeAxis = new DateAxis("Time");
        NumberAxis valueAxis = new NumberAxis("Price");
        valueAxis.setAutoRangeIncludesZero(false);
        valueAxis.setUpperMargin(0.00);
        valueAxis.setLowerMargin(0.00);
        XYPlot plot = new XYPlot(dataset, timeAxis, valueAxis, renderer);
        plot.setDomainPannable(true);

        return new JFreeChart("MP Demo", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
    }

    public static void main(String[] args) throws Exception {
        MarketProfileDemo demo = new MarketProfileDemo("demo");
        demo.pack();
        UIUtils.centerFrameOnScreen(demo);
        demo.setVisible(true);
        demo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


}
