package mod.manager;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.io.FileUtils;

import mod.manager.constant.Constant;
import mod.manager.constant.ModIcon;
import mod.manager.util.Utils;
import mod.manager.vo.CarVO;

/**
 *
 */
public class CarGameTreePanel extends JScrollPane implements ActionListener {
	private static final long serialVersionUID = 1L;

	private static final String MENU_OPEN = "開啟";
	private static final String MENU_DEL = "刪除";
	
	private CarVO mod;
	private JTree tree;
	private CarDetailFrame detailFrame;
	
	public CarGameTreePanel(CarVO mod, CarDetailFrame detailFrame) {
		super();
		this.mod = mod;
		this.detailFrame = detailFrame;

		this.setPreferredSize(new Dimension(500, 500));
		Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder title = BorderFactory.createTitledBorder(loweredetched, "遊戲目錄");
		this.setBorder(title);
		
		DefaultMutableTreeNode root = createRootNodes(mod);
		tree = new JTree(root);
		tree.setRootVisible(true);
		tree.setShowsRootHandles(true);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		expandNeed(tree, new TreePath(tree.getModel().getRoot()));
		this.setViewportView(tree);
		
		TreeCellRenderer renderer = new TreeCellRenderer();
		tree.setCellRenderer(renderer);
		
		//Create the popup menu.
		JPopupMenu popup = new JPopupMenu();
		JMenuItem menuItem = new JMenuItem(MENU_OPEN, ModIcon.OPEN);
	    menuItem.addActionListener(this);
	    popup.add(menuItem);
	    menuItem = new JMenuItem(MENU_DEL, ModIcon.FILE_DEL);
	    menuItem.addActionListener(this);
	    popup.add(menuItem);
	    
	    MouseListener popupListener = new PopupListener(popup);
	    tree.addMouseListener(popupListener);
	}
	
	/**
	 * @param mod
	 * @return
	 */
	private DefaultMutableTreeNode createRootNodes(CarVO mod) {
		String path = Utils.prop.getProperty(Constant.GAME_DIR);
		File modFolder = new File(path);
		
		if (!modFolder.exists() || !modFolder.isDirectory()) {
			return new DefaultMutableTreeNode("遊戲目錄不存在");
		}
		
		DefaultMutableTreeNode root = createNode(modFolder);
		return root;
	}
	
	/**
	 * @param file
	 * @return
	 */
	private DefaultMutableTreeNode createNode(File file) {
		DirTreeNode node = new DirTreeNode(file);
		
		if (file.isDirectory()) {
			File[] current = file.listFiles();
			Arrays.sort(current, new Comparator<File>() {
				@Override
				public int compare(File o1, File o2) {
					if (o1.isDirectory())
						return o2.isDirectory() ? o1.compareTo(o2) : -1;
					else if (o2.isDirectory())
						return 1;

					return o1.compareTo(o2);
				}
			});
			
			for (File f : current) {
				node.add(createNode(f));
			}
			return node;
		} else {
			return node;
		}
	}
	
	/**
	 * @param tree
	 * @param path
	 */
	@SuppressWarnings("rawtypes")
	private void expandNeed(JTree tree, TreePath path) {
		DirTreeNode node = (DirTreeNode) path.getLastPathComponent();
		if (isMatchModFile(node.getFile())) {
			tree.expandPath(path.getParentPath());
		}
        for (Enumeration i = node.children(); i.hasMoreElements(); ) {
            expandNeed(tree, path.pathByAddingChild(i.nextElement()));
        }
    }
	
	private boolean isMatchModFile(File file) {
		if (mod.getFileArray() != null) {
			for (String path : mod.getFileArray()) {
				File pathFile = new File(path);
				if (pathFile.equals(file)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public void refresh() {
		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		DefaultMutableTreeNode root = createRootNodes(mod);
		model.setRoot(root);
		model.reload();
		expandNeed(tree, new TreePath(tree.getModel().getRoot()));
		tree.updateUI();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Utils.log(e.getActionCommand());
		switch (e.getActionCommand()) {
		case MENU_OPEN:
			DirTreeNode node = (DirTreeNode) tree.getSelectionModel().getSelectionPath().getLastPathComponent();
			Utils.open(node.getFile().toURI());
			break;
			
		case MENU_DEL:
			node = (DirTreeNode) tree.getSelectionModel().getSelectionPath().getLastPathComponent();
			if (node.getFile().isDirectory()) {
				if (Utils.showComfirm("確定要刪除目錄: " + node.getFile().getName() + " ? (目錄下的所有檔案會一併刪除!)", "刪除目錄")) {
					try {
						FileUtils.deleteDirectory(node.getFile());
					} catch (IOException e1) {
						Utils.log(e1.getMessage());
						e1.printStackTrace();
					}
					refresh();
					detailFrame.refresh();
					detailFrame.getModTreePanel().refresh();
				}
			} else if (node.getFile().isFile()) {
				if (Utils.showComfirm("確定要刪除檔案: " + node.getFile().getName() + " ?", "刪除檔案")) {
					node.getFile().delete();
					refresh();
					detailFrame.refresh();
					detailFrame.getModTreePanel().refresh();
				}
			}
			
		}
	}
	
	class DirTreeNode extends DefaultMutableTreeNode {
		private File file;
		
		public DirTreeNode(File file) {
			super(file.getName());
			this.file = file;
		}
		
		private static final long serialVersionUID = 1L;
		
		public boolean isLeaf() {
	        return file.isFile();
	    }
		
		public File getFile() {
			return file;
		}
	}
	
	class TreeCellRenderer extends DefaultTreeCellRenderer {
		private static final long serialVersionUID = 1L;
		
		public TreeCellRenderer() {
			super();
			ImageIcon imageIcon = new ImageIcon(CarGameTreePanel.class.getResource("/File-icon.png"));
			this.setLeafIcon(imageIcon);
		}
		
		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
				boolean leaf, int row, boolean hasFocus) {

			Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			DirTreeNode node = (DirTreeNode) value;
			File nodeFile = node.getFile();
			if (mod.getFileArray() != null) {
				for (String path : mod.getFileArray()) {
					File file = new File(path);
					if (nodeFile.equals(file)) {
						c.setForeground(Utils.DARK_GREEN);
						break;
					}
				}
			}
			return c;
		}
		
	}
	
	/**
	 *
	 */
	class PopupListener extends MouseAdapter {
		JPopupMenu popup;

		PopupListener(JPopupMenu popupMenu) {
			popup = popupMenu;
		}

		public void mouseClicked(MouseEvent evt) {
	        if (evt.getClickCount() == 2) {
				
	        }
	    }
		
		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}

		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				TreePath path = tree.getPathForLocation(e.getX(), e.getY());
				Rectangle pathBounds = tree.getUI().getPathBounds(tree, path);
				if (pathBounds != null && pathBounds.contains(e.getX(), e.getY())) {
					tree.setSelectionPath(path);
					popup.show(tree, e.getX(), e.getY());
				}
			}
		}
	}

}
