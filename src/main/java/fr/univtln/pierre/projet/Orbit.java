package fr.univtln.pierre.projet;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Torus;

public class Orbit {
    private final Torus torus ;
    private final Geometry geom;
    private final Material mat; 

    //Factory
    public static Orbit newInstance(int circlesamples, int radialsamples, float innerradius, int outradius, AssetManager assetManager, Node rootNode){
        return new Orbit(circlesamples, radialsamples, innerradius, outradius, assetManager, rootNode);
    }

    //Constructeur sans paramètres
    private Orbit(int circlesamples, int radialsamples, float innerradius, int outradius, AssetManager assetManager, Node rootNode){
        this.torus = new Torus(circlesamples, radialsamples, innerradius, outradius);
        this.geom = new Geometry("WhiteCircle", this.torus);
        this.mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        this.mat.setColor("Color", ColorRGBA.White);
        this.geom.setMaterial(this.mat);
        this.geom.setLocalScale(1, 1, 0.01f);
        this.geom.rotate(-FastMath.PI/2, 0, 0);
        rootNode.attachChild(this.geom);
    }
}
