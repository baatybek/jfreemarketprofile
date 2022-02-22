package com.baatybek.utils.data;

import com.baatybek.utils.csv.model.CSVModel;
import com.baatybek.utils.csv.reader.MCSVReader;
import com.baatybek.utils.datetime.DateTimeUtility;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
import org.jfree.data.xy.OHLCDataset;

import java.io.FileReader;
import java.util.List;

public final class OHLCDataGenerator {

    private static String datasetPath = "/home/baatybek/Workspaces/Thesis/dev/jfreemarketprofile/src/main/resources/csv/ohlc-dataset-30min.csv";

    public static OHLCDataset generate() throws Exception {
        List<CSVModel> csvData = readDataFromCSV();
        OHLCSeries series = new OHLCSeries("08/07/2021");

        for(CSVModel model : csvData) {
            OHLCItem item = convertCSVModelToOHLCItem(model);
            series.add(item);
        }

        OHLCSeriesCollection seriesCollection = new OHLCSeriesCollection();
        seriesCollection.addSeries(series);
        return seriesCollection;
    }

    public static void setDatasetPath(String datasetPath) {
        OHLCDataGenerator.datasetPath = datasetPath;
    }

    private static List<CSVModel> readDataFromCSV() throws Exception {
        FileReader fileReader = new FileReader(datasetPath);
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
