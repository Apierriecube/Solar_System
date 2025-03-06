package fr.univtln.pierre.projet;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Planet{
    /*Initialization of a planet:
     * 
     * - zsample: number of samples along the z-axis
     * - radialsample: number of samples along the radial
     * - radius: radius of the planet
     * - assetManager: the assetManager
     * - rotate: the speed of rotation of the planet
     * - rotation: the speed of rotation of the planet around the sun
     * - maxradius: the maximum distance from the sun
     * - minradius: the minimum distance from the sun
     * - degree: the degree of the planet
     * - name: the name of the planet
     * - size: the size of the planet
     * - weight: the weight of the planet
     * - rootNode: the rootNode
     * - pivot: the pivot
     * - angle: the angle of the planet
     */
    
    //planet parameters for the creation
    private final Spatial spatial;
    private final Node pivot;
    
    //planet parameters for the rotations
    private final float rotate;
    private final float rotation;
    private final float maxradius;
    private final float minradius;
    private final float degree;
        
    //planet parameters for the HUD
    private final String name ;
    private final double size;
    private final double weight;
    
    @Setter
    private float angle = 0.0f;
    
    //Factory
    public static Planet newInstance(int zsample, int radialsample, float radius, AssetManager assetManager, float rotate, float rotation, float maxradius, float minradius, float degree, String name, double size, double weight,Node rootNode, Node pivot, int angle){
        return new Planet(zsample, radialsample, radius, assetManager, rotate, rotation, maxradius, minradius, degree, name, size, weight, rootNode, pivot, angle);
    }
    
    //Constructeur sans paramètres
    private Planet(int zsample,int radialsample,float radius,AssetManager assetManager, float rotate, float rotation, float maxradius, float minradius, float degree, String name,double size,double weight, Node rootNode, Node pivot, int angle){
        //planet creation (sphere, geometry, material, rotation, texture)
        
        if (name.equals("Deimos") || name.equals("Phobos")){
            this.spatial = assetManager.loadModel("3DModel/" + name + ".glb");
            this.spatial.setLocalScale(radius);
        }
        else{
            Sphere sphere = new Sphere(zsample, radialsample, radius);
            this.spatial = (Spatial) new Geometry("Sphere", sphere);
        }
        Material mat = new Material(assetManager,"Common/MatDefs/Light/Lighting.j3md");
        mat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Planets/" + name + ".jpg"));
        mat.setBoolean("UseMaterialColors",true);
        mat.setColor("Ambient", ColorRGBA.White);  
        mat.setColor("Diffuse", ColorRGBA.White);
        this.spatial.setMaterial(mat);
        this.spatial.rotate(-FastMath.PI/2, 0, -FastMath.DEG_TO_RAD * angle);

        //planet attachment to the rootNode and right pivot
        rootNode.attachChild(this.spatial);
        pivot.attachChild(this.spatial);
        this.pivot = pivot;

        //planet parameters for the rotations
        this.rotate = rotate;
        this.rotation = rotation;
        this.maxradius = maxradius;
        this.minradius = minradius;
        this.degree = degree;

        //planet parameters for the HUD
        this.name = name;
        this.size = size;
        this.weight = weight;
    }
}

