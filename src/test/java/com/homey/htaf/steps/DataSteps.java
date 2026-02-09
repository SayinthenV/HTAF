package com.homey.htaf.steps;

import com.homey.htaf.core.BaseStep;
import com.homey.htaf.core.ScenarioContext;
import com.homey.htaf.utils.CsvDataReader;
import io.cucumber.java.en.Given;

import java.util.List;
import java.util.Map;

public class DataSteps extends BaseStep {
    private final CsvDataReader csvDataReader;

    public DataSteps() {
        super(new ScenarioContext());
        this.csvDataReader = new CsvDataReader();
    }

    @Given("I load test data from {string} as {string}")
    public void loadTestData(String file, String key) {
        List<Map<String, String>> rows = csvDataReader.read(file);
        context.set(key, rows);
    }
}
