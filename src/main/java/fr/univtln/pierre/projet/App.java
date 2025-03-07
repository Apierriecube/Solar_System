package fr.univtln.pierre.projet;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Torus;
import com.jme3.system.AppSettings;
import com.jme3.util.SkyFactory;

public class App extends SimpleApplication implements ActionListener {
  /*Initialization of the variables:
   * 
   * - planets: list of the planets
   * - soleilGeom: geometry of the sun
   * - ringGeom: geometry of the saturn's ring
   * - kuiper: node of the kuiper belt
   * 
   * - pivots: list of the pivots
   * 
   * - lowerCam: boolean for the camera movement
   * - upperCam: boolean for the camera movement
   * 
   * - vr: speed of the rotation
   * - pastvr: past speed
   * - pause: boolean for the pause
   * - timeDirection: boolean for the time direction
   * 
   * - hudText: text of the HUD
   * - date: date
   * - soleilSize: size of the sun
   * - soleilWeight: weight of the sun
   * 
   * - followedPlanet: planet followed by the camera
   * - followedPosition: position of the planet followed by the camera
   * - size: size of the planet followed by the camera
   */
  private final List<Planet> planets = new ArrayList<>();
  private Geometry soleilGeom;
  private Geometry ringGeom;
  private final Node kuiper = new Node("kuiper");

  private final List<Node> pivots = new ArrayList<>();

  private boolean lowerCam = false;
  private boolean upperCam = false;

  private float vr = 1f;
  private float pastvr = 0;
  private boolean pause = false;
  private int timeDirection = 1;

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
    /* Initialization of the application:
     * 
     * - Project initialisation (key mapping, camera speed, render distance, position and orientation, HUD creation)
     * - Objects creation (sky, planets, orbits, pivots, light)
    */


    /*------------------------------------------------------------------------------------------------------------- 
     * Project initialisation (key mapping, camera speed, render distance, position and orientation, HUD creation)
     * ------------------------------------------------------------------------------------------------------------
    */

    //key mapping
    inputManager.addMapping("UpperCam", new KeyTrigger(KeyInput.KEY_SPACE));
    inputManager.addListener(this, "UpperCam");
    inputManager.addMapping("LowerCam", new KeyTrigger(KeyInput.KEY_LSHIFT));
    inputManager.addListener(this, "LowerCam");

    inputManager.addMapping("Speed", new KeyTrigger(KeyInput.KEY_NUMPAD1));
    inputManager.addListener(this, "Speed");
    inputManager.addMapping("Slow", new KeyTrigger(KeyInput.KEY_NUMPAD2));
    inputManager.addListener(this, "Slow");
    inputManager.addMapping("Pause", new KeyTrigger(KeyInput.KEY_RETURN));
    inputManager.addListener(this, "Pause");
    inputManager.addMapping("Forwards", new KeyTrigger(KeyInput.KEY_NUMPAD4));
    inputManager.addListener(this, "Forwards");
    inputManager.addMapping("Backwards", new KeyTrigger(KeyInput.KEY_NUMPAD5));
    inputManager.addListener(this, "Backwards");

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


    /*------------------------------------------------------------------------------------------------------------- 
     * Objects creation (sky, planets, orbits, pivots, light)
     * ------------------------------------------------------------------------------------------------------------
    */


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

    //pivots creation
    pivots.add(new Node("Mercury"));
    pivots.add(new Node("Venus"));
    pivots.add(new Node("Earth"));
    pivots.add(new Node("Mars"));
    pivots.add(new Node("Jupiter"));
    pivots.add(new Node("Saturn"));
    pivots.add(new Node("Uranus"));
    pivots.add(new Node("Neptune"));

