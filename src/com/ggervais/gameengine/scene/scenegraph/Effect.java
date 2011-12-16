package com.ggervais.gameengine.scene.scenegraph;

import com.ggervais.gameengine.geometry.primitives.TextureCoords;
import com.ggervais.gameengine.material.texture.Texture;
import com.ggervais.gameengine.math.Vector3D;

import java.awt.Color;
import java.util.*;

public class Effect {
    private Color color;
    private List<Texture> textures;
    private List<Vector3D> textureMinBounds;
    private List<Vector3D> textureMaxBounds;
    private static final Random random = new Random();
    private List<Color> colors;
    private List<Map<Integer, List<TextureCoords>>> textureCoordinates;
    private Map<Integer, Map<Integer, TextureCoords>> textureCoordinatesPerVertex;
    private int id;

    public Effect() {
        this.color = Color.WHITE;
        this.textures = new ArrayList<Texture>();
        this.textureMinBounds = new ArrayList<Vector3D>();
        this.textureMaxBounds = new ArrayList<Vector3D>();
        this.colors = new ArrayList<Color>();
        this.textureCoordinates = new ArrayList<Map<Integer, List<TextureCoords>>>();
        this.textureCoordinatesPerVertex = new HashMap<Integer,  Map<Integer, TextureCoords>>();
        this.id = -1;
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

    public int nbColors() {
        return this.colors.size();
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

        this.textureCoordinates.add(new HashMap<Integer, List<TextureCoords>>());
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

    public void removeTextureCoordinates(int textureId, int nbVerticesPerFace, TextureCoords coords) {
        if (textureCoordinates.get(textureId).containsKey(nbVerticesPerFace)) {
            textureCoordinates.get(textureId).get(nbVerticesPerFace).remove(coords);
        }
    }

    public void removeTextureCoordinates(int textureId, int nbVerticesPerFace, int index) {
        if (textureCoordinates.get(textureId).containsKey(nbVerticesPerFace)) {
            textureCoordinates.get(textureId).get(nbVerticesPerFace).remove(index);
        }
    }

    public int getNbTextureCoords(int textureId) {
        int sum = 0;
        for (int nbVerticesPerFace : textureCoordinates.get(textureId).keySet()) {
            sum += this.textureCoordinates.get(textureId).get(nbVerticesPerFace).size();
        }
        return sum;
    }

    public int getNbTextureCoords(int textureId, int nbVerticesPerFace) {
        if (textureCoordinates.get(textureId).containsKey(nbVerticesPerFace)) {
            return this.textureCoordinates.get(textureId).get(nbVerticesPerFace).size();
        }
        return 0;
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

    public void addColor(int index, Color color) {
        this.colors.add(index, color);
    }

    public void removeColor(Color color) {
        this.colors.remove(color);
    }

    public void removeColor(int index) {
        this.colors.remove(index);
    }

    public void initializeColors(int size) {
        for (int i = 0; i < size; i++) {
            this.colors.add(Color.WHITE);
        }
    }

    public TextureCoords getTextureCoords(int textureIndex, int nbVerticesPerFace,  int index) {
        if (this.textureCoordinates.get(textureIndex).containsKey(nbVerticesPerFace)) {

            List<TextureCoords> array = this.textureCoordinates.get(textureIndex).get(nbVerticesPerFace);
            if (array != null) {
                if (index >= 0 && index < array.size()) {
                    return array.get(index);
                }
        }
        }
        return null;
    }

    public void clearTextureCoordinates(int textureIndex, int nbVerticesPerFace) {
        if (textureIndex >= 0 && textureIndex <= this.textureCoordinates.size() - 1) {
            if (this.textureCoordinates.get(textureIndex).containsKey(nbVerticesPerFace)) {
                List<TextureCoords> array = this.textureCoordinates.get(textureIndex).get(nbVerticesPerFace);
                if (array != null) {
                    array.clear();
                }
            }
        }
    }

    public void addTextureCoordinates(int i, int nbVerticesPerFace, TextureCoords coords) {
        if (i > this.textureCoordinates.size() - 1) {
            this.textureCoordinates.add(new HashMap<Integer, List<TextureCoords>>());
        }

        if (!this.textureCoordinates.get(i).containsKey(nbVerticesPerFace)) {
            this.textureCoordinates.get(i).put(nbVerticesPerFace, new ArrayList<TextureCoords>());
        }

        List<TextureCoords> array = this.textureCoordinates.get(i).get(nbVerticesPerFace);
        if (array != null) {
            array.add(coords);
        }
    }

    public void addTextureCoordinates(int textureIndex, int nbVerticesPerFace, int i, TextureCoords coords) {
        if (this.textureCoordinates.get(textureIndex).containsKey(nbVerticesPerFace)) {
            List<TextureCoords> array = this.textureCoordinates.get(textureIndex).get(nbVerticesPerFace);
            if (array != null) {
                array.add(i, coords);
            }
        }
    }
    
    public int getNbTextureCoordinatesForVertex(int textureIndex) {
        int nb = 0;
        
        if (this.textureCoordinatesPerVertex.containsKey(textureIndex)) {
            nb = this.textureCoordinatesPerVertex.get(textureIndex).keySet().size();
        }
        
        return nb;
    }
    
    public void addTextureCoordinatesForVertex(int textureIndex, int vertexIndex, TextureCoords coords) {
        if (!this.textureCoordinatesPerVertex.containsKey(textureIndex)) {
            this.textureCoordinatesPerVertex.put(textureIndex, new HashMap<Integer, TextureCoords>());
        }
        this.textureCoordinatesPerVertex.get(textureIndex).put(vertexIndex, coords);
    }

    public TextureCoords getTextureCoordsForVertex(int textureIndex, int vertexIndex) {
        if (!this.textureCoordinatesPerVertex.containsKey(textureIndex)) {
            this.textureCoordinatesPerVertex.put(textureIndex, new HashMap<Integer, TextureCoords>());
        }
        return this.textureCoordinatesPerVertex.get(textureIndex).get(vertexIndex);
    }

    public boolean hasTexturesCoordsPerVertex() {
        
        boolean mapIsInitialized = this.textureCoordinatesPerVertex.keySet().size() > 0;
        boolean hasCoords = false;
        
        if (mapIsInitialized) {
            for (int textureIndex : this.textureCoordinatesPerVertex.keySet()) {
                if (this.textureCoordinatesPerVertex.get(textureIndex).keySet().size() > 0) {
                    hasCoords = true;
                    break;
                }
            }
        }
        
        return hasCoords;
    }
    
    public void removeTextureCoordinateForVertex(int textureIndex, int vertexIndex) {
        if (!this.textureCoordinatesPerVertex.containsKey(textureIndex)) {
            return;
        }
        
        if (!this.textureCoordinatesPerVertex.get(textureIndex).containsKey(vertexIndex)) {
            return;
        }
        
        List<Integer> sortedIndices = new ArrayList<Integer>(this.textureCoordinatesPerVertex.get(textureIndex).keySet());
        Collections.sort(sortedIndices);
        for (int index : sortedIndices) {
            if (index >= vertexIndex && this.textureCoordinatesPerVertex.get(textureIndex).containsKey(index + 1)) {
                this.textureCoordinatesPerVertex.get(textureIndex).put(index, this.textureCoordinatesPerVertex.get(textureIndex).get(index + 1));
            } else if (!this.textureCoordinatesPerVertex.get(textureIndex).containsKey(index + 1)) {
                this.textureCoordinatesPerVertex.get(textureIndex).remove(index);
            }
        }
    }

    public void setTextureCoordinatesPerVertex(int textureIndex, int vertexIndex, TextureCoords coords) {
        if (!this.textureCoordinatesPerVertex.containsKey(textureIndex)) {
            return;
        }

        Map<Integer, TextureCoords> map = this.textureCoordinatesPerVertex.get(textureIndex);
        List<Integer> sortedIndices = new ArrayList<Integer>(this.textureCoordinatesPerVertex.get(textureIndex).keySet());
        Collections.sort(sortedIndices, new Comparator<Integer>() {
            public int compare(Integer a, Integer b) {
                return b.compareTo(a);
            }
        });
        
        for (int index : sortedIndices) {
            if (index >= vertexIndex) {
                map.put(index + 1, map.get(index));
                map.remove(index);
            }
        }
        map.put(vertexIndex, coords);
    }
    
    public void removeTopTextureCoordinateForVertex(int textureIndex) {
        if (!this.textureCoordinatesPerVertex.containsKey(textureIndex)) {
            return;
        }
        Map<Integer, TextureCoords> map = this.textureCoordinatesPerVertex.get(textureIndex);
        List<Integer> sortedIndices = new ArrayList<Integer>(this.textureCoordinatesPerVertex.get(textureIndex).keySet());
        Collections.sort(sortedIndices, new Comparator<Integer>() {
            public int compare(Integer a, Integer b) {
                return b.compareTo(a);
            }
        });
        if (sortedIndices.size() > 0) {
            map.remove(sortedIndices.get(0));
        }
    }
    
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float[] getEffectBufferAsFloatArray(int nbVertices) {

        int size = nbVertices * 6;

        Color baseColor = Color.WHITE;
        if (this.color != null) {
            baseColor = this.color;
        }

        TextureCoords baseCoords = new TextureCoords(0, 0);
        
        float[] buffer = new float[size];

        int bufferIndex = 0;
        for (int i = 0; i < nbVertices; i++) {
            Color color = baseColor;
            if (i < this.colors.size()) {
                color = this.colors.get(i);
            }
            float r = color.getRed() / 255f;
            float g = color.getGreen() / 255f;
            float b = color.getBlue() / 255f;
            float a = color.getAlpha() / 255f;
            
            TextureCoords coords = baseCoords;
            if (this.textureCoordinatesPerVertex.containsKey(0)) {
                Map<Integer, TextureCoords> coordsMap = this.textureCoordinatesPerVertex.get(0);
                if (coordsMap.containsKey(i)) {
                    coords = coordsMap.get(i);
                }
            }
            float tu = coords.getTextureU();
            float tv = coords.getTextureV();
            
            buffer[bufferIndex + 0] = r;
            buffer[bufferIndex + 1] = g;
            buffer[bufferIndex + 2] = b;
            buffer[bufferIndex + 3] = a;

            buffer[bufferIndex + 4] = tu;
            buffer[bufferIndex + 5] = tv;

            bufferIndex += 6;
        }

        return buffer;
    }
    
    public int getNbTextures() {
        return this.textures.size();
    }
}
