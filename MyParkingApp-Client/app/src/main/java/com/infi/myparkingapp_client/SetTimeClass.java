package com.infi.myparkingapp_client;

/**
 * Created by INFIi on 5/30/2016.
 */
public class SetTimeClass {
    String label;
    String selected;

    public void setLabel(String label) {
        this.label = label;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    public String getLabel() {
        return label;
    }

    public String getSelected() {
        return selected;
    }

    public  SetTimeClass(String label,String selected){
        this.label=label;
        this.selected=selected;
    }
}
