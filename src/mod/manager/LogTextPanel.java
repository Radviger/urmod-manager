package mod.manager;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 */
public class LogTextPanel extends JScrollPane {
	private static final long serialVersionUID = 1L;

	JTextArea logArea = null;
	
	public LogTextPanel() {
		super();
		
		logArea = new JTextArea(5, 100);
		logArea.setEditable(false);
		
		this.setViewportView(logArea);
	}
	
	public void log(String log) {
		logArea.append(log + "\r\n");
	}
}
