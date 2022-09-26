package org.opennms.horizon.datachoices.internal;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.opennms.horizon.datachoices.dto.UsageStatisticsReportDTO;

import java.util.Collections;

public class DataChoicesTimerTaskTest {
    private static final String TEST_DEVICE_TYPE = "device";
    private static final int TEST_DEVICE_TYPE_COUNT = 1;
    private static final String TEST_SYSTEM_ID = "1234";
    private static final String TEST_VERSION = "1.0.0";
    private static final int TEST_NODE_COUNT = 1;
    private static final int TEST_MONITORED_SERVICES = 2;

    private static final int TEST_PORT = 55555;

    private DataChoicesTimerTask timerTask;

    @Mock
    private UsageStatisticsReporter reporter;

    @Before
    public void setup() {
        timerTask = new DataChoicesTimerTask(reporter, "http://localhost:" + TEST_PORT);
    }

    @Test
    public void testRun() {

        UsageStatisticsReportDTO report = getReport();
        String json = report.toJson();

        System.out.println("json = " + json);


//        stubFor(WireMock.any(WireMock.urlPathEqualTo("/hs-usage-report"))
//            .withRequestBody(WireMock.equalToJson(json))
//            .willReturn(WireMock.ok()));

//        UsageStatisticsReportDTO report = getReport();
//        when(reporter.generateReport()).thenReturn(report);
//
//        timerTask.run();
    }

    private static UsageStatisticsReportDTO getReport() {
        UsageStatisticsReportDTO report = new UsageStatisticsReportDTO();
        report.setSystemId(TEST_SYSTEM_ID);
        report.setVersion(TEST_VERSION);
        report.setNodes(TEST_NODE_COUNT);
        report.setMonitoredServices(TEST_MONITORED_SERVICES);
        report.setDeviceTypeCounts(Collections.singletonMap(TEST_DEVICE_TYPE, TEST_DEVICE_TYPE_COUNT));
        return report;
    }
}