    //planet creation (zsample, radialsample, radius, assetmanager, name, size, weight, rootNode, position, pivot, angle) with sun and saturn's ring initialized separately:
    //sun
    Sphere soleilSphere = new Sphere(128,128,100f); 
    soleilGeom = new Geometry("Sphere", soleilSphere);  
    Material soleilMat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
    soleilMat.setTexture("ColorMap", assetManager.loadTexture("Textures/Planets/Sun.jpg"));
    soleilGeom.setMaterial(soleilMat);
    soleilGeom.rotate(-FastMath.PI/2, 0, 0);
    rootNode.attachChild(soleilGeom);

    //mercury
    planets.add(Planet.newInstance(32,32,1.5f,assetManager,0.1072f, 0.0714f, 141.0f, 137.99f, 7.00f,"Mercury", 2439.7, 3.3011e23, rootNode, pivots.get(0), 1));
    
    //venus
    planets.add(Planet.newInstance(32,32,4f,assetManager, 0.0258f,0.02796f, 181.0f, 180.99f, 3.39f, "Venus", 6051.8, 4.8675e24, rootNode, pivots.get(1), 177));
    
    //earth
    planets.add(Planet.newInstance(32,32,4f,assetManager, 6.2832f,0.0172f, 217.0f, 216.97f, 1.85f,"Earth", 	6371, 5.972168e24, rootNode, pivots.get(2), 23));
    //moon
    planets.add(Planet.newInstance(16,16,1f,assetManager, 0.2299f, 0.2299f, 6.0f, 6.0f, 0.00f,"Moon", 1737.4, 7.346e22, rootNode, pivots.get(2), 1));
    planets.get(3).getSpatial().setLocalTranslation(new Vector3f(6,0,0));

    //mars
    planets.add(Planet.newInstance(32,32,2f,assetManager, 6.1261f, 0.00915f, 275.0f, 273.80f, 1.85f,"Mars", 3389.5, 6.4171e23, rootNode, pivots.get(3), 25));
    //phobos
    planets.add(Planet.newInstance(32,32,0.01f,assetManager, 1794.44f, 19.7047f, 2.22f, 2.22f, 0.00f,"Phobos", 11.08, 1.0659e16, rootNode, pivots.get(3), 1));
    planets.get(5).getSpatial().setLocalTranslation(new Vector3f(2.22f,0,0));
    //deimos
    planets.add(Planet.newInstance(32,32,0.01f,assetManager, 523.60f, 4.9771f, 3.67f, 3.67f, 0.00f, "Deimos", 6.27, 1.4762e15, rootNode, pivots.get(3), 1));
    planets.get(6).getSpatial().setLocalTranslation(new Vector3f(3.67f,0,0));
    
    //jupiter
    planets.add(Planet.newInstance(32,32,40f,assetManager, 15.1750f, 0.00145f, 685.0f, 684.20f, 1.30f, "Jupiter", 69911, 1.8982e27, rootNode, pivots.get(4), 3));
    //io
    planets.add(Planet.newInstance(32,32,1f,assetManager, 3.5512f, 3.5512f, 46.0f, 46.0f, 0.00f,"Io", 1821.6, 8.931938e22, rootNode, pivots.get(4), 1));
    planets.get(8).getSpatial().setLocalTranslation(new Vector3f(46,0,0));
    //europe
    planets.add(Planet.newInstance(32,32,1f,assetManager, 1.7690f, 1.7690f, 52.0f, 52.0f, 0.00f, "Europe", 1560.8, 4.799844e22, rootNode, pivots.get(4), 1));
    planets.get(9).getSpatial().setLocalTranslation(new Vector3f(52,0,0));
    
