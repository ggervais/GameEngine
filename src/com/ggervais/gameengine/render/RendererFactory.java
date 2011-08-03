package com.ggervais.gameengine.render;

import com.ggervais.gameengine.scene.Scene;

public interface RendererFactory {

	public SceneRenderer buildRenderer(Scene scene);
}
