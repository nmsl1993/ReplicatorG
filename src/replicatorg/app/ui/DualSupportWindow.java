package replicatorg.app.ui;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import replicatorg.app.Base;
import replicatorg.dualstrusion.DualStrusionConstruction;
import replicatorg.dualstrusion.DualStrusionWorker;
import replicatorg.dualstrusion.SupportGenerator;
import replicatorg.dualstrusion.Toolheads;
import replicatorg.model.Build;
import replicatorg.plugin.toolpath.ToolpathGenerator;
import replicatorg.plugin.toolpath.ToolpathGeneratorFactory;
import replicatorg.plugin.toolpath.ToolpathGeneratorThread;

import net.miginfocom.swing.MigLayout;

public class DualSupportWindow extends JFrame implements ToolpathGenerator.GeneratorListener {
	

	private static final long serialVersionUID = 1L;
	private boolean useW;
	private String stlPath;
	private File result;
	File gcodeFile;
	Toolheads supportHead;
	
	public DualSupportWindow(String path)
	{
		stlPath = path;

	}
	public DualSupportWindow()
	{
		stlPath = null;
	}
	
	public void go()
	{
		this.dispose();
		this.setResizable(true);
		this.setVisible(true);
		this.setLocation(300,0);
		this.setTitle("Dual Support Window");
		Container cont = this.getContentPane();
		cont.setLayout(new MigLayout("fill"));
		cont.add(new JLabel("Stl to be split"), "split");
		final JTextField input = new JTextField(50);
		input.setText("");
		JButton inputChooserButton = new JButton("Browse...");
		inputChooserButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0) {
				String s = null;
				if(!input.getText().equals(""))
				{
					s = GcodeSelectWindow.goString(new File(input.getText()));	
				}
				else
				{
					s = GcodeSelectWindow.goString();
				}
				if(s != null)
				{
					input.setText(s);
				}

			}

		});
		cont.add(input,"split");
		cont.add(inputChooserButton, "wrap");

		cont.add(new JLabel("Select Extruder to print support material"), "split");
		
		String[] supportExtruders = {"Left","Right"};

		final JComboBox supportExtruder = new JComboBox(supportExtruders);
		supportExtruder.setSelectedIndex(1);
		cont.add(supportExtruder, "wrap");
		
		
		final JCheckBox useWipes = new JCheckBox();
		useWipes.setSelected(true);
		cont.add(new JLabel("Use Wipes DANGER!(Don't disable this unless you know what your doing)"), "split");
		cont.add(useWipes, "wrap");
		
		final JButton finish = new JButton();
		finish.setText("Generate Dual-Support GCode");
		
		//I told the makerbot ppl this would be done for today
		finish.addActionListener(new ActionListener()
				{
				
					public void actionPerformed(ActionEvent arg0)
					{
						stlPath = input.getText();
						useW = useWipes.isSelected();
						if(supportExtruder.equals("Left"))
						{
							supportHead = Toolheads.Primary;
						}
						else
						{
							supportHead = Toolheads.Secondary;
						}
						genGcode();
						gcodeFile = new File(DualStrusionWindow.replaceExtension(stlPath, "gcode"));
					}
				
				});
		
		cont.add(finish,"wrap");
		
		this.pack();
	
		
	}
	private synchronized void genGcode()
	{

		try{
			Build p = new Build(stlPath);
			//JFrame primaryProgress = new JFrame("Primary Progress");
			//primaryProgress.setLocation(200, 200);
			ToolpathGenerator generator1 = ToolpathGeneratorFactory.createSelectedGenerator();
			ToolpathGeneratorThread tg1 = new ToolpathGeneratorThread(this, generator1, p);
			
			tg1.addListener(this);
			tg1.start();

		}
		catch(IOException e)
		{
			System.err.println("cannot read stl");
		} 
	}
	
	@Override
	public void generationComplete(Completion completion, Object details) {
		
		ArrayList<String> gcodeText = DualStrusionWorker.readFiletoArrayList(gcodeFile);

		ArrayList<String> model = new ArrayList<String>();
		ArrayList<String> support = new ArrayList<String>();
		model = SupportGenerator.generateSupport(gcodeText, "model");
		support = SupportGenerator.generateSupport(gcodeText, "support");

		if(supportHead == Toolheads.Primary)
		{
		DualStrusionWorker.mergeShuffle(support, model, gcodeFile, true, true, useW);
		}
		else
		{
		DualStrusionWorker.mergeShuffle(model, support, gcodeFile, true, true, useW);
		}
		Base.getEditor().handleOpenFile(result);
		
	}
	@Override
	public void updateGenerator(String message) {
		// TODO Auto-generated method stub
		
	}

}
 