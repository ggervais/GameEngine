package com.ggervais.gameengine.particle;

import com.ggervais.gameengine.Subsystem;
import com.ggervais.gameengine.UninitializedSubsystemException;

import java.util.ArrayList;
import java.util.List;

public class ParticleSubsystem implements Subsystem {

    private List<ParticleEmitter> emitters;

    private static ParticleSubsystem instance;

    public static ParticleSubsystem getInstance() {
        if (instance == null) {
            instance = new ParticleSubsystem();
        }
        return instance;
    }

    private ParticleSubsystem() {
        this.emitters = new ArrayList<ParticleEmitter>();
    }

    public void init() {
    }

    public void update(long currentTime) throws UninitializedSubsystemException {
        for (ParticleEmitter emitter : this.emitters) {
            emitter.update(currentTime);
        }
    }

    public void destroy() throws UninitializedSubsystemException {
       this.emitters.clear();
    }

    public boolean isInitialized() {
        return this.emitters != null;
    }

    public void addEmitter(ParticleEmitter emitter) {
        this.emitters.add(emitter);
    }

    public int nbEmitters() {
        return this.emitters.size();
    }

    public ParticleEmitter getEmitter(int index) {
        return this.emitters.get(index);
    }
}
