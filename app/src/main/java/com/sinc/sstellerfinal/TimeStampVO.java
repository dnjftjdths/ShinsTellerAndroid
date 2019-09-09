package com.sinc.sstellerfinal;

import java.util.ArrayList;
import java.util.Map;

public class TimeStampVO {
    private String category;
    private Map<String, ArrayList> timeStamp;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Map<String, ArrayList> getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Map<String, ArrayList> timeStamp) {
        this.timeStamp = timeStamp;
    }
}
