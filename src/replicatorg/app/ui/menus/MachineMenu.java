package replicatorg.app.ui.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import replicatorg.app.ui.MainWindow;
import replicatorg.drivers.RealtimeControl;
import replicatorg.machine.MachineListener;
import replicatorg.machine.MachineProgressEvent;
import replicatorg.machine.MachineStateChangeEvent;
import replicatorg.machine.MachineToolStatusEvent;
import replicatorg.uploader.FirmwareUploader;

public class MachineMenu extends JMenu implements MachineListener {

	final MainWindow editor;
	
	MachineDriverMenu machineDriverMenu;
	JMenu serialMenu;
	JMenuItem controlPanel;
	JMenuItem onboardParams;
	JMenuItem extruderParams;
	JMenuItem toolheadIndexing;
	JMenuItem realtimeControl;
	JMenuItem uploadFirmware;
	
	public MachineMenu(MainWindow editor_) {
		super("Machine");
		
		this.editor = editor_;
		
		machineDriverMenu = new MachineDriverMenu(editor);
		add(machineDriverMenu);
	
		// TODO: Heh?
		addMenuListener(new MenuListener() {
			public void menuCanceled(MenuEvent e) {
			}
	
			public void menuDeselected(MenuEvent e) {
			}
	
			public void menuSelected(MenuEvent e) {
				// TODO: Why do we do this here, shouldn't it only happen when starting repg?
				machineDriverMenu.repopulate();
			}
		});
	
		serialMenu = new SerialMenu();
		add(serialMenu);
		
		controlPanel = new JMenuItem("Control Panel", 'C');
		controlPanel.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J,ActionEvent.CTRL_MASK));
		controlPanel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editor.handleControlPanel();
			}
		});
		add(controlPanel);
		
		realtimeControl = new JMenuItem("Real Time Controls");
		realtimeControl.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				editor.handleRealTimeControl();
			}
		});
		add(realtimeControl);

		addSeparator();
		
		realtimeControl = new JMenuItem("Reset Machine");
		realtimeControl.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				editor.handleReset();
			}
		});
		add(realtimeControl);
		
		addSeparator();
		
		onboardParams = new JMenuItem("Motherboard Preferences");
		onboardParams.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				editor.handleOnboardPrefs();
			}
		});
		add(onboardParams);
	
		extruderParams = new JMenuItem("Toolhead Preferences");
		extruderParams.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				editor.handleExtruderPrefs();
			}
		});
		add(extruderParams);
	
		toolheadIndexing = new JMenuItem("Set Toolhead Index");
		toolheadIndexing.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				editor.handleToolheadIndexing();
			}
		});
		add(toolheadIndexing);
		
		uploadFirmware = new JMenuItem("Upload new firmware...");
		uploadFirmware.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				FirmwareUploader.startUploader(editor);
			}
		});
		add(uploadFirmware);
		
		// TODO: Hook this up to the machine loader
	}

	@Override
	public void machineStateChanged(MachineStateChangeEvent evt) {
		// TODO Re-evaluate state
	}

	@Override
	public void machineProgress(MachineProgressEvent event) {
	}

	@Override
	public void toolStatusChanged(MachineToolStatusEvent event) {
	}
}
