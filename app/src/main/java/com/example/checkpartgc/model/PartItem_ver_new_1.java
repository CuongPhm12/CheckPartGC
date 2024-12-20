package com.example.checkpartgc.model;

public class PartItem_ver_new_1 {
    private String PART_NO;
    private int QTY;

    public int getQTY() {
        return QTY;
    }

    public void setQTY(int QTY) {
        this.QTY = QTY;
    }

    public PartItem_ver_new_1(String PART_NO, int QTY ) {
        this.PART_NO = PART_NO;
        this.QTY = QTY;

    }

    public String getPART_NO() {
        return PART_NO;
    }

    public void setPART_NO(String PART_NO) {
        this.PART_NO = PART_NO;
    }
}
