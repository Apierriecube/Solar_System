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
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Torus;
import com.jme3.system.AppSettings;
import com.jme3.util.SkyFactory;

public class App extends SimpleApplication implements ActionListener {

  private Planet terre;
  private Planet lune;
  private Planet mercure;
  private Planet venus;
  private Planet mars;
  private Planet phobos;
  private Planet deimos;
  private Planet jupiter;
  private Planet io;
  private Planet europe;
  private Planet saturn;
  private Planet uranus;
  private Planet neptune;
  private Geometry soleilGeom;
  private Geometry ringGeom;

  private Node pivotT;

  private float angle = 0;


  private boolean lowerCam = false;
  private boolean upperCam = false;

  private int speed = 1;
  private boolean pause = false;

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

    flyCam.setMoveSpeed(100);
    cam.setFrustumFar(10000);

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
    inputManager.addMapping("Speed", new KeyTrigger(KeyInput.KEY_NUMPAD1));
    inputManager.addListener(this, "Speed");
    inputManager.addMapping("Slow", new KeyTrigger(KeyInput.KEY_NUMPAD2));
    inputManager.addListener(this, "Slow");
    inputManager.addMapping("Stop", new KeyTrigger(KeyInput.KEY_NUMPAD4));
    inputManager.addListener(this, "Stop");
    inputManager.addMapping("Go", new KeyTrigger(KeyInput.KEY_NUMPAD5));
    inputManager.addListener(this, "Go");

    terre = Planet.newInstance(32,32,4f,assetManager, "Earth", 	6371, 5.972168e24);
    lune = Planet.newInstance(16,16,1f,assetManager, "Moon", 1737.4, 7.346e22);

    mercure = Planet.newInstance(32,32,1.5f,assetManager, "Mercury", 2439.7, 3.3011e23);
    venus = Planet.newInstance(32,32,4f,assetManager, "Venus", 6051.8, 4.8675e24);
    mars = Planet.newInstance(32,32,2f,assetManager, "Mars", 3389.5, 6.4171e23);
    phobos = Planet.newInstance(32,32,0.1f,assetManager, "Phobos", 11.08, 1.0659e16);
    deimos = Planet.newInstance(32,32,0.1f,assetManager, "Deimos", 6.27, 1.4762e15);
    jupiter = Planet.newInstance(32,32,40f,assetManager, "Jupiter", 69911, 1.8982e27);
    io = Planet.newInstance(32,32,1f,assetManager, "Io", 1821.6, 8.931938e22);
    europe = Planet.newInstance(32,32,1f,assetManager, "Europe", 1560.8, 4.799844e22);
    saturn = Planet.newInstance(32,32,38f,assetManager, "Saturn", 58232, 5.6824e26);
    Torus ringShape = new Torus(100, 100, 5, 45);
    ringGeom = new Geometry("SaturnRings", ringShape);
    Material ringMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    ringMat.setTexture("ColorMap", assetManager.loadTexture("Textures/ColoredTex/SaturnRing.png"));
    ringMat.getAdditionalRenderState().setBlendMode(com.jme3.material.RenderState.BlendMode.Alpha); 
    ringGeom.setQueueBucket(RenderQueue.Bucket.Transparent);
    ringGeom.setMaterial(ringMat);
    ringGeom.setLocalScale(1, 1, 0.01f);
    uranus = Planet.newInstance(32,32,16f,assetManager, "Uranus", 25362, 8.681e25);
    neptune = Planet.newInstance(32,32,15f,assetManager, "Neptune", 24622, 1.02409e26);

    mercure.getGeom().rotate(-FastMath.PI/2, 0, 0);
    mercure.getGeom().setLocalTranslation(new Vector3f(103,0,0));
    rootNode.attachChild(mercure.getGeom());

    venus.getGeom().rotate(-FastMath.PI/2, 0, 0);
    venus.getGeom().setLocalTranslation(new Vector3f(139,0,0));
    rootNode.attachChild(venus.getGeom());

    lune.getGeom().rotate(-FastMath.PI/2, 0, 0);
    lune.getGeom().setLocalTranslation(new Vector3f(0,6,0));
    rootNode.attachChild(lune.getGeom());

    terre.getGeom().rotate(-FastMath.PI/2, 0, 0);
    rootNode.attachChild(terre.getGeom());
    pivotT = new Node("pivotT");
    pivotT.attachChild(lune.getGeom());
    pivotT.attachChild(terre.getGeom());
    pivotT.setLocalTranslation(new Vector3f(171,0,0));
    rootNode.attachChild(pivotT);

    mars.getGeom().rotate(-FastMath.PI/2, 0, 0);
    mars.getGeom().setLocalTranslation(new Vector3f(225,0,0));
    rootNode.attachChild(mars.getGeom());

    phobos.getGeom().rotate(-FastMath.PI/2, 0, 0);
    phobos.getGeom().setLocalTranslation(new Vector3f(225,2.22f,0));
    rootNode.attachChild(phobos.getGeom());

