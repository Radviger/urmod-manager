package mod.manager;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Date;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import mod.manager.constant.ModIcon;
import mod.manager.constant.Status;
import mod.manager.util.Utils;
import mod.manager.vo.CarZipVO;
import mod.manager.vo.ModZipVO;

/**
 * 新增車輛Mod壓縮檔視窗
 */
public class NewCarZipFrame extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;

	private static final String MENU_OPEN = "開啟檔案";
	private static final String MENU_OK = "確定";
	private static final String MENU_CANCEL = "取消";
	
	private CarModList modList;
	private JFileChooser fc;
	private JTextField zipPathTextField, htmlTextField, versionTextField, callTextField, dlcTextField;
	
	public NewCarZipFrame(CarModList modList) {
		super();
		this.modList = modList;
		this.setBounds(100, 100, 800, 350);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		this.getContentPane().setLayout(new BorderLayout(0, 0));
		this.setTitle("新增車輛Mod壓縮檔");
		this.setIconImage(ModIcon.ZIP.getImage());
		
		File downloadDir = new File(Utils.getCarZipDir());
		fc = new JFileChooser(downloadDir);
		
		JPanel mainPane = new JPanel();
		mainPane.setLayout(new GridLayout(5, 1));
		
		JPanel ctrlPane = new JPanel();
		JLabel zipDirLabel = new JLabel("壓縮檔位置:");
		zipPathTextField = new JTextField(40);
		JButton saveDirButton = new JButton(MENU_OPEN, ModIcon.FOLDER_OPEN);
		saveDirButton.addActionListener(this);
		ctrlPane.add(zipDirLabel);
		ctrlPane.add(zipPathTextField);
		ctrlPane.add(saveDirButton);
		
		JPanel ctrlPane3 = new JPanel();
		JLabel versionLabel = new JLabel("版本:");
		versionTextField = new JTextField(40);
		ctrlPane3.add(versionLabel);
		ctrlPane3.add(versionTextField);
		
		JPanel ctrlPane2 = new JPanel();
		JLabel htmlLabel = new JLabel("來源網址:");
		htmlTextField = new JTextField(40);
		ctrlPane2.add(htmlLabel);
		ctrlPane2.add(htmlTextField);
		
		JPanel ctrlPane4 = new JPanel();
		JLabel callLabel = new JLabel("車輛呼叫名稱:");
		callTextField = new JTextField(40);
		ctrlPane4.add(callLabel);
		ctrlPane4.add(callTextField);
		
		JPanel ctrlPane5 = new JPanel();
		JLabel dlcLabel = new JLabel("車輛dlc目錄:");
		dlcTextField = new JTextField(40);
		ctrlPane5.add(dlcLabel);
		ctrlPane5.add(dlcTextField);
		
		mainPane.add(ctrlPane);
		mainPane.add(ctrlPane3);
		mainPane.add(ctrlPane2);
		mainPane.add(ctrlPane4);
		mainPane.add(ctrlPane5);
		
		this.getContentPane().add(mainPane, BorderLayout.CENTER);
		
		
		JPanel buttomPane = new JPanel();
		JButton saveButton = new JButton(MENU_OK, ModIcon.REINSTALL);
		saveButton.addActionListener(this);
		buttomPane.add(saveButton);
		JButton cancelButton = new JButton(MENU_CANCEL, ModIcon.CLOSE);
		cancelButton.addActionListener(this);
		buttomPane.add(cancelButton);
		this.getContentPane().add(buttomPane, BorderLayout.SOUTH);
	}
	
	/**
	 * @param file
	 */
	public void setZipFile(File file) {
		zipPathTextField.setText(file.getAbsolutePath());
		
		//用檔名去找是否有類似的html
        CarZipVO mappedVO = Utils.getCarMappedHtml(file.getName());
        if (mappedVO != null) {
        	htmlTextField.setText(mappedVO.getHtml());
        	callTextField.setText(mappedVO.getCallName());
        	dlcTextField.setText(mappedVO.getDlcName());
        }
        versionTextField.setText(Utils.getVersion(file.getName()));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Utils.log(e.getActionCommand());
		switch (e.getActionCommand()) {
		case MENU_OPEN:
			int returnVal = fc.showOpenDialog(this);

	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File file = fc.getSelectedFile();
	            //This is where a real application would open the file.
	            Utils.log("選擇檔案: " + file.getName() + ".");
	            
	            zipPathTextField.setText(file.getAbsolutePath());
	            
	            //用檔名去找是否有類似的html
	            CarZipVO mappedVO = Utils.getCarMappedHtml(file.getName());
	            if (mappedVO != null) {
	            	htmlTextField.setText(mappedVO.getHtml());
	            	callTextField.setText(mappedVO.getCallName());
	            	dlcTextField.setText(mappedVO.getDlcName());
	            }
	            versionTextField.setText(Utils.getVersion(file.getName()));
	            
	        } else {
	        	Utils.log("取消選擇檔案.");
	        }
			break;
			
		case MENU_OK:
			//檢查輸入是否正確
			File file = new File(zipPathTextField.getText());
			if (!file.exists()) {
				Utils.showAlert("請輸入正確檔案!");
				return;
			}
			
			//檢查檔案是否已存在
			boolean exist = false;
			List<ModZipVO> zips = Utils.loadModZipList();
			for (ModZipVO zip : zips) {
				if (file.equals(new File(zip.getFileUrl()))) {
					exist = true;
					break;
				}
			}
			if (exist) {
				Utils.showAlert("檔案已存在車輛Mod壓縮檔管理中!");
				return;
			}
			
			CarZipVO zip = new CarZipVO();
			zip.setName(file.getName());
			zip.setFileUrl(file.getAbsolutePath());
			zip.setHtml(htmlTextField.getText());
			zip.setVersion(versionTextField.getText());
			zip.setStatus(Status.OK);
			zip.setDownloadDate(new Date());
			zip.setCallName(callTextField.getText());
			zip.setDlcName(dlcTextField.getText());
			
			Utils.log("新增: " + zip.toString());
			
			//存回xml
			DefaultListModel<CarZipVO> listModel = (DefaultListModel<CarZipVO>) modList.getModel();
			listModel.addElement(zip);
			CarZipVO[] array = new CarZipVO[listModel.size()];
			listModel.copyInto(array);
			Utils.saveCarZipList(array);
			
			this.dispose();
			
			//自動選擇剛剛新增的mod
			int index = modList.getModel().getSize() - 1;
			modList.setSelectedIndex(index);
			modList.ensureIndexIsVisible(index);
			modList.repaint();
			break;
			
		case MENU_CANCEL:
			this.dispose();
		}
	}
}
