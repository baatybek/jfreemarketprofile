package com.baatybek.utils.csv.model;

import com.opencsv.bean.CsvBindByPosition;

public class CSVModel {
    @CsvBindByPosition(position = 0)
    private String dateTime;
    @CsvBindByPosition(position = 1)
    private String open;
    @CsvBindByPosition(position = 2)
    private String high;
    @CsvBindByPosition(position = 3)
    private String low;
    @CsvBindByPosition(position = 4)
    private String close;
    @CsvBindByPosition(position = 5)
    private String volume;

    public CSVModel() {
    }

    public CSVModel(String dateTime, String open, String high, String low, String close, String volume) {
        this.dateTime = dateTime;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    @Override
    public String toString() {
        return "CSVModel{" +
                "dateTime='" + dateTime + '\'' +
                ", open='" + open + '\'' +
                ", high='" + high + '\'' +
                ", low='" + low + '\'' +
                ", close='" + close + '\'' +
                ", volume='" + volume + '\'' +
                '}';
    }
}
