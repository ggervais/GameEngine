package com.ggervais.gameengine.material;

import com.ggervais.gameengine.material.texture.Texture;
import com.ggervais.gameengine.resource.Resource;
import com.ggervais.gameengine.resource.ResourceType;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/** The scope, properties and behavior of a material are modelled after
  * the "matlib" element from an OBJ mesh file.
 **/
public class Material implements Resource {

    private static final Logger log = Logger.getLogger(Material.class);

    private String name;

    // We assume a material can have more than one textures.
    // Is that true?
    // TODO: confirm assumption or not.
    private List<Texture> textures;

    public Material() {
        this.name = "";
        this.textures = new ArrayList<Texture>();
    }

    public void addTexture(Texture texture) {
        this.textures.add(texture);
    }

    public int nbTextures() {
        return this.textures.size();
    }

    public Texture getTexture(int index) {
        return this.textures.get(index);
    }

    public String getName() {
        return this.name;
    }

    public void init() {

    }

    public void destroy() {

    }

    public boolean isInitialized() {
        return true;
    }

    public ResourceType getType() {
        return ResourceType.MATERIAL;
    }

    public void setName(String name) {
        this.name = name;
    }
}