    deimos.getGeom().rotate(-FastMath.PI/2, 0, 0);
    deimos.getGeom().setLocalTranslation(new Vector3f(225,2.88f,0));
    rootNode.attachChild(deimos.getGeom());

    jupiter.getGeom().rotate(-FastMath.PI/2, 0, 0);
    jupiter.getGeom().setLocalTranslation(new Vector3f(411,0,0));
    rootNode.attachChild(jupiter.getGeom());
    
    io.getGeom().rotate(-FastMath.PI/2, 0, 0);
    io.getGeom().setLocalTranslation(new Vector3f(411,42.2f,0));
    rootNode.attachChild(io.getGeom());

    europe.getGeom().rotate(-FastMath.PI/2, 0, 0);
    europe.getGeom().setLocalTranslation(new Vector3f(411,45.2f,0));
    rootNode.attachChild(europe.getGeom());

    saturn.getGeom().rotate(-FastMath.PI/2, 0, 0);
    saturn.getGeom().setLocalTranslation(new Vector3f(667,0,0));
    rootNode.attachChild(saturn.getGeom());

    ringGeom.rotate(-FastMath.PI/2, 0, 0);
    ringGeom.setLocalTranslation(new Vector3f(667,0,0));
    rootNode.attachChild(ringGeom);

    uranus.getGeom().rotate(-FastMath.PI/2, 0, 0);
    uranus.getGeom().setLocalTranslation(new Vector3f(1189,0,0));
    rootNode.attachChild(uranus.getGeom());

    neptune.getGeom().rotate(-FastMath.PI/2, 0, 0);
    neptune.getGeom().setLocalTranslation(new Vector3f(1749,0,0));
    rootNode.attachChild(neptune.getGeom());

    Sphere soleilSphere = new Sphere(128,128,64f); 
    soleilGeom = new Geometry("Sphere", soleilSphere);  
    Material soleilMat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
    soleilMat.setTexture("ColorMap", assetManager.loadTexture("Textures/ColoredTex/Sun.jpg"));
    soleilGeom.setMaterial(soleilMat);
    double soleilSize = 696340;
    double soleilWeight = 1.9885e30;

    soleilGeom.rotate(-FastMath.PI/2, 0, 0);
    rootNode.attachChild(soleilGeom);

    PointLight lamp_light = new PointLight();
    lamp_light.setColor(ColorRGBA.White);
    lamp_light.setPosition(new Vector3f(0,0,-75));
    rootNode.addLight(lamp_light);

    rootNode.setLocalTranslation(0, 0, -75);
  }

  @Override
  public void simpleUpdate(float tpf) {

    if (lowerCam){
      cam.setLocation(cam.getLocation().add(0, -tpf * 10, 0));
    }
    if (upperCam){
      cam.setLocation(cam.getLocation().add(0, tpf * 10, 0));
    }

    tpf *= speed;
    if (pause){
      tpf = 0;
    }

    soleilGeom.rotate(0, 0, (float) (0.04*tpf));
    mercure.getGeom().rotate(0, 0, (float) (0.017*tpf));
    venus.getGeom().rotate(0, 0, (float) (-0.004*tpf));
    lune.getGeom().rotate(0, 0, (float) (0.03*tpf));
    terre.getGeom().rotate(0, 0, (float) (1*tpf));
    mars.getGeom().rotate(0, 0, (float) (1*tpf));
    phobos.getGeom().rotate(0, 0, (float) (1*tpf));
    deimos.getGeom().rotate(0, 0, (float) (0.8*tpf));
    jupiter.getGeom().rotate(0, 0, (float) (2.3*tpf));
    io.getGeom().rotate(0, 0, (float) (0.5*tpf));
    europe.getGeom().rotate(0, 0, (float) (0.28*tpf));
    saturn.getGeom().rotate(0, 0, (float) (2.3*tpf));
    ringGeom.rotate(0, 0, (float) (0.002*tpf));
    uranus.getGeom().rotate(0, 0, (float) (1.4*tpf));
    neptune.getGeom().rotate(0, 0, (float) (1.5*tpf));
    
    /*angle += tpf; 
    float xterre = FastMath.cos(angle) * 30;
    float zterre = FastMath.sin(angle) * 30;
    pivotT.setLocalTranslation(xterre, 0, -75 + zterre);*/

  }

  @Override
  public void onAction(String name, boolean isPressed, float tpf){
    if (name.equals("LowerCam")) {
      lowerCam = isPressed;
          
    }
    if (name.equals("UpperCam")) {
      upperCam = isPressed;
    }

    if (name.equals("Speed")) {
      speed *= 2;
    }
    if (name.equals("Slow")) {
      if (speed > 1){
        speed /= 2;
      }
    }
    if (name.equals("Stop")) {
      pause = true;
    }
    if (name.equals("Go")) {
      pause = false;
    }
    
  }
  
}