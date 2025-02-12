package fr.univtln.pierre.projet;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;

import lombok.Getter;

@Getter
public class Planet{
    
    private final Sphere sphere ;
    private final Geometry geom;
    private final Material mat;    

    //Factory
    public static Planet newInstance(int zsample,int radialsample,float radius,AssetManager assetManager,String name){
        return new Planet(zsample, radialsample, radius, assetManager, name);
    }

    //Constructeur sans paramètres
    private Planet(int zsample,int radialsample,float radius,AssetManager assetManager,String name){
        this.sphere = new Sphere(zsample, radialsample, radius); // create cube shape
        this.geom = new Geometry("Sphere", this.sphere);  // create cube geometry from the shape
        this.mat = new Material(assetManager,"Common/MatDefs/Light/Lighting.j3md");
        this.mat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/ColoredTex/" + name + ".jpg"));
        this.mat.setBoolean("UseMaterialColors",true);
        this.mat.setColor("Ambient", ColorRGBA.White);  
        this.mat.setColor("Diffuse", ColorRGBA.White);
        this.geom.setMaterial(this.mat);
    }

}

