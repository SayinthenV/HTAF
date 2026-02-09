package com.homey.htaf.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import io.qameta.allure.testng.AllureTestNg;
import org.testng.annotations.Listeners;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = "com.homey.htaf"
)
@Listeners(AllureTestNg.class)
public class SerialRunner extends AbstractTestNGCucumberTests {
    static {
        configureTags();
    }

    private static void configureTags() {
        String override = System.getProperty("tags");
        if (override == null || override.isBlank()) {
            System.setProperty("cucumber.filter.tags", "@__skip__");
            return;
        }
        String expression = override.contains("@serial")
                ? override
                : "@serial and (" + override + ")";
        System.setProperty("cucumber.filter.tags", expression);
    }
}
