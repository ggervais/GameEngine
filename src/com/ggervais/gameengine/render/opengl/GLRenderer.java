package com.ggervais.gameengine.render.opengl;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

import com.ggervais.gameengine.geometry.Model;
import com.ggervais.gameengine.geometry.Quad;
import com.ggervais.gameengine.geometry.primitives.Face;
import com.ggervais.gameengine.geometry.primitives.TextureCoords;
import com.ggervais.gameengine.geometry.primitives.Vertex;
import com.ggervais.gameengine.material.Material;
import com.ggervais.gameengine.math.*;
import com.ggervais.gameengine.particle.Particle;
import com.ggervais.gameengine.particle.ParticleEmitter;
import com.ggervais.gameengine.particle.ParticleSubsystem;
import com.ggervais.gameengine.physics.boundingvolumes.BoundingBox;
import com.ggervais.gameengine.physics.boundingvolumes.BoundingSphere;
import com.ggervais.gameengine.render.*;
import com.ggervais.gameengine.resource.Resource;
import com.ggervais.gameengine.resource.ResourceSubsystem;
import com.ggervais.gameengine.resource.ResourceType;
import com.ggervais.gameengine.scene.Camera;
import com.ggervais.gameengine.scene.DisplayableEntity;
import com.ggervais.gameengine.scene.Scene;
import com.ggervais.gameengine.material.texture.Texture;
import com.ggervais.gameengine.scene.scenegraph.Effect;
import com.ggervais.gameengine.scene.scenegraph.Geometry;
import com.ggervais.gameengine.scene.scenegraph.Transformation;
import com.ggervais.gameengine.scene.scenegraph.renderstates.AlphaBlendingState;
import com.ggervais.gameengine.scene.scenegraph.renderstates.WireframeState;
import com.ggervais.gameengine.scene.scenegraph.renderstates.ZBufferState;
import org.apache.log4j.Logger;

public class GLRenderer extends SceneRenderer implements GLEventListener {

    private static final Logger log = Logger.getLogger(GLRenderer.class);

	private GLU glu;
    private GL2 gl;

    private static final Random random = new Random();

    private static float particleLife = 1.0f;

	public GLRenderer(Scene scene, GLCanvas canvas) {
		super(scene, canvas);
		canvas.addGLEventListener(this);
		this.glu = new GLU();
	}

    public void display(GLAutoDrawable glDrawable) {
        drawSceneGraph(this.scene.getSceneGraphRoot());
    }

    public void display2(GLAutoDrawable glDrawable) {

        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
        gl.glClearColor(0.53f, 0.81f, 0.98f, 1);
        gl.glClearColor(0, 0, 0, 1);

        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glLoadIdentity();

        Camera camera = this.scene.getCamera();
        Point3D cameraPosition = camera.getPosition();
        Point3D cameraLookAt = camera.getLookAt();
        Vector3D cameraUp = camera.getUp();

        this.glu.gluLookAt(cameraPosition.x(), cameraPosition.y(), cameraPosition.z(),
        				   cameraLookAt.x(), cameraLookAt.y(), cameraLookAt.z(),
        				   cameraUp.x(), cameraUp.y(), cameraUp.z());

        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glEnable(GL.GL_CULL_FACE);
        gl.glDepthFunc(GL.GL_LEQUAL);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);


        OpenGLUtils.drawBaseAxis(gl, Point3D.zero(), 1.0f);

