package mod.manager;

import java.awt.BorderLayout;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.commons.io.FileUtils;

import mod.manager.CarTableModel.ModEnableListener;
import mod.manager.constant.CarModTableColumn;
import mod.manager.constant.Constant;
import mod.manager.constant.ModIcon;
import mod.manager.constant.Status;
import mod.manager.util.CoverUtils;
import mod.manager.util.DlcListUtils;
import mod.manager.util.TrainerUtils;
import mod.manager.util.Utils;
import mod.manager.util.ZipUtils;
import mod.manager.vo.CarExportVO;
import mod.manager.vo.CarVO;
import mod.manager.vo.CarZipVO;

/**
 *
 */
public class CarModPanel extends JPanel implements ActionListener, ModEnableListener {
	private static final long serialVersionUID = 1L;

	//popup menu items
	private static final String MENU_INSTALL = "重新安裝車輛Mod";
	private static final String MENU_EDIT = "編輯Mod";
	private static final String MENU_DEL = "刪除Mod";
	private static final String MENU_REFRESH = "重新整理";
	private static final String MENU_SORT = "按車廠排序";
	private static final String MENU_EDIT_TRAINER = "編輯trainerv.ini";
	private static final String MENU_LOADCOVER = "讀取封面";
	private static final String MENU_CHECK_VERSION = "檢查版本";
	private static final String MENU_CHECK_VERSION_ALL = "檢查版本(ALL)";
	
	private JTable carTable;
	private CarModList carList;
	
	//private Timer showTimer;
    //private Timer disposeTimer;
    //private Point hintCell;
    //private CoverFrame coverFrame;
	
	public CarModPanel() {
		super();
		
		this.setLayout(new BorderLayout(0, 0));
		
		
		List<CarVO> cars = Utils.loadCarTable();
		CarTableModel tableModel = new CarTableModel(cars);
		carTable = new JTable(tableModel);
		
		tableModel.addModEnableListener(this);
		carTable.getColumn(CarModTableColumn.STATUS.getName()).setCellRenderer(new StatusCellRenderer());
		carTable.getColumn(CarModTableColumn.VERSION.getName()).setCellRenderer(new VersionCellRenderer());
		carTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		carTable.setDragEnabled(true);
		carTable.setDropMode(DropMode.USE_SELECTION);
		carTable.setTransferHandler(new TableTransferHandler());
		carTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		carTable.getColumnModel().getColumn(0).setMaxWidth(25);
		carTable.getColumnModel().getColumn(1).setMaxWidth(40);
		carTable.getColumnModel().getColumn(2).setMaxWidth(40);
		carTable.getColumnModel().getColumn(3).setMinWidth(130);
		carTable.getColumnModel().getColumn(4).setMinWidth(450);
		carTable.setRowHeight(70);
		if (cars.size() > 0) {
			carTable.setRowSelectionInterval(0, 0);
		}
		
//		coverFrame = new CoverFrame();
//		showTimer = new Timer(500, new ShowPopupActionHandler());
//        showTimer.setRepeats(false);
//        showTimer.setCoalesce(true);
//        disposeTimer = new Timer(3000, new DisposePopupActionHandler());
//        disposeTimer.setRepeats(false);
//        disposeTimer.setCoalesce(true);
//        carTable.addMouseMotionListener(new MotionAdapter());
		
		
		//Create the popup menu.
		JPopupMenu popup = new JPopupMenu();
		JMenuItem menuItem = new JMenuItem(MENU_INSTALL, ModIcon.REINSTALL);
	    menuItem.addActionListener(this);
	    popup.add(menuItem);
	    menuItem = new JMenuItem(MENU_EDIT, ModIcon.CAR_EDIT);
	    menuItem.addActionListener(this);
	    popup.add(menuItem);
	    menuItem = new JMenuItem(MENU_DEL, ModIcon.CAR_DEL);
	    menuItem.addActionListener(this);
	    popup.add(menuItem);
	    menuItem = new JMenuItem(MENU_REFRESH, ModIcon.REFRESH);
	    menuItem.addActionListener(this);
	    popup.add(menuItem);
	    menuItem = new JMenuItem(MENU_SORT, ModIcon.SORT);
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
	    menuItem = new JMenuItem(MENU_EDIT_TRAINER, ModIcon.TRAINER_EDIT);
	    menuItem.addActionListener(this);
	    popup.add(menuItem);
	    MouseListener popupListener = new PopupListener(popup, carTable);
	    carTable.addMouseListener(popupListener);
	    
	    JScrollPane tableScrollPane = new JScrollPane(carTable);
		tableScrollPane.setPreferredSize(new Dimension(250, 200));
		
		Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder title = BorderFactory.createTitledBorder(loweredetched, "車輛MOD管理列表");
		tableScrollPane.setBorder(title);
		
		this.add(tableScrollPane, BorderLayout.CENTER);
		
		
		//====右邊Mod壓縮檔管理區====
		List<CarZipVO> zips = Utils.loadCarZipList();
		carList = new CarModList(zips, carTable);
		
		JScrollPane listScrollPane = new JScrollPane(carList);
		listScrollPane.setPreferredSize(new Dimension(250, 200));
		this.add(listScrollPane, BorderLayout.EAST);
		
		loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		title = BorderFactory.createTitledBorder(loweredetched, "車輛MOD壓縮檔列表");
		listScrollPane.setBorder(title);
		
		//refresh all
		for (CarVO modVO : cars) {
			modVO.setStatus(Utils.checkCarStatus(modVO));
		}
		carTable.updateUI();
		
//		for (CarVO mod : cars) {
//			if (mod.getHtml() == null) {
//				for (CarZipVO zip : zips) {
//					if (zip.getFileUrl().equals(mod.getZipName())) {
//						mod.setHtml(zip.getHtml());
//						break;
//					}
//				}
//			}
//		}
		
		Utils.saveCarTable(tableModel.getModList());
	}
	
