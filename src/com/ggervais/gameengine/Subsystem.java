package com.ggervais.gameengine;

public interface Subsystem {
    public void init();
	public void update(long currentTime) throws UninitializedSubsystemException;
	public void destroy() throws UninitializedSubsystemException;
    public boolean isInitialized();
}
