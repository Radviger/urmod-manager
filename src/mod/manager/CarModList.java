package mod.manager;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

import mod.manager.constant.ModIcon;
import mod.manager.constant.Status;
import mod.manager.util.Utils;
import mod.manager.util.ZipUtils;
import mod.manager.vo.CarVO;
import mod.manager.vo.CarZipVO;

/**
 * 車輛Mod壓縮檔List
 */
public class CarModList extends JList<CarZipVO> implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	//popup menu items
	private static final String MENU_NEW = "新增車輛Mod壓縮檔";
	private static final String MENU_EDIT = "編輯車輛Mod壓縮檔";
	private static final String MENU_INSTALL = "安裝車輛Mod";
	private static final String MENU_DEL = "刪除車輛Mod壓縮檔";
	private static final String MENU_DETAIL = "車輛Mod壓縮檔內容";
	private static final String MENU_REFRESH = "重新整理";

	private JTable modTable;
	private List<CarZipVO> zips;
	
	public CarModList(List<CarZipVO> zips, JTable modTable) {
		super();
		this.zips = zips;
		this.modTable = modTable;
		DefaultListModel<CarZipVO> listModel = new DefaultListModel<>();
		for (CarZipVO zip : zips) {
			listModel.addElement(zip);
		}
		this.setModel(listModel);
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.setLayoutOrientation(JList.VERTICAL);
		this.setCellRenderer(new ModListCellRenderer());
		this.setDragEnabled(true);
		this.setTransferHandler(new ListTransferHandler(this));
		
		//Create the popup menu.
		JPopupMenu popup = new JPopupMenu();
		JMenuItem menuItem = new JMenuItem(MENU_NEW, ModIcon.ZIP);
	    menuItem.addActionListener(this);
	    popup.add(menuItem);
	    menuItem = new JMenuItem(MENU_EDIT, ModIcon.CAR_EDIT);
	    menuItem.addActionListener(this);
	    popup.add(menuItem);
	    menuItem = new JMenuItem(MENU_INSTALL, ModIcon.CAR_ADD);
	    menuItem.addActionListener(this);
	    popup.add(menuItem);
	    menuItem = new JMenuItem(MENU_DEL, ModIcon.DELETE);
	    menuItem.addActionListener(this);
	    popup.add(menuItem);
	    menuItem = new JMenuItem(MENU_DETAIL, ModIcon.QUERY);
	    menuItem.addActionListener(this);
	    popup.add(menuItem);
	    menuItem = new JMenuItem(MENU_REFRESH, ModIcon.REFRESH);
	    menuItem.addActionListener(this);
	    popup.add(menuItem);
	    
	    
	    MouseListener popupListener = new PopupListener(popup, this);
	    this.addMouseListener(popupListener);
	    
	    Utils.buildCarMap(zips);
	}
	
	/**
	 * 設定選擇的zip
	 * 
	 * @param car
	 */
	public void setSelectedCar(CarVO car) {
		if(car == null)
            setSelectedIndex(-1);
		
		CarZipVO zip = getSelectedValue();
		if (zip == null || !car.getZipName().equals(zip.getFileUrl())) {
			int i,c;
			ListModel<CarZipVO> dm = getModel();
            for(i=0,c=dm.getSize();i<c;i++) {
            	CarZipVO vo = dm.getElementAt(i);
            	if (car.getZipName().equals(vo.getFileUrl())) {
            		setSelectedIndex(i);
            		ensureIndexIsVisible(i);
            		repaint(); 
            		return;
            	}
            }
            setSelectedIndex(-1);
		}
        repaint();
	}
	
	/**
	 *
	 */
	class ModListCellRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 1L;

		public ModListCellRenderer() {
			super();
		}
		
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			DefaultListCellRenderer comp = (DefaultListCellRenderer) 
					super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			CarZipVO mod = (CarZipVO) value;
			
			if (Status.OK == mod.getStatus()) {
				comp.setText((index+1) + ". " + mod.getName() + " - " + mod.getVersion());
				comp.setForeground(Color.BLACK);
			} else {
				comp.setText((index+1) + ". " + mod.getName() + " - Missing!");
				comp.setForeground(Color.RED);
			}
			
			comp.setToolTipText(mod.getName() + " - " + mod.getVersion() + " 新增日期: " + Utils.dateTimeToString(mod.getDownloadDate(), "yyyy/MM/dd HH:mm:ss"));
			return comp;
		}
	}
	
	
	/**
	 *
	 */
	class PopupListener extends MouseAdapter {
		JPopupMenu popup;
		CarModList list;

		PopupListener(JPopupMenu popupMenu, CarModList list) {
			popup = popupMenu;
			this.list = list;
		}

		public void mouseClicked(MouseEvent evt) {
	        @SuppressWarnings("unchecked")
			JList<CarZipVO> list = (JList<CarZipVO>) evt.getSource();
	        if (evt.getClickCount() == 2) {
	        	CarZipVO zipvo2 = list.getSelectedValue();
				if (zipvo2 == null) {
					Utils.showAlert("請先選擇一項Mod壓縮檔!");
					return;
				}
				CarZipDetailFrame detailFrame = new CarZipDetailFrame(zipvo2, modTable);
				Utils.showCenterFrame(detailFrame);
	        }
	    }
		
		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			list.setSelectedIndex(list.locationToIndex(e.getPoint()));
			maybeShowPopup(e);
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
		switch (e.getActionCommand()) {
		case MENU_NEW:
			NewCarZipFrame newFrame = new NewCarZipFrame(this);
			Utils.showCenterFrame(newFrame);
			break;
			
		case MENU_EDIT:
			CarZipVO editZipvo = this.getSelectedValue();
			if (editZipvo == null) {
				Utils.showAlert("請先選擇一項車輛Mod壓縮檔!");
				return;
			}
			CarZipEditFrame editFrame = new CarZipEditFrame(editZipvo, this, modTable);
			Utils.showCenterFrame(editFrame);
			break;
			
		case MENU_INSTALL:
			CarZipVO installZipvo = this.getSelectedValue();
			if (installZipvo == null) {
				Utils.showAlert("請先選擇一項車輛Mod壓縮檔!");
				return;
			}
			InstallCarFrame installFrame = new InstallCarFrame(installZipvo, modTable);
			Utils.showCenterFrame(installFrame);
			break;
			
		case MENU_DEL:
			CarZipVO zipvo = this.getSelectedValue();
			if (zipvo == null) {
				Utils.showAlert("請先選擇一項車輛Mod壓縮檔!");
				return;
			}
			
			if (Utils.showComfirm("確定刪除?", "刪除車輛Mod壓縮檔")) {
				DefaultListModel<CarZipVO> listModel = (DefaultListModel<CarZipVO>) this.getModel();
				listModel.remove(this.getSelectedIndex());
				CarZipVO[] zipArray = new CarZipVO[listModel.size()];
				listModel.copyInto(zipArray);
				zips = Arrays.asList(zipArray);
				this.repaint();
				Utils.log("已刪除!");
				
				File file = new File(zipvo.getFileUrl());
				if (file.exists()) {
					file.delete();
				}
				
				Utils.saveCarZipList(zips);
			}
			break;
			
		case MENU_DETAIL:
			CarZipVO zipvo2 = this.getSelectedValue();
			if (zipvo2 == null) {
				Utils.showAlert("請先選擇一項車輛Mod壓縮檔!");
				return;
			}
			CarZipDetailFrame detailFrame = new CarZipDetailFrame(zipvo2, modTable);
			Utils.showCenterFrame(detailFrame);
			break;
			
		case MENU_REFRESH:
			for (CarZipVO zip : zips) {
				File file = new File(zip.getFileUrl());
				if (file.exists()) {
					zip.setStatus(Status.OK);
				} else {
					Utils.log(zip.getFileUrl() + " is missing!");
					zip.setStatus(Status.MISS);
				}
			}
			this.repaint();
		}
	}
	
	
	/**
	 *
	 */
	class ListTransferHandler extends FileTransferHandler {
		private static final long serialVersionUID = 1L;

		private CarModList modList;
		
		private int[] indices = null;
		private int addIndex = -1; // Location where items were added
		private int addCount = 0; // Number of items added.
		
		public ListTransferHandler(CarModList modList) {
			this.modList = modList;
		}

		// Bundle up the selected items in the list
		// as a single string, for export.
		protected String exportString(JComponent c) {
			@SuppressWarnings("unchecked")
			JList<CarZipVO> list = (JList<CarZipVO>) c;
			indices = list.getSelectedIndices();
			CarZipVO zip = list.getSelectedValue();
			//Object[] values = list.getSelectedValues();

			return Utils.objToXml(CarZipVO.class, zip);
		}

		// Take the incoming string and wherever there is a
		// newline, break it into a separate item in the list.
		protected void importString(JComponent c, String str) {
			@SuppressWarnings("unchecked")
			JList<CarZipVO> target = (JList<CarZipVO>) c;
			DefaultListModel<CarZipVO> listModel = (DefaultListModel<CarZipVO>) target.getModel();
			int index = target.getSelectedIndex();

			// Prevent the user from dropping data back on itself.
			// For example, if the user is moving items #4,#5,#6 and #7 and
			// attempts to insert the items after item #5, this would
			// be problematic when removing the original items.
			// So this is not allowed.

			int selectRow = indices[0];
			if (index == selectRow) {
				//drag跟drop是同位置, 不做事
				indices = null;
				return;
			} else if (index > selectRow) {
				//drop位置比drag位置底下, index要加1
				index++;
			}
			
			CarZipVO zip = Utils.xmlToObj(CarZipVO.class, str);
			addIndex = index;
			addCount = 1;
			listModel.add(index, zip);
		}
		
		@Override
		protected void importFile(JComponent c, List<File> files) {
			for (File file : files) {
				if (ZipUtils.checkZipFile(file)) {
					File dest = Utils.copyCarZip(file);
					
					Utils.log("adding " + dest.getAbsolutePath() + " ...");
					
					NewCarZipFrame newFrame = new NewCarZipFrame(modList);
					newFrame.setZipFile(dest);
					Utils.showCenterFrame(newFrame);
				}
			}
		}

		// If the remove argument is true, the drop has been
		// successful and it's time to remove the selected items
		// from the list. If the remove argument is false, it
		// was a Copy operation and the original list is left
		// intact.
		protected void cleanup(JComponent c, boolean remove) {
			if (remove && indices != null) {
				@SuppressWarnings("unchecked")
				JList<CarZipVO> source = (JList<CarZipVO>) c;
				DefaultListModel<CarZipVO> model = (DefaultListModel<CarZipVO>) source.getModel();
				// If we are moving items around in the same list, we
				// need to adjust the indices accordingly, since those
				// after the insertion point have moved.
				if (addCount > 0) {
					for (int i = 0; i < indices.length; i++) {
						if (indices[i] > addIndex) {
							indices[i] += addCount;
						}
					}
				}
				for (int i = indices.length - 1; i >= 0; i--) {
					model.remove(indices[i]);
				}
				if (addIndex > indices[0])
					addIndex--;
				source.setSelectedIndex(addIndex);
				
				CarZipVO[] array = new CarZipVO[model.getSize()];
				model.copyInto(array);
				Utils.saveCarZipList(array);
				
			}
			indices = null;
			addCount = 0;
			addIndex = -1;
		}

	}
}
