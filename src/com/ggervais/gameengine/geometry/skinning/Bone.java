package com.ggervais.gameengine.geometry.skinning;

import com.ggervais.gameengine.math.Matrix4x4;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.scene.scenegraph.Transformation;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Transform;

import java.util.ArrayList;
import java.util.List;

public class Bone {

    private static final Logger log = Logger.getLogger(Bone.class);

    private static final int MAX_CHILDREN = 8;
    private Point3D startPosition;
    private Point3D endPosition;
    private Bone parent;
    private List<Bone> children;
    private boolean isPositioningAbsolute;
    private String name;
    private Transformation combinedTransformation;

    public Bone() {
        this(null, null, null);
    }

    public Bone(Point3D startPosition, Point3D endPosition, boolean isPositioningAbsolute, String name) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.parent = parent;
        this.children = new ArrayList<Bone>();
        this.isPositioningAbsolute = isPositioningAbsolute;
        this.name = name;
        combinedTransformation = new Transformation();
    }

    public Bone(Point3D startPosition, Point3D endPosition, String name) {
        this(startPosition, endPosition, false, name);
    }

    public void addChild(Bone bone) {
        if (this.children.size() < MAX_CHILDREN && bone != null) {
            this.children.add(bone);
            bone.parent = this;
        }
    }


    public Point3D getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(Point3D startPosition) {
        this.startPosition = startPosition;
    }

    public Point3D getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(Point3D endPosition) {
        this.endPosition = endPosition;
    }

    public Bone getParent() {
        return parent;
    }

    public void setParent(Bone parent) {
        this.parent = parent;
    }

    public boolean isPositioningAbsolute() {
        return isPositioningAbsolute;
    }

    public void setPositioningAbsolute(boolean positioningAbsolute) {
        isPositioningAbsolute = positioningAbsolute;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void dumpTree(int level) {
        StringBuilder logString = new StringBuilder();
        for (int i = 0; i <= level; i++) {
            logString.append("#");
        }
        logString.append(" ").append(this.startPosition).append(" ").append(this.endPosition).append(" ").append(this.isPositioningAbsolute).append(" ").append(this.name);
        log.info(logString);
        for (Bone bone : this.children) {
            bone.dumpTree(level + 1);
        }
    }

    public void propagateTransformation() {
        Transformation boneTransformation = new Transformation();
        boneTransformation.setTranslation(this.startPosition.x(), this.startPosition.y(),  this.startPosition.z());

        if (this.isPositioningAbsolute) {
            this.combinedTransformation = boneTransformation;
        } else {
            if (this.parent != null) {
                this.combinedTransformation.setTranslation(this.parent.getEndPosition().x(), this.parent.getEndPosition().y(), this.parent.getEndPosition().z());
            }
        }

        for (Bone child : this.children) {
            child.propagateTransformation();
        }

        log.info(this.combinedTransformation.getMatrix());
    }

    public Transformation getTransformation() {
        return this.combinedTransformation;
    }

    public List<Bone> getChildren() {
        return this.children;
    }

    public static void main(String[] args) {
        Bone root = new Bone(new Point3D(100, 100, 0), new Point3D(100, 100, 0), false, "NullBone");
        Bone head = new Bone(new Point3D(100, 100, 0), new Point3D(100, 90, 0), false, "Head");
        Bone back = new Bone(Point3D.zero(), new Point3D(100, 60, 0), false, "Back");
        root.addChild(head);
        head.addChild(back);
        root.dumpTree(0);
        root.propagateTransformation();
        int a = 0;
    }
}
