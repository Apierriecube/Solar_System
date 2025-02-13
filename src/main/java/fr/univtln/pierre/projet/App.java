package fr.univtln.pierre.projet;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;
import com.jme3.util.SkyFactory;

public class App extends SimpleApplication implements ActionListener {

  private Planet terre;
  private Planet lune;
  private Geometry soleilGeom;

  private Node pivotT;

  private float angle = 0;


  private boolean lowerCam = false;
  private boolean upperCam = false;

  /**
   * The main method.
   * @param args
   */
  public static void main(String[] args){
    	
    AppSettings settings=new AppSettings(true);

    settings.setFullscreen(true);
    settings.setResolution(1920, 1080);
    	
    App app = new App();
    app.setShowSettings(false);
    app.setSettings(settings);
    app.start();
  }


    
  public App(){}

  @Override
  public void simpleInitApp(){

    flyCam.setMoveSpeed(20);

    rootNode.attachChild(SkyFactory.createSky(
        assetManager,
        assetManager.loadTexture("Textures/Sky/right.png"), 
        assetManager.loadTexture("Textures/Sky/left.png"),  
        assetManager.loadTexture("Textures/Sky/top.png"),    
        assetManager.loadTexture("Textures/Sky/bottom.png"), 
        assetManager.loadTexture("Textures/Sky/front.png"), 
        assetManager.loadTexture("Textures/Sky/back.png")    
    ));

    inputManager.addMapping("UpperCam", new KeyTrigger(KeyInput.KEY_SPACE));
    inputManager.addListener(this, "UpperCam");
    inputManager.addMapping("LowerCam", new KeyTrigger(KeyInput.KEY_LSHIFT));
    inputManager.addListener(this, "LowerCam");

    terre = Planet.newInstance(32,32,2f,assetManager, "Earth", 12.756, 5.975e24);
    lune = Planet.newInstance(16,16,1f,assetManager, "Moon", 3.475, 7.3e22);

    terre.getGeom().rotate(-FastMath.PI/2, 0, 0);
    rootNode.attachChild(terre.getGeom());

    lune.getGeom().rotate(-FastMath.PI/2, 0, 0);
    lune.getGeom().setLocalTranslation(new Vector3f(6,0,0));
    rootNode.attachChild(lune.getGeom());

    pivotT = new Node("pivotT");
    pivotT.attachChild(lune.getGeom());
    pivotT.attachChild(terre.getGeom());
    pivotT.setLocalTranslation(new Vector3f(30,0,-75));
    rootNode.attachChild(pivotT);

    Sphere soleilSphere = new Sphere(128,128,8f); 
    soleilGeom = new Geometry("Sphere", soleilSphere);  
    Material soleilMat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
    soleilMat.setTexture("ColorMap", assetManager.loadTexture("Textures/ColoredTex/Sun.jpg"));
    soleilGeom.setMaterial(soleilMat);

    soleilGeom.rotate(-FastMath.PI/2, 0, 0);
    soleilGeom.setLocalTranslation(new Vector3f(0,0,-75));
    rootNode.attachChild(soleilGeom);

    PointLight lamp_light = new PointLight();
    lamp_light.setColor(ColorRGBA.White);
    lamp_light.setPosition(new Vector3f(0,0,-75));
    rootNode.addLight(lamp_light);

  }

  @Override
  public void simpleUpdate(float tpf) {

    if (lowerCam){
      cam.setLocation(cam.getLocation().add(0, -tpf * 10, 0));
    }
    if (upperCam){
      cam.setLocation(cam.getLocation().add(0, tpf * 10, 0));
    }

    lune.getGeom().rotate(0, 0, (float) (0.03*tpf));
    terre.getGeom().rotate(0, 0, (float) (1*tpf));
    soleilGeom.rotate(0, 0, (float) (0.04*tpf));
    angle += tpf; 
    float xterre = FastMath.cos(angle) * 30;
    float zterre = FastMath.sin(angle) * 30;
    pivotT.setLocalTranslation(xterre, 0, -75 + zterre);

  }

  @Override
  public void onAction(String name, boolean isPressed, float tpf){
    if (name.equals("LowerCam")) {
      lowerCam = isPressed;
          
    }
    if (name.equals("UpperCam")) {
      upperCam = isPressed;
    }
  }
}