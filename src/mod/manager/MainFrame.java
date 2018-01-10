package mod.manager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.commons.io.FileUtils;

import mod.manager.ModTableModel.ModEnableListener;
import mod.manager.constant.Constant;
import mod.manager.constant.ModIcon;
import mod.manager.constant.ModTableColumn;
import mod.manager.constant.Status;
import mod.manager.util.CoverUtils;
import mod.manager.util.Utils;
import mod.manager.util.ZipUtils;
import mod.manager.vo.CarExportVO;
import mod.manager.vo.ModVO;
import mod.manager.vo.ModZipVO;

/**
 * 主視窗
 */
public class MainFrame extends JFrame implements ActionListener, ModEnableListener {
	private static final long serialVersionUID = 1L;

	//menu items
	private static final String MENU_EXPORT_CARS = "匯出Car XML";
	private static final String MENU_IMPORT_CARS = "匯入Car XML";
	
	//popup menu items
	private static final String MENU_INSTALL = "重新安裝Mod";
	private static final String MENU_EDIT = "編輯Mod";
	private static final String MENU_DEL = "刪除Mod";
	private static final String MENU_REFRESH = "重新整理";
	private static final String MENU_LOADCOVER = "讀取封面";
	private static final String MENU_CHECK_VERSION = "檢查版本";
	private static final String MENU_CHECK_VERSION_ALL = "檢查版本(ALL)";
	
	private JTable modTable;
	private ModList modList;
	private CarModPanel carsPanel;
	
	//private Timer showTimer;
    //private Timer disposeTimer;
    //private Point hintCell;
    //private CoverFrame coverFrame;
	
	public MainFrame() {
		super();
		
		this.setBounds(100, 100, 1300, 750);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().setLayout(new BorderLayout(0, 0));
		this.setTitle("Mod管理器");
		this.setIconImage(ModIcon.RSTAR.getImage());
		
		Utils.loadProperties();
		
		//====Menu Bar====
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("匯入/匯出");
		JMenuItem item = new JMenuItem(MENU_EXPORT_CARS, ModIcon.EXPORT);
		item.addActionListener(this);
		menu.add(item);
		
		item = new JMenuItem(MENU_IMPORT_CARS, ModIcon.IMPORT);
		item.addActionListener(this);
		menu.add(item);
		
		menuBar.add(menu);
		this.setJMenuBar(menuBar);
		
		//====上方的遊戲目錄和Mod目錄設定區====
		JPanel ctrlPane = new JPanel();
		JLabel dirLabel = new JLabel("遊戲目錄:");
		JTextField dirPathTextField = new JTextField(30);
		dirPathTextField.setText(Utils.prop.getProperty(Constant.GAME_DIR));
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		JButton openButton = new JButton("開啟", ModIcon.FOLDER_OPEN);
		openButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int returnVal = fc.showOpenDialog(openButton);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					dirPathTextField.setText(file.getAbsolutePath());
				}
			}
		});
		
		JButton saveDirButton = new JButton("保存", ModIcon.SAVE);
		saveDirButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Utils.prop.setProperty(Constant.GAME_DIR, dirPathTextField.getText());
				Utils.saveProperties(Utils.prop);
			}
		});
		
		JButton compButton = new JButton("檔案總管", ModIcon.EXPLORER);
		compButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					File uriFile = new File(dirPathTextField.getText());
					Utils.open(uriFile.toURI());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		ctrlPane.add(dirLabel);
		ctrlPane.add(dirPathTextField);
		ctrlPane.add(openButton);
		ctrlPane.add(saveDirButton);
		ctrlPane.add(compButton);
		
		JPanel ctrlPane2 = new JPanel();
		JLabel modDirLabel = new JLabel("Mod目錄:");
		JTextField modDirPathTextField = new JTextField(30);
		modDirPathTextField.setText(Utils.prop.getProperty(Constant.MOD_DIR));
		
		JButton openButton2 = new JButton("開啟", ModIcon.FOLDER_OPEN);
		openButton2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int returnVal = fc.showOpenDialog(openButton2);

		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
		            modDirPathTextField.setText(file.getAbsolutePath());
		        }
			}
		});
		JButton saveModDirButton = new JButton("保存", ModIcon.SAVE);
		saveModDirButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Utils.prop.setProperty(Constant.MOD_DIR, modDirPathTextField.getText());
				Utils.saveProperties(Utils.prop);
			}
		});
		JButton compButton2 = new JButton("檔案總管", ModIcon.EXPLORER);
		compButton2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					File uriFile = new File(modDirPathTextField.getText());
					Utils.open(uriFile.toURI());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		ctrlPane2.add(modDirLabel);
		ctrlPane2.add(modDirPathTextField);
		ctrlPane2.add(openButton2);
		ctrlPane2.add(saveModDirButton);
		ctrlPane2.add(compButton2);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, ctrlPane, ctrlPane2);
		splitPane.setDividerSize(0);
		splitPane.setEnabled(false);
		
		this.getContentPane().add(splitPane, BorderLayout.NORTH);
		
		
		JTabbedPane tabbedPane = new JTabbedPane();
		JPanel modsPanel = new JPanel();
		modsPanel.setLayout(new BorderLayout(0, 0));
		carsPanel = new CarModPanel();
		
		tabbedPane.addTab("一般Mods", ModIcon.MOD, modsPanel);
		tabbedPane.addTab("車輛Mods", ModIcon.CAR, carsPanel);
		
		//====中間Mod安裝列表====
		List<ModVO> mods = Utils.loadModTable();
		ModTableModel tableModel = new ModTableModel(mods);
		modTable = new JTable(tableModel);
		tableModel.addModEnableListener(this);
		modTable.getColumn(ModTableColumn.STATUS.getName()).setCellRenderer(new StatusCellRenderer());
		modTable.getColumn(ModTableColumn.VERSION.getName()).setCellRenderer(new VersionCellRenderer());
		modTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		modTable.setDragEnabled(true);
		modTable.setDropMode(DropMode.USE_SELECTION);
		modTable.setTransferHandler(new TableTransferHandler());
		modTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		modTable.getColumnModel().getColumn(0).setMaxWidth(25);
		modTable.getColumnModel().getColumn(1).setMaxWidth(40);
		modTable.getColumnModel().getColumn(2).setMaxWidth(40);
		modTable.getColumnModel().getColumn(3).setMinWidth(130);
		modTable.getColumnModel().getColumn(4).setMinWidth(450);
		modTable.setRowHeight(70);
		if (mods.size() > 0) {
			modTable.setRowSelectionInterval(0, 0);
		}
		
