package com.ospgames.goh.clientjava;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Disk;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;
import org.lwjgl.input.Mouse;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.*;

public class ClientApplication
{
    /** Game title */
    public static final String GAME_TITLE = "Game of Honor";

    private boolean done = false;
    private boolean fullscreen = false;
    private final String windowTitle = GAME_TITLE;
    private boolean f1 = false;

    private float sceneYAngle = 0;             // scene rotation angle around the Y axis
	private float sceneXAngle = 0;             // scene rotation angle around the X axis
	private float sceneDistance = 0;
    private DisplayMode displayMode;

	//*****************************************************************************************************************

    public static void main(String args[]) {
        boolean fullscreen = false;
        if(args.length>0) {
            if(args[0].equalsIgnoreCase("fullscreen")) {
                fullscreen = true;
            }
        }

        ClientApplication app = new ClientApplication();
        app.run(fullscreen);
    }

	//*****************************************************************************************************************
    public void run(boolean fullscreen) {
        this.fullscreen = fullscreen;
        try {
            init();
            while (!done) {
                mainloop();
	            renderScene();
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
			if (sceneXAngle < 0f) sceneXAngle = 359f;

			isRotateKeyPressed = true;

		}

		if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			sceneXAngle += keyboardAcceleration;
			if (sceneXAngle > 360f) sceneXAngle = 1f;
			isRotateKeyPressed = true;
		}

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

		mouseDeltaX = Mouse.getEventDX();
		mouseDeltaY = Mouse.getEventDY();

		if (Mouse.isButtonDown(0)) {
			sceneXAngle -= (float)mouseDeltaY/10f;
			if (sceneXAngle > 360f) sceneXAngle = sceneXAngle % 360;
			if (sceneXAngle < 360f) sceneXAngle = sceneXAngle % 360;

			sceneYAngle += (float)mouseDeltaX/10f;
			if (sceneYAngle > 360f) sceneYAngle = sceneYAngle % 360;
			if (sceneYAngle < 360f) sceneYAngle = sceneYAngle % 360;

			// System.out.println("Delta X:"+mouseDeltaX);
			// System.out.println("Delta Y:"+mouseDeltaY);
			// System.out.println();
		}

		mouseWheelDelta = Mouse.getDWheel();
		sceneDistance -= (float)mouseWheelDelta/50;
		if (sceneDistance > 400f) sceneDistance = 400f;
		if (sceneDistance < -150f) sceneDistance = -150f;

		if (sceneDistance >= 0) fogStartAdjust = sceneDistance; else fogStartAdjust = 0;
		GL11.glFogf(GL11.GL_FOG_START, fogStartAdjust);      // adjust fog start depth
		GL11.glFogf(GL11.GL_FOG_END, 125f+sceneDistance);   // adjust fog end depth

		// System.out.println("Scene Distance:"+mouseWheelDelta);

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
            if (d[i].getWidth() == 1280
                && d[i].getHeight() == 1024
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

        initGL();

        initWorld();
    }

	//*****************************************************************************************************************
    private Sphere mSphere;
	private int numberOfStars = 200;
	private float starPositions[][]= new float[numberOfStars][3];
	private float starColors[][]= new float[numberOfStars][3];

    private void initWorld() {
	    int i;
	    Random generator = new Random(System.currentTimeMillis());
	    mSphere = new Sphere();
	    mSphere.setNormals(GL11.GL_SMOOTH);
        //mSphere.setTextureFlag(true);

	  	for (i=0;i<numberOfStars;i++){
			starPositions[i][0] = generator.nextFloat()*100f-50f; //x positions
			starPositions[i][1] = generator.nextFloat()*100f-50f; //y positions
			starPositions[i][2] = -generator.nextFloat()*100f+5; //z positions

			starColors[i][0] = generator.nextFloat();
			starColors[i][1] = generator.nextFloat();
			starColors[i][2] = generator.nextFloat();
		}
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
		// GL11.glEnable(GL11.);
		GL11.glDepthFunc(GL11.GL_LEQUAL); // The Type Of Depth Testing To Do

		//fog
        GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_LINEAR);                  // Fog Mode
        temp.asFloatBuffer().put(fogColor).flip();
        GL11.glFog(GL11.GL_FOG_COLOR, temp.asFloatBuffer());                // Set Fog Color
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
    private void renderScene() {
		int i;

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);          // Clear The Screen And The Depth Buffer

		GL11.glLoadIdentity();                          // Reset The Current Modelview Matrix
        // GL11.glTranslatef(0f,0f,-5.5f);
        // GL11.glColor3f(1f, 0.5f,0f);
        //mSphere.draw(1.5f, 64, 32);
		
		for (i=0;i<numberOfStars;i++){
            GL11.glLoadIdentity();                          // Reset The Current Modelview Matrix
			GL11.glTranslatef(0f, 0f, -52.5f-sceneDistance);
			GL11.glRotatef(sceneYAngle,0.0f,1.0f,0.0f);
			GL11.glRotatef(sceneXAngle,1.0f,0.0f,0.0f);		
			GL11.glTranslatef(0f, 0f, 52.5f);
            GL11.glTranslatef(starPositions[i][0], starPositions[i][1], starPositions[i][2]);
            GL11.glColor3f(starColors[i][0], starColors[i][1], starColors[i][2]);

            mSphere.draw(0.5f, 20, 10);
		}

		// System.out.println("Scene Y angle: "+sceneYAngle);
    }

	//*****************************************************************************************************************
	private static void cleanup() {
        Display.destroy();
    }
}

