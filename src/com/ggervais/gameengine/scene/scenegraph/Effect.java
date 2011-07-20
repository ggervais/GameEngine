package com.ggervais.gameengine.scene.scenegraph;

import com.ggervais.gameengine.material.texture.Texture;
import com.ggervais.gameengine.math.Vector3D;

import java.awt.Color;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class Effect {
    private Color color;
    private List<Texture> textures;
    private List<Vector3D> textureMinBounds;
    private List<Vector3D> textureMaxBounds;
    private static final Random random = new Random();

    public Effect() {
        this.color = Color.WHITE;
        this.textures = new ArrayList<Texture>();
        this.textureMinBounds = new ArrayList<Vector3D>();
        this.textureMaxBounds = new ArrayList<Vector3D>();
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return this.color;
    }

    public int nbTextures() {
        return this.textures.size();
    }

    public Texture getTexture(int index) {
        return this.textures.get(index);
    }

    public void addTexture(Texture texture) {
        this.textures.add(texture);

        int nbCells = texture.getNbCellsWidth() * texture.getNbCellsHeight();
        int index = this.random.nextInt(nbCells);
        this.textureMinBounds.add(texture.getMinBounds(index));
        this.textureMaxBounds.add(texture.getMaxBounds(index));
    }

    public Vector3D getMinBoundsForTexture(int index) {
        return this.textureMinBounds.get(index);
    }

    public Vector3D getMaxBoundsForTexture(int index) {
        return this.textureMaxBounds.get(index);
    }

    public void removeTexture(Texture texture) {
        this.textures.remove(texture);
    }

    public void clearTextures() {
        this.textures.clear();
    }
}