//		coverFrame = new CoverFrame();
//		showTimer = new Timer(500, new ShowPopupActionHandler());
//        showTimer.setRepeats(false);
//        showTimer.setCoalesce(true);
//        disposeTimer = new Timer(3000, new DisposePopupActionHandler());
//        disposeTimer.setRepeats(false);
//        disposeTimer.setCoalesce(true);
//        modTable.addMouseMotionListener(new MotionAdapter());
		
		//Create the popup menu.
		JPopupMenu popup = new JPopupMenu();
		JMenuItem menuItem = new JMenuItem(MENU_INSTALL, ModIcon.REINSTALL);
	    menuItem.addActionListener(this);
	    popup.add(menuItem);
	    menuItem = new JMenuItem(MENU_EDIT, ModIcon.MOD_EDIT);
	    menuItem.addActionListener(this);
	    popup.add(menuItem);
	    menuItem = new JMenuItem(MENU_DEL, ModIcon.MOD_DEL);
	    menuItem.addActionListener(this);
	    popup.add(menuItem);
	    menuItem = new JMenuItem(MENU_REFRESH, ModIcon.REFRESH);
	    menuItem.addActionListener(this);
	    popup.add(menuItem);
	    menuItem = new JMenuItem(MENU_LOADCOVER, ModIcon.READ_COVER);
	    menuItem.addActionListener(this);
	    popup.add(menuItem);
	    menuItem = new JMenuItem(MENU_CHECK_VERSION, ModIcon.READ_COVER);
	    menuItem.addActionListener(this);
	    popup.add(menuItem);
	    menuItem = new JMenuItem(MENU_CHECK_VERSION_ALL, ModIcon.READ_COVER);
	    menuItem.addActionListener(this);
	    popup.add(menuItem);
	    MouseListener popupListener = new PopupListener(popup, modTable);
	    modTable.addMouseListener(popupListener);
		
		JScrollPane tableScrollPane = new JScrollPane(modTable);
		tableScrollPane.setPreferredSize(new Dimension(250, 200));
		
		Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder title = BorderFactory.createTitledBorder(loweredetched, "MOD管理列表");
		tableScrollPane.setBorder(title);
		
		modsPanel.add(tableScrollPane, BorderLayout.CENTER);
		this.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		
		//====右邊Mod壓縮檔管理區====
		List<ModZipVO> zips = Utils.loadModZipList();
		modList = new ModList(zips, modTable);
		
		JScrollPane listScrollPane = new JScrollPane(modList);
		listScrollPane.setPreferredSize(new Dimension(250, 200));
		modsPanel.add(listScrollPane, BorderLayout.EAST);
		
		loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		title = BorderFactory.createTitledBorder(loweredetched, "MOD壓縮檔列表");
		listScrollPane.setBorder(title);
		
		//====下方log顯示區====
		LogTextPanel logPanel = new LogTextPanel();
		Utils.setLogTextPanel(logPanel);
		logPanel.setPreferredSize(new Dimension(750, 100));
		this.getContentPane().add(logPanel, BorderLayout.SOUTH);
		
