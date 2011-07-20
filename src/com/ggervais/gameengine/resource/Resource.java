package com.ggervais.gameengine.resource;

import com.ggervais.gameengine.UninitializedSubsystemException;

public interface Resource {
    // TODO: refactor interface as code progresses.
    public String getName();
    public void init();
    public void destroy();
    public boolean isInitialized();
    public ResourceType getType();
}
