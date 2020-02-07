package com.smile.calendar.format;


import androidx.annotation.NonNull;

import org.joda.time.LocalDate;

/**
 * @author zhaojun (Email:laobadaozjj@gmail.com).
 */
public interface Formatter {

    String getDayName(@NonNull LocalDate date);
    String getHeaderText(int type, @NonNull LocalDate from, @NonNull LocalDate to, @NonNull LocalDate selected);

}