        gl.glFlush();
    }

	public void display3(GLAutoDrawable glDrawable) {

        //GL2 gl = glDrawable.getGL().getGL2();

        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
        gl.glClearColor(0.53f, 0.81f, 0.98f, 1);
        gl.glClearColor(0, 0, 0, 1);

        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glLoadIdentity();

        Camera camera = this.scene.getCamera();
        Point3D cameraPosition = camera.getPosition();
        Point3D cameraLookAt = camera.getLookAt();
        Vector3D cameraUp = camera.getUp();
        
        this.glu.gluLookAt(cameraPosition.x(), cameraPosition.y(), cameraPosition.z(), 
        				   cameraLookAt.x(), cameraLookAt.y(), cameraLookAt.z(),
        				   cameraUp.x(), cameraUp.y(), cameraUp.z());

        List<DepthSortableEntity> items = new ArrayList<DepthSortableEntity>();

        for (DisplayableEntity entity : this.scene.getEntities()) {
            items.add(entity);
        }

        for (int i = 0; i < ParticleSubsystem.getInstance().nbEmitters(); i++) {
            items.add(ParticleSubsystem.getInstance().getEmitter(i));
        }

        CameraDistanceComparator comparator = new CameraDistanceComparator(camera);
        Collections.sort(items, comparator);

        for (DepthSortableEntity dse : items) {
            if (dse instanceof DisplayableEntity) {
                DisplayableEntity entity = (DisplayableEntity) dse;

                gl.glEnable(GL.GL_DEPTH_TEST);
                gl.glEnable(GL.GL_CULL_FACE);
                gl.glDepthFunc(GL.GL_LEQUAL);
                gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

                Texture texture = null;

                if (entity.getMaterial() != null) {
                    texture = entity.getMaterial().getTexture(0);
                }


                //OpenGLUtils.drawBoundingBox(gl, entity.getBoundingBox());

                OpenGLUtils.drawModel(gl, entity.getModel(), texture, entity.getWorldMatrix());

                BoundingSphere sphere = entity.getBoundingSphere();
                Vector3D cameraRotation = camera.getDirection();

                RotationMatrix rotation = RotationMatrix.createFromFacingPositions(sphere.getCenter(), cameraPosition, cameraUp);
                //OpenGLUtils.drawCircle(gl, sphere.getCenter(), sphere.getRadius(), rotation);
            } else if (dse instanceof ParticleEmitter) {
                ParticleEmitter emitter = (ParticleEmitter) dse;


                gl.glDisable(GL.GL_DEPTH_TEST);
                gl.glDisable(GL.GL_CULL_FACE);

                for (int p = 0; p < emitter.nbParticles(); p++) {
                    Particle particle = emitter.getParticle(p);

                    if (particle != null && !particle.isDead()) {
                        Texture texture = particle.getTexture();
                        float aspectRatio = (float) texture.getNbCellsHeight() / (float)texture.getNbCellsWidth();
                        Model particleModel = new Quad(aspectRatio);
                        DisplayableEntity entity = new DisplayableEntity(particleModel);
                        entity.setPosition(particle.getPosition());

                        float scale = particle.getScale();
                        entity.setScale(new Vector3D(scale, scale, scale));

                        RotationMatrix rotation = new RotationMatrix();
                        if (particle.isBillboard()) {
                            rotation = RotationMatrix.createFromFacingPositions(entity.getPosition(), cameraPosition, cameraUp);
                        }
                        //RotationMatrix stepperRotation = RotationMatrix.createFromYawPitchRoll(particle.getRotation().x(), particle.getRotation().y(), particle.getRotation().z());
                        //rotation.mult(stepperRotation);

                        RotationMatrix particleRotation = particle.getRotationMatrix();
                        rotation.mult(particleRotation);

                        if (particle.useAdditiveBlending()) {
                            gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
                        } else {
                            gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
                        }

                        OpenGLUtils.drawParticle(gl, particleModel, texture, particle.getTextureIndex(), particle.getColor(), entity.getWorldMatrix(rotation), particle.getAlpha());
                    }
                }
            }
        }

        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glEnable(GL.GL_CULL_FACE);
        gl.glDepthFunc(GL.GL_LEQUAL);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);


        OpenGLUtils.drawBaseAxis(gl, Point3D.zero(), 1.0f);
        
        gl.glFlush();
	}

	public void dispose(GLAutoDrawable glDrawable) {
	}

	public void init(GLAutoDrawable glDrawable) {
        log.info("Init");
		//GL2 gl = glDrawable.getGL().getGL2();
		this.gl = glDrawable.getGL().getGL2();
        gl.glShadeModel(GLLightingFunc.GL_SMOOTH);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClearDepth(1.0f);
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LEQUAL);
        gl.glHint(GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
        gl.glDisable(GL.GL_CULL_FACE);
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_FILL);

        ResourceSubsystem resourceSubsystem = ResourceSubsystem.getInstance();
        for (Resource resource : resourceSubsystem.findResourcesByType(ResourceType.TEXTURE)) {
            Texture texture = (Texture) resource;
            if (!texture.isInitialized()) {
                OpenGLUtils.uploadTexture(gl, texture);
            }
        }
        for (Resource resource : resourceSubsystem.findResourcesByType(ResourceType.MATERIAL)) {
            Material material = (Material) resource;
            Texture texture = material.getTexture(0);
            if (texture != null) {
                if (!texture.isInitialized()) {
                    OpenGLUtils.uploadTexture(gl, texture);
                }
            }
        }
	}

    @Override
    public void setWorldTransformations(Transformation worldTransformation) {
        //gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glMultMatrixf(worldTransformation.getMatrix().toColumnMajorArray(), 0);
    }

    @Override
    public void restoreWorldTransformations() {
        //gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glPopMatrix();
    }

    @Override
    public void drawElements(Geometry geometry) {
        int nbVerticesPerFace = geometry.getNbVerticesPerFace();
		int glPrimitiveType = GL2.GL_TRIANGLES; // Defaults to triangles.

		switch(nbVerticesPerFace) {
			case 1:
				glPrimitiveType = GL2.GL_POINTS;
				break;
			case 2:
				glPrimitiveType = GL2.GL_LINES;
				break;
			case 3:
				glPrimitiveType = GL2.GL_TRIANGLES;
				break;
			case 4:
				glPrimitiveType = GL2.GL_QUADS;
				break;
		}
        Effect effect = geometry.getEffect();
        Texture texture = null;
        Vector3D minBounds = new Vector3D(0, 0, 0);
        Vector3D maxBounds = new Vector3D(1, 1, 0);
        float w = 1;
        float h = 1;

        if (effect != null && effect.nbTextures() > 0) {
            texture = effect.getTexture(0);
            if (texture != null) {
                int tw = texture.getNbCellsWidth();
                int th = texture.getNbCellsHeight();
                int nb = tw * th;
                int index = this.random.nextInt(nb);
                if (nb > 0) {
                    minBounds = effect.getMinBoundsForTexture(0);
                    maxBounds = effect.getMaxBoundsForTexture(0);
                    w = maxBounds.x() - minBounds.x();
                    h = maxBounds.y() - minBounds.y();
                }
            }
        }

		gl.glBegin(glPrimitiveType);
			for(int i = 0; i < geometry.getFaces().size(); i++) {
				Face face = geometry.getFaces().get(i);

				for (int vi = 0; vi < face.nbVertices(); vi++) {

					Vertex vertex = face.getVertex(vi);
					Point3D vertexPosition = vertex.getPosition();

                    if (face.nbTextureCoords() == face.nbVertices()) {
                        TextureCoords coords = face.getTextureCoords(vi);
                        float tu = coords.getTextureU();
                        float tv = coords.getTextureV();

                        tu = minBounds.x() + tu * w;
                        tv = minBounds.y() + tv * h;

                        gl.glTexCoord2f(tu, tv);
                    }

					gl.glVertex3f(vertexPosition.x(), vertexPosition.y(), vertexPosition.z());
				}
			}
		gl.glEnd();
    }


    @Override
    public void setAlphaBlendingState(AlphaBlendingState state) {
        if (state.isEnabled()) {
            gl.glEnable(GL.GL_BLEND);
            gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
        } else {
            gl.glEnable(GL.GL_BLEND);
            gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        }

    }

    @Override
    public void setWireframeState(WireframeState state) {
        if (state.isEnabled()) {
            gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_LINE);
        } else {
            gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_FILL);
        }
    }

    @Override
    public void setZBufferState(ZBufferState state) {
        if (state.isEnabled()) {
            gl.glEnable(GL.GL_DEPTH_TEST);
            gl.glDepthFunc(GL.GL_LEQUAL);
        } else {
            gl.glDisable(GL.GL_DEPTH_TEST);
        }
    }
    @Override
    public void setColor(Color color) {
        gl.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
    }

    @Override
    public void resetColor() {
        gl.glColor4f(1, 1, 1, 1);
    }

    @Override
    public void bindTexture(Texture texture) {
        if (texture.getId() == -1) {
            OpenGLUtils.uploadTexture(gl, texture);
        }
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture.getId());
    }

    @Override
    public void unbindTexture(Texture texture) {
        gl.glBindTexture(GL.GL_TEXTURE_2D, -1);
    }

    @Override
    public void drawBoundingBox(BoundingBox box, boolean isPicked) {
        OpenGLUtils.drawBoundingBox(gl, box, isPicked);
    }

    public void reshape(GLAutoDrawable glDrawable, int x, int y, int width,	int height) {
        log.info("Reshape");
		//GL2 gl = glDrawable.getGL().getGL2();

        log.info(new Point3D(x, y, 0) + " " + new Vector3D(width, height, 0));
		
		if (height <= 0) {
			height = 1;
		}
		
		float aspectRatio = (float) width / (float) height;
		
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();
		
		this.glu.gluPerspective(45f, aspectRatio, DisplaySubsystem.getInstance().getNear(), DisplaySubsystem.getInstance().getFar());
		
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glLoadIdentity();

        float[] projection = new float[16];
        gl.glGetFloatv(GL2.GL_PROJECTION_MATRIX, projection, 0);

        // TODO bad code, refactor!
        Matrix4x4 projectionMatrix = Matrix4x4.createFromFloatArray(projection, true);
        DisplaySubsystem.getInstance().setProjectionMatrix(projectionMatrix);
        DisplaySubsystem.getInstance().setViewport(new Viewport(x, y, width, height));
	}

	public void update(Observable o, Object arg) {
		this.canvas.repaint();
	}

    @Override
    public void beginRendering() {

        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
        //gl.glClearColor(0.53f, 0.81f, 0.98f, 1);
        gl.glClearColor(0, 0, 0, 1);

        gl.glColor4f(1, 1, 1, 1);

        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glLoadIdentity();

        Camera camera = this.scene.getCamera();
        Point3D cameraPosition = camera.getPosition();
        Point3D cameraLookAt = camera.getLookAt();
        Vector3D cameraUp = camera.getUp();

        this.glu.gluLookAt(cameraPosition.x(), cameraPosition.y(), cameraPosition.z(),
        				   cameraLookAt.x(), cameraLookAt.y(), cameraLookAt.z(),
        				   cameraUp.x(), cameraUp.y(), cameraUp.z());

        float[] projection = new float[16];
        gl.glGetFloatv(GL2.GL_PROJECTION_MATRIX, projection, 0);

        float[] modelView = new float[16];
        gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, modelView, 0);
        Matrix4x4 modelViewMatrix = Matrix4x4.createFromFloatArray(modelView, true);
        DisplaySubsystem.getInstance().setModelViewMatrix(modelViewMatrix);

        int[] viewport = new int[4];
        gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);

        float cx = (viewport[0] + viewport[2]) / 2f;
        float cy = (viewport[1] + viewport[3]) / 2f;
        float near = 0.001f;
        float far = 1000f;

        float[] projectedNear = new float[3];
        float[] projectedFar = new float[3];

        glu.gluUnProject(cx, cy, near, modelView, 0, projection, 0, viewport, 0, projectedNear, 0);
        glu.gluUnProject(cx, cy, far, modelView, 0, projection, 0, viewport, 0, projectedFar, 0);

        Matrix4x4 projectionMatrix = Matrix4x4.createFromFloatArray(projection, true);

        /*System.out.println("Projected near: (" + projectedNear[0] + ", " + projectedNear[1] + ", " + projectedNear[2] + ").");
        System.out.println("Projected far: (" + projectedFar[0] + ", " + projectedFar[1] + ", " + projectedFar[2] + ").");
        System.out.println("Difference: ("  + (projectedFar[0] - projectedNear[0]) + ", " + (projectedFar[1] - projectedNear[1]) + ", " + (projectedFar[2] - projectedNear[2]) + ").");
        System.out.println("=====");     */
    }

    @Override
    public void endRendering() {

        OpenGLUtils.drawAxisGrid(gl, 50);
        OpenGLUtils.drawBaseAxis(gl, Point3D.zero(), 1.0f);

        Ray ray = DisplaySubsystem.getInstance().getPickingRay(this.scene.getCamera());
        Point3D finalPoint = Point3D.add(ray.getOrigin(), ray.getDirection().multiplied(5));

        gl.glFlush();
    }

    public static void main(String[] args) {
        //ProjectFloat glu = new ProjectFloat();
        GLU glu = new GLU();
        Viewport vp = new Viewport(0, 0, 100, 100);
        int[] vpv = {0, 0, 100, 100};
        Matrix4x4 modelView = new Matrix4x4();
        Matrix4x4 projection = new Matrix4x4();
        float[] result = new float[3];

        projection.setElement(1, 1, 1.71f);
        projection.setElement(2, 2, 2.41f);
        projection.setElement(3, 3, -1f);
        projection.setElement(3, 4, -0.002f);
        projection.setElement(4, 3, -1f);
        projection.setElement(4, 4, 0);

        modelView.setElement(1, 1, 0.7109f);
        modelView.setElement(1, 2, 0);
        modelView.setElement(1, 3, -0.7032f);
        modelView.setElement(1, 4, -0.0007947f);

        modelView.setElement(2, 1, -0.6785f);
        modelView.setElement(2, 2, 0.26267f);
        modelView.setElement(2, 3, -0.68594f);
        modelView.setElement(2, 4, 0.0512f);

        modelView.setElement(3, 1, 0.1847f);
        modelView.setElement(3, 2, 0.96488f);
        modelView.setElement(3, 3, 0.1867f);
        modelView.setElement(3, 4, -6.64967f);

        modelView.setElement(4, 1, 0);
        modelView.setElement(4, 2, 0);
        modelView.setElement(4, 3, 0);
        modelView.setElement(4, 4, 1);

        glu.gluUnProject(50, 50, 0, modelView.toColumnMajorArray(), 0, projection.toColumnMajorArray(), 0, vpv, 0, result, 0);

        System.out.println("(" + result[0] + ", " + result[1] + ", " + result[2] + ")");
        System.out.println(vp.unproject(new Point3D(50, 50, 0, 1), modelView, projection));
    }
}
