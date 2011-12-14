package com.ggervais.gameengine.render.opengl;

import java.awt.*;
import java.text.SimpleDateFormat;
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

import com.ggervais.gameengine.geometry.primitives.*;
import com.ggervais.gameengine.material.Material;
import com.ggervais.gameengine.math.*;
import com.ggervais.gameengine.physics.boundingvolumes.BoundingBox;
import com.ggervais.gameengine.render.*;
import com.ggervais.gameengine.render.shader.Program;
import com.ggervais.gameengine.render.shader.Shader;
import com.ggervais.gameengine.resource.Resource;
import com.ggervais.gameengine.resource.ResourceSubsystem;
import com.ggervais.gameengine.resource.ResourceType;
import com.ggervais.gameengine.scene.Camera;
import com.ggervais.gameengine.scene.Scene;
import com.ggervais.gameengine.material.texture.Texture;
import com.ggervais.gameengine.scene.scenegraph.*;
import com.ggervais.gameengine.scene.scenegraph.renderstates.AlphaBlendingState;
import com.ggervais.gameengine.scene.scenegraph.renderstates.LightingState;
import com.ggervais.gameengine.scene.scenegraph.renderstates.WireframeState;
import com.ggervais.gameengine.scene.scenegraph.renderstates.ZBufferState;
import com.jogamp.opengl.util.gl2.GLUT;
import org.apache.log4j.Logger;

public class GLRenderer extends SceneRenderer implements GLEventListener {

    private static final Logger log = Logger.getLogger(GLRenderer.class);

	private GLU glu;
    private GLUT glut;
    private GL2 gl;
    private int nbLights;
    private static final int MAX_LIGHTS = 8;
    private static final int MAX_TEXTURES = 32;
    
    private Shader vertexShader;
    private Shader fragmentShader;
    private Program program;
    private int textureUniformLocation;
    
    private static final Random random = new Random();

    private static float particleLife = 1.0f;

    public GLRenderer(Scene scene, GLCanvas canvas) {
		super(scene, canvas);
		canvas.addGLEventListener(this);
		this.glu = new GLU();
        this.glut = new GLUT();
        this.nbLights = 0;
        this.textureUniformLocation = -1;
	}

    public void display(GLAutoDrawable glDrawable) {
        drawSceneGraph(this.scene.getSceneGraphRoot());
    }

	public void dispose(GLAutoDrawable glDrawable) {
	}

