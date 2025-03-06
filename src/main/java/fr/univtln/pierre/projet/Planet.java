package fr.univtln.pierre.projet;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Planet{
    
    private final Sphere sphere ;
    private final Geometry geom;
    private final Material mat;
    private final Node pivot;

    private final float rotate;
    private final float rotation;
    private final float maxradius;
    private final float minradius;
    
    private final String name ;
    private final double size;
    private final double weight;

    @Setter
    private float angle = 0.0f;

    //Factory
    public static Planet newInstance(int zsample, int radialsample, float radius, AssetManager assetManager, float rotate, float rotation, float maxradius, float minradius, String name, double size, double weight,Node rootNode, Node pivot, int angle){
        return new Planet(zsample, radialsample, radius, assetManager, rotate, rotation, maxradius, minradius, name, size, weight, rootNode, pivot, angle);
    }

    //Constructeur sans paramètres
    private Planet(int zsample,int radialsample,float radius,AssetManager assetManager, float rotate, float rotation, float maxradius, float minradius,String name,double size,double weight, Node rootNode, Node pivot, int angle){
        this.sphere = new Sphere(zsample, radialsample, radius);
        this.geom = new Geometry("Sphere", this.sphere);  
        this.mat = new Material(assetManager,"Common/MatDefs/Light/Lighting.j3md");
        this.mat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/ColoredTex/" + name + ".jpg"));
        this.mat.setBoolean("UseMaterialColors",true);
        this.mat.setColor("Ambient", ColorRGBA.White);  
        this.mat.setColor("Diffuse", ColorRGBA.White);
        this.geom.setMaterial(this.mat);
        this.geom.rotate(-FastMath.PI/2, 0, -FastMath.DEG_TO_RAD * angle);
        rootNode.attachChild(this.geom);

        pivot.attachChild(this.geom);
        this.pivot = pivot;

        this.rotate = rotate;
        this.rotation = rotation;
        this.maxradius = maxradius;
        this.minradius = minradius;

        this.name = name;
        this.size = size;
        this.weight = weight;
    }

}

