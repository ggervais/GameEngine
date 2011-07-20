package com.ggervais.gameengine.render.opengl;

import java.awt.Color;
import java.nio.Buffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import com.ggervais.gameengine.geometry.Model;
import com.ggervais.gameengine.geometry.primitives.Face;
import com.ggervais.gameengine.geometry.primitives.TextureCoords;
import com.ggervais.gameengine.geometry.primitives.Vertex;
import com.ggervais.gameengine.math.*;
import com.ggervais.gameengine.material.texture.Texture;
import com.ggervais.gameengine.material.texture.TextureLoader;
import com.ggervais.gameengine.physics.boundingvolumes.BoundingBox;
import org.apache.log4j.Logger;

public class OpenGLUtils {

    private static final Logger log = Logger.getLogger(OpenGLUtils.class);

	public static void drawModel(GL2 gl, Model model, Texture texture, Matrix4x4 worldMatrix) {
		
		int nbVerticesPerFace = model.getNbVerticesPerFace();
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
		
		gl.glPushMatrix();

		//gl.glColor3f(1.0f, 1.0f, 1.0f);
		
		gl.glMultMatrixf(worldMatrix.toColumnMajorArray(), 0);
		
		if (texture != null) {
			gl.glBindTexture(GL.GL_TEXTURE_2D, texture.getId());
		}
		
		/*boolean useTextureBuffer = false;
		if (model.getIndexBuffer().size() == model.getTextureBuffer().size()) {
			useTextureBuffer = true;
		}*/
		
		gl.glBegin(glPrimitiveType);
			for(int i = 0; i < model.getFaces().size(); i++) {
				Face face = model.getFaces().get(i);
				
				//int index = model.getIndexBuffer().getIndex(i);
				
				for (int vi = 0; vi < face.nbVertices(); vi++) {
					
					//Vertex vertex = model.getVertexBuffer().getVertex(index);
					
					Vertex vertex = face.getVertex(vi);
					Point3D vertexPosition = vertex.getPosition();
					Color vertexColor = vertex.getColor();
					
					float r = vertexColor.getRed();
					float g = vertexColor.getGreen();
					float b = vertexColor.getBlue();
					
					gl.glColor4f(1, 1, 1, 1);
					if (texture != null) {
						/*
						float tu = vertex.getTextureU();
						float tv = vertex.getTextureV();
						if (useTextureBuffer) {
							TextureCoords coords = model.getTextureBuffer().getCoords(i);
							tu = coords.getTextureU();
							tv = coords.getTextureV();
						}*/
		
						if (face.nbTextureCoords() == face.nbVertices()) { 
							TextureCoords coords = face.getTextureCoords(vi);

                            float tu = coords.getTextureU(); // Range between 0 and 1. 1.5 => 0.5, 2 => 1.
                            float tv = coords.getTextureV(); // Range between 0 and 1. 1.5 => 0.5, 2 => 1.

                            Vector3D minBounds = texture.getMinBounds();
                            Vector3D maxBounds = texture.getMaxBounds();
                            float w = maxBounds.x() - minBounds.x();
                            float h = maxBounds.y() - minBounds.y();

                            tu = minBounds.x() + tu * w;
                            tv = minBounds.y() + tv * h;

							gl.glTexCoord2f(tu, tv);
						}
					} else {
						gl.glColor3f(r, g, b);
					}
					gl.glVertex3f(vertexPosition.x(), vertexPosition.y(), vertexPosition.z());
				}
			}
		gl.glEnd();
		
		gl.glBindTexture(GL.GL_TEXTURE_2D, -1);

		gl.glPopMatrix();
	}
	
	public static void drawBaseAxis(GL2 gl, Point3D position, float scale) {
		gl.glPushMatrix();
		
		gl.glTranslatef(position.x(), position.y(), position.z());
		gl.glScalef(scale, scale, scale);
		gl.glBegin(GL.GL_LINES);
        	// X axis;
	    	gl.glColor3f(1f, 0f, 0f);
	        gl.glVertex3f(0f, 0f, 0f);
	        gl.glVertex3f(1f, 0f, 0f);
	        
	        // Y axis;
	    	gl.glColor3f(0f, 1f, 0f);
	        gl.glVertex3f(0f, 0f, 0f);
	        gl.glVertex3f(0f, 1f, 0f);
	        
	        // Z axis;
	    	gl.glColor3f(0f, 0f, 1f);
	        gl.glVertex3f(0f, 0f, 0f);
	        gl.glVertex3f(0f, 0f, 1f);
        gl.glEnd();
        
        gl.glPopMatrix();
	}

