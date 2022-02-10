package com.baatybek.utils.csv.reader;

import com.baatybek.properties.ConstProperties;
import com.baatybek.utils.csv.model.CSVModel;
import com.baatybek.utils.csv.reader.MCSVReader;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MCSVReaderTest {
    @Test
    public void readAllTest() throws Exception {
        FileReader fileReader = new FileReader(ConstProperties.DATASET_PATH);
        List<CSVModel> expected = new ArrayList<>(Arrays.asList(
                new CSVModel("2021-07-08 09:30:00","428.78","429.07","428.4","429.01","1271900"),
                new CSVModel("2021-07-08 09:31:00","429.01","429.22","428.99","429.11","561400"),
                new CSVModel("2021-07-08 09:32:00","429.11","429.24","429.07","429.12","535400")
        ));

        List<CSVModel> actual = MCSVReader.readAll(fileReader);

        Assert.assertEquals(expected.size(), actual.size());
        for(int i = 0; i < expected.size(); i++) {
            CSVModel expectedModel = expected.get(i);
            CSVModel actualModel = actual.get(i);

            Assert.assertEquals(expectedModel.getDateTime(), actualModel.getDateTime());
            Assert.assertEquals(expectedModel.getOpen(), actualModel.getOpen());
            Assert.assertEquals(expectedModel.getHigh(), actualModel.getHigh());
            Assert.assertEquals(expectedModel.getLow(), actualModel.getLow());
            Assert.assertEquals(expectedModel.getClose(), actualModel.getClose());
            Assert.assertEquals(expectedModel.getVolume(), actualModel.getVolume());
        }
    }
}