	public void init(GLAutoDrawable glDrawable) {
        log.info("Init");

        this.gl = glDrawable.getGL().getGL2();

        this.shaderFactory = new GLShaderFactory(this.gl);
        this.programFactory = new GLProgramFactory(this.gl);

        gl.glShadeModel(GLLightingFunc.GL_SMOOTH);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClearDepth(1.0f);
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LEQUAL);
        gl.glHint(GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
        gl.glEnable(GL.GL_CULL_FACE);
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

        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glEnable(GL2.GL_NORMALIZE);

        this.vertexShader = this.shaderFactory.buildVertexShader("shaders/vertex/hello-gl.v.glsl");
        boolean vertexShaderCompilationSuccess = this.vertexShader.compile();
        if (vertexShaderCompilationSuccess) {
            log.info("Vertex shader compiled!");
        }
        
        this.fragmentShader = this.shaderFactory.buildFragmentShader("shaders/fragment/hello-gl.f.glsl");
        boolean fragmentShaderCompilationSuccess = this.fragmentShader.compile();
        if (fragmentShaderCompilationSuccess) {
            log.info("Fragment shader compiled!");
        }

        if (vertexShaderCompilationSuccess && fragmentShaderCompilationSuccess) {
            this.program = this.programFactory.buildProgram(this.vertexShader, this.fragmentShader);
            boolean programLinkSuccess = this.program.linkShaders();
            if (programLinkSuccess) {
                log.info("Program linked!");
                boolean isProgram = gl.glIsProgram(this.program.getId());
                log.info("Is program: " + isProgram);
                this.textureUniformLocation = gl.glGetUniformLocation(this.program.getId(), "texture");
                log.info(this.textureUniformLocation);
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
        
        gl.glUseProgram(this.program.getId());
        
        //int nbVerticesPerFace = geometry.getNbVerticesPerFace();
		int glPrimitiveType = GL2.GL_TRIANGLES; // Defaults to triangles.

		Effect effect = geometry.getEffect();
        Texture texture = null;


        VertexBuffer vertexBuffer = geometry.getVertexBuffer();
        IndexBuffer indexBuffer = geometry.getIndexBuffer();
        TextureBuffer textureBuffer = geometry.getTextureBuffer();

        List<Integer> subIndicesKeys = indexBuffer.getNbVerticesList();

        for (int nbVerticesPerFace : subIndicesKeys) {
            List<Integer> subIndexBuffer = indexBuffer.getSubIndexBuffer(nbVerticesPerFace);

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

            gl.glBegin(glPrimitiveType);
                for(int i = 0; i < subIndexBuffer.size(); i++) {
                    int index = subIndexBuffer.get(i);
                    if (index < vertexBuffer.size()) {

                        Vertex vertex = vertexBuffer.getVertex(index);
                        Point3D vertexPosition = vertex.getPosition();
                        Vector3D normal = geometry.getNormal(index);

                        if (effect != null) {
                            Color color = effect.getColor(index);
                            gl.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
                        }

                        if (effect != null) {
                            for (int textureIndex = 0; textureIndex < effect.nbTextures(); textureIndex++) {
                                TextureCoords coords = null;
                                try {
                                    if (effect.hasTexturesCoordsPerVertex()) {
                                        coords = effect.getTextureCoordsForVertex(textureIndex, index);
                                    } else {
                                        coords = effect.getTextureCoords(textureIndex, nbVerticesPerFace, i); // i refers to loop index for the sub index buffer.
                                    }
                                } catch (Exception e) {

                                }

                                if (coords != null) {
                                    float tu = coords.getTextureU();
                                    float tv = coords.getTextureV();

                                    gl.glMultiTexCoord2f(GL.GL_TEXTURE0 + textureIndex, tu, tv);
                                }

                                if (textureIndex == 0) {
                                    gl.glActiveTexture(GL.GL_TEXTURE0 + textureIndex);
                                    gl.glBindTexture(GL.GL_TEXTURE_2D, effect.getTexture(textureIndex).getId());
                                    gl.glUniform1i(this.textureUniformLocation, textureIndex);
                                }
                            }
                        }

                        if (normal != null) {
                            gl.glNormal3f(normal.x(), normal.y(), normal.z());
                        }

                        gl.glVertex3f(vertexPosition.x(), vertexPosition.y(), vertexPosition.z());
                    }
                }
            gl.glEnd();
        }

        /*gl.glBegin(GL.GL_LINES);
        gl.glColor4f(1f, 1f, 1f, 1f);
        for (int i = 0; i < vertexBuffer.size(); i++) {
            Vector3D normal = geometry.getNormal(i);
            if (normal != null) {
                Point3D beginningOfNormal = vertexBuffer.getVertex(i).getPosition();
                Point3D endOfNormal = Point3D.add(beginningOfNormal, normal.multiplied(0.5f));
                gl.glVertex3f(beginningOfNormal.x(), beginningOfNormal.y(), beginningOfNormal.z());
                gl.glVertex3f(endOfNormal.x(), endOfNormal.y(), endOfNormal.z());
            }
        }
        gl.glEnd();*/
        
        gl.glUseProgram(0);
    }

    @Override
    public void setLightingState(LightingState state) {
        if (state.isEnabled()) {
            gl.glEnable(GL2.GL_LIGHTING);
        } else {
            gl.glDisable(GL2.GL_LIGHTING);
        }
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

        if (state.isDepthTestEnabled()) {
            gl.glEnable(GL.GL_DEPTH_TEST);
            gl.glDepthFunc(GL.GL_LEQUAL);
        } else {
            gl.glDisable(GL.GL_DEPTH_TEST);
        }

        if (state.isZBufferWritingEnabled()) {
            gl.glDepthMask(true);
        } else {
            gl.glDepthMask(false);
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
    public void bindTexture(int index, Texture texture) {

        if (index < MAX_TEXTURES) {
            if (texture.getId() == -1) {
                OpenGLUtils.uploadTexture(gl, texture);
            }

            int textureIndex = GL.GL_TEXTURE0 + index;

            gl.glActiveTexture(textureIndex);
            gl.glBindTexture(GL.GL_TEXTURE_2D, texture.getId());
            gl.glEnable(GL.GL_TEXTURE_2D);
        }
    }

    @Override
    public void unbindTexture(Texture texture) {
        gl.glBindTexture(GL.GL_TEXTURE_2D, -1);
    }

    @Override
    public void unbindTexture(int index, Texture texture) {
        if (index < MAX_TEXTURES) {
            int textureIndex = GL.GL_TEXTURE0 + index;
            gl.glActiveTexture(textureIndex);
            gl.glBindTexture(GL.GL_TEXTURE_2D, -1);
        }
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
		
		this.glu.gluPerspective((float) Math.toDegrees(this.scene.getCamera().getFieldOfView()), aspectRatio, this.scene.getCamera().getNear(), this.scene.getCamera().getFar());
		
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glLoadIdentity();

        float[] projection = new float[16];
        gl.glGetFloatv(GL2.GL_PROJECTION_MATRIX, projection, 0);

        // TODO bad code, refactor!
        this.scene.setViewport(new Viewport(x, y, width, height));
        Matrix4x4 projectionMatrix = Matrix4x4.createFromFloatArray(projection, true);
        this.scene.setProjectionMatrix(projectionMatrix);
        //DisplaySubsystem.getInstance().setViewport(new Viewport(x, y, width, height));
	}

	public void update(Observable o, Object arg) {
		this.canvas.repaint();
	}

    private void resetLights() {
        this.nbLights = 0;
        gl.glDisable(GL2.GL_LIGHT0);
        gl.glDisable(GL2.GL_LIGHT1);
        gl.glDisable(GL2.GL_LIGHT2);
        gl.glDisable(GL2.GL_LIGHT3);
        gl.glDisable(GL2.GL_LIGHT4);
        gl.glDisable(GL2.GL_LIGHT5);
        gl.glDisable(GL2.GL_LIGHT6);
        gl.glDisable(GL2.GL_LIGHT7);
    }

    @Override
    public void beginRendering() {

        resetLights();

        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
        //gl.glClearColor(0.53f, 0.81f, 0.98f, 1);
        gl.glClearColor(0, 0, 0, 1);

        gl.glColor4f(1, 1, 1, 1);


        Camera camera = this.scene.getCamera();

        float[] projection = new float[16];
        gl.glGetFloatv(GL2.GL_PROJECTION_MATRIX, projection, 0);
        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();
		this.glu.gluPerspective((float) Math.toDegrees(camera.getFieldOfView()), this.scene.getViewport().getAspectRatio(), this.scene.getCamera().getNear(), this.scene.getCamera().getFar());

        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glLoadIdentity();

        Point3D cameraPosition = camera.getPosition();
        Point3D cameraLookAt = camera.getLookAt();
        Vector3D cameraUp = camera.getUp();

        this.glu.gluLookAt(cameraPosition.x(), cameraPosition.y(), cameraPosition.z(),
        				   cameraLookAt.x(), cameraLookAt.y(), cameraLookAt.z(),
        				   cameraUp.x(), cameraUp.y(), cameraUp.z());

        float[] modelView = new float[16];
        gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, modelView, 0);
        Matrix4x4 modelViewMatrix = Matrix4x4.createFromFloatArray(modelView, true);
        this.scene.setModelViewMatrix(modelViewMatrix);

        int[] viewport = new int[4];
        gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);

        // Init lighting.
        float[] specularLight = { 1.0f, 1.0f, 1.0f, 1.0f };
        float[] lightShininess = { 50.0f };
        gl.glShadeModel (GL2.GL_SMOOTH);

        gl.glColorMaterial(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, specularLight, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, lightShininess, 0);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_COLOR_MATERIAL);


        /*log.info("Projected near: (" + projectedNear[0] + ", " + projectedNear[1] + ", " + projectedNear[2] + ").");
        log.info("Projected far: (" + projectedFar[0] + ", " + projectedFar[1] + ", " + projectedFar[2] + ").");
        log.info("Difference: ("  + (projectedFar[0] - projectedNear[0]) + ", " + (projectedFar[1] - projectedNear[1]) + ", " + (projectedFar[2] - projectedNear[2]) + ").");
        log.info("=====");     */
    }

    private void drawCursor(GL2 gl) {
        int w = (int) this.scene.getViewport().getWidth();
        int h = (int) this.scene.getViewport().getHeight();

        int cursorWidth = 20;
        int cursorHeight = 20;

        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glDisable(GL.GL_DEPTH_TEST);
        gl.glDepthMask(false);

        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glOrthof(0, w, h, 0, -1, 1);
        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);

        gl.glPushMatrix();
        gl.glLoadIdentity();


        gl.glBegin(GL2.GL_LINES);
            gl.glColor4f(1, 1, 1, 1);
            gl.glVertex2f(w / 2f - cursorWidth / 2f, h / 2f);
            gl.glVertex2f(w / 2f + cursorWidth / 2f, h / 2f);
            gl.glVertex2f(w / 2f, h / 2f - cursorHeight / 2f);
            gl.glVertex2f(w / 2f, h / 2f + cursorHeight / 2f);
        gl.glEnd();

        Point3D cameraPosition = this.scene.getCamera().getPosition();
        OpenGLUtils.drawString(gl, new Point3D(5, 5, 0), "Camera position: " + String.format("(%.4f, %.4f, %.4f)", cameraPosition.x(), cameraPosition.y(), cameraPosition.z()));
        OpenGLUtils.drawString(gl, new Point3D(5, 25, 0), "Current date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));

        gl.glPopMatrix();

        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        gl.glPopMatrix();
        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);

        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthMask(true);
    }

