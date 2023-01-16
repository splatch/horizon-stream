package org.opennms.horizon.dockerit;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.opennms.horizon.minion.flows.shell.SendFlowCmd;

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

    }

    private void sendNetflowPackage() throws Exception {
        SendFlowCmd cmd = new SendFlowCmd();
        cmd.execute();
    }

    private void checkIfReceivedInCloud() {

    }

}
