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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;

import mod.manager.constant.Constant;
import mod.manager.constant.ModIcon;
import mod.manager.constant.TrainerTableColumn;
import mod.manager.util.TrainerUtils;
import mod.manager.util.Utils;
import mod.manager.vo.CarTrainerArray;
import mod.manager.vo.CarTrainerVO;
import mod.manager.vo.CarVO;

public class TrainerEditFrame extends JFrame implements ActionListener, TableModelListener {
	private static final long serialVersionUID = 1L;

	private static final String MENU_SAVE = "保存";
	private static final String MENU_CANCEL = "關閉";
	private static final String MENU_OPEN = "檔案總管";
	private static final String MENU_GEN_PRICE = "產生車商Mod車價ini";
	
	private static final String MENU_RECOVER = "恢復預設值";
	
	private CarVO car;
	private List<CarVO> tableCars;
	private List<CarTrainerVO> cars;
	
	private JTable table;
	private TitledBorder title;
	private JScrollPane tableScrollPane;
	
	public TrainerEditFrame(CarVO car, List<CarVO> tableCars) {
		super();
		
		this.car = car;
		this.tableCars = tableCars;
		this.setBounds(100, 100, 1000, 700);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.getContentPane().setLayout(new BorderLayout(0, 0));
		this.setTitle("Trainer產車內容");
		this.setIconImage(ModIcon.TRAINER_EDIT.getImage());
		
		//load trainerv.ini
		cars = TrainerUtils.loadTrainer();
		
		JPanel ctrlPane = new JPanel();
		JLabel nameLabel = new JLabel("車輛Mod名稱: ");
		JTextField nameField = new JTextField(50);
		nameField.setText(this.car.getName().replaceAll("_", " "));
		nameField.setEditable(false);
		ctrlPane.add(nameLabel);
		ctrlPane.add(nameField);
		
		JPanel ctrlPane2 = new JPanel();
		JLabel callLabel = new JLabel("車輛呼叫名稱: ");
		JTextField callField = new JTextField(50);
		callField.setText(car.getCallName());
		callField.setEditable(false);
		ctrlPane2.add(callLabel);
		ctrlPane2.add(callField);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, ctrlPane, ctrlPane2);
		splitPane.setDividerSize(0);
		splitPane.setEnabled(false);
		
		this.getContentPane().add(splitPane, BorderLayout.NORTH);
		
