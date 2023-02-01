package org.opennms.horizon.alarmservice;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Cucumber.class)
@CucumberOptions(
    glue = {"org.opennms.horizon.alarmservice.steps"},
    plugin = {"json:target/cucumber-report.json", "html:target/cucumber.html", "pretty"},
    objectFactory = io.cucumber.spring.SpringFactory.class,
    features = "classpath:org/opennms/horizon/spring"
)
public class SpringCucumberRunnerIT {
    private static final Logger LOG = LoggerFactory.getLogger(SpringCucumberRunnerIT.class);
}
