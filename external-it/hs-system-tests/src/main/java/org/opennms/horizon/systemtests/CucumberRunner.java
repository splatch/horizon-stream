package org.opennms.horizon.systemtests;

import io.cucumber.core.options.CommandlineOptionsParser;
import io.cucumber.core.options.RuntimeOptions;
import io.cucumber.core.runtime.Runtime;
import org.springframework.stereotype.Component;

@Component
public class CucumberRunner {

    public static final String[] DEFAULT_CUCUMBER_OPTIONS = {
        "--plugin", "json:cucumber.reports/cucumber-report.json",
        "--plugin", "html:cucumber.reports/cucumber-report.html",
        "--plugin", "pretty",
        "--glue", "org.opennms.horizon.systemtests.definitions", // where steps definitions are located
        "--tags", "@horizon-stream",
        "classpath:features" // location of the feature file

        // org.opennms.horizon.systemtests/system-tests.feature
    };

    public void run() {
        CommandlineOptionsParser commandlineOptionsParser = new CommandlineOptionsParser(System.out);

        RuntimeOptions runtimeOptions =
            commandlineOptionsParser.parse(DEFAULT_CUCUMBER_OPTIONS)
                .build();

        Runtime cucumberRuntime =
            Runtime.builder()
                .withRuntimeOptions(runtimeOptions)
                .withClassLoader(() -> this.getClass().getClassLoader())
                .build()
            ;

        cucumberRuntime.run();

        System.exit(cucumberRuntime.exitStatus());
    }
}
