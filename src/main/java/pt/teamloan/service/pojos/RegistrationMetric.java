package pt.teamloan.service.pojos;

import pt.teamloan.model.enums.Intent;

public class RegistrationMetric {
    private String businessArea;
    private Intent intent;
    private long total;

    public RegistrationMetric() {
    }

    public RegistrationMetric(String businessArea, Intent intent, long total) {
        this.businessArea = businessArea;
        this.intent = intent;
        this.total = total;
    }

    public String getBusinessArea() {
        return this.businessArea;
    }

    public void setBusinessArea(String businessArea) {
        this.businessArea = businessArea;
    }

    public Intent getIntent() {
        return this.intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    public long getTotal() {
        return this.total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
