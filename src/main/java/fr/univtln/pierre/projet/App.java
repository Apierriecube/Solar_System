package fr.univtln.pierre.projet;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
  private final List<Planet> planets = new ArrayList<>();
  private Geometry soleilGeom;
  private Geometry ringGeom;

  private final Node pivotMercure = new Node("Mercury");
  private final Node pivotVenus = new Node("Venus");
  private final Node pivotTerre = new Node("Earth");
  private final Node pivotMars = new Node("Mars");
  private final Node pivotJupiter = new Node("Jupiter");
  private final Node pivotSaturn = new Node("Saturn");
  private final Node pivotUranus = new Node("Uranus");
  private final Node pivotNeptune = new Node("Neptune");


  private boolean lowerCam = false;
  private boolean upperCam = false;

  private int vr = 1;
  private boolean pause = false;

  private BitmapText hudText;
  //date of the last planets alignment
  LocalDate specificDate = LocalDate.of(2025, 2, 28);
  private float date = 0;

  private Planet followedPlanet = null;
  private Vector3f followedPosition = null;
  private float size = 0;

  private final double soleilSize = 696340;
  private final double soleilWeight = 1.9885e30;
  
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

  @SuppressWarnings("deprecation")
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
     * 
     * 1: follow the sun
     * 2: follow mercury
     * 3: follow venus
     * 4: follow earth
     * 5: follow mars
     * 6: follow jupiter
     * 7: follow saturn
     * 8: follow uranus
     * 9: follow neptune
     * 0: follow none
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

    inputManager.addMapping("Sun", new KeyTrigger(KeyInput.KEY_1));
    inputManager.addListener(this, "Sun");
    inputManager.addMapping("Mercury", new KeyTrigger(KeyInput.KEY_2));
    inputManager.addListener(this, "Mercury");
    inputManager.addMapping("Venus", new KeyTrigger(KeyInput.KEY_3));
    inputManager.addListener(this, "Venus");
    inputManager.addMapping("Earth", new KeyTrigger(KeyInput.KEY_4));
    inputManager.addListener(this, "Earth");
    inputManager.addMapping("Mars", new KeyTrigger(KeyInput.KEY_5));
    inputManager.addListener(this, "Mars");
    inputManager.addMapping("Jupiter", new KeyTrigger(KeyInput.KEY_6));
    inputManager.addListener(this, "Jupiter");
    inputManager.addMapping("Saturn", new KeyTrigger(KeyInput.KEY_7));
    inputManager.addListener(this, "Saturn");
    inputManager.addMapping("Uranus", new KeyTrigger(KeyInput.KEY_8));
    inputManager.addListener(this, "Uranus");
    inputManager.addMapping("Neptune", new KeyTrigger(KeyInput.KEY_9));
    inputManager.addListener(this, "Neptune");
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
    soleilGeom.rotate(-FastMath.PI/2, 0, 0);
    rootNode.attachChild(soleilGeom);

    //planet mercure to saturn (with moons)
    planets.add(Planet.newInstance(32,32,1.5f,assetManager,0.1072f, 0.0714f, 141.0f, 137.99f,"Mercury", 2439.7, 3.3011e23, rootNode, pivotMercure, 1));
    
    planets.add(Planet.newInstance(32,32,4f,assetManager, 0.0258f,0.02796f, 181.0f, 180.99f, "Venus", 6051.8, 4.8675e24, rootNode, pivotVenus, 177));
    
    planets.add(Planet.newInstance(32,32,4f,assetManager, 6.2832f,0.0172f, 217.0f, 216.97f,"Earth", 	6371, 5.972168e24, rootNode, pivotTerre, 23));
    planets.add(Planet.newInstance(16,16,1f,assetManager, 0.2299f, 0.2299f, 6.0f, 6.0f,"Moon", 1737.4, 7.346e22, rootNode, pivotTerre, 1));
    planets.get(3).getGeom().setLocalTranslation(new Vector3f(6,0,0));

    planets.add(Planet.newInstance(32,32,2f,assetManager, 6.1261f, 0.00915f, 275.0f, 273.80f,"Mars", 3389.5, 6.4171e23, rootNode, pivotMars, 25));
    planets.add(Planet.newInstance(32,32,0.1f,assetManager, 1794.44f, 19.7047f, 2.22f, 2.22f,"Phobos", 11.08, 1.0659e16, rootNode, pivotMars, 1));
    planets.get(5).getGeom().setLocalTranslation(new Vector3f(2.22f,0,0));
    planets.add(Planet.newInstance(32,32,0.1f,assetManager, 523.60f, 4.9771f, 3.67f, 3.67f, "Deimos", 6.27, 1.4762e15, rootNode, pivotMars, 1));
    planets.get(6).getGeom().setLocalTranslation(new Vector3f(3.67f,0,0));
    
    planets.add(Planet.newInstance(32,32,40f,assetManager, 15.1750f, 0.00145f, 685.0f, 684.20f, "Jupiter", 69911, 1.8982e27, rootNode, pivotJupiter, 3));
    planets.add(Planet.newInstance(32,32,1f,assetManager, 3.5512f, 3.5512f, 46.0f, 46.0f,"Io", 1821.6, 8.931938e22, rootNode, pivotJupiter, 1));
    planets.get(8).getGeom().setLocalTranslation(new Vector3f(46,0,0));
    planets.add(Planet.newInstance(32,32,1f,assetManager, 1.7690f, 1.7690f, 52.0f, 52.0f, "Europe", 1560.8, 4.799844e22, rootNode, pivotJupiter, 1));
    planets.get(9).getGeom().setLocalTranslation(new Vector3f(52,0,0));
    
    planets.add(Planet.newInstance(32,32,38f,assetManager, 14.1445f, 0.000585f, 1195.0f, 1193.09f, "Saturn", 58232, 5.6824e26, rootNode, pivotSaturn, 27));
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
    planets.add(Planet.newInstance(32,32,16f,assetManager, 8.7482f, 0.000205f, 2217.0f, 2214.65f, "Uranus", 25362, 8.681e25, rootNode, pivotUranus, 98));
    planets.add(Planet.newInstance(32,32,15f,assetManager, 9.3693f, 0.000105f, 3337.0f, 	3336.88f, "Neptune", 24622, 1.02409e26, rootNode, pivotNeptune, 30));




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
    orbitL.getGeom().setLocalTranslation(planets.get(2).getGeom().getLocalTranslation());
    Orbit orbitP = Orbit.newInstance(200, 200, 0.1f,  2.22f, 1, assetManager, rootNode, new ColorRGBA(0.2f,0.2f,0.2f,1f));
    orbitP.getGeom().setLocalTranslation(planets.get(4).getGeom().getLocalTranslation());
    Orbit orbitD = Orbit.newInstance(200, 200, 0.1f, 3.67f, 1, assetManager, rootNode, new ColorRGBA(0.3f,0.3f,0.3f,1f));
    orbitD.getGeom().setLocalTranslation(planets.get(4).getGeom().getLocalTranslation());
    Orbit orbitI = Orbit.newInstance(200, 200, 0.1f, 46, 1, assetManager, rootNode, new ColorRGBA(0.5f,0.6f,0f,1f));
    orbitI.getGeom().setLocalTranslation(planets.get(7).getGeom().getLocalTranslation());
    Orbit orbitE = Orbit.newInstance(200, 200, 0.1f, 52, 1, assetManager, rootNode, new ColorRGBA(0.8f,0.8f,0.8f,1f));
    orbitE.getGeom().setLocalTranslation(planets.get(7).getGeom().getLocalTranslation());




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

    //rotation of the planets and moons on their own axis and around the sun/planets
    soleilGeom.rotate(0, 0, (float) (0.2476*tpf));
    ringGeom.rotate(0, 0, (float) (0.0121*tpf));
    for (Planet planet : planets){
      planet.getGeom().rotate(0, 0, (float) (planet.getRotate()*tpf));
      rotation(planet.getPivot(), planet, -(float) planet.getRotation()*tpf, planet.getMaxradius(), planet.getMinradius());
    }

    //camera follow the planet
    if (followedPosition != null){
      followPlanet(followedPosition, size);
      if (followedPlanet == null){
        //HUD update (speed, pause, date, flycam, sun)
        hudText.setText("Speed: " + vr + "x" + "\n" + "Pause: " + pause + "\n" + "Time: " + specificDate + "\nFlycam: off \n --------------------------------------" + "\n" + "Followed planet: Sun \n" + "Size: " + soleilSize + " km" + "\n" + "Weight: " + soleilWeight + " kg");
      }
      else{
        //HUD update (speed, pause, date, flycam,followed planet)
        hudText.setText("Speed: " + vr + "x" + "\n" + "Pause: " + pause + "\n" + "Time: " + specificDate + "\nFlycam: off \n --------------------------------------" + "\n" + "Followed planet: " + followedPlanet.getName() + "\n" + "Size: " + followedPlanet.getSize() + " km" + "\n" + "Weight: " + followedPlanet.getWeight() + " kg");
      }
    }
    else{
      //HUD update (speed, pause, date, flycam)
      hudText.setText("Speed: " + vr + "x" + "\n" + "Pause: " + pause + "\n" + "Time: " + specificDate + "\nFlycam: on");
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
  }





  public void rotation(Node node, Planet planet, float speed,float a,float b){ 
    float angle = planet.getAngle();
    angle += speed;
    float x = a * (float) Math.cos(angle);  
    float z = b * (float) Math.sin(angle);  
    planet.setAngle(angle);
    if (node.getName().equals(planet.getName())){
      node.setLocalTranslation( x, node.getLocalTranslation().getY(), z);
    }
    else{
      planet.getGeom().setLocalTranslation( x, planet.getGeom().getLocalTranslation().getY(), z);
    }
    
  }

  public void followPlanet(Vector3f position, float size){
    cam.setLocation(position.add(-position.x/100 * size*2, size * 2 , -300 - position.z/100 * size*2));
    cam.lookAt(position.add(0, 0, -300), Vector3f.UNIT_Y);
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

    if (name.equals("Speed") && !pause) {
      vr += 10;
    }
    if (name.equals("Slow") && !pause) {
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

    if (name.equals("Sun")) {
      followedPlanet = null;
      followedPosition = soleilGeom.getLocalTranslation();
      size = 250f;
    }
    if (name.equals("Mercury")) {
      followedPlanet = planets.get(0);
      followedPosition = pivotMercure.getLocalTranslation();
      size = 2f;
    }
    if (name.equals("Venus")) {
      followedPlanet = planets.get(1);
      followedPosition = pivotVenus.getLocalTranslation();
      size = 4f;
    }
    if (name.equals("Earth")) {
      followedPlanet = planets.get(2);
      followedPosition = pivotTerre.getLocalTranslation();
      size = 4f;
    }
    if (name.equals("Mars")) {
      followedPlanet = planets.get(4);
      followedPosition = pivotMars.getLocalTranslation();
      size = 2f;
    }
    if (name.equals("Jupiter")) {
      followedPlanet = planets.get(7);
      followedPosition = pivotJupiter.getLocalTranslation();
      size = 8;
    }
    if (name.equals("Saturn")) {
      followedPlanet = planets.get(10);
      followedPosition = pivotSaturn.getLocalTranslation();
      size = 6;
    }
    if (name.equals("Uranus")) {
      followedPlanet = planets.get(11);
      followedPosition = pivotUranus.getLocalTranslation();
      size = 1.5f;
    }
    if (name.equals("Neptune")) {
      followedPlanet = planets.get(12);
      followedPosition = pivotNeptune.getLocalTranslation();
      size = 1;
    }
    if (name.equals("None")) {
      followedPlanet = null;
      followedPosition = null;
      size = 0;
    }
    
  }
  
}