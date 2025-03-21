package java3DObjects;

import java.io.FileNotFoundException;

import org.jogamp.java3d.*;
import org.jogamp.java3d.loaders.*;
import org.jogamp.java3d.loaders.objectfile.ObjectFile;
import org.jogamp.java3d.utils.geometry.Box;
import org.jogamp.java3d.utils.image.TextureLoader;
import org.jogamp.vecmath.*;
import org.jogamp.java3d.utils.geometry.Primitive;

public abstract class TableLampObj {
	private Alpha rotationAlpha;                           
	protected BranchGroup objBG;                           // load external object to 'objBG'
	protected TransformGroup objTG;                        // use 'objTG' to position an object
	protected TransformGroup objRG;                        // use 'objRG' to rotate an object
	protected double scale;                                // use 'scale' to define scaling
	protected Vector3f post;                               // use 'post' to specify location
	protected Shape3D obj_shape, obj_shape2 , obj_shape3, obj_shape4, obj_shape5;	// NEW: Added obj_shape2...5, for the FanBlades and the front side of the cylinder connecting them
	
	
	public abstract TransformGroup position_Object();      // need to be defined in derived classes
	public abstract void add_Child(TransformGroup nextTG);
	
	public Alpha get_Alpha() { return rotationAlpha; };    // NOTE: keep for future use 
	
	// NEW: Added to set up the value of Alpha------------
	public void setAlpha(Alpha newAlpha) {				   
		rotationAlpha = newAlpha ;	
	}
	//---------------------------------------------------

	/* a function to load and return object shape from the file named 'obj_name' */
	protected Scene loadShape(String obj_name) {
		ObjectFile f = new ObjectFile(ObjectFile.RESIZE, (float) (60 * Math.PI / 180.0));
		Scene s = null;
		try {                                              // load object's definition file to 's'
			s = f.load("objects/" + obj_name + ".obj");
		} catch (FileNotFoundException e) {
			System.err.println(e);
			System.exit(1);
		} catch (ParsingErrorException e) {
			System.err.println(e);
			System.exit(1);
		} catch (IncorrectFormatException e) {
			System.err.println(e);
			System.exit(1);
		}
		return s;                                          // return the object shape in 's'
	}
	
	/* function to set 'objTG' and attach object after loading the model from external file */
	protected void transform_Object(String obj_name) {
		Transform3D scaler = new Transform3D();
		scaler.setScale(scale);                           // set scale for the 4x4 matrix
		scaler.setTranslation(post);                      // set translations for the 4x4 matrix
		objTG = new TransformGroup(scaler);               // set the translation BG with the 4x4 matrix
		objBG = loadShape(obj_name).getSceneGroup();      // load external object to 'objBG'
		obj_shape = (Shape3D) objBG.getChild(0);          // Get and cast the object to 'obj_shape'
		obj_shape.setName(obj_name);                      // Use the name to identify the object 
		
	}
		
	protected Appearance app = new Appearance();
	private int shine = 32;                                // specify common values for object's appearance
	protected Color3f[] mtl_clr = {
			new Color3f(1.000000f, 1.000000f, 1.000000f),
			new Color3f(0.772500f, 0.654900f, 0.000000f),	
			new Color3f(0.175000f, 0.175000f, 0.175000f),
			new Color3f(0.000000f, 0.000000f, 0.000000f)
   };
	
	protected void obj_Appearance() {
	    System.out.println("Applying appearance to " + obj_shape.getName());
	    Material mtl = new Material();
	    mtl.setShininess(shine);
	    mtl.setAmbientColor(mtl_clr[0]); // Ambient color
	    mtl.setDiffuseColor(mtl_clr[1]); // Diffuse color
	    mtl.setSpecularColor(mtl_clr[2]); // Specular color
	    mtl.setEmissiveColor(mtl_clr[3]); // Emissive color
	    mtl.setLightingEnable(true); // Enable lighting
	    app.setMaterial(mtl); // Set the material to the appearance
	    obj_shape.setAppearance(app); // Apply the appearance to the shape
	}
	protected static Texture textured_App(String name) {
		String filename = "textures/" + name + ".jpg";       // tell the folder of the image
		TextureLoader loader = new TextureLoader(filename, null);
		ImageComponent2D image = loader.getImage();        // load the image
		if (image == null)
			System.out.println("Cannot load file: " + filename);

		Texture2D texture = new Texture2D(Texture.BASE_LEVEL,
				Texture.RGBA, image.getWidth(), image.getHeight());
		texture.setImage(0, image);                        // set image for the texture

		return texture;
	}
	
}

