package com.ospgames.goh.clientjava;

import com.ospgames.goh.space.Star;
import com.ospgames.goh.space.StarCluster;
import com.ospgames.goh.space.StarType;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

import javax.vecmath.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Random;


public class ClientApplication extends IceClient
{
    /** Game title */
    public static final String GAME_TITLE = "Game of Honor";
    public static final String PARAM_FULLSCREEN = "fullscreen";
    public static final String PARAM_USEICE = "useice";

    private boolean done = false;
    private boolean iceAvailable = false;
    private boolean fullscreen = false;
    private final String windowTitle = GAME_TITLE;
    private boolean f1 = false;

	private int windowWidth = 1024;
	private int windowHeight = 768;

    private float sceneYAngle = 0;             // scene rotation angle around the Y axis
	private float sceneXAngle = 0;             // scene rotation angle around the X axis
	private float sceneDistance = 0;
    private DisplayMode displayMode;

    //*****************************************************************************************************************
    public static void main(String args[]) {
        ClientApplication app = new ClientApplication();

        if (hasParameter(PARAM_USEICE, args)) {
            // super class expects call to main to initialize ice.
            System.exit(app.main(GAME_TITLE,args));
        }
        else {
            app.run(hasParameter(PARAM_FULLSCREEN,args));
        }
    }

    public static boolean hasParameter(String key, String[] args) {
        for (String arg : args) {
            if (arg.equalsIgnoreCase(key)) {
                return true;
            }
        }
        return false;
    }
    
    //*****************************************************************************************************************

    /**
     * This method is called by super class when ice is initialized.
     * @param args  command line parameter excluding the ice parameters.
     * @return int that indicates normal 0 or error each other.
     */
    @Override
    public int runWithinIceContainer(String[] args) {
        iceAvailable = true;
        run(hasParameter(PARAM_FULLSCREEN, args));
        return 0;
    }

	//*****************************************************************************************************************
    public void run(boolean fullscreen) {
        this.fullscreen = fullscreen;
        try {
            init();
            while (!done) {
                mainloop();
	            renderScene();
	            // DrawEntities();
                Display.update();
            }
            cleanup();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

	//*****************************************************************************************************************

	// two variables needed to make the key's effect a smooth acceleration and deceleration
	private static float keyboardAcceleration = 0.01f;
	private boolean isRotateKeyPressed = false;
	private boolean isLeftMouseKeyPressed = false;
	private boolean isRightMouseKeyPressed = false;

	private void mainloop() {

		// mouse movement variables
		int mouseDeltaX;
		int mouseDeltaY;
		int mouseWheelDelta;

		float fogStartAdjust = 0;

        // *** escape key
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {       // Exit if Escape is pressed
            done = true;
        }
        if(Display.isCloseRequested()) {                     // Exit if window is closed
            done = true;
        }

		// *** toggle fullscreen/windowed mode
		if(Keyboard.isKeyDown(Keyboard.KEY_F1) && !f1) {    // Is F1 Being Pressed?
            f1 = true;                                      // Tell Program F1 Is Being Held
            switchMode();                                   // Toggle Fullscreen / Windowed Mode
        }
        if(!Keyboard.isKeyDown(Keyboard.KEY_F1)) {          // Is F1 Being Pressed?
            f1 = false;
        }

		// *** rotate star field keys
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			sceneYAngle -= keyboardAcceleration;
			if (sceneYAngle < 0f) sceneYAngle = 359f;

			isRotateKeyPressed = true;

		}

		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			sceneYAngle += keyboardAcceleration;
			if (sceneYAngle > 360f) sceneYAngle = 1f;

			isRotateKeyPressed = true;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			sceneXAngle -= keyboardAcceleration;
			if (sceneXAngle < -90) sceneXAngle = -90f;

			isRotateKeyPressed = true;

		}

		if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			sceneXAngle += keyboardAcceleration;
			if (sceneXAngle > 90f) sceneXAngle = 90f;
			isRotateKeyPressed = true;
		}

		// softly start and stop the rotation by keyboard
		if (isRotateKeyPressed){
			keyboardAcceleration *= 1.15f;
			if (keyboardAcceleration > 1) keyboardAcceleration = 1f;
		}
		else
		{
			keyboardAcceleration /= 1.25f;
			if (keyboardAcceleration < 0.01f) keyboardAcceleration = 0.01f;

		}

		isRotateKeyPressed = false;

		// *** rotate starfield if mouse is dragged
		Mouse.next();