		TrainerTableModel tableModel = new TrainerTableModel(cars);
		tableModel.addTableModelListener(this);
		table = new JTable(tableModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		table.getColumn(TrainerTableColumn.NUM.getName()).setCellRenderer(new LineCellRenderer());//NameCellRenderer
		table.getColumn(TrainerTableColumn.CALL.getName()).setCellRenderer(new NameCellRenderer());
		table.setDragEnabled(true);
		table.setDropMode(DropMode.USE_SELECTION);
		table.setTransferHandler(new TableTransferHandler());
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		table.getColumnModel().getColumn(0).setMaxWidth(25);
		table.getColumnModel().getColumn(1).setMaxWidth(50);
		table.getColumnModel().getColumn(1).setMinWidth(200);
		
		
		//Create the popup menu.
		JPopupMenu popup = new JPopupMenu();
		JMenuItem menuItem = new JMenuItem(MENU_RECOVER, ModIcon.RECOVERY);
	    menuItem.addActionListener(this);
	    popup.add(menuItem);
	    MouseListener popupListener = new PopupListener(popup, table);
	    table.addMouseListener(popupListener);
		
		tableScrollPane = new JScrollPane(table);
		tableScrollPane.setPreferredSize(new Dimension(250, 200));
		
		
		Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		title = BorderFactory.createTitledBorder(loweredetched, "trainerv.ini車輛呼叫列表");
		tableScrollPane.setBorder(title);
		updateTitle(cars);
		
		this.getContentPane().add(tableScrollPane, BorderLayout.CENTER);
		
		JPanel pane1 = new JPanel();
		JButton unButton = new JButton(MENU_SAVE, ModIcon.SAVE);
		unButton.addActionListener(this);
		pane1.add(unButton);
		JButton cancelButton = new JButton(MENU_CANCEL, ModIcon.CLOSE);
		cancelButton.addActionListener(this);
		pane1.add(cancelButton);
		JButton openButton = new JButton(MENU_OPEN, ModIcon.EXPLORER);
		openButton.addActionListener(this);
		pane1.add(openButton);
		JButton priceButton = new JButton(MENU_GEN_PRICE, ModIcon.TRAINER_EDIT);
		priceButton.addActionListener(this);
		pane1.add(priceButton);
		
		this.getContentPane().add(pane1, BorderLayout.SOUTH);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Utils.log(e.getActionCommand());
		switch (e.getActionCommand()) {
		case MENU_SAVE:
			TrainerTableModel model = (TrainerTableModel) table.getModel();
			TrainerUtils.saveTrainer(model.getModList());
			Utils.showAlert("保存成功!");
			break;
			
		case MENU_OPEN:
			try {
				//遊戲目錄下的trainerv.ini
				String path = Utils.prop.getProperty(Constant.GAME_DIR) + File.separator;
				File uriFile = new File(path, "trainerv.ini");
				if (uriFile.exists()) {
					Utils.open(uriFile.toURI());
				} else {
					Utils.showAlert("trainerv.ini不存在遊戲目錄中, 請先安裝trainer Mod!");
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			break;
			
		case MENU_GEN_PRICE:
			TrainerTableModel model2 = (TrainerTableModel) table.getModel();
			CarPriceFrame priceFrame = new CarPriceFrame(model2.getModList());
			Utils.showCenterFrame(priceFrame);
			break;
			
		case MENU_RECOVER:
			TrainerTableModel tableModel = (TrainerTableModel) table.getModel();
			//CarTrainerVO mod = tableModel.getRow(table.getSelectedRow());
			int[] rows = table.getSelectedRows();
			if (rows != null && rows.length > 0) {
				for (int index : rows) {
					CarTrainerVO mod = tableModel.getRow(index);
					if (mod != null) {
						mod.setDisplayName("Car");
						mod.setEnable("0");
						mod.setModelName("turismor");
					}
				}
				updateTitle(tableModel.getModList());
				table.updateUI();
			}
			
			break;
			
		case MENU_CANCEL:
			this.dispose();
		}
	}
	
	private void updateTitle(List<CarTrainerVO> cars) {
		int cnt = 0;
		for (CarTrainerVO car : cars) {
			if ("1".equals(car.getEnable())) {
				cnt++;
			}
		}
		title.setTitle("trainerv.ini車輛呼叫列表 (啟用:" + cnt + ")");
		tableScrollPane.updateUI();
	}
	
	/**
	 * TrainerTable drag&drop所需TransferHandler
	 */
	class TableTransferHandler extends StringTransferHandler {
		private static final long serialVersionUID = 1L;

		private int[] rows = null;

		private int addIndex = -1; // Location where items were added

		private int addCount = 0; // Number of items added.

		@Override
		protected String exportString(JComponent c) {
			JTable table = (JTable) c;
			TrainerTableModel model = (TrainerTableModel) table.getModel();
			rows = table.getSelectedRows();

			StringBuffer buff = new StringBuffer();

			List<CarTrainerVO> list = new ArrayList<>();
			for (int i = 0; i < rows.length; i++) {
				CarTrainerVO mod = model.getRow(rows[i]);
				list.add(mod);
			}
			CarTrainerArray array = new CarTrainerArray();
			array.setCarArray(list.toArray(new CarTrainerVO[list.size()]));
			buff.append(Utils.objToXml(CarTrainerArray.class, array));
			
			return buff.toString();
		}

		@Override
		protected void importString(JComponent c, String str) {
			JTable target = (JTable) c;
			TrainerTableModel model = (TrainerTableModel) target.getModel();
			int index = target.getSelectedRow();
			int selectRow = rows[rows.length - 1];
			
			int found = Arrays.binarySearch(rows, 0, rows.length, index);
			if (found >= 0) {
				//drag跟drop是同位置, 不做事
				rows = null;
				return;
			} else if (index > selectRow) {
				//drop位置比drag位置底下, index要加1
				index++;
			}
			
			addIndex = index;
			
			CarTrainerArray array = Utils.xmlToObj(CarTrainerArray.class, str);
			if (array.getCarArray() != null && array.getCarArray().length > 0) {
				for (int i=0; i<array.getCarArray().length; i++) {
					CarTrainerVO mod = array.getCarArray()[i];
					model.insertRow(index, mod);
					index++;
				}
					
				addCount = array.getCarArray().length;
			} else {
				addCount = 0;
			}
			
		}

		@Override
		protected void cleanup(JComponent c, boolean remove) {
			JTable source = (JTable) c;
			if (remove && rows != null) {
				TrainerTableModel model = (TrainerTableModel) source.getModel();

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
				
				//TrainerUtils.saveTrainer(model.getModList());
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

		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			//有選擇到row時才顯示menu
			int row = table.rowAtPoint(e.getPoint());
			if (table.isRowSelected(row)) {
				maybeShowPopup(e);
			}
		}

		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}
	
	/**
	 * 
	 */
	class LineCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(
	                        JTable table, Object value,
	                        boolean isSelected, boolean hasFocus,
	                        int row, int column) {
	        JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	        
	        if (row % 40 == 0) {
	        	c.setBackground(Color.GRAY);
	        } else {
	        	c.setBackground(Color.WHITE);
	        }
	        return c;
	    }
	}
	
	/**
	 * 
	 */
	class NameCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(
	                        JTable table, Object value,
	                        boolean isSelected, boolean hasFocus,
	                        int row, int column) {
	        JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	        //檢查是否car mod已經被disable了
	        
	        TrainerTableModel model = (TrainerTableModel) table.getModel();
	        CarTrainerVO vo = model.getRow(row);
	        
	        boolean isEnable = false;
	        if (!"turismor".equals(vo.getModelName())) {
	        	for (CarVO car : tableCars) {
	        		String callName = car.getCallName();
	        		if (callName.contains(",")) {
	        			String[] callNames = callName.split(",");
	        			for (String name : callNames) {
	        				if (name != null && vo.getModelName().equals(name.trim()) && car.isEnable()) {
	        					isEnable = true;
			        			break;
	        				}
	        			}
	        		} else {
	        			if (car.getCallName().equals(vo.getModelName()) && car.isEnable()) {
	        				isEnable = true;
		        			break;
		        		}
	        		}
	        	}
	        } else {
	        	isEnable = true;
	        }
	        
	        if (isEnable) {
	        	if (!"turismor".equals(vo.getModelName())) {
	        		if ("1".equals(vo.getEnable())) {
		        		c.setForeground(Color.BLACK);
		        	} else {
		        		c.setForeground(Color.GREEN);
		        	}
	        	} else {
	        		c.setForeground(Color.BLACK);
	        	}
	        	
	        } else {
	        	if ("1".equals(vo.getEnable())) {
	        		c.setForeground(Color.RED);
	        	} else {
	        		c.setForeground(Color.GRAY);
	        	}
	        }
	        
	        if (vo.getModelName().equals(car.getCallName())) {
	        	c.setBackground(Color.PINK);
	        } else {
	        	c.setBackground(Color.WHITE);
	        }
	        
	        return c;
	    }
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		if (e != null && e.getColumn() == TrainerTableColumn.ENABLE.getIndex()) {
			TrainerTableModel tableModel = (TrainerTableModel) table.getModel();
			updateTitle(tableModel.getModList());
			
			CarTrainerVO vo = tableModel.getRow(e.getFirstRow());
			if ("0".equals(vo.getEnable())) {
				if (!vo.getDisplayName().startsWith("[DEL]")) {
					vo.setDisplayName("[DEL]" + vo.getDisplayName());
				}
			} else {
				if (vo.getDisplayName().startsWith("[DEL]")) {
					vo.setDisplayName(vo.getDisplayName().replaceFirst("\\[DEL\\]", ""));
				}
			}
			//table.updateUI();
		}
	}
}
