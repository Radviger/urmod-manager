package mod.manager;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

import mod.manager.constant.Constant;
import mod.manager.constant.ModIcon;
import mod.manager.constant.Status;
import mod.manager.util.Utils;
import mod.manager.util.ZipUtils;
import mod.manager.vo.CarVO;
import mod.manager.vo.CarZipVO;

/**
 * 車輛Mod安裝頁面
 */
public class InstallCarFrame extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;

	private static final String MENU_OK = "確定";
	private static final String MENU_CANCEL = "取消";
	
	private CarZipVO zip;
	private JTable modTable;
	private JTextField dirTextField;
	
	public InstallCarFrame(CarZipVO zip, JTable modTable) {
		super();
		this.modTable = modTable;
		this.zip = zip;
		this.setBounds(100, 100, 750, 250);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		this.getContentPane().setLayout(new BorderLayout(0, 0));
		this.setTitle("安裝車輛Mod");
		this.setIconImage(ModIcon.CAR_ADD.getImage());
		
		JPanel mainPane = new JPanel();
		mainPane.setLayout(new GridLayout(1, 1));
		
		JPanel ctrlPane = new JPanel();
		JLabel dirLabel = new JLabel("車輛Mod名稱:");
		dirTextField = new JTextField(40);
		dirTextField.setText(Utils.getFileName(zip.getName()));
		ctrlPane.add(dirLabel);
		ctrlPane.add(dirTextField);
		
		mainPane.add(ctrlPane);
		
		this.getContentPane().add(mainPane, BorderLayout.CENTER);
		
		JPanel buttomPane = new JPanel();
		JButton saveButton = new JButton(MENU_OK, ModIcon.CAR_ADD);
		saveButton.addActionListener(this);
		buttomPane.add(saveButton);
		JButton cancelButton = new JButton(MENU_CANCEL, ModIcon.CLOSE);
		cancelButton.addActionListener(this);
		buttomPane.add(cancelButton);
		this.getContentPane().add(buttomPane, BorderLayout.SOUTH);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Utils.log(e.getActionCommand());
		switch (e.getActionCommand()) {
		case MENU_OK:
			String modName = dirTextField.getText();
			if (modName == null || "".equals(modName)) {
				Utils.showAlert("請輸入正確車輛Mod名稱!");
				return;
			}
			
			//檢查目錄是否已存在
			String fileDir = Utils.prop.getProperty(Constant.MOD_DIR) + File.separator + Constant.CARS_FOLDER + File.separator;
			File file = new File(fileDir, modName);
			if (file.isDirectory() && file.exists()) {
				if (!Utils.showComfirm("目錄已存在, 是否要覆蓋?", "確認覆蓋")) {
					this.dispose();
				}
			} else {
				file.mkdirs();
				Utils.log(file.getAbsolutePath() + " 已建立");
			}
			
			//解壓縮zip至目標目錄
			ZipUtils.unzipCar(zip.getFileUrl(), file.getName());
			
			//新增一筆mod至modtable
			CarVO modVO = new CarVO();
			modVO.setInstallDate(new Date());
			modVO.setName(file.getName());
			modVO.setZipName(zip.getFileUrl());
			modVO.setStatus(Status.OK);
			modVO.setVersion(zip.getVersion());
			modVO.setCallName(zip.getCallName());
			modVO.setDlcName(zip.getDlcName());
			modVO.setHtml(zip.getHtml());
			
			CarTableModel tableModel = (CarTableModel) modTable.getModel();
			tableModel.addMod(modVO);
			modTable.updateUI();
			
			//存mod.xml
			Utils.saveCarTable(tableModel.getModList());
			
			this.dispose();
			
			//自動選擇剛剛新增的mod
			int index = tableModel.getRowCount() - 1;
			modTable.setRowSelectionInterval(index, index);
			modTable.scrollRectToVisible(new Rectangle(modTable.getCellRect(index, 0, true)));
			modTable.updateUI();
			break;
			
		case MENU_CANCEL:
			this.dispose();
		}
	}

}
