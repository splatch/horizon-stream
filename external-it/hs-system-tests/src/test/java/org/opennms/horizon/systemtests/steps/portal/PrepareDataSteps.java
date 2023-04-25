package org.opennms.horizon.systemtests.steps.portal;

import io.cucumber.java.en.Given;
import org.opennms.horizon.systemtests.CucumberHooks;
import org.opennms.horizon.systemtests.api.portal.models.BtoInstancesResponse;
import org.opennms.horizon.systemtests.keyvalue.SecretsStorage;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;

import java.util.List;

import static org.opennms.horizon.systemtests.CucumberHooks.INSTANCES;
import static org.opennms.horizon.systemtests.CucumberHooks.portalApi;

public class PrepareDataSteps {

    @Given("No BTO instances created")
    public void emptyState() {
        portalApi.deleteAllBtoInstances();
    }

    @Given("BTO instance name {string} created")
    public void prepareAnInstance(String instanceName) {
        if (instanceName.startsWith("random")) {
            instanceName = "Instance_" + RandomStringUtils.randomAlphabetic(10);
            INSTANCES.add(instanceName);
        }
        CucumberHooks.portalApi.createBtoInstance(instanceName, SecretsStorage.adminUserEmail);
    }

    @Given("A list of BTO instances are created")
    public void prepareAnInstance(List<String> instanceNames) {
        instanceNames.forEach(name ->
            CucumberHooks.portalApi.createBtoInstance(name, SecretsStorage.adminUserEmail)
        );
    }

    @Given("someone deletes the {string} instance")
    public void deleteInstanceByName(String instanceName) {
        BtoInstancesResponse allBtoInstancesByName = CucumberHooks.portalApi.getAllBtoInstancesByName(instanceName);
        portalApi.deleteBtoInstance(allBtoInstancesByName.pagedRecords.get(0).id);
    }
}
