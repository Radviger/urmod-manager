package mod.manager;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
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
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import mod.manager.constant.Constant;
import mod.manager.constant.ModIcon;
import mod.manager.util.Utils;
import mod.manager.vo.ModVO;

/**
 *
 */
public class ModTreePanel extends JScrollPane implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	private static final String MENU_OPEN = "開啟";
	
	private ModVO mod;
	private JTree tree;
	
	public ModTreePanel(ModVO mod) {
		super();
		this.mod = mod;

		this.setPreferredSize(new Dimension(500, 500));
		Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder title = BorderFactory.createTitledBorder(loweredetched, mod.getName());
		this.setBorder(title);
		
		DefaultMutableTreeNode root = createRootNodes(mod);
		tree = new JTree(root);
		tree.setRootVisible(true);
		tree.setShowsRootHandles(true);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		expandAll(tree, new TreePath(tree.getModel().getRoot()));
		this.setViewportView(tree);
		
		TreeCellRenderer renderer = new TreeCellRenderer();
		tree.setCellRenderer(renderer);
		
		//Create the popup menu.
		JPopupMenu popup = new JPopupMenu();
		JMenuItem menuItem = new JMenuItem(MENU_OPEN, ModIcon.OPEN);
	    menuItem.addActionListener(this);
	    popup.add(menuItem);
	    
	    MouseListener popupListener = new PopupListener(popup);
	    tree.addMouseListener(popupListener);
	}
	
	/**
	 * @param mod
	 * @return
	 */
	private DefaultMutableTreeNode createRootNodes(ModVO mod) {
		String path = Utils.prop.getProperty(Constant.MOD_DIR) + File.separator + Constant.MODS_FOLDER + File.separator;
		File modFolder = new File(path, mod.getName());
		
		if (!modFolder.exists() || !modFolder.isDirectory()) {
			return new DefaultMutableTreeNode("Mod目錄不存在");
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
	private void expandAll(JTree tree, TreePath path) {
        //assert (tree != null) && (path != null);
        tree.expandPath(path);
        TreeNode node = (TreeNode) path.getLastPathComponent();
        for (Enumeration i = node.children(); i.hasMoreElements(); ) {
            expandAll(tree, path.pathByAddingChild(i.nextElement()));
        }
    }
	
	public void refresh() {
		tree.updateUI();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Utils.log(e.getActionCommand());
		switch (e.getActionCommand()) {
		case MENU_OPEN:
			DirTreeNode node = (DirTreeNode) tree.getSelectionModel().getSelectionPath().getLastPathComponent();
			Utils.open(node.getFile().toURI());
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
			ImageIcon imageIcon = new ImageIcon(GameTreePanel.class.getResource("/File-icon.png"));
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
					if (isMatch(nodeFile, file)) {
						if (!file.exists()) {
							c.setForeground(Color.RED);
						} else {
							c.setForeground(Utils.DARK_GREEN);
						}
					}
				}
			}
			
			if (nodeFile.isDirectory() && nodeFile.getName().equalsIgnoreCase("readme")) {
				c.setForeground(Color.GRAY);
			}
			return c;
		}
		
		private boolean isMatch(File modFile, File gameFile) {
			String modDir = Utils.prop.getProperty(Constant.MOD_DIR) + File.separator + Constant.MODS_FOLDER + File.separator + mod.getName() + File.separator;
			String modPath = modFile.getAbsolutePath();
			if (modDir.length() <= modPath.length()) { 
				modPath = modPath.substring(modDir.length(), modPath.length());
			}
			
			String gameDir = Utils.prop.getProperty(Constant.GAME_DIR) + File.separator;
			String gamePath = gameFile.getAbsolutePath();
			gamePath = gamePath.substring(gameDir.length(), gamePath.length());
			return modPath.equals(gamePath);
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
