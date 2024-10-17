package com.example.checkpartgc.model;

public class MI_Master {
    private String MI_INDEX;
    private  String LOCATION_ID;
    private String SPEC;

    public MI_Master(String MI_INDEX, String LOCATION_ID, String SPEC) {
        this.MI_INDEX = MI_INDEX;
        this.LOCATION_ID = LOCATION_ID;
        this.SPEC = SPEC;
    }

    public String getMI_INDEX() {
        return MI_INDEX;
    }

    public void setMI_INDEX(String MI_INDEX) {
        this.MI_INDEX = MI_INDEX;
    }

    public String getLOCATION_ID() {
        return LOCATION_ID;
    }

    public void setLOCATION_ID(String LOCATION_ID) {
        this.LOCATION_ID = LOCATION_ID;
    }

    public String getSPEC() {
        return SPEC;
    }

    public void setSPEC(String SPEC) {
        this.SPEC = SPEC;
    }
}
