package replicatorg.app.ui.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import replicatorg.app.Base;
import replicatorg.app.MachineFactory;
import replicatorg.app.ui.MainWindow;

public class MachineDriverMenu extends JMenu {
	
	MachineMenuListener machineMenuListener;
	
	class MachineMenuListener implements ActionListener {
		final MachineDriverMenu machineDriverMenu;
		final MainWindow editor;
		
		MachineMenuListener(MachineDriverMenu machineDriverMenu, MainWindow editor) {
			this.machineDriverMenu = machineDriverMenu;
			this.editor = editor;
		}
			
		public void actionPerformed(ActionEvent e) {
			// TODO: make this into an interface, don't hit the gui bis directly?
			int count = machineDriverMenu.getItemCount();
			for (int i = 0; i < count; i++) {
				((JCheckBoxMenuItem) machineDriverMenu.getItem(i)).setState(false);
			}

			JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
			item.setState(true);
			final String name = item.getText();
			Base.preferences.put("machine.name", name);

			// load it and set it.
			Thread t = new Thread() {
				public void run() {
					// TODO: Allow autoo-connect to work here.
					editor.loadMachine(name, false);
				}
			};
			t.start();
		}
	}

	public MachineDriverMenu(MainWindow editor) {
		super("Driver");
		
		machineMenuListener = new MachineMenuListener(this, editor);
		
		repopulate();
		
		// TODO: WTF???
		if (getItemCount() == 0) {
			setEnabled(false);
		}
	}
	
	public void repopulate() {
		removeAll();
		boolean empty = true;
		
		try {
			for (String name : MachineFactory.getMachineNames() ) {
				JMenuItem rbMenuItem = new JCheckBoxMenuItem(name,
						name.equals(Base.preferences
								.get("machine.name",null)));
				rbMenuItem.addActionListener(machineMenuListener);
				add(rbMenuItem);
				empty = false;
			}
			if (!empty) {
				setEnabled(true);
			}
		} catch (Exception exception) {
			System.out.println("error retrieving machine list");
			exception.printStackTrace();
		}
	}
}