    public static void drawCircle(GL2 gl, Point3D center, float radius, RotationMatrix rotationMatrix) {
        drawCircle(gl, center, radius, rotationMatrix, false);
    }

    public static void drawCircle(GL2 gl, Point3D center, float radius, RotationMatrix rotationMatrix, boolean showRadius) {

        Matrix4x4 world = new Matrix4x4();

		TranslationMatrix translationMatrix = TranslationMatrix.createFromXYZ(center.x(), center.y(), center.z());
		ScaleMatrix scaleMatrix = ScaleMatrix.createFromXYZ(radius, radius, radius);

		world.identity();

		world.mult(translationMatrix);
		world.mult(rotationMatrix);
		world.mult(scaleMatrix);

        int nbSteps = 36;

        // Ten degrees
        float stepInRadians = (float) Math.toRadians(360 / nbSteps);
        float upperBound = (float) Math.toRadians(360); // 2PI

        gl.glPushMatrix();


        gl.glMultMatrixf(world.toColumnMajorArray(), 0);
        gl.glBegin(GL.GL_LINE_STRIP);
        gl.glColor3f(0, 0, 0);
        for (int i = 0; i <= nbSteps; i++) {
            float angle = (i * stepInRadians) % (float) Math.toRadians(360);
            float x = (float) Math.cos(angle);
            float y = (float) Math.sin(angle);
            gl.glVertex3f(x, y, 0.0f);
        }
        if (showRadius) {
            gl.glVertex3f(0, 0, 0);
        }

        gl.glEnd();

        gl.glPopMatrix();
    }

    public static void drawBoundingBox(GL2 gl, BoundingBox box) {

        gl.glPushMatrix();

        Point3D firstPoint = box.getMinCorner();
        Point3D secondPoint = Point3D.add(firstPoint, new Vector3D(box.getWidth(), 0, 0));
        Point3D thirdPoint = Point3D.add(secondPoint, new Vector3D(0, 0, box.getDepth()));
        Point3D fourthPoint = Point3D.add(thirdPoint, new Vector3D(-box.getWidth(), 0, 0));

        Point3D fifthPoint = Point3D.add(firstPoint, new Vector3D(0, box.getHeight(), 0));
        Point3D sixthPoint = Point3D.add(fifthPoint, new Vector3D(box.getWidth(), 0, 0));
        Point3D seventhPoint = Point3D.add(fifthPoint, new Vector3D(0, 0, box.getDepth()));
        Point3D eighthPoint = box.getMaxCorner();


        gl.glBegin(GL.GL_LINES);
            gl.glColor3f(1, 0, 0);

            // First
            gl.glVertex3f(firstPoint.x(), firstPoint.y(), firstPoint.z());
            gl.glVertex3f(secondPoint.x(), secondPoint.y(), secondPoint.z());

            // Second
            gl.glVertex3f(secondPoint.x(), secondPoint.y(), secondPoint.z());
            gl.glVertex3f(thirdPoint.x(), thirdPoint.y(), thirdPoint.z());

            // Third
            gl.glVertex3f(thirdPoint.x(), thirdPoint.y(), thirdPoint.z());
            gl.glVertex3f(fourthPoint.x(), fourthPoint.y(), fourthPoint.z());

            // Fourth
            gl.glVertex3f(fourthPoint.x(), fourthPoint.y(), fourthPoint.z());
            gl.glVertex3f(firstPoint.x(), firstPoint.y(), firstPoint.z());


            // Fifth
            gl.glVertex3f(fifthPoint.x(), fifthPoint.y(), fifthPoint.z());
            gl.glVertex3f(sixthPoint.x(), sixthPoint.y(), sixthPoint.z());

            // Sixth
            gl.glVertex3f(sixthPoint.x(), sixthPoint.y(), sixthPoint.z());
            gl.glVertex3f(eighthPoint.x(), eighthPoint.y(), eighthPoint.z());

            // Seventh
            gl.glVertex3f(eighthPoint.x(), eighthPoint.y(), eighthPoint.z());
            gl.glVertex3f(seventhPoint.x(), seventhPoint.y(), seventhPoint.z());

            // Eighth
            gl.glVertex3f(seventhPoint.x(), seventhPoint.y(), seventhPoint.z());
            gl.glVertex3f(fifthPoint.x(), fifthPoint.y(), fifthPoint.z());


            // Ninth
            gl.glVertex3f(firstPoint.x(), firstPoint.y(), firstPoint.z());
            gl.glVertex3f(fifthPoint.x(), fifthPoint.y(), fifthPoint.z());

            // Tenth
            gl.glVertex3f(secondPoint.x(), secondPoint.y(), secondPoint.z());
            gl.glVertex3f(sixthPoint.x(), sixthPoint.y(), sixthPoint.z());

            // Eleventh
            gl.glVertex3f(thirdPoint.x(), thirdPoint.y(), thirdPoint.z());
            gl.glVertex3f(eighthPoint.x(), eighthPoint.y(), eighthPoint.z());

            // Twelfth
            gl.glVertex3f(fourthPoint.x(), fourthPoint.y(), fourthPoint.z());
            gl.glVertex3f(seventhPoint.x(), seventhPoint.y(), seventhPoint.z());

        gl.glEnd();

        gl.glPopMatrix();
    }

