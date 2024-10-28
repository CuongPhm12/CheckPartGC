package com.example.checkpartgc.model;

public class MI_Master {
    private String MI_INDEX;
    private  String LOCATION_ID;
    private String SPEC;
    private int countOfCheck;

    public MI_Master(String MI_INDEX, String LOCATION_ID, String SPEC, int COUNTOFCHECK) {
        this.MI_INDEX = MI_INDEX;
        this.LOCATION_ID = LOCATION_ID;
        this.SPEC = SPEC;
        this.countOfCheck = COUNTOFCHECK;
    }

    public int getCOUNTOFCHECK() {
        return countOfCheck;
    }

    public void setCOUNTOFCHECK(int COUNTOFCHECK) {
        this.countOfCheck = COUNTOFCHECK;
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
