package fr.univtln.pierre.projet;

import java.time.LocalDate;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
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

  //Initialization of the variables
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

  private final Node pivotMercure = new Node("pivotMercure");
  private final Node pivotVenus = new Node("pivotVenus");
  private final Node pivotTerre = new Node("pivotTerre");
  private final Node pivotMars = new Node("pivotMars");
  private final Node pivotJupiter = new Node("pivotJupiter");
  private final Node pivotSaturn = new Node("pivotSaturn");
  private final Node pivotUranus = new Node("pivotUranus");
  private final Node pivotNeptune = new Node("pivotNeptune");


  private boolean lowerCam = false;
  private boolean upperCam = false;

  private int vr = 1;
  private boolean pause = false;

  private BitmapText hudText;
  //date of the last planets alignment
  LocalDate specificDate = LocalDate.of(2025, 2, 28);
  private float date = 0;

  private Vector3f followedPlanet = null;
  
  /**
   * The main method.
   * @param args
   */
  public static void main(String[] args){
      
    AppSettings settings=new AppSettings(true);

    settings.setFullscreen(true);
    settings.setResolution(1920, 1080);
    settings.setFrameRate(60);

    App app = new App();
    app.setShowSettings(false);
    app.setSettings(settings);
    app.start();
  }


    
  public App(){}

  @Override
  public void simpleInitApp(){

    //cam speed, render distance, position and orientation
    flyCam.setMoveSpeed(500);
    cam.setFrustumFar(10000);
    cam.setLocation(new Vector3f(0,1000,1000));
    cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);

    //remove old HUD
    setDisplayStatView(false);
    setDisplayFps(false);
    
    //HUD creation
    hudText = new BitmapText(guiFont, false);
    hudText.setSize(guiFont.getCharSet().getRenderedSize());     
    hudText.setColor(ColorRGBA.White);                            
    hudText.setText("starting...");             
    hudText.setLocalTranslation(0, 1080, 0);
    guiNode.attachChild(hudText);

    /*input initialization:
     *
     * space bar: camera up
     * shift bar: camera down
     * numpad 1: speed up
     * numpad 2: speed down
     * numpad 4: pause
     * numpad 5: play  
    */
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

    inputManager.addMapping("Mercury", new KeyTrigger(KeyInput.KEY_1));
    inputManager.addListener(this, "Mercury");
    inputManager.addMapping("Venus", new KeyTrigger(KeyInput.KEY_2));
    inputManager.addListener(this, "Venus");
    inputManager.addMapping("Earth", new KeyTrigger(KeyInput.KEY_3));
    inputManager.addListener(this, "Earth");
    inputManager.addMapping("Mars", new KeyTrigger(KeyInput.KEY_4));
    inputManager.addListener(this, "Mars");
    inputManager.addMapping("Jupiter", new KeyTrigger(KeyInput.KEY_5));
    inputManager.addListener(this, "Jupiter");
    inputManager.addMapping("Saturn", new KeyTrigger(KeyInput.KEY_6));
    inputManager.addListener(this, "Saturn");
    inputManager.addMapping("Uranus", new KeyTrigger(KeyInput.KEY_7));
    inputManager.addListener(this, "Uranus");
    inputManager.addMapping("Neptune", new KeyTrigger(KeyInput.KEY_8));
    inputManager.addListener(this, "Neptune");
    inputManager.addMapping("Sun", new KeyTrigger(KeyInput.KEY_9));
    inputManager.addListener(this, "Sun");
    inputManager.addMapping("None", new KeyTrigger(KeyInput.KEY_0));
    inputManager.addListener(this, "None");

    //sky creation
    rootNode.attachChild(SkyFactory.createSky(
      assetManager,
      assetManager.loadTexture("Textures/Sky/right.png"),
      assetManager.loadTexture("Textures/Sky/left.png"),
      assetManager.loadTexture("Textures/Sky/top.png"),
      assetManager.loadTexture("Textures/Sky/bottom.png"),
      assetManager.loadTexture("Textures/Sky/front.png"),
      assetManager.loadTexture("Textures/Sky/back.png")
    ));

    //planet creation (zsample, radialsample, radius, assetmanager, name, size, weight, rootNode, position, pivot, angle) with sun and saturn's ring initialized separately:

    //sun
    Sphere soleilSphere = new Sphere(128,128,100f); 
    soleilGeom = new Geometry("Sphere", soleilSphere);  
    Material soleilMat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
    soleilMat.setTexture("ColorMap", assetManager.loadTexture("Textures/ColoredTex/Sun.jpg"));
    soleilGeom.setMaterial(soleilMat);
    double soleilSize = 696340;
    double soleilWeight = 1.9885e30;
    soleilGeom.rotate(-FastMath.PI/2, 0, 0);
    rootNode.attachChild(soleilGeom);

    //planet mercure to saturn (with moons)
    mercure = Planet.newInstance(32,32,1.5f,assetManager, "Mercury", 2439.7, 3.3011e23, rootNode, pivotMercure, 1);
    venus = Planet.newInstance(32,32,4f,assetManager, "Venus", 6051.8, 4.8675e24, rootNode, pivotVenus, 177);
    terre = Planet.newInstance(32,32,4f,assetManager, "Earth", 	6371, 5.972168e24, rootNode, pivotTerre, 23);
    lune = Planet.newInstance(16,16,1f,assetManager, "Moon", 1737.4, 7.346e22, rootNode, pivotTerre, 1);
    lune.getGeom().setLocalTranslation(new Vector3f(6,0,0));
    mars = Planet.newInstance(32,32,2f,assetManager, "Mars", 3389.5, 6.4171e23, rootNode, pivotMars, 25);
    phobos = Planet.newInstance(32,32,0.1f,assetManager, "Phobos", 11.08, 1.0659e16, rootNode, pivotMars, 1);
    phobos.getGeom().setLocalTranslation(new Vector3f(2.22f,0,0));
    deimos = Planet.newInstance(32,32,0.1f,assetManager, "Deimos", 6.27, 1.4762e15, rootNode, pivotMars, 1);
    deimos.getGeom().setLocalTranslation(new Vector3f(3.67f,0,0));
    jupiter = Planet.newInstance(32,32,40f,assetManager, "Jupiter", 69911, 1.8982e27, rootNode, pivotJupiter, 3);
    io = Planet.newInstance(32,32,1f,assetManager, "Io", 1821.6, 8.931938e22, rootNode, pivotJupiter, 1);
    io.getGeom().setLocalTranslation(new Vector3f(46,0,0));
    europe = Planet.newInstance(32,32,1f,assetManager, "Europe", 1560.8, 4.799844e22, rootNode, pivotJupiter, 1);
    europe.getGeom().setLocalTranslation(new Vector3f(52,0,0));
    saturn = Planet.newInstance(32,32,38f,assetManager, "Saturn", 58232, 5.6824e26, rootNode, pivotSaturn, 27);

    //saturn's rings
    Torus ringShape = new Torus(100, 100, 5, 45);
    ringGeom = new Geometry("SaturnRings", ringShape);
    Material ringMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    ringMat.setTexture("ColorMap", assetManager.loadTexture("Textures/ColoredTex/SaturnRing.png"));
    ringMat.getAdditionalRenderState().setBlendMode(com.jme3.material.RenderState.BlendMode.Alpha); 
    ringGeom.setQueueBucket(RenderQueue.Bucket.Transparent);
    ringGeom.setMaterial(ringMat);
    ringGeom.setLocalScale(1, 1, 0.01f);
    ringGeom.rotate(-FastMath.PI/2, 0, -FastMath.DEG_TO_RAD * 27);
    rootNode.attachChild(ringGeom);
    pivotSaturn.attachChild(ringGeom);

    //uranus and neptune
    uranus = Planet.newInstance(32,32,16f,assetManager, "Uranus", 25362, 8.681e25, rootNode, pivotUranus, 98);
    neptune = Planet.newInstance(32,32,15f,assetManager, "Neptune", 24622, 1.02409e26, rootNode, pivotNeptune, 30);

    //creation of the orbits of the planets
    Orbit.newInstance(200, 200, 0.5f, 141, 0.978f,assetManager, rootNode, new ColorRGBA(0.4f,0.4f,0.4f,1f));
    Orbit.newInstance(200, 200, 0.5f, 181, 0.999f, assetManager, rootNode, new ColorRGBA(0.7f,0.4f,0f,1f));
    Orbit.newInstance(200, 200, 0.5f, 217, 0.999f, assetManager, rootNode, new ColorRGBA(0f,0.4f,1f,1f));
    Orbit.newInstance(200, 200, 0.5f, 275, 0.995f, assetManager, rootNode, new ColorRGBA(0.8f,0.1f,0f,1f));
    Orbit.newInstance(200, 200, 0.5f, 685, 0.998f, assetManager, rootNode, ColorRGBA.Brown);
    Orbit.newInstance(200, 200, 0.5f, 1195, 0.998f, assetManager, rootNode, new ColorRGBA(0.9f,0.6f,0f,1f));
    Orbit.newInstance(200, 200, 0.5f, 2217, 0.998f, assetManager, rootNode, new ColorRGBA(0f,1f,1f,1f));
    Orbit.newInstance(200, 200, 0.5f, 3337, 0.999f, assetManager, rootNode, new ColorRGBA(0f,0f,1f,1f));

    //creation of the orbits of the moons
    Orbit orbitL = Orbit.newInstance(200, 200, 0.1f, 6, 1, assetManager, rootNode, new ColorRGBA(0.4f,0.4f,0.4f,1f));
    orbitL.getGeom().setLocalTranslation(terre.getGeom().getLocalTranslation());
    Orbit orbitP = Orbit.newInstance(200, 200, 0.1f,  2.22f, 1, assetManager, rootNode, new ColorRGBA(0.2f,0.2f,0.2f,1f));
    orbitP.getGeom().setLocalTranslation(mars.getGeom().getLocalTranslation());
    Orbit orbitD = Orbit.newInstance(200, 200, 0.1f, 3.67f, 1, assetManager, rootNode, new ColorRGBA(0.3f,0.3f,0.3f,1f));
    orbitD.getGeom().setLocalTranslation(mars.getGeom().getLocalTranslation());
    Orbit orbitI = Orbit.newInstance(200, 200, 0.1f, 46, 1, assetManager, rootNode, new ColorRGBA(0.5f,0.6f,0f,1f));
    orbitI.getGeom().setLocalTranslation(jupiter.getGeom().getLocalTranslation());
    Orbit orbitE = Orbit.newInstance(200, 200, 0.1f, 52, 1, assetManager, rootNode, new ColorRGBA(0.8f,0.8f,0.8f,1f));
    orbitE.getGeom().setLocalTranslation(jupiter.getGeom().getLocalTranslation());

    //creation of the pivots for the planets and moons rotations
    rootNode.attachChild(pivotMercure);
    pivotMercure.setLocalTranslation(new Vector3f(141,0,0));
    rootNode.attachChild(pivotVenus);
    pivotVenus.setLocalTranslation(new Vector3f(181,0,0));
    rootNode.attachChild(pivotTerre);
    pivotTerre.attachChild(orbitL.getGeom());
    pivotTerre.setLocalTranslation(new Vector3f(217,0,0));
    rootNode.attachChild(pivotMars);
    pivotMars.attachChild(orbitP.getGeom());
    pivotMars.attachChild(orbitD.getGeom());
    pivotMars.setLocalTranslation(new Vector3f(275,0,0));
    rootNode.attachChild(pivotJupiter);
    pivotJupiter.attachChild(orbitI.getGeom());
    pivotJupiter.attachChild(orbitE.getGeom());
    pivotJupiter.setLocalTranslation(new Vector3f(685,0,0));
    rootNode.attachChild(pivotSaturn);
    pivotSaturn.setLocalTranslation(new Vector3f(1195,0,0));
    rootNode.attachChild(pivotUranus);
    pivotUranus.setLocalTranslation(new Vector3f(2217,0,0));
    rootNode.attachChild(pivotNeptune);
    pivotNeptune.setLocalTranslation(new Vector3f(3337,0,0));

    //velocity initialization (abort code part for rotation using the newton's law of universal gravitation)
    //terre.setVelocity(new Vector3f(-2,0,-6.95f));

    //light initialization
    PointLight lamp_light = new PointLight();
    lamp_light.setColor(ColorRGBA.White);
    lamp_light.setPosition(new Vector3f(0,0,-300));
    rootNode.addLight(lamp_light);

    //rootNode position
    rootNode.setLocalTranslation(0,0,-300);

  }

  @Override
  public void simpleUpdate(float tpf) {
    /*
    * set button reactions, rotation, orbit of objects and HUD update
    */

    // buttons for camera movement (up and down)
    if (lowerCam){
      cam.setLocation(cam.getLocation().add(0, -tpf * 500, 0));
    }
    if (upperCam){
      cam.setLocation(cam.getLocation().add(0, tpf * 500, 0));
    }

    //speed of rotation
    tpf *= vr;
    if (pause){
      tpf = 0;
    }

    //rotation of the planets and moons on their own axis
    soleilGeom.rotate(0, 0, (float) (0.2476*tpf));
    mercure.getGeom().rotate(0, 0, (float) (0.1072*tpf));
    venus.getGeom().rotate(0, 0, (float) (0.0258*tpf));
    lune.getGeom().rotate(0, 0, (float) (0.2299*tpf));
    terre.getGeom().rotate(0, 0, (float) (6.2832 *tpf));
    mars.getGeom().rotate(0, 0, (float) (6.1261*tpf));
    phobos.getGeom().rotate(0, 0, (float) (1794.44*tpf));
    deimos.getGeom().rotate(0, 0, (float) (523.60*tpf));
    jupiter.getGeom().rotate(0, 0, (float) (15.1750*tpf));
    io.getGeom().rotate(0, 0, (float) (3.5512*tpf));
    europe.getGeom().rotate(0, 0, (float) (1.7690*tpf));
    saturn.getGeom().rotate(0, 0, (float) (14.1445*tpf));
    ringGeom.rotate(0, 0, (float) (0.0121*tpf));
    uranus.getGeom().rotate(0, 0, (float) (8.7482*tpf));
    neptune.getGeom().rotate(0, 0, (float) (9.3693*tpf));

    //rotation of the planets around the sun
    rotation(pivotTerre, terre, -(float) 0.0172*tpf, 217.0f, 216.97f);
    rotation(pivotMercure, mercure, -(float) 0.0714*tpf, 141.0f, 137.99f);
    rotation(pivotVenus, venus, -(float) 0.02796*tpf, 181.0f, 180.99f);
    rotation(pivotMars, mars, -(float) 0.00915*tpf, 275.0f, 273.80f);
    rotation(pivotJupiter, jupiter, -(float) 0.00145*tpf, 685.0f, 684.20f);
    rotation(pivotSaturn, saturn, -(float) 0.000585*tpf, 1195.0f, 	1193.09f);
    rotation(pivotUranus, uranus, -(float) 0.000205*tpf, 2217.0f, 2214.65f);
    rotation(pivotNeptune, neptune, -(float) 0.000105*tpf, 3337.0f, 	3336.88f);

    //rotation of the moons around their planet
    rotation(lune, -(float) 0.2299*tpf, 6.0f, 6.0f);
    rotation(phobos, -(float) 19.7047*tpf, 2.22f, 2.22f);
    rotation(deimos, -(float) 4.9771*tpf, 3.67f, 3.67f);
    rotation(io, -(float) 3.5512*tpf, 46.0f, 46.0f);
    rotation(europe, -(float) 1.7690*tpf, 52.0f, 52.0f);

    //camera follow the planet
    if (followedPlanet != null){
      followPlanet(followedPlanet);
    }
    //date management
    date += vr;
    if (date>=60){
      specificDate = specificDate.plusDays((long) (date/60));
      date = date%60;
    }
    else if (date<=-60){
      specificDate = specificDate.minusDays(-(long) (date/60));
      date = date%60;
    }

    //HUD update (speed, pause, date)
    hudText.setText("Speed: " + vr + "x" + "\n" + "Pause: " + pause + "\n" + "Time: " + specificDate);
    
    //about code for the rotation using the newton's law of universal gravitation
    //terre.getGeom().setLocalTranslation(getNextPosition(terre,tpf));
  }

  @Override
  public void onAction(String name, boolean isPressed, float tpf){
    //set button reactions
    if (name.equals("LowerCam")) {
      lowerCam = isPressed;
          
    }
    if (name.equals("UpperCam")) {
      upperCam = isPressed;
    }

    if (name.equals("Speed")) {
      vr += 10;
    }
    if (name.equals("Slow")) {
      vr -= 10;
    }
    if (name.equals("Stop")) {
      pause = true;
      vr = 0;
    }
    if (name.equals("Go")) {
      pause = false;
      vr = 1;
    }

    if (name.equals("Mercury")) {
      followedPlanet = pivotMercure.getLocalTranslation();
    }
    if (name.equals("Venus")) {
      followedPlanet = pivotVenus.getLocalTranslation();
    }
    if (name.equals("Earth")) {
      followedPlanet = pivotTerre.getLocalTranslation();
    }
    if (name.equals("Mars")) {
      followedPlanet = pivotMars.getLocalTranslation();
    }
    if (name.equals("Jupiter")) {
      followedPlanet = pivotJupiter.getLocalTranslation();
    }
    if (name.equals("Saturn")) {
      followedPlanet = pivotSaturn.getLocalTranslation();
    }
    if (name.equals("Uranus")) {
      followedPlanet = pivotUranus.getLocalTranslation();
    }
    if (name.equals("Neptune")) {
      followedPlanet = pivotNeptune.getLocalTranslation();
    }
    if (name.equals("Sun")) {
      followedPlanet = soleilGeom.getLocalTranslation();
    }
    if (name.equals("None")) {
      followedPlanet = null;
    }
    
  }

  
  public void rotation(Node node, Planet planet, float speed,float a,float b){ 
    float angle = planet.getAngle();
    angle += speed;
    float x = a * (float) Math.cos(angle);  
    float z = b * (float) Math.sin(angle);  
    planet.setAngle(angle);
    node.setLocalTranslation( x, node.getLocalTranslation().getY(), z);
  }

  public void rotation(Planet planet, float speed,float a,float b){ 
    float angle = planet.getAngle();
    angle += speed;
    float x = a * (float) Math.cos(angle);  
    float z = b * (float) Math.sin(angle);  
    planet.setAngle(angle);
    planet.getGeom().setLocalTranslation(x, planet.getGeom().getLocalTranslation().getY(),z);
  }

  public void followPlanet(Vector3f position){
    cam.setLocation(position.add(0, 50, -300));
    cam.lookAt(position.add(0, 0, -300), Vector3f.UNIT_Y);
  }

  //two fonction for the rotation using the newton's law of universal gravitation (abort code)


  /*public static Vector3f computeAcceleration(Vector3f r) {
    double rMag = Math.sqrt(r.x * r.x + r.y * r.y + r.z * r.z);
    double factor = -25000 / (rMag * rMag * rMag);

    return new Vector3f((float) (factor * r.x), (float) (factor * r.y), (float) (factor * r.z));
  }

public static Vector3f getNextPosition(Planet planet,float tpf) {

    Vector3f r = planet.getGeom().getLocalTranslation();
    Vector3f a = computeAcceleration(r);

    Vector3f v = planet.getVelocity();
    
    v.x += a.x * tpf;
    v.y += a.y * tpf;
    v.z += a.z * tpf;
    
    r.x += planet.getVelocity().x * tpf;
    r.y += planet.getVelocity().y * tpf;
    r.z += planet.getVelocity().z * tpf;


    planet.setVelocity(v);
    return r;
}*/
  
}