class BaseShape extends TableLampObj {
	public BaseShape() {
		Transform3D translator = new Transform3D();
		translator.setTranslation(new Vector3d(0.0, -0.54, 0));
		objTG = new TransformGroup(translator);            // down half of the tower and base's heights

		objTG.addChild(create_Object());                   // attach the object to 'objTG'
	}
	
	protected Node create_Object() {
		app = CommonsLH.set_Appearance(CommonsLH.White);   // set the appearance for the base
		app.setTexture(textured_App("MarbleTexture"));     // set texture for the base
		TransparencyAttributes ta =                        // value: FASTEST NICEST SCREEN_DOOR BLENDED NONE
				new TransparencyAttributes(TransparencyAttributes.SCREEN_DOOR, 0.5f);
		app.setTransparencyAttributes(ta);                 // set transparency for the base
		return new Box(0.5f, 0.04f, 0.5f, Box.GENERATE_NORMALS | Box.GENERATE_TEXTURE_COORDS, app);
	}

	public TransformGroup position_Object() {
		objTG.addChild(objBG);                             // attach "BaseShapeA" to 'objTG'
		return objTG;                                      // use 'objTG' to attach "BaseShapeA" to the previous TG
	}

	public void add_Child(TransformGroup nextTG) {
		objTG.addChild(nextTG);                            // attach the next transformGroup to 'objTG'
	}
}
//---------------------------------------------------------------------

//Class responsible for making the TabeLamp's base  
class TLBase extends TableLampObj{
	public TLBase() {
		scale = 0.8d;                                        	// use to scale up/down original size
		post = new Vector3f(0f, -2.5f, 0f);                   // use to move object for positioning
		transform_Object("TLBase");                      	// set transformation to 'objTG' and load object file
		create_Appearance() ;								// Create the Full appearance of the body, including the colors, and texture
	}

	protected void create_Appearance() {
		// Defining colors, and  setting them on the shape
		mtl_clr[0]= new Color3f(0.2f, 0.2f, 0.2f) ;  
		mtl_clr[1] = new Color3f(0.3f, 0.3f, 0.35f); 		// Darker grey diffuse color
		mtl_clr[2] = new Color3f(1.0f, 1.0f, 1.0f) ;
		mtl_clr[3] = new Color3f(0.0f, 0.0f, 0.0f) ; 
		obj_Appearance();									// Applying colors on the object                                 
		
		// Enable texture attributes
        TextureAttributes texAttr = new TextureAttributes();
        texAttr.setTextureMode(TextureAttributes.MODULATE); // MODULATE, REPLACE, BLEND, etc.
        
        // Scale the Texture if needed 
        float scl = 0.6f;
	    Transform3D transMap = new Transform3D();  			// to scale it 
        Vector3d scale = new Vector3d(scl, scl, scl); 
	    transMap.setScale(scale);							// apply scaling
        texAttr.setTextureTransform(transMap);
        
        // Set the Texture to the appearance 
        app.setTextureAttributes(texAttr);
		app.setTexture(textured_App("TLOtherP"));     		
	}
	
	
	public TransformGroup position_Object() {              // attach object BranchGroup "LTBody" to 'objTG'
		objTG.addChild(objBG);                             // position "FanStand" by attaching 'objRG' to 'objTG'		
		return objTG;                                      
	}

	public void add_Child(TransformGroup nextTG) {
		objTG.addChild(nextTG);                            // attach the next transformGroup to 'objTG'
	}
	
}