	/**
	 * @param exports
	 */
	public void importCarXml(List<CarExportVO> exports) {
		CarTableModel tableModel = (CarTableModel) carTable.getModel();
		List<CarVO> cars = tableModel.getModList();
		for (CarVO car : cars) {
			for (CarExportVO export : exports) {
				if (car.getName().equals(export.getName())) {
					if (car.isEnable() && !export.isEnable()) {
						//enable -> disable
						Utils.removeCarData(car);
						
						car.setEnable(false);
						car.setFileArray(null);
						car.setStatus(Utils.checkCarStatus(car));
						Utils.saveCarTable(cars);

						//直接幫忙刪除dlc資訊存dlclist.xml
						DlcListUtils.delDlc(car.getDlcName());
						
						//直接幫忙取消勾選trainerv.ini (若存在的話)
						TrainerUtils.disableCar(car);
					} else if (!car.isEnable() && export.isEnable()) {
						//disable -> enable
						Utils.copyCarDir(car);
						car.setEnable(true);
						
						Utils.saveCarTable(cars);
						
						//直接幫忙新增dlc資訊至dlclist.xml
						DlcListUtils.addDlc(car.getDlcName());
						
						//直接幫忙勾選trainerv.ini (若存在的話)
						TrainerUtils.enableCar(car);
					}
				}
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Utils.log(e.getActionCommand());
		CarTableModel tableModel = (CarTableModel) carTable.getModel();
		List<CarVO> mods = tableModel.getModList();
		CarVO mod = tableModel.getRow(carTable.getSelectedRow());
		
		switch (e.getActionCommand()) {
		case MENU_INSTALL:
			if (Utils.showComfirm("確定要重新安裝?", "確認")) {
				//解壓縮zip至目標目錄
				ZipUtils.unzipCar(mod.getZipName(), mod.getName());
			}
			break;
			
		case MENU_EDIT:
			CarEditFrame editFrame = new CarEditFrame(mod, carTable);
			Utils.showCenterFrame(editFrame);
			break;
			
		case MENU_DEL:
			if (mod.isEnable()) {
				Utils.showAlert("請先取消安裝之後再刪除車輛Mod!");
				return;
			}
			
			if (Utils.showComfirm("確定要刪除車輛Mod?", "確認")) {
				tableModel.removeRow(carTable.getSelectedRow());
				carTable.updateUI();
				
				Utils.saveCarTable(tableModel.getModList());
				
				String path = Utils.prop.getProperty(Constant.MOD_DIR) + File.separator + Constant.CARS_FOLDER + File.separator;
				File file = new File(path, mod.getName());
				if (file.exists() && file.isDirectory()) {
					file.delete();
					try {
						FileUtils.forceDelete(file);
					} catch (IOException e1) {
						e1.printStackTrace();
						Utils.log(e1.getMessage());
					}
					Utils.log("Car Mod: " + mod.getName() + " 已刪除 (" + file.getAbsolutePath() + ")");
				}
			}
			break;
			
		case MENU_REFRESH:
			for (CarVO modVO : mods) {
				modVO.setStatus(Utils.checkCarStatus(modVO));
			}
			carTable.updateUI();
			
			Utils.saveCarTable(tableModel.getModList());
			break;
			
		case MENU_SORT:
			List<String> brands = Utils.loadBrands();
			Map<String, List<CarVO>> brandCars = new LinkedHashMap<>();
			for (String brand : brands) {
				brandCars.put(brand, new ArrayList<CarVO>());
			}
			List<CarVO> others = new ArrayList<CarVO>();
			
			for (CarVO car : tableModel.getModList()) {
				boolean isOther = true;
				for (String brand : brandCars.keySet()) {
					if (isBrand(car, brand)) {
						brandCars.get(brand).add(car);
						isOther = false;
						break;
					}
				}
				if (isOther) {
					others.add(car);
				}
			}
			
			List<CarVO> sortedCars = new ArrayList<>();
			for (List<CarVO> cars : brandCars.values()) {
				sortedCars.addAll(cars);
			}
			sortedCars.addAll(others);
			tableModel.setCarList(sortedCars);
			
			carTable.updateUI();
			Utils.saveCarTable(tableModel.getModList());
			
			//若排序前有選擇車輛, 排序完後自動選擇該車輛
			if (mod != null) {
				for (int i=0; i<sortedCars.size(); i++) {
					CarVO vo = sortedCars.get(i);
					if (vo.getName().equals(mod.getName())) {
						carTable.setRowSelectionInterval(i, i);
						carTable.scrollRectToVisible(new Rectangle(carTable.getCellRect(i, 0, true)));
						carTable.updateUI();
						break;
					}
				}
			}
			break;
			
		case MENU_LOADCOVER:
			Thread coverthread = new Thread(new Runnable() {
				@Override
				public void run() {
					CoverUtils.loadCover(mod);
					carTable.updateUI();
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
					
					carTable.updateUI();
					Utils.saveCarTable(tableModel.getModList());
				}
			});
			
			thread.start();
			break;
			
		case MENU_CHECK_VERSION_ALL:
			Thread thread2 = new Thread(new Runnable() {
				@Override
				public void run() {
					for (CarVO car : tableModel.getModList()) {
						String ver = CoverUtils.loadVersion(car.getHtml());
						if (ver != null && !ver.equals(car.getVersion())) {
							car.setNewVersion(ver);
						} else {
							car.setNewVersion(null);
						}
					}
					
					carTable.updateUI();
					Utils.saveCarTable(tableModel.getModList());
				}
			});
			
			thread2.start();
			break;
			
		case MENU_EDIT_TRAINER:
			TrainerEditFrame trainerFrame = new TrainerEditFrame(mod, tableModel.getModList());
			Utils.showCenterFrame(trainerFrame);
			break;
		}
	}
	
	private boolean isBrand(CarVO car, String brand) {
		String carName = car.getName().toLowerCase();
		return carName.contains(brand.toLowerCase());
	}

	@Override
	public void modEnableUpdated(CarVO mod, boolean enable) {
		if (enable) {
			//勾選
			if (Utils.showComfirm("確定安裝車輛Mod至遊戲目錄?", "確認")) {
				Utils.copyCarDir(mod);
				mod.setEnable(true);
				
				CarTableModel tableModel = (CarTableModel) carTable.getModel();
				Utils.saveCarTable(tableModel.getModList());
				
				//直接幫忙新增dlc資訊至dlclist.xml
				DlcListUtils.addDlc(mod.getDlcName());
				
				//直接幫忙勾選trainerv.ini (若存在的話)
				TrainerUtils.enableCar(mod);
			}
		} else {
			//取消勾選
			if (Utils.showComfirm("確定將車輛Mod從遊戲目錄中移除?", "確認")) {
				Utils.removeCarData(mod);
				
				mod.setEnable(false);
				mod.setFileArray(null);
				
				CarTableModel tableModel = (CarTableModel) carTable.getModel();
				List<CarVO> mods = tableModel.getModList();
				for (CarVO modVO : mods) {
					modVO.setStatus(Utils.checkCarStatus(modVO));
				}
				
				Utils.saveCarTable(tableModel.getModList());

				//直接幫忙刪除dlc資訊存dlclist.xml
				DlcListUtils.delDlc(mod.getDlcName());
				
				//直接幫忙取消勾選trainerv.ini (若存在的話)
				TrainerUtils.disableCar(mod);
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
	        
	        CarTableModel tableModel = (CarTableModel) table.getModel();
	        CarVO mod = tableModel.getRow(row);
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
	 * CarTable drag&drop所需TransferHandler
	 */
	class TableTransferHandler extends StringTransferHandler {
		private static final long serialVersionUID = 1L;

		private int[] rows = null;

		private int addIndex = -1; // Location where items were added

		private int addCount = 0; // Number of items added.

		@Override
		protected String exportString(JComponent c) {
			JTable table = (JTable) c;
			CarTableModel model = (CarTableModel) table.getModel();
			rows = table.getSelectedRows();

			StringBuffer buff = new StringBuffer();

			for (int i = 0; i < rows.length; i++) {
				CarVO mod = model.getRow(rows[i]);
				buff.append(Utils.objToXml(CarVO.class, mod));
			}

			return buff.toString();
		}

		@Override
		protected void importString(JComponent c, String str) {
			JTable target = (JTable) c;
			CarTableModel model = (CarTableModel) target.getModel();
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
			
			CarVO mod = Utils.xmlToObj(CarVO.class, str);
			model.insertRow(index, mod);
			addCount = 1;
			
		}

		@Override
		protected void cleanup(JComponent c, boolean remove) {
			JTable source = (JTable) c;
			if (remove && rows != null) {
				CarTableModel model = (CarTableModel) source.getModel();

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
				
				Utils.saveCarTable(model.getModList());
			}
			rows = null;
			addCount = 0;
			addIndex = -1;
			source.updateUI();
		}
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
			CarTableModel model = (CarTableModel) table.getModel();
        	CarVO mod = model.getRow(table.getSelectedRow());
	        if (evt.getClickCount() == 2) {
	        	int col = table.columnAtPoint(evt.getPoint());
				if (col != CarModTableColumn.RATE.getIndex()) {
					if (mod == null) {
						Utils.showAlert("請先選擇一項車輛Mod!");
						return;
					}
					CarDetailFrame detailFrame = new CarDetailFrame(mod, table);
					Utils.showCenterFrame(detailFrame);
				}
	        }
	        
	        if (mod != null) {
	        	carList.setSelectedCar(mod);
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
					if (col == CarModTableColumn.RATE.getIndex()) {
						CarTableModel model = (CarTableModel) table.getModel();
						CarVO mod = model.getRow(table.getSelectedRow());
						mod.setRate(mod.getRate() + 1 > 3 ? 0 : mod.getRate() + 1);
						table.updateUI();
						Utils.saveCarTable(model.getModList());
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
	
//	class MotionAdapter extends MouseMotionAdapter {
//		@Override
//		public void mouseMoved(MouseEvent e) {
//			Point p = e.getPoint();
//			int row = carTable.rowAtPoint(p);
//			int col = carTable.columnAtPoint(p);
//
//			if ((row > -1 && row < carTable.getRowCount()) && (col > -1 && col < carTable.getColumnCount())) {
//				if (hintCell == null || (hintCell.x != col || hintCell.y != row)) {
//					Object value = carTable.getValueAt(row, col);
//					
//					if (value instanceof ImageIcon) {
//						hintCell = new Point(col, row);
//						CarTableModel model = (CarTableModel) carTable.getModel();
//						CarVO car = model.getRow(row);
//						coverFrame.showCover(car);
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
//                Rectangle bounds = carTable.getCellRect(hintCell.y, hintCell.x, true);
//                int x = bounds.x + bounds.width;
//                int y = bounds.y + bounds.height;
//
//                coverFrame.show(carTable, x, y);
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
