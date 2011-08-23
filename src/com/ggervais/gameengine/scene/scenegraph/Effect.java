package com.ggervais.gameengine.scene.scenegraph;

import com.ggervais.gameengine.geometry.primitives.TextureCoords;
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
    private List<Color> colors;
    private List<List<TextureCoords>> textureCoordinates;

    public Effect() {
        this.color = Color.WHITE;
        this.textures = new ArrayList<Texture>();
        this.textureMinBounds = new ArrayList<Vector3D>();
        this.textureMaxBounds = new ArrayList<Vector3D>();
        this.colors = new ArrayList<Color>();
        this.textureCoordinates = new ArrayList<List<TextureCoords>>();
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setColor(int i, Color color) {
        this.colors.set(i, color);
    }

    public Color getColor() {
        return this.color;
    }

    public Color getColor(int i) {
        return this.colors.get(i);
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

        this.textureCoordinates.add(new ArrayList<TextureCoords>());
    }

    public Vector3D getMinBoundsForTexture(int index) {
        return this.textureMinBounds.get(index);
    }

    public Vector3D getMaxBoundsForTexture(int index) {
        return this.textureMaxBounds.get(index);
    }

    public void removeTexture(Texture texture) {
        int index = this.textures.indexOf(texture);
        if (index > -1) {
            this.textures.remove(index);
            this.textureCoordinates.remove(index);
        }


    }

    public void clearTextures() {
        this.textures.clear();
    }

    public void resetColorList() {
        this.colors.clear();
    }

    public void addColor(Color color) {
        this.colors.add(color);
    }

    public void removeColor(int index) {
        this.colors.remove(index);
    }

    public void initializeColors(int size) {
        for (int i = 0; i < size; i++) {
            this.colors.add(Color.WHITE);
        }
    }

    public TextureCoords getTextureCoords(int textureIndex, int index) {
        List<TextureCoords> array = this.textureCoordinates.get(textureIndex);
        if (array != null) {
            if (index >= 0 && index < array.size()) {
                return array.get(index);
            }
        }
        return null;
    }

    public void clearTextureCoordinates(int i) {
        List<TextureCoords> array = this.textureCoordinates.get(i);
        if (array != null) {
            array.clear();
        }
    }

    public void addTextureCoordinates(int i, TextureCoords coords) {
        List<TextureCoords> array = this.textureCoordinates.get(i);
        if (array != null) {
            array.add(coords);
        }
    }
}
