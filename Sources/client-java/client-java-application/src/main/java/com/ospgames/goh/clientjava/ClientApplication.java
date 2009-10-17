package com.ospgames.goh.clientjava;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.glu.Disk;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class ClientApplication
{
    /** Game title */
    public static final String GAME_TITLE = "Game of Honor";

        private boolean done = false;
    private boolean fullscreen = false;
    private final String windowTitle = "NeHe's OpenGL Lesson 5 for LWJGL (3D Shapes)";
    private boolean f1 = false;

    private float rtri;                 // Angle For The Triangle ( NEW )
    private float rquad;                // Angle For The Quad     ( NEW )
    private DisplayMode displayMode;

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
    private void mainloop() {
        if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {       // Exit if Escape is pressed
            done = true;
        }
        if(Display.isCloseRequested()) {                     // Exit if window is closed
            done = true;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_F1) && !f1) {    // Is F1 Being Pressed?
            f1 = true;                                      // Tell Program F1 Is Being Held
            switchMode();                                   // Toggle Fullscreen / Windowed Mode
        }
        if(!Keyboard.isKeyDown(Keyboard.KEY_F1)) {          // Is F1 Being Pressed?
            f1 = false;
        }
    }

    private void switchMode() {
        fullscreen = !fullscreen;
        try {
            Display.setFullscreen(fullscreen);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private boolean render() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);          // Clear The Screen And The Depth Buffer

        GL11.glLoadIdentity();                          // Reset The Current Modelview Matrix

        GL11.glTranslatef(-1.5f,0.0f,-6.0f);                // Move Left 1.5 Units And Into The Screen 6.0
        GL11.glRotatef(rtri,0.0f,1.0f,0.0f);                // Rotate The Triangle On The Y axis ( NEW )
        GL11.glBegin(GL11.GL_TRIANGLES);                    // Drawing Using Triangles
            GL11.glColor3f(1.0f,0.0f,0.0f);             // Red
            GL11.glVertex3f( 0.0f, 1.0f, 0.0f);         // Top Of Triangle (Front)
            GL11.glColor3f(0.0f,1.0f,0.0f);             // Green
            GL11.glVertex3f(-1.0f,-1.0f, 1.0f);         // Left Of Triangle (Front)
            GL11.glColor3f(0.0f,0.0f,1.0f);             // Blue
            GL11.glVertex3f( 1.0f,-1.0f, 1.0f);         // Right Of Triangle (Front)
            GL11.glColor3f(1.0f,0.0f,0.0f);             // Red
            GL11.glVertex3f( 0.0f, 1.0f, 0.0f);         // Top Of Triangle (Right)
            GL11.glColor3f(0.0f,0.0f,1.0f);             // Blue
            GL11.glVertex3f( 1.0f,-1.0f, 1.0f);         // Left Of Triangle (Right)
            GL11.glColor3f(0.0f,1.0f,0.0f);             // Green
            GL11.glVertex3f( 1.0f,-1.0f, -1.0f);            // Right Of Triangle (Right)
            GL11.glColor3f(1.0f,0.0f,0.0f);             // Red
            GL11.glVertex3f( 0.0f, 1.0f, 0.0f);         // Top Of Triangle (Back)
            GL11.glColor3f(0.0f,1.0f,0.0f);             // Green
            GL11.glVertex3f( 1.0f,-1.0f, -1.0f);            // Left Of Triangle (Back)
            GL11.glColor3f(0.0f,0.0f,1.0f);             // Blue
            GL11.glVertex3f(-1.0f,-1.0f, -1.0f);            // Right Of Triangle (Back)
            GL11.glColor3f(1.0f,0.0f,0.0f);             // Red
            GL11.glVertex3f( 0.0f, 1.0f, 0.0f);         // Top Of Triangle (Left)
            GL11.glColor3f(0.0f,0.0f,1.0f);             // Blue
            GL11.glVertex3f(-1.0f,-1.0f,-1.0f);         // Left Of Triangle (Left)
            GL11.glColor3f(0.0f,1.0f,0.0f);             // Green
            GL11.glVertex3f(-1.0f,-1.0f, 1.0f);         // Right Of Triangle (Left)
        GL11.glEnd();                                       // Finished Drawing The Triangle

        GL11.glLoadIdentity();                          // Reset The Current Modelview Matrix
        GL11.glTranslatef(1.5f,0.0f,-7.0f);             // Move Right 1.5 Units And Into The Screen 6.0
        GL11.glRotatef(rquad,1.0f,1.0f,1.0f);               // Rotate The Quad On The X axis ( NEW )
        GL11.glColor3f(0.5f,0.5f,1.0f);                 // Set The Color To Blue One Time Only
        GL11.glBegin(GL11.GL_QUADS);                        // Draw A Quad
            GL11.glColor3f(0.0f,1.0f,0.0f);             // Set The Color To Green
            GL11.glVertex3f( 1.0f, 1.0f,-1.0f);         // Top Right Of The Quad (Top)
            GL11.glVertex3f(-1.0f, 1.0f,-1.0f);         // Top Left Of The Quad (Top)
            GL11.glVertex3f(-1.0f, 1.0f, 1.0f);         // Bottom Left Of The Quad (Top)
            GL11.glVertex3f( 1.0f, 1.0f, 1.0f);         // Bottom Right Of The Quad (Top)
            GL11.glColor3f(1.0f,0.5f,0.0f);             // Set The Color To Orange
            GL11.glVertex3f( 1.0f,-1.0f, 1.0f);         // Top Right Of The Quad (Bottom)
            GL11.glVertex3f(-1.0f,-1.0f, 1.0f);         // Top Left Of The Quad (Bottom)
            GL11.glVertex3f(-1.0f,-1.0f,-1.0f);         // Bottom Left Of The Quad (Bottom)
            GL11.glVertex3f( 1.0f,-1.0f,-1.0f);         // Bottom Right Of The Quad (Bottom)
            GL11.glColor3f(1.0f,0.0f,0.0f);             // Set The Color To Red
            GL11.glVertex3f( 1.0f, 1.0f, 1.0f);         // Top Right Of The Quad (Front)
            GL11.glVertex3f(-1.0f, 1.0f, 1.0f);         // Top Left Of The Quad (Front)
            GL11.glVertex3f(-1.0f,-1.0f, 1.0f);         // Bottom Left Of The Quad (Front)
            GL11.glVertex3f( 1.0f,-1.0f, 1.0f);         // Bottom Right Of The Quad (Front)
            GL11.glColor3f(1.0f,1.0f,0.0f);             // Set The Color To Yellow
            GL11.glVertex3f( 1.0f,-1.0f,-1.0f);         // Bottom Left Of The Quad (Back)
            GL11.glVertex3f(-1.0f,-1.0f,-1.0f);         // Bottom Right Of The Quad (Back)
            GL11.glVertex3f(-1.0f, 1.0f,-1.0f);         // Top Right Of The Quad (Back)
            GL11.glVertex3f( 1.0f, 1.0f,-1.0f);         // Top Left Of The Quad (Back)
            GL11.glColor3f(0.0f,0.0f,1.0f);             // Set The Color To Blue
            GL11.glVertex3f(-1.0f, 1.0f, 1.0f);         // Top Right Of The Quad (Left)
            GL11.glVertex3f(-1.0f, 1.0f,-1.0f);         // Top Left Of The Quad (Left)
            GL11.glVertex3f(-1.0f,-1.0f,-1.0f);         // Bottom Left Of The Quad (Left)
            GL11.glVertex3f(-1.0f,-1.0f, 1.0f);         // Bottom Right Of The Quad (Left)
            GL11.glColor3f(1.0f,0.0f,1.0f);             // Set The Color To Violet
            GL11.glVertex3f( 1.0f, 1.0f,-1.0f);         // Top Right Of The Quad (Right)
            GL11.glVertex3f( 1.0f, 1.0f, 1.0f);         // Top Left Of The Quad (Right)
            GL11.glVertex3f( 1.0f,-1.0f, 1.0f);         // Bottom Left Of The Quad (Right)
            GL11.glVertex3f( 1.0f,-1.0f,-1.0f);         // Bottom Right Of The Quad (Right)
        GL11.glEnd();                                       // Done Drawing The Quad

        rtri+=0.2f;                                     // Increase The Rotation Variable For The Triangle ( NEW )
        rquad-=0.15f;                                   // Decrease The Rotation Variable For The Quad     ( NEW )
        return true;
    }
    private void createWindow() throws Exception {
        Display.setFullscreen(fullscreen);
        DisplayMode d[] = Display.getAvailableDisplayModes();
        for (int i = 0; i < d.length; i++) {
            if (d[i].getWidth() == 640
                && d[i].getHeight() == 480
                && d[i].getBitsPerPixel() == 32) {
                displayMode = d[i];
                break;
            }
        }
        Display.setDisplayMode(displayMode);
        Display.setTitle(windowTitle);
        Display.create();
    }
    private void init() throws Exception {
        createWindow();

        initGL();

        initWorld();
    }

    private Sphere mSphere = new Sphere();


    private void initWorld() {
        mSphere = new Sphere();
        mSphere.setNormals(GL11.GL_SMOOTH);
        //mSphere.setTextureFlag(true);
    }

    private void initGL() {
        GL11.glEnable(GL11.GL_TEXTURE_2D); // Enable Texture Mapping
        GL11.glShadeModel(GL11.GL_SMOOTH); // Enable Smooth Shading
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // Black Background
        GL11.glClearDepth(1.0); // Depth Buffer Setup
        GL11.glEnable(GL11.GL_DEPTH_TEST); // Enables Depth Testing
        GL11.glDepthFunc(GL11.GL_LEQUAL); // The Type Of Depth Testing To Do


        initLights();

        GL11.glMatrixMode(GL11.GL_PROJECTION); // Select The Projection Matrix
        GL11.glLoadIdentity(); // Reset The Projection Matrix

        // Calculate The Aspect Ratio Of The Window
        GLU.gluPerspective(
          45.0f,
          (float) displayMode.getWidth() / (float) displayMode.getHeight(),
          0.1f,
          100.0f);
        GL11.glMatrixMode(GL11.GL_MODELVIEW); // Select The Modelview Matrix

        // Really Nice Perspective Calculations
        GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
    }
    private static void cleanup() {
        Display.destroy();
    }

    private float mZ = 0;

    public static final float[] LIGHT_AMBIENT = new float[] { 0.7f, 0.7f, 0.7f, 1.0f };
    public static final float[] LIGHT_DIF = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
    public static final float[] LIGHT_POS = new float[] { 4.0f, 4.0f, 6.0f, 1.0f };
    ByteBuffer lightBuffer;

    private void initLights() {
        lightBuffer = ByteBuffer.allocateDirect(16);
        lightBuffer.order(ByteOrder.nativeOrder());
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, (FloatBuffer)lightBuffer.asFloatBuffer().put(LIGHT_AMBIENT).flip());              // Setup The Ambient Light
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, (FloatBuffer)lightBuffer.asFloatBuffer().put(LIGHT_DIF).flip());              // Setup The Diffuse Light
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, (FloatBuffer)lightBuffer.asFloatBuffer().put(LIGHT_POS).flip());         // Position The Light

        GL11.glEnable(GL11.GL_LIGHT0);                                // Enable Light 0
        GL11.glEnable(GL11.GL_LIGHTING);                              // Enable Lighting
    }

    private void renderScene() {

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);          // Clear The Screen And The Depth Buffer

        GL11.glLoadIdentity();                          // Reset The Current Modelview Matrix

        GL11.glTranslatef(0,0,-5.5f );
        GL11.glColor3f(1.0f, 1.0f, 0.0f);
        mSphere.draw(0.35f, 32, 16);

    }

}