    //saturn
    planets.add(Planet.newInstance(32,32,38f,assetManager, 14.1445f, 0.000585f, 1195.0f, 1193.09f, 2.49f, "Saturn", 58232, 5.6824e26, rootNode, pivots.get(5), 27));
    //saturn's rings
    Torus ringShape = new Torus(100, 100, 15, 58);
    ringGeom = new Geometry("SaturnRings", ringShape);
    Material ringMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    ringMat.setTexture("ColorMap", assetManager.loadTexture("Textures/Planets/SaturnRing.png"));
    ringMat.getAdditionalRenderState().setBlendMode(com.jme3.material.RenderState.BlendMode.Alpha); 
    ringGeom.setQueueBucket(RenderQueue.Bucket.Transparent);
    ringGeom.setMaterial(ringMat);
    ringGeom.setLocalScale(1, 1, 0.01f);
    ringGeom.rotate(-FastMath.PI/2, 0, -FastMath.DEG_TO_RAD * 27);
    rootNode.attachChild(ringGeom);
    pivots.get(5).attachChild(ringGeom);

    //uranus
    planets.add(Planet.newInstance(32,32,16f,assetManager, 8.7482f, 0.000205f, 2217.0f, 2214.65f, 0.77f, "Uranus", 25362, 8.681e25, rootNode, pivots.get(6), 98));
    //neptune
    planets.add(Planet.newInstance(32,32,15f,assetManager, 9.3693f, 0.000105f, 3337.0f, 	3336.88f, 1.77f, "Neptune", 24622, 1.02409e26, rootNode, pivots.get(7), 30));

    Random random = new Random();
    
    //kuiper belt creation
    for (int i = 0; i < 7000; i++) {

      
      int choice = random.nextInt(3);
      Material mat;
      Spatial asteroid;

      switch (choice) {
          case 0 ->                 {
                  asteroid = assetManager.loadModel("3DModel/Eros.glb");
                  asteroid.setLocalScale(0.002f);
                  mat = new Material(assetManager,"Common/MatDefs/Light/Lighting.j3md");
                  mat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Asteroids/Eros.png"));
                }
          case 1 ->                 {
                  asteroid = assetManager.loadModel("3DModel/Itokawa.glb");
                  asteroid.setLocalScale(0.02f);
                  mat = new Material(assetManager,"Common/MatDefs/Light/Lighting.j3md");
                  mat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Asteroids/Itokawa.jpg"));
                }
          default ->                 {
                  asteroid = assetManager.loadModel("3DModel/Vesta.glb");
                  asteroid.setLocalScale(0.002f);
                  mat = new Material(assetManager,"Common/MatDefs/Light/Lighting.j3md");
                  mat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Asteroids/Vesta.jpg"));
                }
        }

      mat.setBoolean("UseMaterialColors",true);
      mat.setColor("Ambient", ColorRGBA.White);  
      mat.setColor("Diffuse", ColorRGBA.White);
      asteroid.setMaterial(mat);
  
      // Random position in a circular belt
      float angle = FastMath.nextRandomFloat() * FastMath.TWO_PI;
      float radius = 6337f + FastMath.nextRandomFloat() * 2000f;
      float x = FastMath.cos(angle) * radius;
      float z = FastMath.sin(angle) * radius;
      float y = -300 + (FastMath.nextRandomFloat() - 0.5f) * 800f;

      float randomX = FastMath.nextRandomFloat() * FastMath.TWO_PI;
      float randomY = FastMath.nextRandomFloat() * FastMath.TWO_PI;
      float randomZ = FastMath.nextRandomFloat() * FastMath.TWO_PI;
      asteroid.setLocalRotation(new Quaternion().fromAngles(randomX, randomY, randomZ));
  
      asteroid.setLocalTranslation(x, y, z);
      kuiper.attachChild(asteroid);
    }
    rootNode.attachChild(kuiper);

