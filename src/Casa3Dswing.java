import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.glu.GLU;


/**
 * @author
 * 
 */
public class Casa3Dswing implements GLEventListener, KeyListener { 

	private float zoom = 1.0f;
	private ArrayList<ArrayList<float[]>> faces;
	private HashMap<Integer, float[]> vertices;
	private ArrayList<float[]> texturesVt;
	private float[] minVertices;
	private float[] maxVertices;
	private boolean textureShown;

	GLU glu = new GLU();

	public void init(GLAutoDrawable gLDrawable) {
		glDraw = gLDrawable;
		ObjectLoader ol = new ObjectLoader(new File("objects/avatar_head.obj"));
		faces = ol.getFaces();
		vertices = ol.getVertices();
		minVertices = ol.getMinVertices();
		maxVertices = ol.getMaxVertices();
		texturesVt = ol.getTexturesVt();

		GL gl = gLDrawable.getGL();
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
		glDraw.addKeyListener(this);
	}

	public void display(GLAutoDrawable gLDrawable) {
		final GL gl = gLDrawable.getGL();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		int height = gLDrawable.getHeight();
		int width = gLDrawable.getWidth();
		float aspect = (float)width / (float)height;

		float px = (minVertices[0] + maxVertices[0]) / 2.0f;
		float py = (minVertices[1] + maxVertices[1]) / 2.0f;
		float pz = (minVertices[2] + maxVertices[2]) / 2.0f;
		float r = (float)Math.sqrt(Math.pow(maxVertices[0]-px,2)+
				Math.pow(maxVertices[1]-py,2)+Math.pow(maxVertices[2]-pz,2));               
		float fDistance = (float)(r/Math.tan(Math.PI/5));

		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();


		// ortogonal
		if(tabOp == 0) {
			// frente
			if (width <= height)	       
				gl.glOrtho(-r*zoom, r*zoom, -r/aspect*zoom, r/aspect*zoom,
						-r, r);
			else
				gl.glOrtho(-r*aspect*zoom, r*aspect*zoom, -r*zoom, r*zoom,
						-r, r);

			// planta
			if(pla.isSelected()) {
				gl.glRotatef(90, 1.0f, 0.0f, 0.0f);
			}
			// esquerda
			else if (esq.isSelected()) {
				gl.glRotatef(90, 0.0f, 1.0f, 0.0f);
			}
			// direita
			else if (dir.isSelected()) {
				gl.glRotatef(-90, 0.0f, 1.0f, 0.0f);
			}

		} else if(tabOp == 1) {	         
			//obliqua

			if (width <= height)
				gl.glOrtho(-r*zoom, r*zoom, -r/aspect*zoom, r/aspect*zoom,
						-r, r);
			else
				gl.glOrtho(-r*aspect*zoom, r*aspect*zoom, -r*zoom, r*zoom,
						-r, r);


			double l = Double.parseDouble(lTF.getText());
			double alpha = Math.toRadians(Integer.parseInt(alphaTF.getText()));

			double[] m = {  1,0,-l*Math.cos(alpha),0,
					0,1,-l*Math.sin(alpha),0,
					0,0,1,0,
					0,0,0,1    };
			gl.glMultTransposeMatrixd(m, 0);

		} else if(tabOp == 2) {

			// axonometrica
			if (width <= height)
				gl.glOrtho(-r*zoom, r*zoom, -r/aspect*zoom, r/aspect*zoom,
						-r, r);
			else
				gl.glOrtho(-r*aspect*zoom, r*aspect*zoom, -r*zoom, r*zoom,
						-r, r);

			double a = Math.toRadians(Double.parseDouble(aTF.getText())+
					Double.parseDouble(aMTF.getText())/60);
			double b = Math.toRadians(Double.parseDouble(bTF.getText())+
					Double.parseDouble(bMTF.getText())/60);

			double theta = Math.toDegrees((Math.atan(Math.sqrt(
					Math.tan(a)/Math.tan(b)))-Math.PI/2));
			double gamma = Math.toDegrees((Math.asin(Math.sqrt(
					Math.tan(a)*Math.tan(b)))));


			gl.glRotated(gamma, 1.0f, 0.0f, 0.0f);
			gl.glRotated(theta, 0.0f, 1.0f, 0.0f);

		} else if(tabOp == 3) {
			// perspectiva

			double zNear = fDistance - r;
			double zFar = (fDistance + r)*5;

			double left = -r/5;
			double right = r/5;
			double bottom = -r/5;
			double top = r/5;
			
			if(width<=height) {
				bottom /= aspect;
				top /= aspect;
			}
			else {
				left *= aspect;
				right *= aspect;
			}
			
			gl.glFrustum(left, right, bottom, top, zNear, zFar);

		}

		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();


		
		if(tabOp == 3) {
			
			
			double eyeXD = Double.parseDouble(eyeXTF.getText());
			double eyeYD = Double.parseDouble(eyeYTF.getText());
			double eyeZD = Double.parseDouble(eyeZTF.getText());
		
			glu.gluLookAt(0.0f+eyeXD, 0.0f+eyeYD, (fDistance*1.5)*zoom+eyeZD,
					0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
			
		}

		gl.glColor3f(0.5f, 0.5f, 0.5f);
		// wireframe
		//gl.glPolygonMode(GL.GL_FRONT_AND_BACK,GL.GL_LINE);
		// fill
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK,GL.GL_FILL);


		// center the model
		gl.glTranslatef(-px, -py, -pz);


		if(!textureShown) {

			File textureFile = new File("textures/leath01.png");
			BufferedImage img = null;

			try {
				img = ImageIO.read(textureFile);
			} catch (IOException e) {
				e.printStackTrace();
			}   
			WritableRaster raster = img.getRaster();   
			int widthR = raster.getWidth();   
			int heightR = raster.getHeight();   
			DataBuffer buf = raster.getDataBuffer();

			switch( buf.getDataType() ) {   
			case DataBuffer.TYPE_BYTE:
				DataBufferByte bb = (DataBufferByte) buf;   
				byte im[] = bb.getData();
				int[] textureId = new int[1];
				gl.glActiveTexture(1);
				gl.glGenTextures( 1, textureId, 0 );
				gl.glBindTexture( GL.GL_TEXTURE_2D, textureId[0] );
				gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE,
						GL.GL_DECAL);
				gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S,
						GL.GL_REPEAT);
				gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T,
						GL.GL_REPEAT);
				gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
						GL.GL_LINEAR);
				gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
						GL.GL_LINEAR);
				gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB, widthR, heightR,    
						0, GL.GL_BGR, GL.GL_UNSIGNED_BYTE, ByteBuffer.wrap(im));
				gl.glEnable(GL.GL_TEXTURE_2D);

				break;
			case DataBuffer.TYPE_UNDEFINED:
				break;
			}

			textureShown=true;
		}

		Iterator<float[]> it;
		float[] pts = null;
		float[] ptsT = null;
		Iterator<ArrayList<float[]>> it1 = faces.iterator();
		while(it1.hasNext()) {
			it = it1.next().iterator();
			gl.glBegin(GL.GL_POLYGON);
			while(it.hasNext()) {
				float[] el = it.next();
				if(el[1]!=0) {    // se textura aplicavel
					ptsT = texturesVt.get((int)el[1]-1);
					gl.glTexCoord2d(ptsT[0],ptsT[1]);
				}
				pts = vertices.get((int)el[0]-1);
				gl.glVertex3f(pts[0],pts[1],pts[2]);
			}
			gl.glEnd(); 
		}
		gl.glFlush();
	}

	public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width,
			int height) {
		final GL gl = gLDrawable.getGL();

		gl.glViewport(0, 0, width, height);


	}

	public void displayChanged(GLAutoDrawable gLDrawable, boolean modeChanged,
			boolean deviceChanged) {}

	public static void reDesenhar() {
		glDraw.display();
	}

	public static JPanel criarPainel() {

		class RadioButtonListener implements ActionListener {		
			public void actionPerformed(ActionEvent event) {
				reDesenhar();
			}
		}

		class SliderListener implements ChangeListener {
			public void stateChanged(ChangeEvent event) {
				JSlider source = (JSlider) event.getSource();

				
					if(source==lSlider) {
						if (!source.getValueIsAdjusting())
							lTF.setValue((double)source.getValue()/100);
						else
							lTF.setText("" + (double)source.getValue()/100);
					}
					else if(source==alphaSlider) {
						if (!source.getValueIsAdjusting())
							alphaTF.setValue(source.getValue());
						else
							alphaTF.setText("" + source.getValue());
					}
					else if(source==aSlider) {
						if (!source.getValueIsAdjusting()) 
							aTF.setValue(source.getValue());
						else
							aTF.setText("" + source.getValue());
					}
					else if(source==bSlider) {
						if (!source.getValueIsAdjusting())
							bTF.setValue(source.getValue());
						else
							bTF.setText("" + source.getValue());
					}
					else if(source==aMSlider) {
						if (!source.getValueIsAdjusting())
							aMTF.setValue(source.getValue());
						else
							aMTF.setText("" + source.getValue());
					}
					else if(source==bMSlider) {
						if (!source.getValueIsAdjusting())
							bMTF.setValue(source.getValue());
						else
							bMTF.setText("" + source.getValue());
					}
					else if(source==eyeXSlider) {
						if (!source.getValueIsAdjusting())
							eyeXTF.setValue((double)source.getValue()/100);
						else
							eyeXTF.setText("" + (double)source.getValue()/100);
					}
					else if(source==eyeYSlider) {
						if (!source.getValueIsAdjusting())
							eyeYTF.setValue((double)source.getValue()/100);
						else
							eyeYTF.setText("" + (double)source.getValue()/100);
					}
					else if(source==eyeZSlider) {
						if (!source.getValueIsAdjusting())
							eyeZTF.setValue((double)source.getValue());
						else
							eyeZTF.setText("" + (double)source.getValue()/100);
					}

					reDesenhar();
			}
		};

		class TextListener implements PropertyChangeListener {	
			public void propertyChange(PropertyChangeEvent e) {
				if ("value".equals(e.getPropertyName())) {
					JFormattedTextField source =
							(JFormattedTextField) e.getSource();
					Object nValue = e.getNewValue();
					int value = 0;
					if(nValue instanceof String)
						value = Integer.parseInt((String)nValue);
					else if(nValue instanceof Integer)
						value = (Integer)nValue;
					else if(nValue instanceof Double)
						value = (int) ((Double)nValue*100);

					if(source==lTF)
						lSlider.setValue(value);
					else if(source==alphaTF)
						alphaSlider.setValue(value);
					else if(source==aTF)
						aSlider.setValue(value);
					else if(source==bTF)
						bSlider.setValue(value);
					else if(source==aMTF)
						aMSlider.setValue(value);
					else if(source==bMTF)
						bMSlider.setValue(value);
					else if(source==eyeXTF)
						eyeXSlider.setValue(value);
					else if(source==eyeYTF)
						eyeYSlider.setValue(value);
					else if(source==eyeZTF)
						eyeZSlider.setValue(value);

					reDesenhar();
				}
			}
		};

		RadioButtonListener radioListener = new RadioButtonListener();

		SliderListener lListener = new SliderListener();
		SliderListener alphaListener = new SliderListener();
		SliderListener aListener = new SliderListener();
		SliderListener aMListener = new SliderListener();
		SliderListener bListener = new SliderListener();
		SliderListener bMListener = new SliderListener();
		SliderListener eyeXListener = new SliderListener();
		SliderListener eyeYListener = new SliderListener();
		SliderListener eyeZListener = new SliderListener();

		TextListener lTFListener = new TextListener();
		TextListener alphaTFListener = new TextListener();
		TextListener aTFListener = new TextListener();
		TextListener aMTFListener = new TextListener();
		TextListener bTFListener = new TextListener();
		TextListener bMTFListener = new TextListener();
		TextListener eyeXTFListener = new TextListener();
		TextListener eyeYTFListener = new TextListener();
		TextListener eyeZTFListener = new TextListener();


		/*
		 * Paineis
		 */
		painel = new JPanel();
		tabs = new JTabbedPane();

		tabOrto = new JPanel();
		tabOblq = new JPanel();
		tabAxon = new JPanel();
		tabPers = new JPanel();


		/*
		 * Ortogonal tab
		 */
		pri = new JRadioButton("Alçado Principal");
		dir = new JRadioButton("Alç. Lat. Direito");
		esq = new JRadioButton("Alç. Lat. Esquerdo");
		pla = new JRadioButton("Planta");

		ButtonGroup ortGroup = new ButtonGroup();
		ortGroup.add(pri);
		ortGroup.add(dir);
		ortGroup.add(esq);
		ortGroup.add(pla);
		pri.setSelected(true); //estado default

		pri.addActionListener(radioListener);
		dir.addActionListener(radioListener);
		esq.addActionListener(radioListener);
		pla.addActionListener(radioListener);

		tabOrto.add(pri);
		tabOrto.add(dir);
		tabOrto.add(esq);
		tabOrto.add(pla);  



		/*
		 * Oblíqua tab
		 */
		Hashtable<Integer, JLabel> axonLabelTable =
				new Hashtable<Integer, JLabel>();
		axonLabelTable.put(new Integer(0), new JLabel("0"));
		axonLabelTable.put(new Integer(25), new JLabel("0.25"));
		axonLabelTable.put(new Integer(50), new JLabel("0.5"));
		axonLabelTable.put(new Integer(75), new JLabel("0.75"));
		axonLabelTable.put(new Integer(100), new JLabel("1"));


		JLabel alphaLabel = new JLabel("\u03B1", JLabel.CENTER);
		alphaLabel.setFont(new Font(alphaLabel.getName(), Font.PLAIN, 20));

		JLabel lLabel = new JLabel("\u2113", JLabel.CENTER);
		lLabel.setFont(new Font(lLabel.getName(), Font.PLAIN, 20));


		alphaSlider = new JSlider(JSlider.HORIZONTAL, 0, 90, 45);
		alphaSlider.setPaintTicks(true);
		alphaSlider.setPaintLabels(true);
		alphaSlider.setMajorTickSpacing(10);
		alphaSlider.setMinorTickSpacing(5);
		alphaSlider.add(alphaLabel);
		alphaSlider.addChangeListener(alphaListener);

		lSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);  
		lSlider.setLabelTable(axonLabelTable);
		lSlider.setPaintTicks(true);
		lSlider.setPaintLabels(true);
		lSlider.setMajorTickSpacing(10);
		lSlider.setMinorTickSpacing(5);
		lSlider.add(lLabel);
		lSlider.addChangeListener(lListener);

		alphaTF = new JFormattedTextField("45");
		alphaTF.setColumns(2);
		alphaTF.addPropertyChangeListener(alphaTFListener);

		lTF = new JFormattedTextField("0.5");
		lTF.setColumns(3);
		lTF.addPropertyChangeListener(lTFListener);


		tabOblq.setLayout(new FlowLayout(FlowLayout.CENTER));
		tabOblq.add(alphaLabel);
		tabOblq.add(alphaSlider);
		tabOblq.add(alphaTF);
		tabOblq.add(Box.createRigidArea(new Dimension(10,0)));
		tabOblq.add(lLabel);
		tabOblq.add(lSlider);
		tabOblq.add(lTF);


		/*
		 * Axonométrica tab
		 */
		JLabel aLabel = new JLabel("A (°)(')", JLabel.CENTER);
		aLabel.setFont(new Font(aLabel.getName(), Font.PLAIN, 20));

		JLabel bLabel = new JLabel("B (°)(')", JLabel.CENTER);
		bLabel.setFont(new Font(bLabel.getName(), Font.PLAIN, 20));

		aSlider = new JSlider(JSlider.HORIZONTAL, 0, 90, 24);
		aSlider.setPaintTicks(true);
		aSlider.setPaintLabels(true);
		aSlider.setMajorTickSpacing(10);
		aSlider.setMinorTickSpacing(5);
		aSlider.add(aLabel);
		aSlider.addChangeListener(aListener);
		aMSlider = new JSlider(JSlider.HORIZONTAL, 0, 59, 46);
		aMSlider.setPaintTicks(true);
		aMSlider.setPaintLabels(true);
		aMSlider.setMajorTickSpacing(10);
		aMSlider.setMinorTickSpacing(5);
		aMSlider.addChangeListener(aMListener);

		bSlider = new JSlider(JSlider.HORIZONTAL, 0, 90, 17);  
		bSlider.setPaintTicks(true);
		bSlider.setPaintLabels(true);
		bSlider.setMajorTickSpacing(10);
		bSlider.setMinorTickSpacing(5);
		bSlider.add(bLabel);
		bSlider.addChangeListener(bListener);
		bMSlider = new JSlider(JSlider.HORIZONTAL, 0, 59, 0);  
		bMSlider.setPaintTicks(true);
		bMSlider.setPaintLabels(true);
		bMSlider.setMajorTickSpacing(10);
		bMSlider.setMinorTickSpacing(5);
		bMSlider.addChangeListener(bMListener);

		aTF = new JFormattedTextField("24");
		aTF.setColumns(2);
		aTF.addPropertyChangeListener(aTFListener);
		aMTF = new JFormattedTextField("46");
		aMTF.setColumns(2);
		aMTF.addPropertyChangeListener(aMTFListener);

		bTF = new JFormattedTextField("17");
		bTF.addPropertyChangeListener(bTFListener);
		bTF.setColumns(2);
		bMTF = new JFormattedTextField("0");
		bMTF.setColumns(2);
		bMTF.addPropertyChangeListener(bMTFListener);

		tabAxon.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		tabAxon.add(aLabel);
		tabAxon.add(aSlider);
		tabAxon.add(aTF);
		tabAxon.add(aMSlider);
		tabAxon.add(aMTF);
		c.gridx = 0;
		c.gridy = 1;
		tabAxon.add(bLabel, c);
		c.gridx++;
		tabAxon.add(bSlider, c);
		c.gridx++;
		tabAxon.add(bTF, c);
		c.gridx++;
		tabAxon.add(bMSlider, c);
		c.gridx++;
		tabAxon.add(bMTF, c);

		
		
		/*
		 * Perspectiva tab
		 */

		// eyeX eyeY eyeZ centerX centerY centerZ upX upY upZ
		
		Hashtable<Integer, JLabel> persLabelTable =
				new Hashtable<Integer, JLabel>();
		persLabelTable.put(new Integer(-100), new JLabel("-1"));
		//persLabelTable.put(new Integer(-75), new JLabel("-0.75"));
		persLabelTable.put(new Integer(-50), new JLabel("-0.5"));
		//persLabelTable.put(new Integer(-25), new JLabel("-0.25"));
		persLabelTable.put(new Integer(0), new JLabel("0"));
		//persLabelTable.put(new Integer(25), new JLabel("0.25"));
		persLabelTable.put(new Integer(50), new JLabel("0.5"));
		//persLabelTable.put(new Integer(75), new JLabel("0.75"));
		persLabelTable.put(new Integer(100), new JLabel("1"));

		
		JLabel eyeLabel = new JLabel("eye x y z", JLabel.CENTER);
		aLabel.setFont(new Font(aLabel.getName(), Font.PLAIN, 20));

		JLabel centerLabel = new JLabel("center x y z", JLabel.CENTER);
		bLabel.setFont(new Font(bLabel.getName(), Font.PLAIN, 20));
		
		eyeXSlider = new JSlider(JSlider.HORIZONTAL, -100, 100, 0);
		eyeXSlider.setLabelTable(persLabelTable);
		eyeXSlider.setPaintTicks(true);
		eyeXSlider.setPaintLabels(true);
		eyeXSlider.setMajorTickSpacing(20);
		eyeXSlider.setMinorTickSpacing(10);
		eyeXSlider.addChangeListener(eyeXListener);

		eyeYSlider = new JSlider(JSlider.HORIZONTAL, -100, 100, 0);
		eyeYSlider.setLabelTable(persLabelTable);
		eyeYSlider.setPaintTicks(true);
		eyeYSlider.setPaintLabels(true);
		eyeYSlider.setMajorTickSpacing(20);
		eyeYSlider.setMinorTickSpacing(10);
		eyeYSlider.addChangeListener(eyeYListener);

		eyeZSlider = new JSlider(JSlider.HORIZONTAL, -100, 100, 0);
		eyeZSlider.setLabelTable(persLabelTable);
		eyeZSlider.setPaintTicks(true);
		eyeZSlider.setPaintLabels(true);
		eyeZSlider.setMajorTickSpacing(20);
		eyeZSlider.setMinorTickSpacing(10);
		eyeZSlider.addChangeListener(eyeZListener);
		
		eyeXTF = new JFormattedTextField("0");
		eyeXTF.setColumns(2);
		eyeXTF.addPropertyChangeListener(eyeXTFListener);
		eyeYTF = new JFormattedTextField("0");
		eyeYTF.setColumns(2);
		eyeYTF.addPropertyChangeListener(eyeYTFListener);
		eyeZTF = new JFormattedTextField("0");
		eyeZTF.setColumns(2);
		eyeZTF.addPropertyChangeListener(eyeZTFListener);
		
		
		
		
		tabPers.setLayout(new GridBagLayout());
		GridBagConstraints cP = new GridBagConstraints();
		tabPers.add(eyeLabel);
		tabPers.add(eyeXSlider);
		tabPers.add(eyeXTF);
		tabPers.add(eyeYSlider);
		tabPers.add(eyeYTF);
		tabPers.add(eyeZSlider);
		tabPers.add(eyeZTF);
		/*c.gridx = 0;
		c.gridy = 1;
		tabAxon.add(bLabel, c);
		c.gridx++;
		tabAxon.add(bSlider, c);
		c.gridx++;
		tabAxon.add(bTF, c);
		c.gridx++;
		tabAxon.add(bMSlider, c);
		c.gridx++;
		tabAxon.add(bMTF, c);*/
		
		

		painel.setLayout(new GridLayout(1, 4));
		tabs.addTab("Ortogonal", tabOrto);
		tabs.addTab("Obliqua", tabOblq);
		tabs.addTab("Axonometrica", tabAxon);
		tabs.addTab("Perspectiva", tabPers);

		painel.add(tabs);
		return painel;
	}


	public void keyTyped(KeyEvent e) {
		if (e.getKeyChar() == '\u001B' ||  // escape
				e.getKeyChar() == 'Q' ||
				e.getKeyChar() == 'q') {   // quit
			System.exit(0);
		}
	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {

		}
		else if(e.getKeyCode() == KeyEvent.VK_RIGHT) { }
		else if(e.getKeyCode() == KeyEvent.VK_UP) {
			zoom -= 0.01f;        
		}
		else if(e.getKeyCode() == KeyEvent.VK_RIGHT) { }
		else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
			zoom += 0.01f;
		}
		else if(e.getKeyCode() == KeyEvent.VK_ADD) {
			zoom -= 0.01f;
		}
		else if(e.getKeyCode() == KeyEvent.VK_SUBTRACT) {
			zoom += 0.01f;
		}
		else if(e.getKeyCode() == KeyEvent.VK_NUMPAD0) {
			zoom = 1.0f;
		}
		
		reDesenhar();

	}  

	public void keyReleased(KeyEvent e) {}


	public static void main(String[] args) {
		JFrame frame = new JFrame("Projections");
		GLCanvas canvas = new GLCanvas();
		canvas.addGLEventListener(new Casa3Dswing());
		canvas.setSize(400, 280);
		frame.add(canvas, BorderLayout.CENTER);
		JPanel painel = criarPainel();
		frame.add(painel, BorderLayout.SOUTH);
		frame.pack();
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		canvas.requestFocusInWindow();
		frame.setVisible(true);

		tabs.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {

				System.out.println(tabs.getSelectedIndex());

				if(tabs.getSelectedIndex() == 0)
					tabOp = 0;
				else if(tabs.getSelectedIndex() == 1)
					tabOp = 1;
				else if(tabs.getSelectedIndex() == 2)
					tabOp = 2;
				else if(tabs.getSelectedIndex() == 3)
					tabOp = 3;

				reDesenhar();
			}
		}
				);
	}

	private static GLAutoDrawable glDraw;

	private static JPanel painel;
	private static JTabbedPane tabs;
	private static JPanel tabOrto;
	private static JPanel tabOblq;
	private static JPanel tabAxon;
	private static JPanel tabPers;
	private static JRadioButton pri;
	private static JRadioButton dir;
	private static JRadioButton pla;
	private static JRadioButton esq;
	private static JSlider alphaSlider;
	private static JSlider lSlider;
	private static JSlider aSlider;
	private static JSlider aMSlider;
	private static JSlider bSlider;
	private static JSlider bMSlider;
	private static JSlider eyeXSlider;
	private static JSlider eyeYSlider;
	private static JSlider eyeZSlider;
	private static JFormattedTextField alphaTF;
	private static JFormattedTextField lTF;
	private static JFormattedTextField aTF;
	private static JFormattedTextField aMTF;
	private static JFormattedTextField bTF;
	private static JFormattedTextField bMTF;
	private static JFormattedTextField eyeXTF;
	private static JFormattedTextField eyeYTF;
	private static JFormattedTextField eyeZTF;

	private static int tabOp = 0;

}