package com.github.soramame0256.infectionfishingstats.util;

import java.io.IOException;

public interface IDataUtil<T> {
    String getStringData(String index);
    void saveStringData(String index, String value);
    Number getNumberData(String index);
    void saveNumberData(String index, Number value);
    boolean getBooleanData(String index);
    void saveBooleanData(String index, boolean value);
    void saveData(String index, T value);
    T getRoot();
    void flush() throws IOException;

}
