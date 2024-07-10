package org.example.model;

import java.util.List;

public class Currency {
    private List<String[]> supported_codes;
    private double conversion_rate;

    public void setConversion_rate(double conversion_rate) {
        this.conversion_rate = conversion_rate;
    }

    public double getConversion_rate() {
        return conversion_rate;
    }

    public List<String[]> getSupported_codes() {
        return supported_codes;
    }

    public void setSupported_codes(List<String[]> supported_codes) {
        this.supported_codes = supported_codes;
    }
}
