package mod.manager;

import java.awt.EventQueue;

import javax.swing.UIManager;

import mod.manager.util.Utils;

/**
 * 
 */
public class Main {
	boolean packFrame = false;
	private MainFrame frame;

	public Main() {
		frame = new MainFrame();

		if (packFrame) {
			frame.pack();
		} else {
			frame.validate();
		}

		// Center the window
		Utils.showCenterFrame(frame);
	}

	public static void main(String args[]) {
		//test();
		
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					new Main();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});
	}

}