//		for (ModVO mod : mods) {
//			if (mod.getHtml() == null) {
//				for (ModZipVO zip : zips) {
//					if (zip.getFileUrl().equals(mod.getZipName())) {
//						mod.setHtml(zip.getHtml());
//						break;
//					}
//				}
//			}
//		}
		Utils.saveModTable(mods);
	}
	
	/**
	 * JPopupMenu使用的Listener
	 */
	class PopupListener extends MouseAdapter {
		JPopupMenu popup;
		JTable table;

		PopupListener(JPopupMenu popupMenu, JTable table) {
			popup = popupMenu;
			this.table = table;
		}

		public void mouseClicked(MouseEvent evt) {
			JTable table = (JTable) evt.getSource();
			ModTableModel model = (ModTableModel) table.getModel();
        	ModVO mod = model.getRow(table.getSelectedRow());
        	
	        if (evt.getClickCount() == 2) {
	        	int col = table.columnAtPoint(evt.getPoint());
				if (col != ModTableColumn.RATE.getIndex()) {
					if (mod == null) {
						Utils.showAlert("請先選擇一項Mod!");
						return;
					}
					ModDetailFrame detailFrame = new ModDetailFrame(mod, table);
					Utils.showCenterFrame(detailFrame);
				}
	        }
	        
	        if (mod != null) {
	        	modList.setSelectedMod(mod);
	        }
	    }
		
		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			//有選擇到row時才顯示menu
			int row = table.rowAtPoint(e.getPoint());
			
			if (table.isRowSelected(row)) {
				maybeShowPopup(e);
				
				if (e.getButton() == MouseEvent.BUTTON1) {
					int col = table.columnAtPoint(e.getPoint());
					if (col == ModTableColumn.RATE.getIndex()) {
						ModTableModel model = (ModTableModel) table.getModel();
						ModVO mod = model.getRow(table.getSelectedRow());
						mod.setRate(mod.getRate() + 1 > 3 ? 0 : mod.getRate() + 1);
						table.updateUI();
						Utils.saveModTable(model.getModList());
					}
				}
			}
		}

		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Utils.log(e.getActionCommand());
		ModTableModel tableModel = (ModTableModel) modTable.getModel();
		List<ModVO> mods = tableModel.getModList();
		ModVO mod = tableModel.getRow(modTable.getSelectedRow());
		
		switch (e.getActionCommand()) {
		case MENU_INSTALL:
			if (Utils.showComfirm("確定要重新安裝?", "確認")) {
				//解壓縮zip至目標目錄
				ZipUtils.unzip(mod.getZipName(), mod.getName());
			}
			break;
			
		case MENU_EDIT:
			ModEditFrame editFrame = new ModEditFrame(mod, modTable);
			Utils.showCenterFrame(editFrame);
			break;
			
		case MENU_DEL:
			if (mod.isEnable()) {
				Utils.showAlert("請先取消安裝之後再刪除Mod!");
				return;
			}
			
			if (Utils.showComfirm("確定要刪除Mod?", "確認")) {
				tableModel.removeRow(modTable.getSelectedRow());
				modTable.updateUI();
				
				Utils.saveModTable(tableModel.getModList());
				
				String path = Utils.prop.getProperty(Constant.MOD_DIR) + File.separator + Constant.MODS_FOLDER + File.separator;
				File file = new File(path, mod.getName());
				if (file.exists() && file.isDirectory()) {
					file.delete();
					try {
						FileUtils.forceDelete(file);
					} catch (IOException e1) {
						e1.printStackTrace();
						Utils.log(e1.getMessage());
					}
					Utils.log("Mod: " + mod.getName() + " 已刪除 (" + file.getAbsolutePath() + ")");
				}
			}
			break;
			
		case MENU_REFRESH:
			for (ModVO modVO : mods) {
				modVO.setStatus(Utils.checkModStatus(modVO));
			}
			modTable.updateUI();
			
			Utils.saveModTable(tableModel.getModList());
			break;
			
		case MENU_LOADCOVER:
			Thread coverthread = new Thread(new Runnable() {
				@Override
				public void run() {
					CoverUtils.loadCover(mod);
					modTable.updateUI();
				}
			});
			coverthread.start();
			break;
			
		case MENU_CHECK_VERSION:
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					String version = CoverUtils.loadVersion(mod.getHtml());
					if (version != null && !version.equals(mod.getVersion())) {
						mod.setNewVersion(version);
					} else {
						mod.setNewVersion(null);
					}
					
					modTable.updateUI();
					
					Utils.saveModTable(tableModel.getModList());
				}
			});
			thread.start();
			break;
			
		case MENU_CHECK_VERSION_ALL:
			Thread thread2 = new Thread(new Runnable() {
				@Override
				public void run() {
					for (ModVO modVO : mods) {
						String ver = CoverUtils.loadVersion(modVO.getHtml());
						if (ver != null && !ver.equals(modVO.getVersion())) {
							modVO.setNewVersion(ver);
						} else {
							modVO.setNewVersion(null);
						}
					}
					
					modTable.updateUI();
					
					Utils.saveModTable(tableModel.getModList());
				}
			});
			thread2.start();
			break;
			
		case MENU_EXPORT_CARS:
			File exportDir = new File(Utils.prop.getProperty(Constant.MOD_DIR) + File.separator, Constant.EXPORT_FOLDER);
			FileFilter filter = new FileNameExtensionFilter("xml file", "xml");
			JFileChooser fc = new JFileChooser(exportDir);
			fc.setFileFilter(filter);
			
			int returnVal = fc.showOpenDialog(this);

	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File file = fc.getSelectedFile();
	            if (!file.getName().endsWith(".xml")) {
	            	file = new File(file.getParentFile(), file.getName() + ".xml");
	            }
	            Utils.log("選擇檔案: " + file.getName());
	            
	            //輸出檔案
	            Utils.exportCarXml(file);
	            
	        } else {
	        	Utils.log("取消選擇檔案.");
	        }
			break;
			
		case MENU_IMPORT_CARS:
			File exportDir2 = new File(Utils.prop.getProperty(Constant.MOD_DIR) + File.separator, Constant.EXPORT_FOLDER);
			FileFilter filter2 = new FileNameExtensionFilter("xml file", "xml");
			JFileChooser fc2 = new JFileChooser(exportDir2);
			fc2.setFileFilter(filter2);
			
			int returnVal2 = fc2.showOpenDialog(this);

	        if (returnVal2 == JFileChooser.APPROVE_OPTION) {
	            File file = fc2.getSelectedFile();
	            if (!file.getName().endsWith(".xml")) {
	            	file = new File(file.getParentFile(), file.getName() + ".xml");
	            }
	            Utils.log("選擇檔案: " + file.getName());
	            
	            //匯入檔案
	            List<CarExportVO> exports = Utils.importCarXml(file);
	            carsPanel.importCarXml(exports);
	            
	        } else {
	        	Utils.log("取消選擇檔案.");
	        }
			break;
		}
		
		
	}

	@Override
	public void modEnableUpdated(ModVO mod, boolean enable) {
		if (enable) {
			//勾選
			if (Utils.showComfirm("確定安裝Mod至遊戲目錄?", "確認")) {
				Utils.copyModDir(mod);
				mod.setEnable(true);
				
				ModTableModel tableModel = (ModTableModel) modTable.getModel();
				Utils.saveModTable(tableModel.getModList());
			}
		} else {
			//取消勾選
			if (Utils.showComfirm("確定將Mod從遊戲目錄中移除?", "確認")) {
				Utils.removeModData(mod);
				
				mod.setEnable(false);
				mod.setFileArray(null);
				
				ModTableModel tableModel = (ModTableModel) modTable.getModel();
				List<ModVO> mods = tableModel.getModList();
				for (ModVO modVO : mods) {
					modVO.setStatus(Utils.checkModStatus(modVO));
				}
				
				Utils.saveModTable(tableModel.getModList());
			}
		}
	}
	
	/**
	 * JTable中的狀態欄位顯示
	 */
	class StatusCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(
	                        JTable table, Object value,
	                        boolean isSelected, boolean hasFocus,
	                        int row, int column) {
	        JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	        Status status = Status.valueOf((String) value);
	        c.setToolTipText(status.hint);
	        if (status != Status.OK) {
	        	c.setForeground(Color.RED);
	        } else {
	        	c.setForeground(Color.BLACK);
	        }
	        return c;
	    }
	}
	
	/**
	 * JTable中的版本欄位顯示
	 */
	class VersionCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(
	                        JTable table, Object value,
	                        boolean isSelected, boolean hasFocus,
	                        int row, int column) {
	        JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	        
	        ModTableModel tableModel = (ModTableModel) table.getModel();
	        ModVO mod = tableModel.getRow(row);
	        if (mod.getNewVersion() != null && mod.getNewVersion().length() > 0) {
	        	c.setForeground(Color.RED);
	        	c.setToolTipText("有新版本: " + mod.getNewVersion());
	        } else {
	        	c.setForeground(Color.BLACK);
	        	c.setToolTipText("目前版本: " + mod.getVersion());
	        }
	        return c;
	    }
	}
	
	/**
	 * ModTable drag&drop所需TransferHandler
	 */
	class TableTransferHandler extends StringTransferHandler {
		private static final long serialVersionUID = 1L;

		private int[] rows = null;

		private int addIndex = -1; // Location where items were added

		private int addCount = 0; // Number of items added.

		@Override
		protected String exportString(JComponent c) {
			JTable table = (JTable) c;
			ModTableModel model = (ModTableModel) table.getModel();
			rows = table.getSelectedRows();

			StringBuffer buff = new StringBuffer();

			for (int i = 0; i < rows.length; i++) {
				ModVO mod = model.getRow(rows[i]);
				buff.append(Utils.objToXml(ModVO.class, mod));
			}

			return buff.toString();
		}

		@Override
		protected void importString(JComponent c, String str) {
			JTable target = (JTable) c;
			ModTableModel model = (ModTableModel) target.getModel();
			int index = target.getSelectedRow();
			int selectRow = rows[0];
			
			if (index == selectRow) {
				//drag跟drop是同位置, 不做事
				rows = null;
				return;
			} else if (index > selectRow) {
				//drop位置比drag位置底下, index要加1
				index++;
			}
			
			addIndex = index;
			
			ModVO mod = Utils.xmlToObj(ModVO.class, str);
			model.insertRow(index, mod);
			addCount = 1;
			
		}

		@Override
		protected void cleanup(JComponent c, boolean remove) {
			JTable source = (JTable) c;
			if (remove && rows != null) {
				ModTableModel model = (ModTableModel) source.getModel();

				//把原本的那筆刪除
				if (addCount > 0) {
					for (int i = 0; i < rows.length; i++) {
						if (rows[i] > addIndex) {
							rows[i] += addCount;
						}
					}
				}
				for (int i = rows.length - 1; i >= 0; i--) {
					model.removeRow(rows[i]);
				}
				
				Utils.saveModTable(model.getModList());
			}
			rows = null;
			addCount = 0;
			addIndex = -1;
			source.updateUI();
		}
	}
	