    @Override
    public void endRendering() {


        gl.glDisable(GL2.GL_LIGHTING);
        OpenGLUtils.drawAxisGrid(gl, 50);
        OpenGLUtils.drawBaseAxis(gl, Point3D.zero(), 1.0f);

        drawCursor(gl);

        List<Point3D> controlPoints = new ArrayList<Point3D>();
        controlPoints.add(new Point3D(-2.5f, -2.5f, -10));
        controlPoints.add(new Point3D(2.5f, 2.5f, -10));
        controlPoints.add(new Point3D(4f, -8f, -15));
        drawBezierCurve(new Point3D(-5, 0, -10), new Point3D(5, 0, -10), controlPoints, 100);


        //OpenGLUtils.drawBoundingBox(gl, this.scene.getCamera().getCameraGeometry().getBoundingBox(), false);


        BoundingBox boundingBox = new BoundingBox(new Point3D(-1.1010036f, -1332.267f, -10.783917f), new Point3D(57.401665f, 36.188408f, 23.829382f));
        List<Point3D> points = new ArrayList<Point3D>();
        points.add(new Point3D(57.817886f, -0.7727107f, 19.195755f));
        points.add(new Point3D(57.81893f, -0.7727107f, 19.195305f));
        points.add(new Point3D(57.81784f, -0.7735305f, 19.195644f));
        points.add(new Point3D(57.818886f, -0.7735305f, 19.195194f));
        points.add(new Point3D(-833.4447f, 552.6644f, -610.42487f));
        points.add(new Point3D(215.01859f, 552.6644f, -1060.647f));
        points.add(new Point3D(-880.35486f, -267.18738f, -719.6679f));
        points.add(new Point3D(168.10837f, -267.18738f, -1169.89f));

        //OpenGLUtils.drawBoundingBox(gl, boundingBox, true);
        //OpenGLUtils.drawViewFustrumPoints(gl, points);

        gl.glEnable(GL2.GL_LIGHTING);

        gl.glFlush();
    }

