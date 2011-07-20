package com.ggervais.gameengine.render;

import com.ggervais.gameengine.scene.Scene;

public abstract class RendererFactory {

	public abstract SceneRenderer buildRenderer(Scene scene);
}