//	class MotionAdapter extends MouseMotionAdapter {
//		@Override
//		public void mouseMoved(MouseEvent e) {
//			Point p = e.getPoint();
//			int row = modTable.rowAtPoint(p);
//			int col = modTable.columnAtPoint(p);
//
//			if ((row > -1 && row < modTable.getRowCount()) && (col > -1 && col < modTable.getColumnCount())) {
//				if (hintCell == null || (hintCell.x != col || hintCell.y != row)) {
//					Object value = modTable.getValueAt(row, col);
//					
//					if (value instanceof ImageIcon) {
//						hintCell = new Point(col, row);
//						ModTableModel model = (ModTableModel) modTable.getModel();
//						ModVO mod = model.getRow(row);
//						coverFrame.showCover(mod);
//						showTimer.restart();
//					}
//				}
//			}
//		}
//	}
	
//	class ShowPopupActionHandler implements ActionListener {
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            if (hintCell != null) {
//                disposeTimer.stop(); // don't want it going off while we're setting up
//
//                coverFrame.setVisible(false);
//
//                // You might want to check that the object hint data is update and valid...
//                Rectangle bounds = modTable.getCellRect(hintCell.y, hintCell.x, true);
//                int x = bounds.x + bounds.width;
//                int y = bounds.y + bounds.height;
//
//                coverFrame.show(modTable, x, y);
//                disposeTimer.start();
//            }
//        }
//    }

//    class DisposePopupActionHandler implements ActionListener {
//        @Override
//        public void actionPerformed(ActionEvent e) {
//        	coverFrame.setVisible(false);
//        }
//    }
}
