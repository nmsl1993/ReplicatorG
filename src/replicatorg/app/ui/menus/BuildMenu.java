package replicatorg.app.ui.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import replicatorg.app.ui.MainWindow;
import replicatorg.machine.MachineListener;
import replicatorg.machine.MachineProgressEvent;
import replicatorg.machine.MachineStateChangeEvent;
import replicatorg.machine.MachineToolStatusEvent;
import replicatorg.plugin.toolpath.ToolpathGeneratorFactory;
import replicatorg.plugin.toolpath.ToolpathGeneratorFactory.ToolpathGeneratorDescriptor;

public class BuildMenu extends JMenu implements MachineListener {
	final MainWindow editor;
	
	JMenuItem generate;
	JMenuItem estimate;
	JMenuItem simulate;
	JMenuItem build;
	JMenuItem pause;
	JMenuItem stop;
	JMenuItem generatorPicker;
	
	public BuildMenu(MainWindow editor_) {
		super("Build");
		this.editor = editor_;

		generate = new EasyJMenuItem("Generate GCode", 'G');
		generate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editor.runToolpathGenerator();
			}
		});
		add(generate);
		
		addSeparator();
		
		estimate = new EasyJMenuItem("Estimate", 'E');
		estimate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editor.handleEstimate();
			}
		});
		add(estimate);

		simulate = new EasyJMenuItem("Simulate", 'L');
		simulate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editor.handleSimulate();
			}
		});
		add(simulate);

		build = new EasyJMenuItem("Build", 'B');
		build.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editor.handleBuild();
			}
		});
		add(build);

		pause = new EasyJMenuItem("Pause", 'E');
		pause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editor.handlePause();
			}
		});
		add(pause);

		stop = new EasyJMenuItem("Stop", '.');
		stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editor.handleStop();
			}
		});
		add(stop);

		addSeparator();
		
		generatorPicker = new JMenu("Choose GCode generator");
		Vector<ToolpathGeneratorDescriptor> generators = ToolpathGeneratorFactory.getGeneratorList();
		String name = ToolpathGeneratorFactory.getSelectedName();
		ButtonGroup group = new ButtonGroup();
		for (ToolpathGeneratorDescriptor tgd : generators) {
			JRadioButtonMenuItem i = new JRadioButtonMenuItem(tgd.name);
			group.add(i);
			final String n = tgd.name;
			i.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ToolpathGeneratorFactory.setSelectedName(n);
				}
			});
			if (name.equals(tgd.name)) { i.setSelected(true); }
			generatorPicker.add(i);
		}
		add(generatorPicker);
		
		// TODO: Wire the menu up to the machine loader.
	}


	@Override
	public void machineStateChanged(MachineStateChangeEvent evt) {
		// TODO: Register for machine evnets!
	}

	@Override
	public void machineProgress(MachineProgressEvent event) {	
	}


	@Override
	public void toolStatusChanged(MachineToolStatusEvent event) {
	}
}
