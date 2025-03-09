package com.svilvo.dialogs;

import com.svilvo.hc_database.entities.DayEntity;

public interface DayDeleteCallback {
    void onComplete(DayEntity day);
}