    public void drawBezierCurve(Point3D startPoint, Point3D endPoint, List<Point3D> controlPoints, int resolution) {

        gl.glBegin(GL2.GL_LINE_STRIP);
        for (int i = 0; i <= resolution; i++) {
            float ratio = (i + 0f) / resolution;
            List<Point3D> pointsToProcess = new ArrayList<Point3D>();
            pointsToProcess.add(startPoint);
            pointsToProcess.addAll(controlPoints);
            pointsToProcess.add(endPoint);

            while (pointsToProcess.size() > 1) {
                List<Point3D> generatedPoints = new ArrayList<Point3D>();
                for (int pointIndex = 0; pointIndex < pointsToProcess.size(); pointIndex++) {
                    Point3D point = pointsToProcess.get(pointIndex);
                    if (pointIndex < pointsToProcess.size() - 1) {
                        Point3D next = pointsToProcess.get(pointIndex + 1);
                        Vector3D diff = next.sub(point);
                        Point3D newPoint = Point3D.add(point, diff.multiplied(ratio));
                        generatedPoints.add(newPoint);
                    }
                }
                pointsToProcess.clear();
                pointsToProcess.addAll(generatedPoints);
            }

            if (pointsToProcess.size() == 1) {
                Point3D pointToDraw = pointsToProcess.get(0);
                gl.glVertex3f(pointToDraw.x(), pointToDraw.y(), pointToDraw.z());
            }
        }
        gl.glEnd();
    }

