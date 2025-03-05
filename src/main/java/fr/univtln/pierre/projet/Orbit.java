package fr.univtln.pierre.projet;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Torus;

import lombok.Getter;

@Getter
public class Orbit {
    private final Torus torus ;
    private final Geometry geom;
    private final Material mat; 

    //Factory
    public static Orbit newInstance(int circlesamples, int radialsamples, float innerradius, float outradius, AssetManager assetManager, Node rootNode, ColorRGBA color){
        return new Orbit(circlesamples, radialsamples, innerradius, outradius, assetManager, rootNode, color);
    }

    //Constructeur sans paramètres
    private Orbit(int circlesamples, int radialsamples, float innerradius, float outradius, AssetManager assetManager, Node rootNode, ColorRGBA color){
        this.torus = new Torus(circlesamples, radialsamples, innerradius, outradius);
        this.geom = new Geometry("WhiteCircle", this.torus);
        this.mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        this.mat.setColor("Color", color);
        this.geom.setMaterial(this.mat);
        this.geom.setLocalScale(1, 1, 0.01f);
        this.geom.rotate(-FastMath.PI/2, 0, 0);
        rootNode.attachChild(this.geom);
    }
}
