package fr.univtln.pierre.projet;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;

import lombok.Getter;

@Getter
public class Planet{
    
    private final Sphere sphere ;
    private final Geometry geom;
    private final Material mat;  
    
    private final String name ;
    private final double size;
    private final double weight;

    /*@Setter
    private Vector3f velocity;*/

    //Factory
    public static Planet newInstance(int zsample, int radialsample, float radius, AssetManager assetManager, String name, double size, double weight,Node rootNode, Vector3f pos){
        return new Planet(zsample, radialsample, radius, assetManager,name, size, weight, rootNode, pos);
    }

    //Constructeur sans paramètres
    private Planet(int zsample,int radialsample,float radius,AssetManager assetManager,String name,double size,double weight, Node rootNode, Vector3f pos){
        this.sphere = new Sphere(zsample, radialsample, radius);
        this.geom = new Geometry("Sphere", this.sphere);  
        this.mat = new Material(assetManager,"Common/MatDefs/Light/Lighting.j3md");
        this.mat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/ColoredTex/" + name + ".jpg"));
        this.mat.setBoolean("UseMaterialColors",true);
        this.mat.setColor("Ambient", ColorRGBA.White);  
        this.mat.setColor("Diffuse", ColorRGBA.White);
        this.geom.setMaterial(this.mat);
        this.geom.rotate(-FastMath.PI/2, 0, 0);
        this.geom.setLocalTranslation(pos);
        rootNode.attachChild(this.geom);

        this.name = name;
        this.size = size;
        this.weight = weight;
    }

}

