package com.baatybek.demo;

import com.baatybek.dataset.OHLCDataGenerator;
import com.baatybek.renderer.MarketProfileRenderer;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.ui.UIUtils;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.ui.ApplicationFrame;

import javax.swing.*;
import java.awt.*;

public class MarketProfileCandlestickDemo extends ApplicationFrame {
    public MarketProfileCandlestickDemo(String title) throws Exception {
        super(title);

        JFreeChart chart = createChart();
        ChartPanel panel = new ChartPanel(chart, false);
        panel.setMouseWheelEnabled(true);
        panel.setPreferredSize(new Dimension(1920, 1080));
        this.setContentPane(panel);
    }

    private static JFreeChart createChart() throws Exception {
        // Dataset
        OHLCDataset dataset = OHLCDataGenerator.generate();

        // Plot
        ValueAxis timeAxis = new DateAxis("Time");
        NumberAxis valueAxis = new NumberAxis("Price");
        valueAxis.setAutoRangeIncludesZero(false);
        valueAxis.setUpperMargin(0.10);
        valueAxis.setLowerMargin(0.10);
        XYPlot plot = new XYPlot(null, timeAxis, valueAxis, null);
        plot.setDomainPannable(true);

        plot.setDataset(0, dataset);
        plot.setRenderer(0, new CandlestickRenderer());

        plot.setDataset(1, dataset);
        plot.setRenderer(1, new MarketProfileRenderer(60));

        return new JFreeChart(
                "Candlestick and MarketProfile Demo",
                JFreeChart.DEFAULT_TITLE_FONT,
                plot,
                true
        );
    }

    public static void main(String[] args) throws Exception {
        MarketProfileCandlestickDemo demo = new MarketProfileCandlestickDemo("Demo");
        demo.pack();
        UIUtils.centerFrameOnScreen(demo);
        demo.setVisible(true);
        demo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
