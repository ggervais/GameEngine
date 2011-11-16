package com.ggervais.gameengine.geometry.skinning;

import java.util.ArrayList;
import java.util.List;

public class Animation {

    private String boneName;
    private String name;
    private List<AnimationKey> animationKeys;

    public Animation() {
        this.animationKeys = new ArrayList<AnimationKey>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBoneName() {
        return boneName;
    }

    public void setBoneName(String boneName) {
        this.boneName = boneName;
    }

    public void addAnimationKey(AnimationKey animationKey) {
        this.animationKeys.add(animationKey);
    }

    public int getNbAnimationKeys() {
        return this.animationKeys.size();
    }

    public AnimationKey getAnimationKey(int i) {
        AnimationKey animationKey = null;

        if (i >= 0 && i < this.animationKeys.size()) {
            animationKey = this.animationKeys.get(i);
        }

        return animationKey;
    }
}
