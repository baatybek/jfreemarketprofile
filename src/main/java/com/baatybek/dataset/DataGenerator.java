package com.baatybek.dataset;

import com.baatybek.utils.csv.CSVModel;
import com.baatybek.utils.csv.MCSVReader;
import com.baatybek.utils.datetime.DateTimeUtility;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.data.xy.OHLCDataset;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

public final class DataGenerator {

    public static DefaultHighLowDataset generateDefaultHighLowDataset() throws Exception {
        List<CSVModel> csvData = readDataFromCSV();
        int dataSize = csvData.size();
        Date[] date = new Date[dataSize];
        double[] open = new double[dataSize];
        double[] high = new double[dataSize];
        double[] low = new double[dataSize];
        double[] close = new double[dataSize];
        double[] volume = new double[dataSize];

        for(int i = 0; i < dataSize; i++) {
            CSVModel model = csvData.get(i);
            date[i] = DateTimeUtility.DEFAULT_DATE_TIME_FORMAT.parse(model.getDateTime());
            open[i] = Double.parseDouble(model.getOpen());
            high[i] = Double.parseDouble(model.getHigh());
            low[i] = Double.parseDouble(model.getLow());
            close[i] = Double.parseDouble(model.getClose());
            volume[i] = Long.parseLong(model.getVolume());
        }

        return new DefaultHighLowDataset("Dataset", date, high, low, open, close, volume);
    }


    public static OHLCSeriesCollection generateOHLCSeriesCollection() throws Exception {
        List<CSVModel> csvData = readDataFromCSV();
        OHLCSeries series = new OHLCSeries("OHLC Dataset");

        for(CSVModel model : csvData) {
            OHLCItem item = convertCSVModelToOHLCItem(model);
            series.add(item);
        }

        OHLCSeriesCollection seriesCollection = new OHLCSeriesCollection();
        seriesCollection.addSeries(series);
        return seriesCollection;
    }


    private static List<CSVModel> readDataFromCSV() throws Exception {
        URL res = DataGenerator.class.getClassLoader().getResource("csv/ohlc-dataset.csv");
        assert res != null;
        File file = Paths.get(res.toURI()).toFile();
        FileReader fileReader = new FileReader(file);
        return MCSVReader.readAll(fileReader);
    }

    private static OHLCItem convertCSVModelToOHLCItem(CSVModel model) throws Exception {
        FixedMillisecond time = DateTimeUtility.generateFixMilliSec(model.getDateTime());
        double open = Double.parseDouble(model.getOpen());
        double high = Double.parseDouble(model.getHigh());
        double low = Double.parseDouble(model.getLow());
        double close = Double.parseDouble(model.getClose());

        return new OHLCItem(time, open, high, low, close);
    }
}
