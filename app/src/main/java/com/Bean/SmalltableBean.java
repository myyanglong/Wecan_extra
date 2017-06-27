package com.Bean;

/**
 * 小表配置信息
 * Created by yanglong on 2017/6/8.
 */

public class SmalltableBean {
    private String TableId;//水表ID
    private String Accumulatedflow;//累计流量
    private String FlowCalibrationFactor;//流量校准系数
    private String CX1;
    private String CX2;
    private String CX3;
    private String CX4;
    private String CX5;
    private String CX6;

    public String getAccumulatedflow() {
        return Accumulatedflow;
    }

    public void setAccumulatedflow(String accumulatedflow) {
        Accumulatedflow = accumulatedflow;
    }

    public String getFlowCalibrationFactor() {
        return FlowCalibrationFactor;
    }

    public void setFlowCalibrationFactor(String flowCalibrationFactor) {
        FlowCalibrationFactor = flowCalibrationFactor;
    }

    public String getCX1() {
        return CX1;
    }

    public void setCX1(String CX1) {
        this.CX1 = CX1;
    }

    public String getCX2() {
        return CX2;
    }

    public void setCX2(String CX2) {
        this.CX2 = CX2;
    }

    public String getCX3() {
        return CX3;
    }

    public void setCX3(String CX3) {
        this.CX3 = CX3;
    }

    public String getCX4() {
        return CX4;
    }

    public void setCX4(String CX4) {
        this.CX4 = CX4;
    }

    public String getCX5() {
        return CX5;
    }

    public void setCX5(String CX5) {
        this.CX5 = CX5;
    }

    public String getCX6() {
        return CX6;
    }

    public void setCX6(String CX6) {
        this.CX6 = CX6;
    }

    public String getTableId() {

        return TableId;
    }

    public void setTableId(String tableId) {
        TableId = tableId;
    }
}