    public static void drawParticle(GL2 gl, Model particleModel, Texture particleTexture, int textureIndex, Vector3D color, Matrix4x4 particleWorldMatrix, float alpha) {
        // Life is between 0 and 1 (works like alpha!).

        int nbVerticesPerFace = particleModel.getNbVerticesPerFace();
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

		gl.glPushMatrix();

		gl.glMultMatrixf(particleWorldMatrix.toColumnMajorArray(), 0);

		if (particleTexture != null) {
			gl.glBindTexture(GL.GL_TEXTURE_2D, particleTexture.getId());
		}
		gl.glBegin(glPrimitiveType);
			for(int i = 0; i < particleModel.getFaces().size(); i++) {
				Face face = particleModel.getFaces().get(i);

				for (int vi = 0; vi < face.nbVertices(); vi++) {

					Vertex vertex = face.getVertex(vi);
					Point3D vertexPosition = vertex.getPosition();

                    gl.glColor4f(color.x(), color.y(), color.z(), alpha);
					if (particleTexture != null) {
						if (face.nbTextureCoords() == face.nbVertices()) {
							TextureCoords coords = face.getTextureCoords(vi);

                            float tu = coords.getTextureU(); // Range between 0 and 1. 1.5 => 0.5, 2 => 1.
                            float tv = coords.getTextureV(); // Range between 0 and 1. 1.5 => 0.5, 2 => 1.

                            Vector3D minBounds = particleTexture.getMinBounds(textureIndex);
                            Vector3D maxBounds = particleTexture.getMaxBounds(textureIndex);
                            float w = maxBounds.x() - minBounds.x();
                            float h = maxBounds.y() - minBounds.y();

                            tu = minBounds.x() + tu * w;
                            tv = minBounds.y() + tv * h;

							gl.glTexCoord2f(tu, tv);
						}
                    }
					gl.glVertex3f(vertexPosition.x(), vertexPosition.y(), vertexPosition.z());
				}
			}
		gl.glEnd();

        gl.glBindTexture(GL.GL_TEXTURE_2D, -1);

		gl.glPopMatrix();


    }

	
	public static int createTexture(GL2 gl, String filename) {
		Texture texture = TextureLoader.loadTexture(filename);
		int id = 0;
		if (texture != null) {
			id = uploadTexture(gl, texture);
		}
		return id;
	}
	
	public static int uploadTexture(GL2 gl, Texture texture) {
		int id = 0;
		
		Buffer buffer = texture.getBuffer();
		
		// Generate the texture.
		int[] tempId = new int[1];
		gl.glGenTextures(1, tempId, 0);
		id = tempId[0];
		
		// Create the texture data.
		gl.glBindTexture(GL.GL_TEXTURE_2D, id);
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, texture.getWidth(), texture.getHeight(), 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, buffer);
		
		// Set some texture parameters.
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
		
        gl.glBindTexture(GL.GL_TEXTURE_2D, -1);
        
        texture.setId(id);
        
		return id;
	}
}
