package com.homey.htaf.core;

public abstract class BaseStep {
    protected final ScenarioContext context;

    protected BaseStep() {
        this.context = ScenarioContext.getInstance();
    }

    protected ScenarioContext getContext() {
        return context;
    }
}
