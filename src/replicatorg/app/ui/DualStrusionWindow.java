package replicatorg.app.ui;
/*
Part of the ReplicatorG project - http://www.replicat.org
Copyright (c) 2008 Zach Smith

Forked from Arduino: http://www.arduino.cc

Based on Processing http://www.processing.org
Copyright (c) 2004-05 Ben Fry and Casey Reas
Copyright (c) 2001-04 Massachusetts Institute of Technology

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software Foundation,
Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

$Id: MainWindow.java 370 2008-01-19 16:37:19Z mellis $
 */
/**
 * @author Noah Levy
 * 
 * <class>DualStrusionWindow</class> is a Swing class designed to integrate DualStrusion into the existing ReplicatorG GUI
 */


import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import replicatorg.app.Base;
import replicatorg.dualstrusion.DualStrusionConstruction;
import replicatorg.model.Build;
import replicatorg.model.BuildCode;
import replicatorg.model.GCodeSource;
import replicatorg.model.StringListSource;
import replicatorg.plugin.toolpath.ToolpathGenerator;
import replicatorg.plugin.toolpath.ToolpathGeneratorFactory;
import replicatorg.plugin.toolpath.ToolpathGeneratorThread;
import replicatorg.plugin.toolpath.ToolpathGenerator.GeneratorListener.Completion;

import net.miginfocom.swing.MigLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
public class DualStrusionWindow extends JFrame implements ActionListener, ItemListener, ToolpathGenerator.GeneratorListener{
	/**
	 * 
	 */
	public static CountDownLatch cdl;
	private static final long serialVersionUID = 2548421042732389328L; //Generated serial
	//File result; // final combined gcode will be saved here
	File primary, secondary, dest, result, primarygcode, secondarygcode;
	JFrame frame = new JFrame("DualStrusion Window");
	volatile short triggerNum;
	private ToolpathGeneratorThread tpgt;
	boolean start2nd = false;
	boolean hasOneGcode; //this boolean is true if the constructor is passed one gcode file to begin with, it later effect the layout of the Swing Window
	boolean repStart, repEnd, uWipe;
	String originalGcodePath;
	/**
	 * 
	 */