    //creation of the orbits of the planets (mercurry to neptune)
    Orbit(200, 200, 0.5f, 141.5f, 0.978f,assetManager, rootNode, new ColorRGBA(0.4f,0.4f,0.4f,1f), 0);
    Orbit(200, 200, 0.5f, 181, 0.999f, assetManager, rootNode, new ColorRGBA(0.7f,0.4f,0f,1f), 1);
    Orbit(200, 200, 0.5f, 217, 0.999f, assetManager, rootNode, new ColorRGBA(0f,0.4f,1f,1f), 2);
    Orbit(200, 200, 0.5f, 275, 0.995f, assetManager, rootNode, new ColorRGBA(0.8f,0.1f,0f,1f), 4);
    Orbit(200, 200, 0.5f, 685, 0.998f, assetManager, rootNode, ColorRGBA.Brown, 7);
    Orbit(200, 200, 0.5f, 1195, 0.998f, assetManager, rootNode, new ColorRGBA(0.9f,0.6f,0f,1f), 10);
    Orbit(200, 200, 0.5f, 2217, 0.998f, assetManager, rootNode, new ColorRGBA(0f,1f,1f,1f), 11);
    Orbit(200, 200, 0.5f, 3337, 0.999f, assetManager, rootNode, new ColorRGBA(0f,0f,1f,1f), 12);

    //translation of the pivots to their respective distances
    List<Integer> distances = new ArrayList<>(Arrays.asList(141, 181, 217, 275, 685, 1195, 2217, 3337));
    for (int i = 0; i < pivots.size(); i++) {
      Node pivot = pivots.get(i);
      rootNode.attachChild(pivot);
      pivot.setLocalTranslation(new Vector3f(distances.get(i), 0, 0));
    }
    //creations and attachment of the moons orbits on the planets pivots
    pivots.get(2).attachChild(Orbit(200, 200, 0.1f, 6, 1, assetManager, rootNode, new ColorRGBA(0.4f,0.4f,0.4f,1f), 2));
    pivots.get(3).attachChild(Orbit(200, 200, 0.1f,  2.22f, 1, assetManager, rootNode, new ColorRGBA(0.2f,0.2f,0.2f,1f), 4));
    pivots.get(3).attachChild(Orbit(200, 200, 0.1f, 3.67f, 1, assetManager, rootNode, new ColorRGBA(0.3f,0.3f,0.3f,1f), 4));
    pivots.get(4).attachChild(Orbit(200, 200, 0.1f, 46, 1, assetManager, rootNode, new ColorRGBA(0.5f,0.6f,0f,1f), 7));
    pivots.get(4).attachChild(Orbit(200, 200, 0.1f, 52, 1, assetManager, rootNode, new ColorRGBA(0.8f,0.8f,0.8f,1f), 7));

    //light initialization
    PointLight lamp_light = new PointLight();
    lamp_light.setColor(ColorRGBA.White);
    lamp_light.setPosition(new Vector3f(0,0,-300));
    rootNode.addLight(lamp_light);

    //rootNode position
    rootNode.setLocalTranslation(0,0,-300);

  }


//---------------------------------------------------------------------------------------------------------------------------------------------------------------------


  private Geometry Orbit(int circlesamples, int radialsamples, float innerradius, float outradius, float scale, AssetManager assetManager, Node rootNode, ColorRGBA color, int idp){
    /*
     * Create the orbits of the planets:
     * 
     * - circlesamples: number of samples in the circles
     * - radialsamples: number of samples in the radials
     * - innerradius: inner radius of the torus
     * - outradius: outer radius of the torus
     * - scale: scale of the torus
     * - assetManager: asset manager
     * - rootNode: root node
     * - color: color of the torus
     * - idp: id of the planet to follow (if moons, for other set to -1)
     */

    Torus torus = new Torus(circlesamples, radialsamples, innerradius, outradius);
    Geometry geom = new Geometry("WhiteCircle", torus);
    Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat.setColor("Color", color);
    geom.setMaterial(mat);
    geom.setLocalScale(1, scale, 0.01f);
    geom.rotate(-FastMath.PI/2 - (float) Math.toRadians( planets.get(idp).getDegree()), 0, 0);
    rootNode.attachChild(geom);
    if (idp != 3 && idp != 5 && idp != 6 && idp != 8 && idp != 9){
      geom.setLocalTranslation(planets.get(idp).getSpatial().getLocalTranslation());
    }
    return geom;
  }