//Class responsible for making the TabeLamp's Body 
class TLBody extends TableLampObj{
	public TLBody() {
		scale = 1.4d;                                        	// use to scale up/down original size
		post = new Vector3f(0f, 1.55f, 0f);                   	// use to move object for positioning
		transform_Object("TLBody");                      	// set transformation to 'objTG' and load object file
		create_Appearance() ;								// Create the Full appearance of the body, including the colors, and texture
	}

	protected void create_Appearance() {
		// Defining colors, and  setting them on the shape
		mtl_clr[0]= new Color3f(0.2f, 0.2f, 0.2f) ;  
		mtl_clr[1] = new Color3f(0.3f, 0.3f, 0.35f); 		// Darker grey diffuse color
		mtl_clr[2] = new Color3f(1.0f, 1.0f, 1.0f) ;
		mtl_clr[3] = new Color3f(0.0f, 0.0f, 0.0f) ; 
		obj_Appearance();									// Applying colors on the object                                 
		
		// Enable texture attributes
        TextureAttributes texAttr = new TextureAttributes();
        texAttr.setTextureMode(TextureAttributes.MODULATE); // MODULATE, REPLACE, BLEND, etc.
        
        // Scale the Texture if needed 
        float scl = 0.6f;
	    Transform3D transMap = new Transform3D();  			// to scale it 
        Vector3d scale = new Vector3d(scl, scl, scl); 
	    transMap.setScale(scale);							// apply scaling
        texAttr.setTextureTransform(transMap);
        
        // Set the Texture to the appearance 
        app.setTextureAttributes(texAttr);
		app.setTexture(textured_App("TLBody"));     		
	}
	
	
	public TransformGroup position_Object() {              // attach object BranchGroup "LTBody" to 'objTG'
		objTG.addChild(objBG);                             // position "FanStand" by attaching 'objRG' to 'objTG'
		return objTG;                                      
	}

	public void add_Child(TransformGroup nextTG) {
		objTG.addChild(nextTG);                            // attach the next transformGroup to 'objTG'
	}
	
}


//Class responsible for making the TabeLamp's other parts
class TLOtherP extends TableLampObj{
	
	public TLOtherP() {
		scale = 1.5d;                                        // use to scale up/down original size
		post = new Vector3f(-0.01f, 2.5f, 0.25f);                 // use to move object for positioning
		transform_Object("TLOtherP");                      	// set transformation to 'objTG' and load object file
		create_Appearance() ;
	}

	protected void create_Appearance() {
		// Defining colors, and  setting them on the shape
		mtl_clr[0]= new Color3f(0.2f, 0.2f, 0.2f) ;  
		mtl_clr[1] = new Color3f (0.95f, 0.95f, 0.92f) ;
		mtl_clr[2] = new Color3f(1.0f, 1.0f, 1.0f) ;
		mtl_clr[3] = new Color3f(0.0f, 0.0f, 0.0f) ; 
		obj_Appearance();                                  
		
		// Enable texture attributes
        TextureAttributes texAttr = new TextureAttributes();
        texAttr.setTextureMode(TextureAttributes.MODULATE); 	// MODULATE, REPLACE, BLEND, etc.
        app.setTextureAttributes(texAttr);
		app.setTexture(textured_App("TLOtherP"));     			// set texture for the base	
	}
	
	public TransformGroup position_Object() {              		// attach object BranchGroup of "TLOthers" to 'objTG'
		objTG.addChild(objBG);                             
		return objTG;                                      
	}

	public void add_Child(TransformGroup nextTG) {
		objTG.addChild(nextTG);                            		// attach the next transformGroup to 'objTG'
	}
	
}


//Class responsible for making the TabeLamp's Bulb
class TLBulb extends TableLampObj {
    private PointLight bulbLight;  							// Light source for the bulb
	private Switch bulbSwitch; 								// Needed to switch between two Bulbs, to control turning on and off the light