    @Override
    public void enableLight(Light light) {

        // At this stage, current matrix is in object space (not world space).
        if (this.nbLights >= 0 && this.nbLights < MAX_LIGHTS) {

            nbLights++;

            int lightId = GL2.GL_LIGHT0 + (this.nbLights - 1);

            Matrix4x4 world = light.getWorldTransformation().getMatrix();

            gl.glMatrixMode(GL2.GL_MODELVIEW);
            gl.glPushMatrix();
            gl.glMultMatrixf(world.toColumnMajorArray(), 0);

            Vector3D translation = Vector3D.zero();

            float[] position = {translation.x(), translation.y(), translation.z(), 1};
            float[] ambient = {light.getAmbient().getRed() / 255f, light.getAmbient().getGreen() / 255f, light.getAmbient().getBlue() / 255f, 1};
            float[] diffuse = {light.getDiffuse().getRed() / 255f, light.getDiffuse().getGreen() / 255f, light.getDiffuse().getBlue() / 255f, 1};
            float[] specular = {light.getSpecular().getRed() / 255f, light.getSpecular().getGreen() / 255f, light.getSpecular().getBlue() / 255f, 1};

            gl.glEnable(lightId);
            gl.glLightfv(lightId, GL2.GL_SPECULAR, specular, 0);
            gl.glLightfv(lightId, GL2.GL_DIFFUSE, diffuse, 0);
            gl.glLightfv(lightId, GL2.GL_AMBIENT, ambient, 0);

            float[] spotDirection = {light.getDirection().x(), light.getDirection().y(), light.getDirection().z()};
            if (light.getType() == LightType.SPOT) {
                gl.glLightfv(lightId, GL2.GL_SPOT_DIRECTION, spotDirection, 0);
                gl.glLightf(lightId, GL2.GL_SPOT_CUTOFF, light.getSpotCutoff());
                gl.glLightf(lightId, GL2.GL_SPOT_EXPONENT, light.getSpotExponent());
            }
            gl.glLightfv(lightId, GL2.GL_POSITION, position, 0);

            gl.glPopMatrix();
        }
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

        log.info("(" + result[0] + ", " + result[1] + ", " + result[2] + ")");
        log.info(vp.unproject(new Point3D(50, 50, 0, 1), modelView, projection));
    }
}
