package com.ggervais.gameengine.resource;

import com.ggervais.gameengine.Subsystem;

import java.util.ArrayList;
import java.util.List;

/**
 * This class abstracts the resource subsystem.
 * The subsystem is responsible for handling resources: textures, materials, sounds, etc.
 * In it simplest implementation, it is a "database" of resources.
 * Future functionalities will probably be logic the handle loading and unloading specific
 * resources, for example when we are loading a level or something like that.
 */
public class ResourceSubsystem implements Subsystem {

    private List<Resource> resources;
    private static ResourceSubsystem instance;
    private boolean initialized;

    private ResourceSubsystem() {
        this.resources = new ArrayList<Resource>();
        this.initialized = false;
    }

    public static ResourceSubsystem getInstance() {
        if (instance == null) {
            instance = new ResourceSubsystem();
        }
        return instance;
    }

    public void init() {
        this.initialized = true;
    }

    public void update(long currentTime) {
        // TODO: is this our responsability?
        for (Resource resource : this.resources) {
            if (!resource.isInitialized()) {
                resource.init();
            }
        }
    }

    public void addResource(Resource resource) {
        this.resources.add(resource);
    }

    public void destroy() {

    }

    public boolean isInitialized() {
        return this.initialized;
    }

    public Resource findResourceByTypeAndName(ResourceType type, String name) {

        for (Resource resource : this.resources) {
            if (resource.getType() == type && resource.getName().equals(name)) {
                return resource;
            }
        }

        return null;
    }

    public List<Resource> findResourcesByType(ResourceType type) {

        List<Resource> resourceList = new ArrayList<Resource>();
        for (Resource resource : this.resources) {
            if (resource.getType() == type) {
                resourceList.add(resource);
            }
        }

        return resourceList;
    }
}
