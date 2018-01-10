package mod.manager;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

import lombok.Getter;
import mod.manager.constant.Constant;
import mod.manager.constant.ModIcon;
import mod.manager.util.DlcListUtils;
import mod.manager.util.TrainerUtils;
import mod.manager.util.Utils;
import mod.manager.vo.CarVO;

/**
 *
 */
public class CarDetailFrame extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;

	private static final String MENU_INSTALL = "安裝MOD";
	private static final String MENU_UNINSTALL = "移除MOD";
	private static final String MENU_BACKUP = "回存MOD";
	private static final String MENU_OPENDIR = "檔案總管";
	private static final String MENU_OPENZIP = "開啟MOD壓縮檔";
	private static final String MENU_CANCEL = "關閉";
	
	private CarVO mod;
	private JTable modTable;
	private JButton inButton, unButton, backupButton;
	@Getter
	private CarGameTreePanel gameTreePanel;
	@Getter
	private CarTreePanel modTreePanel;
	
	public CarDetailFrame(CarVO mod, JTable modTable) {
		super();
		this.mod = mod;
		this.modTable = modTable;
		this.setBounds(100, 100, 1000, 700);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		this.getContentPane().setLayout(new BorderLayout(0, 0));
		this.setTitle("車輛Mod內容");
		this.setIconImage(ModIcon.CAR.getImage());
		
		JPanel mainPane = new JPanel();
		mainPane.setLayout(new GridLayout(1, 2));
		
		modTreePanel = new CarTreePanel(mod);
		gameTreePanel = new CarGameTreePanel(mod, this);
		mainPane.add(modTreePanel);
		mainPane.add(gameTreePanel);
		this.getContentPane().add(mainPane, BorderLayout.CENTER);
		
		
		JPanel buttomPane = new JPanel();
		buttomPane.setLayout(new GridLayout(4, 1));
		
		JPanel pane2 = new JPanel();
		JLabel dlcLabel = new JLabel("記得修改dlclist.xml:");
		JTextField dlcTextField = new JTextField(50);
		dlcTextField.setText("<Item>dlcpacks:\\" + mod.getDlcName() + "\\</Item>");
		dlcTextField.setEditable(false);
		JButton dlclistButton = new JButton("開啟", ModIcon.FOLDER_OPEN);
		dlclistButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					//dlclist.xml目錄
					String path = Utils.prop.getProperty(Constant.MOD_DIR) + File.separator + 
							Constant.OPENIV_DLCLIST_DIR + File.separator;
					File uriFile = new File(path);
					Utils.open(uriFile.toURI());
				} catch (Exception e1) {
					e1.printStackTrace();
					Utils.showAlert(e1.getMessage());
				}
			}
		});
		pane2.add(dlcLabel);
		pane2.add(dlcTextField);
		pane2.add(dlclistButton);
		
		JPanel pane21 = new JPanel();
		JLabel dlcTipLabel = new JLabel("OpenIV dlclist.xml 位置: update\\update.rpf\\common\\data\\dlclist.xml");
		pane21.add(dlcTipLabel);
		
		JPanel pane3 = new JPanel();
		JLabel callLabel = new JLabel("記得修改trainerv.ini:");
		JTextField callTextField = new JTextField(50);
		callTextField.setText("EnableX=1   ModelNameX=" + mod.getCallName() + "   DisplayNameX=" + mod.getName());
		callTextField.setEditable(false);
		JButton iniButton = new JButton("編輯", ModIcon.TRAINER_EDIT);
		iniButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CarTableModel tableModel = (CarTableModel) modTable.getModel();
				TrainerEditFrame trainerFrame = new TrainerEditFrame(mod, tableModel.getModList());
				Utils.showCenterFrame(trainerFrame);
			}
		});
		pane3.add(callLabel);
		pane3.add(callTextField);
		pane3.add(iniButton);
		
		JPanel pane1 = new JPanel();
		inButton = new JButton(MENU_INSTALL, ModIcon.CAR_ADD);
		inButton.addActionListener(this);
		pane1.add(inButton);
		unButton = new JButton(MENU_UNINSTALL, ModIcon.CAR_DEL);
		unButton.addActionListener(this);
		pane1.add(unButton);
		backupButton = new JButton(MENU_BACKUP, ModIcon.RECOVERY);
		backupButton.addActionListener(this);
		backupButton.setToolTipText("把MOD資料從遊戲目錄底下備份回MOD資料夾");
		pane1.add(backupButton);
		JButton openDirButton = new JButton(MENU_OPENDIR, ModIcon.FOLDER_OPEN);
		openDirButton.addActionListener(this);
		pane1.add(openDirButton);
		JButton openZipButton = new JButton(MENU_OPENZIP, ModIcon.ZIP);
		openZipButton.addActionListener(this);
		pane1.add(openZipButton);
		JButton cancelButton = new JButton(MENU_CANCEL, ModIcon.CLOSE);
		cancelButton.addActionListener(this);
		pane1.add(cancelButton);

		buttomPane.add(pane2);
		buttomPane.add(pane21);
		buttomPane.add(pane3);
		buttomPane.add(pane1);
		this.getContentPane().add(buttomPane, BorderLayout.SOUTH);
		
		refreshButton();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Utils.log(e.getActionCommand());
		switch (e.getActionCommand()) {
		case MENU_INSTALL:
			if (Utils.showComfirm("確定安裝車輛Mod至遊戲目錄?", "確認")) {
				Utils.copyCarDir(mod);
				mod.setEnable(true);
				
				CarTableModel tableModel = (CarTableModel) modTable.getModel();
				Utils.saveCarTable(tableModel.getModList());
				
				//直接幫忙新增dlc資訊至dlclist.xml
				DlcListUtils.addDlc(mod.getDlcName());
				
				//直接幫忙勾選trainerv.ini (若存在的話)
				TrainerUtils.enableCar(mod);
			}
			modTable.updateUI();
			modTreePanel.refresh();
			gameTreePanel.refresh();
			refreshButton();
			break;
			
		case MENU_UNINSTALL:
			if (Utils.showComfirm("確定將車輛Mod從遊戲目錄中移除?", "確認")) {
				Utils.removeCarData(mod);
				
				mod.setEnable(false);
				mod.setFileArray(null);
				
				CarTableModel tableModel = (CarTableModel) modTable.getModel();
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
			modTable.updateUI();
			modTreePanel.refresh();
			gameTreePanel.refresh();
			refreshButton();
			break;
			
		case MENU_BACKUP:
			Utils.backupCarDir(mod);
			
			CarTableModel tableModel = (CarTableModel) modTable.getModel();
			List<CarVO> mods = tableModel.getModList();
			for (CarVO modVO : mods) {
				modVO.setStatus(Utils.checkCarStatus(modVO));
			}
			
			Utils.saveCarTable(tableModel.getModList());
			modTable.updateUI();
			break;
			
		case MENU_OPENDIR:
			try {
				//MOD目錄
				String path = Utils.prop.getProperty(Constant.MOD_DIR) + File.separator + 
						Constant.CARS_FOLDER + File.separator + mod.getName();
				File uriFile = new File(path);
				Utils.open(uriFile.toURI());
			} catch (Exception e1) {
				e1.printStackTrace();
				Utils.showAlert(e1.getMessage());
			}
			break;
			
		case MENU_OPENZIP:
			try {
				//ZIP檔案
				File uriFile = new File(mod.getZipName());
				Utils.open(uriFile.toURI());
			} catch (Exception e1) {
				e1.printStackTrace();
				Utils.showAlert(e1.getMessage());
			}
			break;
			
		case MENU_CANCEL:
			this.dispose();
		}
	}
	
	public void refresh() {
		mod.setStatus(Utils.checkCarStatus(mod));
		modTable.updateUI();
		ModTableModel tableModel = (ModTableModel) modTable.getModel();
		Utils.saveModTable(tableModel.getModList());
	}
	
	private void refreshButton() {
		unButton.setEnabled(mod.isEnable());
		unButton.updateUI();
		backupButton.setEnabled(mod.isEnable());
		backupButton.updateUI();
		inButton.setEnabled(!mod.isEnable());
		inButton.updateUI();
	}
	
}
