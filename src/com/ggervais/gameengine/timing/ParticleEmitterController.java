package com.ggervais.gameengine.timing;

import com.ggervais.gameengine.particle.ParticleEmitterConfiguration;
import com.ggervais.gameengine.scene.scenegraph.Node;
import com.ggervais.gameengine.scene.scenegraph.Spatial;

public abstract class ParticleEmitterController extends Controller {

    protected ParticleEmitterConfiguration configuration;
    protected long lastLaunch;

    public ParticleEmitterController(ParticleEmitterConfiguration configuration) {
        this(null, configuration);
    }

    public ParticleEmitterController(Node controlledSpatialObject, ParticleEmitterConfiguration configuration) {
        super();
        setControllerObject(controlledSpatialObject);
        this.configuration = configuration;
        this.lastLaunch = 0;
    }

    public void setControllerObject(Node node) {
        super.setControlledObject(node);
    }

    @Override
    public void setControlledObject(Spatial object) {
        throw new IllegalArgumentException();
    }
}
