package com.ggervais.gameengine.scene.scenegraph;

import com.ggervais.gameengine.math.MathUtils;
import com.ggervais.gameengine.timing.Controller;
import org.apache.log4j.Logger;

import java.awt.*;

public class AlphaController extends Controller {


    private static Logger log = Logger.getLogger(AlphaController.class);
    private float startAlpha;
    private float endAlpha;
    private float step;
    private float currentAlpha;

    public AlphaController(Spatial controlledSpatialObject, long startTime, long duration, float startAlpha, float endAlpha) {
        super(controlledSpatialObject, startTime, duration);
        this.startAlpha = startAlpha;
        this.endAlpha = endAlpha;

        this.currentAlpha = startAlpha;

        this.step = (endAlpha - startAlpha) / duration;
    }

    @Override
    public void doUpdate(long currentTime) {

        float diff = (float) (currentTime - this.lastUpdateTime);
        this.currentAlpha += diff * this.step;

        Effect effect = this.controlledSpatialObject.getEffect();
        if (effect != null) {
            Color originalColor = effect.getColor();
            int r = originalColor.getRed();
            int g = originalColor.getGreen();
            int b = originalColor.getBlue();
            int a = MathUtils.clamp((int) Math.floor(this.currentAlpha * 255), 0, 255);

            Color newColor = new Color(r, g, b, a);
            effect.setColor(newColor);

            if ((this.currentAlpha <= this.endAlpha && this.endAlpha < this.startAlpha) ||
                (this.currentAlpha >= this.endAlpha && this.endAlpha > this.startAlpha)) {
                this.currentAlpha = startAlpha;
            }
        }
    }
}
