package com.homey.htaf.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import io.qameta.allure.testng.AllureTestNg;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = "com.homey.htaf"
)
@Listeners(AllureTestNg.class)
public class ParallelRunner extends AbstractTestNGCucumberTests {
    static {
        configureTags();
    }

    private static void configureTags() {
        String override = System.getProperty("tags");
        if (override == null || override.isBlank()) {
            System.setProperty("cucumber.filter.tags", "@parallel");
            return;
        }
        if (override.contains("@serial")) {
            System.setProperty("cucumber.filter.tags", "@__skip__");
            return;
        }
        System.setProperty("cucumber.filter.tags", "(" + override + ") and not @serial");
    }

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
