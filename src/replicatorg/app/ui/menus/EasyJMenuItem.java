package replicatorg.app.ui.menus;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;


/**
 * Just like a JMenuItem, but simpler to create.
 * 
 * Previous description
 * A software engineer, somewhere, needs to have his abstraction taken away.
 * In some countries they jail or beat people for writing the sort of API
 * that would require a five line helper function just to set the command
 * key for a menu item.
 */
public class EasyJMenuItem extends JMenuItem {
	public EasyJMenuItem(String title, int what) {
		this(title, what, false);
	}


	public EasyJMenuItem(String title, int what, boolean shift) {
		super(title);
		int modifiers = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		if (shift)
			modifiers |= ActionEvent.SHIFT_MASK;
		setAccelerator(KeyStroke.getKeyStroke(what, modifiers));
	}


}
