package org.opennms.horizon.dockerit;

import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.opennms.horizon.minion.flows.shell.SendFlowCmd;
import org.opennms.horizon.testtool.miniongateway.wiremock.api.SinkMessageDto;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertFalse;

/** Tests the full flow process as black box test:
 *  1.) Enable flows
 *  2.) Sends Netflow9 UDP packet
 *  3.) Check if packet was processed and send to cloud
  */
@Slf4j
public class FlowsIT {

    @Test
    public void blackBoxTestFlows() throws Exception {
        log.info("running FlowsIT.blackBoxTestFlows()");
        enableFlows();
        sendNetflowPackage();
        checkIfReceivedInCloud();
    }

    private void enableFlows() {
      // TODO: Patrick: when Freddy is done with his work
    }

    private void sendNetflowPackage() throws Exception {
        SendFlowCmd cmd = new SendFlowCmd();
        cmd.setHost("application"); // TODO: Patrick
        cmd.execute();
    }

    private void checkIfReceivedInCloud() throws MalformedURLException {

        RestAssuredConfig restAssuredConfig = RestAssuredConfig.config()
            .httpClient(HttpClientConfig.httpClientConfig()
            );

        RequestSpecification requestSpecification =
            RestAssured
                .given()
                .config(restAssuredConfig);

        final SinkMessageDto[] result = requestSpecification
            .get("http://minion-gateway:8080/api/sinkMessages")
            .thenReturn()
            .as(SinkMessageDto[].class);
        List<SinkMessageDto> messages = Arrays.stream(result)
            .filter(msg -> "Flow".equals(msg.getModuleId()))
            .collect(Collectors.toList());
        log.info("Cloud Gateway received:\n   {} SinkMessages in total\n   {} of these were Flow messages", result.length, messages.size());
        assertFalse(messages.isEmpty());
    }

}
