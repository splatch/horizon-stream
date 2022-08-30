package org.opennms.horizon.notifications.api.dto;

public enum PagerDutyEventAction {
    RESOLVE("resolve"),
    ACKNOWLEDGE("acknowledge"),
    TRIGGER("trigger");

    private String val;
    PagerDutyEventAction(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }
}