    public TLBulb() {
    	
        scale = 0.3d;                                          // use to scale up/down original size
        post = new Vector3f(0f, 0f, -0.15f);                    // use to move object for positioning
        
        bulbSwitch = new Switch();
    	bulbSwitch.setCapability(Switch.ALLOW_SWITCH_WRITE);   // Allow Switch manipualtion during runtime    	
        transform_Object("TLBulb");                            // set transformation to 'objTG' and load object file
        create_Appearance();                                   // Create the Full appearance of the Bulb (its the colors)
        //bulbSwitch.addChild(obj_shape) ; 
        
        create_Light();                                        // Create and attach the light source to the Bulb objTG
       // bulbSwitch.addChild(obj_shape) ; 
     
		//bulbSwitch.setWhichChild(0);   
       	
		
		 objTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		objTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objTG.setCapability(Node.ENABLE_PICK_REPORTING); // need for mouse picking
	    	
		
		// Attach objSwitch to objTG
		objTG.addChild(bulbSwitch);   
             
        
    }

    protected void create_Appearance() {
        // Defining colors, and setting them on the shape       
        mtl_clr[0] = new Color3f(0.2f, 0.2f, 0.2f);
        mtl_clr[1] = new Color3f(0.3f, 0.3f, 0.35f);        // Darker grey diffuse color
        mtl_clr[2] = new Color3f(1.0f, 1.0f, 1.0f);         // Specular color
        mtl_clr[3] = new Color3f(0.8f, 0.8f, 0.8f);         // Emissive color (makes the bulb appear to glow)
        obj_Appearance();                                   // Applying colors on the object
    }

    private void create_Light() {
        // Create a PointLight to simulate the bulb emitting light
        bulbLight = new PointLight();
        bulbLight.setColor(new Color3f(3.0f, 1.5f, 1.0f)); 			 // White light
        bulbLight.setPosition(new Point3f(post.x, post.y, post.z));  // Position the light at the bulb's location
        bulbLight.setInfluencingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 5.0));  // Set the light's influence bounds
        // Add the light to the bulb's TransformGroup
        objTG.addChild(bulbLight);
    }
    
    /*
    private void createSwitch(String obj_name){	
    	Transform3D scaler = new Transform3D();
		scaler.setScale(scale);                           // set scale for the 4x4 matrix
		scaler.setTranslation(post);                      // set translations for the 4x4 matrix
		objTG = new TransformGroup(scaler);               // set the translation BG with the 4x4 matrix
		objBG = loadShape(obj_name).getSceneGroup();      // load external object to 'objBG'
		obj_shape = (Shape3D) objBG.getChild(0);          // Get and cast the object to 'obj_shape'
		obj_shape.setName(obj_name);                      // Use the name to identify the object 
		bulbSwitch.addChild(obj_shape) ; 
		bulbSwitch.setWhichChild(1);                     // start with green box
				
    }*/

    public TransformGroup position_Object() {
        objTG.addChild(objBG);  // position "TLBulb" by attaching 'objBG' to 'objTG'
        return objTG;
    }

    public void add_Child(TransformGroup nextTG) {
        objTG.addChild(nextTG);  // attach the next transformGroup to 'objTG'
    }
}
 
 
 
