package org.opennms.horizon.core.identity;

public class IdentityImpl implements Identity {
    @Override
    public String getId() {
        return "0ddba11";
    }

    @Override
    public String getLocation() {
        return "cloud";
    }

    @Override
    public String getType() {
        return "somethingnew";
    }
}
