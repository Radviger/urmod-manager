package mod.manager;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import mod.manager.util.Utils;
import mod.manager.vo.CarVO;
import mod.manager.vo.ModVO;

/**
 *
 */
public class CoverFrame extends JPopupMenu {
	private static final long serialVersionUID = 1L;

	private ImageIcon cover;
	private MenuItem item;
	
	public CoverFrame() {
		super();
		
		//this.setBounds(0, 0, 800, 487);
		//this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		//this.getContentPane().setLayout(new BorderLayout(0, 0));
		//this.setTitle("Cover");
		//this.setIconImage(ModIcon.TRAINER_EDIT.getImage());
		
		//JLabel label = new JLabel("", cover, JLabel.CENTER);
		//panel = new JPanel(new BorderLayout());
		//panel.add(label, BorderLayout.CENTER);
		//this.add(panel, BorderLayout.CENTER);
		item = new MenuItem();
		this.add(item);
	}
	
	public void showCover(ModVO mod) {
		if (mod != null) {
			cover = Utils.loadFullCover(mod);
			item.setIcon(cover);
			item.updateUI();
		}
	}
	
	public void showCover(CarVO car) {
		if (car != null) {
			cover = Utils.loadFullCover(car);
			item.setIcon(cover);
			item.updateUI();
		}
	}
	
	class MenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		
		public MenuItem() {
			super();
			this.setIcon(cover);
		}
	}
}
