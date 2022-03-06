package com.baatybek.utils.csv;

import com.opencsv.bean.CsvToBeanBuilder;

import java.io.Reader;
import java.util.List;

public final class MCSVReader {
    public static List<CSVModel> readAll(Reader reader) throws Exception {
        return new CsvToBeanBuilder<CSVModel>(reader)
                .withSkipLines(1)
                .withType(CSVModel.class)
                .build()
                .parse();
    }
}
