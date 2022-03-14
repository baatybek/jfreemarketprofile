package com.baatybek.dataset;

import com.baatybek.utils.csv.CSVModel;
import com.baatybek.utils.csv.MCSVReader;
import com.baatybek.utils.datetime.DateTimeUtility;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
import org.jfree.data.xy.OHLCDataset;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

public final class DataGenerator {

    public static OHLCDataset generate() throws Exception {
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
