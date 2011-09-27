package com.ggervais.gameengine.geometry;

import com.ggervais.gameengine.geometry.primitives.Face;
import com.ggervais.gameengine.geometry.primitives.TextureCoords;
import com.ggervais.gameengine.geometry.primitives.Vertex;
import com.ggervais.gameengine.material.texture.Texture;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.scene.scenegraph.Effect;
import com.ggervais.gameengine.scene.scenegraph.Geometry;

import java.awt.*;

public class GridGeometry extends Geometry {

	private int nbCellsWidth;
	private int nbCellsDepth;

	public GridGeometry() {
		this(10, 10);
	}

    @Override
    protected void generateTextureCoords(Effect effect) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public GridGeometry(int nbCellsWidth, int nbCellsDepth) {
		super(3);
		this.nbCellsWidth = nbCellsWidth;
		this.nbCellsDepth = nbCellsDepth;
		create();
	}
	
	public int getNbCellsWidth() {
		return this.nbCellsWidth;
	}
	
	public int getNbCellsDepth() {
		return this.nbCellsDepth;
	}
	
	public void create() {
		int nbVerticesWidth = this.nbCellsWidth + 1;
		int nbVerticesDepth = this.nbCellsDepth + 1;
		
		float stepWidth = 1.0f / this.nbCellsWidth;
		float stepDepth = 1.0f / this.nbCellsDepth;
		
		float widthOffset = nbCellsWidth / 2.0f;
		float depthOffset = nbCellsDepth / 2.0f;
		
		for (int j = 0; j < nbVerticesDepth; j++) {
			for (int i = 0; i < nbVerticesWidth; i++) {
				float x = i - widthOffset;
				float z = j - depthOffset;
				x *= stepWidth;
				z *= stepDepth;
				
				float tu = i * 1.0f / (nbVerticesWidth - 1); // 0..N-1
				float tv = j * 1.0f / (nbVerticesDepth - 1); // 0..N-1
				
				Vertex vertex = new Vertex(new Point3D(x, 0, z), Color.WHITE, tu, tv);
				this.vertexBuffer.addVertex(vertex);
			}
		}
		
		int index = 0;
		for (int j = 0; j < this.nbCellsDepth; j++) {
			for (int i = 0; i < this.nbCellsWidth; i++) {
				int first = index;
	            int second = index + nbVerticesWidth;
	            int third = index + 1;
	            int fourth = index + 1 + nbVerticesWidth;
	            
	            Face face1 = new Face();
	            Face face2 = new Face();
	            
	            Vertex firstVertex = this.vertexBuffer.getVertex(first);
	            Vertex secondVertex = this.vertexBuffer.getVertex(second);
	            Vertex thirdVertex = this.vertexBuffer.getVertex(third);
	            Vertex fourthVertex = this.vertexBuffer.getVertex(fourth);
	            
	            face1.addVertex(firstVertex);
	            face1.addVertex(secondVertex);
	            face1.addVertex(fourthVertex);
	            face1.addTextureCoords(new TextureCoords(firstVertex.getTextureU(), firstVertex.getTextureV()));
	            face1.addTextureCoords(new TextureCoords(secondVertex.getTextureU(), secondVertex.getTextureV()));
	            face1.addTextureCoords(new TextureCoords(fourthVertex.getTextureU(), fourthVertex.getTextureV()));
	            
	            face2.addVertex(firstVertex);
	            face2.addVertex(fourthVertex);
	            face2.addVertex(thirdVertex);
	            face2.addTextureCoords(new TextureCoords(firstVertex.getTextureU(), firstVertex.getTextureV()));
	            face2.addTextureCoords(new TextureCoords(fourthVertex.getTextureU(), fourthVertex.getTextureV()));
	            face2.addTextureCoords(new TextureCoords(thirdVertex.getTextureU(), thirdVertex.getTextureV()));
	            
	            this.indexBuffer.addIndex(first);
	            this.indexBuffer.addIndex(second);
	            this.indexBuffer.addIndex(fourth);
	            
	            this.indexBuffer.addIndex(first);
	            this.indexBuffer.addIndex(fourth);
	            this.indexBuffer.addIndex(third);
	            
	            index++;
			}
			index++;
		}
	}

	public void elevateWithHeighMap(Texture texture) {
		
		if (!texture.keepPixels()) {
			return;
		}
		
		int nbVerticesWidth = this.nbCellsWidth + 1;
		int nbVerticesDepth = this.nbCellsDepth + 1;
		
		int width = texture.getWidth();
		int height = texture.getHeight();
		
		for (int j = 0; j < nbVerticesDepth; j++) {
			for (int i = 0; i < nbVerticesWidth; i++) {
				int px = (int) ((i * 1.0f / (nbVerticesWidth - 1)) * width);
				int py = (int) ((j * 1.0f / (nbVerticesDepth - 1)) * height);
				if (px < 0) { px = 0; }
				if (px >= width) { px = width - 1; }
				if (py < 0) { px = 0; }
				if (py >= width) { py = height - 1; }
				
				int pixel = texture.getPixel(px, py);
				
				int r = (pixel >> 16) & 0xFF;
				int g = (pixel >> 8) & 0xFF;
				int b = (pixel >> 0) & 0xFF;
				float avg = (r + g + b) / 3;
				
				float y = avg / 255 * 0.5f;
				
				int index = j * nbVerticesWidth + i;
				Vertex vertex = this.vertexBuffer.getVertex(index);
				vertex.getPosition().y(y);
			}
		}	
	}
}