//---------------------------------------------------------------------------------------------------------------------------------------------------------------------


  @Override
  public void simpleUpdate(float tpf) {
    /* Update of the application:
     * 
     * - buttons for camera movement (up and down)
     * - speed of rotation
     * - rotation of the planets and moons on their own axis and around the sun/planets
     * - camera follow the planet
     * - HUD update (speed, pause, date, flycam, others)
     * - date management
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
    kuiper.rotate(0, (float) (0.00009*tpf), 0);
    for (Planet planet : planets){
      planet.getSpatial().rotate(0, 0, (float) (planet.getRotate()*tpf));
      planet.rotation(tpf);
    }

    //camera follow the planet
    if (followedPosition != null){
      cam.setLocation(followedPosition.add(-followedPosition.x/100 * size*2, size * 2 , -300 - followedPosition.z/100 * size*2));
      cam.lookAt(followedPosition.add(0, 0, -300), Vector3f.UNIT_Y);
      if (followedPlanet == null){
        //HUD update (speed, pause, date, flycam, sun)
        hudText.setText("Speed: " + vr + "x" + "\n" + "Pause: " + pause + "\n" + "Time: " + specificDate + "\nFlycam: off \n --------------------------------------" + "\n" 
        + "Followed planet: Sun \n" + "Size: " + soleilSize + " km" + "\n" + "Weight: " + soleilWeight + " kg");
      }
      else{
        //test if the planet has moons (one, two or none)
        if (planets.indexOf(followedPlanet)!=12){
          if (planets.get(planets.indexOf(followedPlanet)+1).getPivot().getName().equals(planets.get(planets.indexOf(followedPlanet)).getName())){
            Planet moon = planets.get(planets.indexOf(followedPlanet)+1);
            if (planets.get(planets.indexOf(followedPlanet)+2).getPivot().getName().equals(planets.get(planets.indexOf(followedPlanet)).getName())){
              Planet moon2 = planets.get(planets.indexOf(followedPlanet)+2);
              //HUD update (speed, pause, date, flycam,followed planet, first moon, second moon)
              hudText.setText("Speed: " + vr + "x" + "\n" + "Pause: " + pause + "\n" + "Time: " + specificDate + "\nFlycam: off \n --------------------------------------" + "\n" 
              + "Followed planet: " + followedPlanet.getName() + "\n" + "Size: " + followedPlanet.getSize() + " km" + "\n" + "Weight: " + followedPlanet.getWeight() + " kg"
              + "\n--------------------------------------\n" + "First moon: " + moon.getName() + "\n" + "Size: " + moon.getSize() + " km" + "\n" + "Weight: " + moon.getWeight() + " kg"
              + "\n--------------------------------------\n" + "Second moon: " + moon2.getName() + "\n" + "Size: " + moon2.getSize() + " km" + "\n" + "Weight: " + moon2.getWeight() + " kg");
            }
            else{
              //HUD update (speed, pause, date, flycam,followed planet, first moon)
              hudText.setText("Speed: " + vr + "x" + "\n" + "Pause: " + pause + "\n" + "Time: " + specificDate + "\nFlycam: off \n --------------------------------------" + "\n" 
              + "Followed planet: " + followedPlanet.getName() + "\n" + "Size: " + followedPlanet.getSize() + " km" + "\n" + "Weight: " + followedPlanet.getWeight() + " kg"
              + "\n--------------------------------------\n" + "Moon: " + moon.getName() + "\n" + "Size: " + moon.getSize() + " km" + "\n" + "Weight: " + moon.getWeight() + " kg");
            }
          }
          else{
            //HUD update (speed, pause, date, flycam,followed planet)
            hudText.setText("Speed: " + vr + "x" + "\n" + "Pause: " + pause + "\n" + "Time: " + specificDate + "\nFlycam: off \n --------------------------------------" + "\n" 
            + "Followed planet: " + followedPlanet.getName() + "\n" + "Size: " + followedPlanet.getSize() + " km" + "\n" + "Weight: " + followedPlanet.getWeight() + " kg");
          }
        }
        else{
          //HUD update (speed, pause, date, flycam,followed planet)
          hudText.setText("Speed: " + vr + "x" + "\n" + "Pause: " + pause + "\n" + "Time: " + specificDate + "\nFlycam: off \n --------------------------------------" + "\n" 
          + "Followed planet: " + followedPlanet.getName() + "\n" + "Size: " + followedPlanet.getSize() + " km" + "\n" + "Weight: " + followedPlanet.getWeight() + " kg");
        }
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



//---------------------------------------------------------------------------------------------------------------------------------------------------------------------


  @Override
  public void onAction(String name, boolean isPressed, float tpf){
    /*input meaning:
     *
     * z,q,s,d: camera movement
     * arrows or mouse: camera rotation
     * 
     * space bar: camera up
     * shift bar: camera down
     * 
     * numpad 1: speed up
     * numpad 2: speed down
     * numpad 4: forwards
     * numpad 5: backwards
     * return: pause
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
    
    //camera up and down management
    if (name.equals("LowerCam")) {
      lowerCam = isPressed;
          
    }
    if (name.equals("UpperCam")) {
      upperCam = isPressed;
    }

    //speed up, speed down, pause and play management
    if (name.equals("Speed") && !pause) {
      if (Math.abs(vr) < 1){
        vr += 0.1f * timeDirection;
      }
      else{
        vr += 10 * timeDirection;
      }
      
    }
    if (name.equals("Slow") && !pause) {
      if (Math.abs(vr) > 10){
        vr -= 10 * timeDirection;
      }
      if (Math.abs(vr) <= 1 && Math.abs(vr) > 0.1f){
        vr -= 0.1f * timeDirection;
      }
    }
    if (name.equals("Pause") && isPressed) {
      if (pause){
        vr = pastvr;
        pause = false;
        pastvr = 0;
      }
      else{
        pastvr = vr;
        pause = true;
        vr = 0;
      }
    }
    if (name.equals("Forwards") && !pause) {
      if (timeDirection == -1){
        vr = -vr;
        timeDirection = 1;
      }
    }
    if (name.equals("Backwards") && !pause) {
      if (timeDirection == 1){
        vr = -vr;
        timeDirection = -1;
      }
    }

    //camera follow the planet management
    if (name.equals("Sun")) {
      followedPlanet = null;
      followedPosition = soleilGeom.getLocalTranslation();
      size = 250f;
    }
    if (name.equals("Mercury")) {
      followedPlanet = planets.get(0);
      followedPosition = pivots.get(0).getLocalTranslation();
      size = 2f;
    }
    if (name.equals("Venus")) {
      followedPlanet = planets.get(1);
      followedPosition = pivots.get(1).getLocalTranslation();
      size = 4f;
    }
    if (name.equals("Earth")) {
      followedPlanet = planets.get(2);
      followedPosition = pivots.get(2).getLocalTranslation();
      size = 4f;
    }
    if (name.equals("Mars")) {
      followedPlanet = planets.get(4);
      followedPosition = pivots.get(3).getLocalTranslation();
      size = 2f;
    }
    if (name.equals("Jupiter")) {
      followedPlanet = planets.get(7);
      followedPosition = pivots.get(4).getLocalTranslation();
      size = 8;
    }
    if (name.equals("Saturn")) {
      followedPlanet = planets.get(10);
      followedPosition = pivots.get(5).getLocalTranslation();
      size = 6;
    }
    if (name.equals("Uranus")) {
      followedPlanet = planets.get(11);
      followedPosition = pivots.get(6).getLocalTranslation();
      size = 1.5f;
    }
    if (name.equals("Neptune")) {
      followedPlanet = planets.get(12);
      followedPosition = pivots.get(7).getLocalTranslation();
      size = 1;
    }
    if (name.equals("None")) {
      followedPlanet = null;
      followedPosition = null;
      size = 0;
    }
    
  }
  
}