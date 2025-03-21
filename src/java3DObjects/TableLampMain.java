package java3DObjects;

import java.awt.AWTException;

/* Copyright material for students working on assignments */

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GraphicsConfiguration;
import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.awt.Robot;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.geometry.Box;
import org.jogamp.java3d.utils.picking.PickResult;
import org.jogamp.java3d.utils.picking.PickTool;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.java3d.utils.universe.ViewingPlatform;
import org.jogamp.vecmath.*;

import com.jogamp.nativewindow.util.Point;


public class TableLampMain extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private static JFrame frame;
	private static TableLampObj[] TLObjects = new TableLampObj[6];				// TableLamp objects
	private Canvas3D canvas;                             

	
	private static TransformGroup create_TableLamp() {
		
		TransformGroup TableLampTG = new TransformGroup();		  // Initializing sceneTG (TableLampTG)
				
		
		// Create the base of the TableLamp
		TLObjects[0] = new TLBase(); 
		TableLampTG = TLObjects[0].position_Object();             // set TableLampTG to TLBase's objTG   	
						
		// Create the Body of the TableLamp and attach it to TLBase objTG
		TLObjects[1] = new TLBody(); 
		TLObjects[0].add_Child(TLObjects[1].position_Object());
		
		// Create other Parts of the Table Lamp and attach it to TLBodyTG
		TLObjects[2] = new TLOtherP(); 
		TLObjects[1].add_Child(TLObjects[2].position_Object());
		
		// Create the table lamp's Bulb and attach it's objTG to TLothers objTG 
		TLObjects[3] = new TLBulb(); 
		TLObjects[2].add_Child(TLObjects[3].position_Object());
		
		// Create the table lamp's shade and attach it's objTG to TLothers objTG
		TLObjects[4] = new TLShade(); 
		TLObjects[2].add_Child(TLObjects[4].position_Object());
		
		
		TLObjects[5] = new TLShadeFitting(); 
		TLObjects[4].add_Child(TLObjects[5].position_Object());
		
		
		return TableLampTG;
	}
	
	

	/* a function to build the content branch, including the fan and other environmental settings */
	public static BranchGroup create_Scene() {
		BranchGroup sceneBG = new BranchGroup();
		TransformGroup sceneTG = new TransformGroup();
	
		
		// Create the lamp 
		sceneTG.addChild(create_TableLamp());
		// rotate the lamp 	
	    sceneTG.addChild(CommonsLH.rotate_Behavior(10000, sceneTG));

		
		
		// Setting up the light resource, and applying it to the TableLamp sceneBG
		// Add ambient light
        AmbientLight ambientLight = new AmbientLight(new Color3f(1.0f, 1.0f, 1.0f));
        ambientLight.setInfluencingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        sceneBG.addChild(ambientLight);
        // Add directional light
        DirectionalLight directionalLight = new DirectionalLight(
            new Color3f(1.0f, 1.0f, 1.0f), // Light color
            new Vector3f(-1.0f, -1.0f, -1.0f) // Light direction
        );
        directionalLight.setInfluencingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        
         
       
        sceneBG.addChild(directionalLight);    
		sceneBG.addChild(sceneTG);                         // keep the following stationary
		
			
		return sceneBG;
	}

	/* NOTE: Keep the constructor for each of the labs and assignments */
	public TableLampMain(BranchGroup sceneBG) {
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        canvas = new Canvas3D(config);

        SimpleUniverse su = new SimpleUniverse(canvas);    // create a SimpleUniverse
        CommonsLH.define_Viewer(su, new Point3d(0.25d, 0.25d, 10.0d));   // set the viewer's location

        sceneBG.compile();		                           // optimize the BranchGroup
        su.addBranchGraph(sceneBG);                        // attach the scene to SimpleUniverse

        setLayout(new BorderLayout());
        add("Center", canvas);

        frame.setSize(800, 800);                           // set the size of the JFrame
        frame.setVisible(true);
        
    }
	

	public static void main(String[] args) {
		frame = new JFrame("TableLamp");                   
		frame.getContentPane().add(new TableLampMain(create_Scene()));  // start the program
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
    
}
	
	