package com.ggervais.gameengine.material.texture;

import com.ggervais.gameengine.math.Vector3D;

/**
 * Created by IntelliJ IDEA.
 * User: ggervais
 * Date: 10/06/11
 * Time: 6:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class GridTexture extends Texture {

    private int nbCellsWidth;
    private int nbCellsHeight;

    public GridTexture(Texture texture, int nbCellsWidth, int nbCellsHeight) {
        super(texture.getFilename(), texture.getPixels(), texture.getWidth(), texture.getHeight());
        this.nbCellsWidth = nbCellsWidth;
        this.nbCellsHeight = nbCellsHeight;
    }

    public GridTexture(String filename, int[] pixels, int width, int height, int nbCellsWidth, int nbCellsHeight) {
		super(filename, pixels, width, height);
        this.nbCellsWidth = nbCellsWidth;
        this.nbCellsHeight = nbCellsHeight;
	}

	public GridTexture(String filename, int[] pixels, int width, int height, boolean keepPixels, int nbCellsWidth, int nbCellsHeight) {
		super(filename, pixels, width, height, keepPixels);
        this.nbCellsWidth = nbCellsWidth;
        this.nbCellsHeight = nbCellsHeight;
	}


    @Override
    public Vector3D getMinBounds(int index) {
        int x = index % this.nbCellsWidth;
        int y = index / this.nbCellsWidth;

        return getMinBounds(x, y);
    }

    @Override
    public Vector3D getMaxBounds(int index) {
        int x = index % this.nbCellsWidth;
        int y = index / this.nbCellsWidth;

        return getMaxBounds(x, y);
    }

    @Override
    public int getNbCellsWidth() {
        return this.nbCellsWidth;
    }

    @Override
    public int getNbCellsHeight() {
        return this.nbCellsHeight;
    }

    public Vector3D getMinBounds(int x, int y) {
        float cellWidth = 1.0f / this.nbCellsWidth;
        float cellHeight = 1.0f / this.nbCellsHeight;

        return new Vector3D(x * cellWidth, y * cellHeight, 0);
    }

    public Vector3D getMaxBounds(int x, int y) {
        float cellWidth = 1.0f / this.nbCellsWidth;
        float cellHeight = 1.0f / this.nbCellsHeight;

        return new Vector3D((x + 1) * cellWidth, (y + 1) * cellHeight, 0);
    }
}
