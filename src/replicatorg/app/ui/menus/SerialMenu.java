package replicatorg.app.ui.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import replicatorg.app.Base;
import replicatorg.app.util.serial.Name;
import replicatorg.app.util.serial.Serial;

public class SerialMenu extends JMenu {
	public SerialMenu() {
		super("Serial Port");
		reload();
	}
	
	public void reload() {
		String currentName;
		
		removeAll();
		// TODO: Delete this bit. Don't use this menu if you don't want serial!
//		if (machine == null) {
//			JMenuItem item = new JMenuItem("No machine selected.");
//			item.setEnabled(false);
//			serialMenu.add(item);
//			return;
//		} else if (!(machine.driver instanceof UsesSerial))  {
//			JMenuItem item = new JMenuItem("Currently selected machine does not use a serial port.");
//			item.setEnabled(false);
//			serialMenu.add(item);
//			return;
//		}
		
		//TODO: ask the machine for it's name :-/
//		String currentName = null;
//		UsesSerial us = (UsesSerial)machine.driver;
//		if (us.getSerial() != null) {
//			currentName = us.getSerial().getName();
//		}
//		else {
			currentName = Base.preferences.get("serial.last_selected", null);
//		}
		Vector<Name> names = Serial.scanSerialNames();
		Collections.sort(names);
		
		// Filter /dev/cu. devices on OS X, since they work the same as .tty for our purposes.
		if (Base.isMacOS()) {
			Vector<Name> filteredNames = new Vector<Name>();
			
			for (Name name : names) {
				if(!(name.getName().startsWith("/dev/cu")
					|| name.getName().equals("/dev/tty.Bluetooth-Modem")
					|| name.getName().equals("/dev/tty.Bluetooth-PDA-Sync"))) {
					filteredNames.add(name);
				}
			}
			
			names = filteredNames;
		}

		
		ButtonGroup radiogroup = new ButtonGroup();
		for (Name name : names) {
			JRadioButtonMenuItem item = new JRadioButtonMenuItem(name.toString());
			item.setEnabled(name.isAvailable());
			item.setSelected(name.getName().equals(currentName));
			final String portName = name.getName();
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Base.preferences.put("serial.last_selected", portName);
				}
			});
			radiogroup.add(item);
			add(item);
		}
		if (names.isEmpty()) {
			JMenuItem item = new JMenuItem("No serial ports detected");
			item.setEnabled(false);
			add(item);			
		}
		addSeparator();
		JMenuItem item = new JMenuItem("Rescan serial ports");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reload();
			}
		});
		add(item);
	}
}
