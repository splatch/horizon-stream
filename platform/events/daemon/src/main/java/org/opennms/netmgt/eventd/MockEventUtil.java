package org.opennms.netmgt.eventd;

import java.sql.SQLException;

public class MockEventUtil extends AbstractEventUtil {
    @Override
    public String getHardwareFieldValue(String parm, long nodeId) {
        return null;
    }

    @Override
    public String getHostName(int nodeId, String hostip) throws SQLException {
        return null;
    }

    @Override
    public String getIfAlias(long nodeId, String ipAddr) throws SQLException {
        return null;
    }

    @Override
    public String getAssetFieldValue(String parm, long nodeId) {
        return null;
    }

    @Override
    public String getForeignId(long nodeId) throws SQLException {
        return null;
    }

    @Override
    public String getForeignSource(long nodeId) throws SQLException {
        return null;
    }

    @Override
    public String getNodeLabel(long nodeId) throws SQLException {
        return null;
    }

    @Override
    public String getNodeLocation(long nodeId) throws SQLException {
        return null;
    }

    @Override
    public String getPrimaryInterface(long nodeId) throws SQLException {
        return null;
    }
}
