package com.ggervais.gameengine.render.opengl;

import java.awt.Component;

import javax.media.opengl.awt.GLCanvas;

import com.ggervais.gameengine.render.DisplayFactory;
import com.ggervais.gameengine.render.RendererFactory;
import com.ggervais.gameengine.render.SceneRenderer;
import com.ggervais.gameengine.scene.Scene;

public class GLRendererFactory implements RendererFactory {
	
	public SceneRenderer buildRenderer(Scene scene) {
		
		GLRenderer renderer = new GLRenderer(scene, new GLCanvas());
		
		return renderer;
	}

}