	/**
	 * 
	 * This is the default constructor, it is only invoked if the ReplicatorG window did not already have a piece of gcode open
	 */
	public DualStrusionWindow()
	{
		hasOneGcode = false;
	}
	/**
	 * This is a constructor that takes the filepath of the gcode open currently in ReplicatorG
	 * @param s the path of the gcode currently open in RepG
	 */
	public DualStrusionWindow(String s) {
		// TODO Auto-generated constructor stub
		hasOneGcode = true;
		originalGcodePath = s;

	}
	/**
	 * This method creates and shows the DualStrusionWindow GUI, this window is a MigLayout with 3 JFileChooser-TextBox Pairs, the first two being source gcodes and the last being the combined gcode destination.
	 * It also links to online DualStrusion Documentation NOTE: This may be buggy, it uses getDesktop() which is JDK 1.6 and scary.
	 * This method also invokes the thread in which the gcode combining operations run in, I would like to turn this into a SwingWorker soon.
	 */
	public void go()
	{
		cdl = new CountDownLatch(1);
		//JCheckBox useSD = new JCheckBox(false);
		frame.dispose();
		frame.setResizable(true);
		//frame.setContentPane(this);
		frame.setVisible(true);	
		frame.setLocation(400, 0);
		Container cont = this.getContentPane();
		cont.setLayout(new MigLayout("fill"));
		cont.setVisible(true);
		JTextArea explanation = new JTextArea();
		explanation.setText("This window is used to combine two Gcode files generated by SkeinForge. This allows for multiple objects in one print job or multiple materials or colors in one printed object. The resulting gcode assumes that Toolhead1 is on the left and Toolhead0 is on the right");
		explanation.setOpaque(false);
		explanation.setEditable(false);
		explanation.setWrapStyleWord(true);
		explanation.setSize(700, 200);
		explanation.setLineWrap(true);
		//cont.add(explanation, "wrap");
		/*
		final JLabel linkage = new JLabel("<html><u>See documentation</u></html>");
		linkage.setForeground(Color.BLUE);
		linkage.addMouseListener(new MouseListener()
		{

			@Override
			public void mouseClicked(MouseEvent arg0) {

				try {
					Desktop.getDesktop().browse(new URI("http://www.makerbot.com/docs/dualstrusion"));
					linkage.setForeground(Color.MAGENTA);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		cont.add(linkage, "wrap");
		 */
		cont.add(new JLabel("Extruder A (Left)"), "split");//TOOLHEAD 1

		final JTextField Toolhead1 = new JTextField(60);
		Toolhead1.setText("");
		if(hasOneGcode)
		{
			Toolhead1.setText(originalGcodePath);
		}
		JButton Toolhead1ChooserButton = new JButton("Browse...");
		Toolhead1ChooserButton.addActionListener(new ActionListener()
		{


			public void actionPerformed(ActionEvent arg0) {
				String s = null;
				if(!Toolhead1.getText().equals(""))
				{
					s = GcodeSelectWindow.goString(new File(Toolhead1.getText()));	
				}
				else
				{
					s = GcodeSelectWindow.goString();
				}
				if(s != null)
				{
					Toolhead1.setText(s);
				}

			}

		});
		cont.add(Toolhead1,"split");
		cont.add(Toolhead1ChooserButton, "wrap");

		//

		final JTextField Toolhead0 = new JTextField(60);
		Toolhead0.setText("");

		JButton Toolhead0ChooserButton = new JButton("Browse...");
		Toolhead0ChooserButton.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent arg0) {
				String s = null;
				if(!Toolhead0.getText().equals(""))
				{
					s = GcodeSelectWindow.goString(new File(Toolhead0.getText()));	
				}
				else
				{
					s = GcodeSelectWindow.goString();
				}
				if(s != null)
				{
					Toolhead0.setText(s);
				}

			}

		});
		JButton switchItem = new JButton("Switch Toolheads"); //This button switches the contents of the two text fields in order to easily swap Primary and Secondary Toolheads
		switchItem.addActionListener(new ActionListener()
		{



			public void actionPerformed(ActionEvent arg0) {
				String temp = Toolhead1.getText();
				Toolhead1.setText(Toolhead0.getText());
				Toolhead0.setText(temp);

			}
		});
		cont.add(switchItem, "wrap");
		cont.add(new JLabel("Extruder B (Right)"), "split");

		cont.add(Toolhead0,"split");
		cont.add(Toolhead0ChooserButton, "wrap");

		final JTextField DestinationTextField = new JTextField(60);
		DestinationTextField.setText("");

		JButton DestinationChooserButton = new JButton("Browse...");
		DestinationChooserButton.addActionListener(new ActionListener()
		{


			public void actionPerformed(ActionEvent arg0) {
				String s = null;
				if(!DestinationTextField.getText().equals(""))
				{
					s = GcodeSaveWindow.goString(new File(DestinationTextField.getText()));	
				}
				else
				{
					if(!Toolhead1.getText().equals(""))
					{
						int i = Toolhead1.getText().lastIndexOf("/");
						String a = Toolhead1.getText().substring(0, i + 1) + "untitled.gcode";
						File untitled = new File(a);
						System.out.println(a);
						s = GcodeSaveWindow.goString(untitled);
					}	
					else
					{
						s = GcodeSaveWindow.goString();
					}
				}

				if(s != null)
				{
					if(s.contains("."))
					{
						int lastp = s.lastIndexOf(".");
						if(!s.substring(lastp + 1, s.length()).equals("gcode"))
						{
							s = s.substring(0, lastp) + ".gcode";
						}
					}
					else
					{
						s = s + ".gcode";
					}
					DestinationTextField.setText(s);
				}

			}

		});
		cont.add(new JLabel("Combined Gcode: "), "split");
		cont.add(DestinationTextField, "split");
		cont.add(DestinationChooserButton, "wrap");
		
		//Replace Start/End Checkboxes
		final JCheckBox replaceStart = new JCheckBox();
		replaceStart.setSelected(true);
		cont.add(new JLabel("Use default start.gcode: "), "split");
		cont.add(replaceStart,"wrap");
		
		final JCheckBox replaceEnd = new JCheckBox();
		replaceEnd.setSelected(true);
		cont.add(new JLabel("Use default end.gcode: "), "split");
		cont.add(replaceEnd,"wrap");
		
		//Use Wipes	
		final JCheckBox useWipes = new JCheckBox();
		useWipes.setSelected(true);
		cont.add(new JLabel("Use Wipes DANGER!(Don't disable this unless you know what your doing)"), "split");
		cont.add(useWipes, "wrap");
		
		//Merge
		JButton merge = new JButton("Merge");

		merge.addActionListener(new ActionListener()

		{

			public void actionPerformed(ActionEvent arg0) {
				boolean warning = false;
				if(Toolhead1.getText().equals(Toolhead0.getText()))
				{
					//JFrame fr = new JFrame();
					int option = JOptionPane.showConfirmDialog(null, "You are trying to combine two of the same file. Are you sure you want to do this?", "Continue?", 
							JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
					System.out.println(option);
					if(option == 0)
					{
						warning = false;
					}
					else if (option == 1)
					{
						warning = true;	
					}
				}
				if(!warning)
					primary = new File(Toolhead1.getText());
				secondary = new File(Toolhead0.getText());
				dest = new File(DestinationTextField.getText());
				primarygcode = new File(replaceExtension(Toolhead1.getText(), "gcode"));
				secondarygcode = new File(replaceExtension(Toolhead0.getText(), "gcode"));
				repStart = replaceStart.isSelected();
				repEnd = replaceEnd.isSelected();
				uWipe = useWipes.isSelected();

				if(getExtension(primary.getName()).equalsIgnoreCase("stl") || getExtension(secondary.getName()).equalsIgnoreCase("stl"))
				{
					if(getExtension(primary.getName()).equalsIgnoreCase("stl") && getExtension(secondary.getName()).equalsIgnoreCase("stl"))
					{

						startBoth();
					}
					else if(getExtension(primary.getName()).equalsIgnoreCase("stl"))
					{

						startPrimary();
					}
					else if(getExtension(secondary.getName()).equalsIgnoreCase("stl"))
					{

						startSecondary();
					}
				}
				else
				{
					triggerNum = 1;
					finish();
				}




			}

		});
		cont.add(merge, "split");
		final JButton help = new JButton("Help");
		help.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				try {
					Desktop.getDesktop().browse(new URI("http://goo.gl/DV5vn"));
					//That goo.gl redirects to http://www.makerbot.com/docs/dualstrusion I just wanted to build in a convient to track how many press the help button
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		cont.add(help);
		frame.add(cont);
		frame.pack();

	}
	private static String replaceExtension(String s, String newExtension)
	{
		int i = s.lastIndexOf(".");
		s = s.substring(0, i+1);
		s = s + newExtension;
		System.out.println(s);
		return s;
	}
	private synchronized void startBoth()
	{
		ToolpathGenerator generator1 = ToolpathGeneratorFactory.createSelectedGenerator();
		ToolpathGenerator generator2 = ToolpathGeneratorFactory.createSelectedGenerator();
		System.out.println("heere " + generator1.toString());
		try{
			Build p = new Build(primary.getAbsolutePath());
			Build s = new Build(secondary.getAbsolutePath());
			JFrame primaryProgress = new JFrame("Primary Progress");
			JFrame secondaryProgress = new JFrame("Secondary Progress");
			//primaryProgress.setVisible(true);
			//primaryProgress.setLocation(200, 200);
			//secondaryProgress.setLocation(200+primaryProgress.getWidth(), 200+primaryProgress.getHeight());
			//JFrame combinedWindow = new JFrame("Gcode Generator");
			//Container cont = new Container();
			//JTabbedPane jtb = new JTabbedPane();
			//jtb.addTab("Left", primaryProgress);
			//jtb.addTab("Right", secondaryProgress);
			//cont.add(jtb);
			//ombinedWindow.add(jtb);
			//combinedWindow.pack();
			//combinedWindow.setVisible(true);
			ToolpathGeneratorThread tg1 = new ToolpathGeneratorThread(primaryProgress, generator1, p);
			ToolpathGeneratorThread tg2 = new ToolpathGeneratorThread(secondaryProgress, generator2, s);


			tg1.addListener(this);
			tg2.addListener(this);
			tg1.setDualStrusionSupportFlag(true, 200, 300, "Extruder A (Left)"); //This preps all the toolpathGenerator stuff for dualstrusion and makes it deploy swing windows at given coordinates
			tg2.setDualStrusionSupportFlag(true, 650, 300, "Extruder B (Right)");
			//SwingUtilities.invokeLater(tg1);
			//SwingUtilities.invokeLater(tg2);
			tpgt = tg2;
			tg1.start();
			
			triggerNum = 2;

		}
		catch(IOException e)
		{
			System.err.println("cannot read stl");
		} 
	}
	private synchronized void start2ndGeneration()
	{
		System.out.println("Ill never let go jack!");
		ToolpathGenerator generator2 = ToolpathGeneratorFactory.createSelectedGenerator();
		try {
			Build s = new Build(secondary.getAbsolutePath());
		
		JFrame secondaryProgress = new JFrame("Secondary Progress");
		

		ToolpathGeneratorThread tg2 = new ToolpathGeneratorThread(secondaryProgress, generator2, s);
		tg2.addListener(this);
		tg2.setDualStrusionSupportFlag(true, 650, 300, "Extruder B (Right)");
		tg2.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private synchronized void startPrimary()
	{
		ToolpathGenerator generator1 = ToolpathGeneratorFactory.createSelectedGenerator();

		try{
			Build p = new Build(primary.getAbsolutePath());
			JFrame primaryProgress = new JFrame("Primary Progress");
			primaryProgress.setLocation(200, 200);
			ToolpathGeneratorThread tg1 = new ToolpathGeneratorThread(primaryProgress, generator1, p);
			tg1.addListener(this);
			tg1.start();
			triggerNum = 1;

		}
		catch(IOException e)
		{
			System.err.println("cannot read stl");
		} 
	}
	private synchronized void startSecondary()
	{
		ToolpathGenerator generator2 = ToolpathGeneratorFactory.createSelectedGenerator();
		try{
			Build s = new Build(secondary.getAbsolutePath());
			JFrame secondaryProgress = new JFrame("Secondary Progress");
			//primaryProgress.setVisible(true);
			secondaryProgress.setLocation(200, 200);

			ToolpathGeneratorThread tg2 = new ToolpathGeneratorThread(secondaryProgress, generator2, s);
			tg2.addListener(this);
			tg2.start();
			triggerNum = 1;

		}
		catch(IOException e)
		{
			System.err.println("cannot read stl");
		} 
	}
	private synchronized void finish()
	{
		triggerNum--;
		if(triggerNum == 0)
			
		{
			System.out.println("Primary is" + primarygcode.getName() + " secondary is " + secondarygcode.getName());

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(primarygcode.getName() + " and " + secondarygcode.getName());
			DualStrusionConstruction dcs = new DualStrusionConstruction(primarygcode, secondarygcode, dest, repStart, repEnd, uWipe);
			dcs.run();
			result = dcs.getCombinedFile();
			frame.removeAll();
			frame.dispose();
		}
	}

	private static String getExtension(String path)
	{
		int i = path.lastIndexOf(".");
		String ext = path.substring(i+1, path.length());
		return ext;
	}
	/**
	 * This method returns the result of the gcode combining operation.
	 * @return the combined gcode.
	 */

	public File getCombined()
	{
		return result;
	}
	/**
	 * This method is unused and a prime canidate for deletion.
	 * @param savethis
	 */
	private static void saveGCodeSource(GCodeSource savethis)
	{StringListSource slss = (StringListSource) savethis;
	Iterator<String> slit = slss.iterator();
	try {
		FileWriter fwr = new FileWriter(new File("/home/makerbot/Desktop/target.gcode"));
		while(slit.hasNext())
		{
			fwr.write(slit.next());
		}
		fwr.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}	

	}
	public void actionPerformed(ActionEvent arg0) 
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void itemStateChanged(ItemEvent arg0) 
	{
		Object source = arg0.getItemSelectable();

	}
	@Override
	public void generationComplete(Completion completion, Object details) {
		if (completion == Completion.SUCCESS) {
			finish();
		}

	}
	@Override
	public void updateGenerator(String message) {
		if(message.equals("Config Done") && !start2nd)
		{
			start2nd = true;
			System.out.println("Message recieved");
			start2ndGeneration();
			
		}

	}

}
/*
class runConcurrentToolPathGeneratorThreads implements Runnable
{
	ToolpathGeneratorThread tt1, tt2;
	runConcurrentToolPathGeneratorThreads(ToolpathGeneratorThread t1, ToolpathGeneratorThread t2)
	{
		tt1 = t1;
		tt2 = t2;
	}
	@Override
	public void run() {
		//SwingUtilities.invokeLater(tt1);
		//SwingUtilities.invokeLater(tt2);
		tt1.start();
		
		try {
			tt1.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		tt2.start();

		
	}
	
}
*/
/*
class genListener implements ToolpathGenerator.GeneratorListener
{
	boolean finished = false;
	public genListener()
	{

	}
	public boolean getFinished()
	{
		return finished;
	}
	@Override
	public void generationComplete(Completion completion, Object details) {
		if (completion == Completion.SUCCESS) 
		{
			finished = true;
		}


	}
	@Override
	public void updateGenerator(String message) {
		// TODO Auto-generated method stub

	}
}
class checkFinished implements Runnable
{
	genListener gen1;
	genListener gen2;
	public checkFinished(genListener g1, genListener g2)
	{
		gen1 = g1;
		gen2 = g2;
	}
	public void run() 
	{
		while(!(gen1.getFinished() && gen2.getFinished()))
		{
			//wait
		}
		DualStrusionWindow.cdl.countDown();

	}

}

 */