		mouseDeltaX = Mouse.getDX();
		mouseDeltaY = Mouse.getDY();

		// System.out.println("Delta X:"+mouseDeltaX);
		// System.out.println("Delta Y:"+mouseDeltaY);
		// System.out.println();

		if (Mouse.isButtonDown(1)) {
			sceneXAngle -= (float)mouseDeltaY/10f;
			if (sceneXAngle > 90f) sceneXAngle = 90;
			if (sceneXAngle < -90f) sceneXAngle = -90;

			sceneYAngle += (float)mouseDeltaX/10f;
			if (sceneYAngle > 360f) sceneYAngle = sceneYAngle - 360;
			if (sceneYAngle < 0f) sceneYAngle = sceneYAngle + 360;
		}

		if (Mouse.isButtonDown(0)) {
			if (!isLeftMouseKeyPressed){
				selection(Mouse.getX(), Mouse.getY());
				isLeftMouseKeyPressed=true;
			}
		}
		else
		{
			isLeftMouseKeyPressed=false;
		}

		mouseWheelDelta = Mouse.getDWheel();
		sceneDistance -= (float)mouseWheelDelta/50;
		if (sceneDistance > 400f) sceneDistance = 400f;
		if (sceneDistance < -150f) sceneDistance = -150f;

		if (sceneDistance >= 0) fogStartAdjust = sceneDistance; else fogStartAdjust = 0;
		GL11.glFogf(GL11.GL_FOG_START, fogStartAdjust);      // adjust fog start depth
		GL11.glFogf(GL11.GL_FOG_END, 125f+sceneDistance);    // adjust fog end depth
    }

	//*****************************************************************************************************************
    private void switchMode() {
        fullscreen = !fullscreen;
        try {
            Display.setFullscreen(fullscreen);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

	//*****************************************************************************************************************
    private void createWindow() throws Exception {
        Display.setFullscreen(fullscreen);
        DisplayMode d[] = Display.getAvailableDisplayModes();
        for (int i = 0; i < d.length; i++) {
            if (d[i].getWidth() == windowWidth
                && d[i].getHeight() == windowHeight
                && d[i].getBitsPerPixel() == 32) {
                displayMode = d[i];
                break;
            }
        }
        Display.setDisplayMode(displayMode);
        Display.setTitle(windowTitle);
        Display.create();

		Mouse.create();
		Mouse.setGrabbed(false);
    }

	//*****************************************************************************************************************
    private void init() throws Exception {
        createWindow();
        if (iceAvailable)
            loadWorld();
        else
            createRandomWorld();

        initGL();
        initWorld();
    }

    private void loadWorld() {
        StarCluster cluster = getLobbyServicePrx().getCluster();

        // we could use stars and wormholes to draw but for now i just
        // convert them to the existing format
        numberOfStars = cluster.stars.size();
        starPositions = new float[numberOfStars][3];
        starColors = new float[numberOfStars][3];
        starsSelected = new int[numberOfStars];

        int i=0;
        for (Star star : cluster.stars) {
            starPositions[i][0]=star.position.x;
            starPositions[i][1]=star.position.y;
            starPositions[i][2]=star.position.z;

            // it might be a good idea to get colors from server
            // so all clients can use the same colors
            starColors[i]=getStarColor(star.type);
            i++;
        }

        // TODO handle wormholes
    }

    private void createRandomWorld() {
        int i;
        Random generator = new Random(System.currentTimeMillis());
        for (i=0;i<numberOfStars;i++){
          starPositions[i][0] = generator.nextFloat()*100f-50f; //x positions
          starPositions[i][1] = generator.nextFloat()*100f-50f; //y positions
          starPositions[i][2] = -generator.nextFloat()*100f+5; //z positions

          starColors[i][0] = generator.nextFloat();
          starColors[i][1] = generator.nextFloat();
          starColors[i][2] = generator.nextFloat();
      }
    }


    public static final HashMap<String,float[]> STAR_COLORS ;
    public static final float[] STARTYPE_COLOR_OTHER = { 155.0f, 155.0f, 155f};
    static  {
        // color codes from http://de.wikipedia.org/wiki/Spektralklasse
        STAR_COLORS = new HashMap<String,float[]>();
        STAR_COLORS.put("O",new float[] { 155f, 176f, 255f});
        STAR_COLORS.put("B",new float[] { 170f, 191f, 255f});
        STAR_COLORS.put("A",new float[] { 228f, 232f, 252f});
        STAR_COLORS.put("F",new float[] { 249f, 250f, 231f});
        STAR_COLORS.put("G",new float[] { 253f, 249f, 179f});
        STAR_COLORS.put("K",new float[] { 255f, 216f, 112f});
        STAR_COLORS.put("M",new float[] { 251f, 200f, 134f});

        // normalize colors to the color range used by OpenGL 0..1
        for (float[] colors : STAR_COLORS.values()) {
            colors[0] = colors[0]/255f;
            colors[1] = colors[1]/255f;
            colors[2] = colors[2]/255f;
        }
    }

    private float[] getStarColor(StarType type) {

        float[] result=null;
        if (type != null && STAR_COLORS.containsKey(type.name)) {
            result = STAR_COLORS.get(type.name);
        }

        if (result == null ) {
            result = STARTYPE_COLOR_OTHER;
        }
        return result;
    }

    //*****************************************************************************************************************


    private int numberOfStars = 100;
	private float starPositions[][]= new float[numberOfStars][3];
	private float starColors[][]= new float[numberOfStars][3];
	private int starsSelected[] = new int[numberOfStars];

	private int starSphere;
	private int billboard;

    private void initWorld() {
	    int i;
	    Sphere mSphere;

		//generate spheres as stars

	    mSphere = new Sphere();
	    mSphere.setNormals(GL11.GL_SMOOTH);
        //mSphere.setTextureFlag(true);
        // create display list for the star sphere
	    starSphere = GL11.glGenLists(1);
	    GL11.glNewList(starSphere,GL11.GL_COMPILE);
	    mSphere.draw(0.5f, 20, 10);
	    GL11.glEndList();

	    // create display list for the name and status billboard
	    billboard = GL11.glGenLists(1);
	    GL11.glNewList(billboard,GL11.GL_COMPILE);
	        GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_LINE);
	        GL11.glLineWidth(2f);
	        GL11.glTranslatef(1.0f, 0.0f, 0.0f);
			GL11.glBegin(GL11.GL_LINE_LOOP);
	            GL11.glVertex3f(2.0f, 0.5f,  0.0f);
	            GL11.glVertex3f(0.0f, 0.5f,  0.0f);
				GL11.glVertex3f(0.0f, -0.5f, 0.0f);
				GL11.glVertex3f( 2.0f, -0.5f, 0.0f);
			GL11.glEnd();
	    GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);	        
	    GL11.glEndList();
    }



    //*****************************************************************************************************************
    private void initGL() {

        float fogColor[] = {0.5f, 0.5f, 0.5f, 1.0f};        // Fog Color

		ByteBuffer temp = ByteBuffer.allocateDirect(16);

        GL11.glEnable(GL11.GL_TEXTURE_2D); // Enable Texture Mapping
        GL11.glShadeModel(GL11.GL_SMOOTH); // Enable Smooth Shading
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // Black Background
        GL11.glClearDepth(1.0); // Depth Buffer Setup
        GL11.glEnable(GL11.GL_DEPTH_TEST); // Enables Depth Testing
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glDepthFunc(GL11.GL_LEQUAL); // The Type Of Depth Testing To Do

		//fog
        GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_LINEAR);                     // Fog Mode
        temp.asFloatBuffer().put(fogColor).flip();
        GL11.glFog(GL11.GL_FOG_COLOR, temp.asFloatBuffer());               // Set Fog Color
        GL11.glFogf(GL11.GL_FOG_DENSITY, 0.5f);                            // How Dense Will The Fog Be
        GL11.glHint(GL11.GL_FOG_HINT, GL11.GL_DONT_CARE);                   // Fog Hint Value
        GL11.glFogf(GL11.GL_FOG_START, 1.0f);                               // Fog Start Depth
        GL11.glFogf(GL11.GL_FOG_END, 125f);                                 // Fog End Depth
        GL11.glEnable(GL11.GL_FOG);                                         // Enables GL_FOG

        initLights();

        GL11.glMatrixMode(GL11.GL_PROJECTION); // Select The Projection Matrix
        GL11.glLoadIdentity(); // Reset The Projection Matrix

        // Calculate The Aspect Ratio Of The Window
        GLU.gluPerspective(
          45.0f,
          (float) displayMode.getWidth() / (float) displayMode.getHeight(),
          0.1f,
          500.0f);

        GL11.glMatrixMode(GL11.GL_MODELVIEW); // Select The Modelview Matrix

        // Really Nice Perspective Calculations
        GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);

		// Set viewport size
		GL11.glViewport(0, 0, windowWidth, windowHeight);
    }

	//*****************************************************************************************************************
    private float mZ = 0;

    public static final float[] LIGHT_AMBIENT = new float[] { 0.0f, 0.0f, 0.0f, 1.0f };
    public static final float[] LIGHT_DIF = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
    public static final float[] LIGHT_POS = new float[] { 10.0f, 10.0f, 6.0f, 1.0f };
    ByteBuffer lightBuffer;

    private void initLights() {
        lightBuffer = ByteBuffer.allocateDirect(16);
        lightBuffer.order(ByteOrder.nativeOrder());
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, (FloatBuffer)lightBuffer.asFloatBuffer().put(LIGHT_AMBIENT).flip());              // Setup The Ambient Light
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, (FloatBuffer)lightBuffer.asFloatBuffer().put(LIGHT_DIF).flip());              // Setup The Diffuse Light
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, (FloatBuffer)lightBuffer.asFloatBuffer().put(LIGHT_POS).flip());         // Position The Light

        GL11.glEnable(GL11.GL_LIGHT0);         // Enable Light 0
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL); // Enable Material Lighting
    }

	//*****************************************************************************************************************
	// The selection buffer
	private void selection(int mouse_x, int mouse_y) {
		IntBuffer selBuffer = ByteBuffer.allocateDirect(1024).order(ByteOrder.nativeOrder()).asIntBuffer();
		int buffer[] = new int[256];

		IntBuffer vpBuffer = ByteBuffer.allocateDirect(64).order(ByteOrder.nativeOrder()).asIntBuffer();

		// The size of the viewport. [0] Is <x>, [1] Is <y>, [2] Is <width>, [3] Is <height>
		int[] viewport = new int[4];

		// The number of "hits" (objects within the pick area)
		int hits;

// System.out.println("Selection");

		// Get the viewport info
		GL11.glGetInteger(GL11.GL_VIEWPORT, vpBuffer);
		vpBuffer.get(viewport);

		// Set the buffer that OpenGL uses for selection to our buffer
		GL11.glSelectBuffer(selBuffer);

		// Change to selection mode
		GL11.glRenderMode(GL11.GL_SELECT);

		// Initialize the name stack (used for identifying which object was selected)
		GL11.glInitNames();
		GL11.glPushName(0);

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();

		/*  create 5x5 pixel picking region near cursor location */
		GLU.gluPickMatrix( (float) mouse_x, (float) mouse_y, 5.0f, 5.0f, IntBuffer.wrap(viewport));

		GLU.gluPerspective(45.0f, (float) (viewport[2] - viewport[0]) / (float) (viewport[3] - viewport[1]), 0.1f, 500.0f);
		renderScene();

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPopMatrix();

		// Exit selection mode and return to render mode, returns number selected
		hits = GL11.glRenderMode(GL11.GL_RENDER);

// System.out.println("Hits: "+hits);

		selBuffer.get(buffer);
		// Objects Were Drawn Where The Mouse Was
		if (hits > 0) {
			// If There Were More Than 0 Hits
			int choose = buffer[3]; // Make Our Selection The First Object
			int depth = buffer[1];  // Store How Far Away It Is
			for (int i = 1; i < hits; i++) {
				// Loop Through All The Detected Hits
				// If This Object Is Closer To Us Than The One We Have Selected
				if (buffer[i * 4 + 1] < (int) depth) {
					choose = buffer[i * 4 + 3]; // Select The Closer Object
					depth = buffer[i * 4 + 1]; // Store How Far Away It Is
				}
			}

//			System.out.println("Chosen Star: "+choose);
			starsSelected[choose]=(starsSelected[choose]==0)?1:0;                            // Toggle The Object As Being Hit by player 1
		}
	}

	//*****************************************************************************************************************
    private void renderScene() {
		int i;

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);          // Clear The Screen And The Depth Buffer

		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		for (i=0; i<numberOfStars; i++){
			drawStar(i);
		}

		// System.out.println("Scene Y angle: "+sceneYAngle);
    }

	//*****************************************************************************************************************
	private void drawStar(int star){
		GL11.glLoadName(star);                                     // Assign Object A Name (ID)

		GL11.glLoadIdentity();                                  // Reset The Current Modelview Matrix
		GL11.glTranslatef(0f, 0f, -52.5f-sceneDistance);
		GL11.glRotatef(sceneYAngle,0.0f,1.0f,0.0f);
		GL11.glRotatef(sceneXAngle,1.0f,0.0f,0.0f);
		GL11.glTranslatef(0f, 0f, 52.5f);
        GL11.glTranslatef(starPositions[star][0], starPositions[star][1], starPositions[star][2]);

		// choose color according to select state
		switch (starsSelected[star]) {
			case 1: {
				GL11.glColor3f(starColors[star][0], starColors[star][1], starColors[star][2]);
				GL11.glPushMatrix();
				GL11.glCallList(starSphere);
				GL11.glPopMatrix();

				drawStarNameAndStatus(star);				
				break;
			}
			case 2: {
				GL11.glColor3f(0.5f, 1f, 0.5f);
				GL11.glCallList(starSphere);
				break;
			}
			default: {
				GL11.glColor3f(starColors[star][0], starColors[star][1], starColors[star][2]);
				GL11.glCallList(starSphere);
			}
		}

	}

	//*****************************************************************************************************************
	private void drawStarNameAndStatus(int star){
		GL11.glPushMatrix();

		GL11.glRotatef(sceneXAngle, -1, 0, 0); // undo perspective X rotation
        GL11.glRotatef(sceneYAngle, 0, -1, 0); // undo perspective Y rotation
        // GL11.glRotatef (rotate_z, 0, 0, -1); // there currently is no z axis rotation

		GL11.glDisable(GL11.GL_LIGHTING); //switch off lighting so the billboard always appears lighted from the front
		GL11.glColor3f(1f, 1f, 1f);

		GL11.glCallList(billboard);

		GL11.glEnable(GL11.GL_LIGHTING);   //re-enable lighting

		GL11.glPopMatrix();
	}

	//*****************************************************************************************************************
	static void billboardSphericalBegin(
				float camX, float camY, float camZ,
				float objPosX, float objPosY, float objPosZ) {

		// This is the original lookAt vector for the object
		// in world coordinates
		javax.vecmath.Vector3f lookAt = new javax.vecmath.Vector3f(0, 0, 1);

		// objToCamProj is the vector in world coordinates from the
		// local origin to the camera projected in the XZ plane
		javax.vecmath.Vector3f objToCamProj = new javax.vecmath.Vector3f(camX-objPosX, 0, camZ-objPosZ);

		// objToCam is the vector in world coordinates from
		// the local origin to the camera
		javax.vecmath.Vector3f objToCam = new javax.vecmath.Vector3f(camX-objPosX, camY-objPosY, camZ-objPosZ);

		javax.vecmath.Vector3f upAux = new Vector3f();
		float modelview[] = new float[16];
		float angleCosine;

		GL11.glPushMatrix();

	// normalize both vectors to get the cosine directly afterwards
		objToCamProj.normalize();

	// easy fix to determine wether the angle is negative or positive
	// for positive angles upAux will be a vector pointing in the
	// positive y direction, otherwise upAux will point downwards
	// effectively reversing the rotation.

		upAux.cross(lookAt,objToCamProj);

	// compute the angle
		angleCosine = lookAt.dot(objToCamProj);

	// perform the rotation. The if statement is used for stability reasons
	// if the lookAt and objToCamProj vectors are too close together then
	// |angleCosine| could be bigger than 1 due to lack of precision
	   if ((angleCosine < 0.99990) && (angleCosine > -0.9999))
		  GL11.glRotatef((float)(Math.acos(angleCosine)*180/3.14), upAux.x, upAux.y, upAux.z);

	// so far it is just like the cylindrical billboard. The code for the
	// second rotation comes now
	// The second part tilts the object so that it faces the camera

	// Normalize to get the cosine afterwards
		objToCam.normalize();

	// Compute the angle between objToCamProj and objToCam,
	//i.e. compute the required angle for the lookup vector

		angleCosine = objToCamProj.dot(objToCam);

	// Tilt the object. The test is done to prevent instability
	// when objToCam and objToCamProj have a very small
	// angle between them

		if ((angleCosine < 0.99990) && (angleCosine > -0.9999))
			if (objToCam.y < 0)
				GL11.glRotatef((float)(Math.acos(angleCosine)*180/3.14),1,0,0);
			else
				GL11.glRotatef((float)(Math.acos(angleCosine)*180/3.14),-1,0,0);
	}

	//*****************************************************************************************************************
	static void billboardEnd() {
		// restore the previously 
		// stored modelview matrix
		GL11.glPopMatrix();
	}

	//*****************************************************************************************************************
	private static void cleanup() {
        Display.destroy();
    }
}
