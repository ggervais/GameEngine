package com.ggervais.gameengine.geometry.skinning;

import java.util.ArrayList;
import java.util.List;

public class AnimationSet {

    private String name;
    private List<Animation> animations;

    public AnimationSet() {
        this.animations = new ArrayList<Animation>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addAnimation(Animation animation) {
        this.animations.add(animation);
    }

    public int getNbAnimations() {
        return this.animations.size();
    }

    public Animation getAnimation(int i) {
        Animation animation = null;
        if (i >= 0 && i < this.animations.size()) {
            animation = this.animations.get(i);
        }
        return animation;
    }
}
