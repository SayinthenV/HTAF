package com.homey.htaf.core;

public abstract class BaseStep {
    protected final ScenarioContext context;

    protected BaseStep(ScenarioContext context) {
        this.context = context;
    }

    protected ScenarioContext getContext() {
        return context;
    }
}