//Class responsible for making the TabeLamp's shade  
  class TLShade extends TableLampObj {
	    private PointLight bulbLight;  // Light source inside the lampshade

	    public TLShade() {
	        scale = 1.14d;                                        // Scale the object
	        post = new Vector3f(0f, 0.14f, -0.14f);             // Position the object
	        transform_Object("TLShade");                         // Load and transform the 3D object
	        create_Appearance();                                 // Set up the appearance
	        create_Light();                                      // Add a light source inside the lampshade
	    }

	    protected void create_Appearance() {
	        // Define material colors
	        mtl_clr[0] = new Color3f(0.4f, 0.4f, 0.4f);         
	        mtl_clr[1] = new Color3f(0.8f, 0.8f, 0.8f);         
	        mtl_clr[2] = new Color3f(0.1f, 0.1f, 0.1f);        
	        mtl_clr[3] = new Color3f(0.8f, 0.8f, 0.8f); 
	        obj_Appearance();                                   // Apply the colors

	        // Enable texture attributes
	        TextureAttributes texAttr = new TextureAttributes();
	        texAttr.setTextureMode(TextureAttributes.MODULATE); // Combine texture with material colors

	        // Scale the texture
	        float scl = 0.6f;
	        Transform3D transMap = new Transform3D();           // Create a transformation for scaling
	        Vector3d scale = new Vector3d(scl, scl, scl);      // Define scaling factors
	        transMap.setScale(scale);                           // Apply scaling
	        texAttr.setTextureTransform(transMap);              // Set the texture transformation

	        // Apply the texture to the appearance
	        app.setTextureAttributes(texAttr);
	        app.setTexture(textured_App("TLShade"));           // Load and apply the texture

	        // Add transparency to simulate fabric
	        TransparencyAttributes transparency = new TransparencyAttributes();
	        transparency.setTransparencyMode(TransparencyAttributes.BLENDED); // Smooth transparency
	        transparency.setTransparency(0.4f);                // 40% transparency
	        app.setTransparencyAttributes(transparency);

	        // Enable two-sided lighting
	        PolygonAttributes polyAttrs = new PolygonAttributes();
	        polyAttrs.setCullFace(PolygonAttributes.CULL_NONE); // Disable culling to render both sides
	        app.setPolygonAttributes(polyAttrs);
	    }

	    private void create_Light() {
	        // Create a PointLight to simulate the bulb inside the lampshade
	        bulbLight = new PointLight();
	        bulbLight.setColor(new Color3f(2.0f, 1.0f, 0.9f));  // Warm white light
	        bulbLight.setPosition(new Point3f(0, 0, -0 ));     // Position the light inside the lampshade
	        bulbLight.setInfluencingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));  // Set influence bounds

	        // Add the light to the lampshade's TransformGroup
	        objTG.addChild(bulbLight);
	    }

	    public TransformGroup position_Object() {
	        objTG.addChild(objBG);                             // Attach the object's BranchGroup to its TransformGroup
	        return objTG;                                      // Return the TransformGroup
	    }

	    public void add_Child(TransformGroup nextTG) {
	        objTG.addChild(nextTG);                            // Attach another TransformGroup to the object's TransformGroup
	    }
	}
  
  
// Class responsible for making the TabeLamp shade fitting 
class TLShadeFitting extends TableLampObj{
	
	public TLShadeFitting() {
		scale = 1.04d;                                        // use to scale up/down original size
		post = new Vector3f(0f, -0.01f, 0f);                 // use to move object for positioning
		transform_Object("TLShadeFitting");                      	// set transformation to 'objTG' and load object file
		create_Appearance() ;
	}

	protected void create_Appearance() {
		// Defining colors, and  setting them on the shape
		 mtl_clr[0] = new Color3f(0.4f, 0.4f, 0.4f);         
	        mtl_clr[1] = new Color3f(0.8f, 0.8f, 0.8f);         
	        mtl_clr[2] = new Color3f(0.1f, 0.1f, 0.1f);        
	        mtl_clr[3] = new Color3f(0.8f, 0.8f, 0.8f);
		obj_Appearance();                                  
		
		// Enable texture attributes
        TextureAttributes texAttr = new TextureAttributes();
        texAttr.setTextureMode(TextureAttributes.MODULATE); 	// MODULATE, REPLACE, BLEND, etc.
        app.setTextureAttributes(texAttr);
        
     // Add transparency to simulate fabric
        TransparencyAttributes transparency = new TransparencyAttributes();
        transparency.setTransparencyMode(TransparencyAttributes.BLENDED); // Smooth transparency
        transparency.setTransparency(0.1f);                // 40% transparency
        app.setTransparencyAttributes(transparency);

            
     // Enable two-sided lighting
        PolygonAttributes polyAttrs = new PolygonAttributes();
        polyAttrs.setCullFace(PolygonAttributes.CULL_NONE); // Disable culling to render both sides
        
        
		app.setTexture(textured_App("TLShadeFitting"));     			// set texture for the base	
	}
	
	public TransformGroup position_Object() {              		// attach object BranchGroup of "TLOthers" to 'objTG'
		objTG.addChild(objBG);                             
		return objTG;                                      
	}

	public void add_Child(TransformGroup nextTG) {
		objTG.addChild(nextTG);                            		// attach the next transformGroup to 'objTG'
	}
	
	
}

 
  
  
  
  
  