package mod.manager;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

import mod.manager.constant.ModIcon;
import mod.manager.util.Utils;
import mod.manager.vo.ModVO;
import mod.manager.vo.ModZipVO;

/**
 * 編輯Mod壓縮檔內容視窗
 */
public class ModZipEditFrame extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;

	private static final String MENU_SAVE = "確定";
	private static final String MENU_CANCEL = "關閉";
	
	private ModZipVO zip;
	private ModList modList;
	private JTable modTable;
	private JTextField nameTextField, versionTextField, htmlTextField;
	
	public ModZipEditFrame(ModZipVO zip, ModList modList, JTable modTable) {
		super();
		this.zip = zip;
		this.modList = modList;
		this.modTable = modTable;
		this.setBounds(100, 100, 800, 250);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		this.getContentPane().setLayout(new BorderLayout(0, 0));
		this.setTitle("Mod壓縮檔內容");
		this.setIconImage(ModIcon.ZIP.getImage());
		
		JPanel mainPane = new JPanel();
		mainPane.setLayout(new GridLayout(3, 1));
		
		JPanel ctrlPane = new JPanel();
		JLabel zipDirLabel = new JLabel("壓縮檔名稱:");
		nameTextField = new JTextField(40);
		nameTextField.setText(zip.getName());
		ctrlPane.add(zipDirLabel);
		ctrlPane.add(nameTextField);
		
		JPanel ctrlPane3 = new JPanel();
		JLabel versionLabel = new JLabel("版本:");
		versionTextField = new JTextField(40);
		versionTextField.setText(zip.getVersion());
		ctrlPane3.add(versionLabel);
		ctrlPane3.add(versionTextField);
		
		JPanel ctrlPane2 = new JPanel();
		JLabel htmlLabel = new JLabel("來源網址:");
		htmlTextField = new JTextField(40);
		htmlTextField.setText(zip.getHtml());
		
		ctrlPane2.add(htmlLabel);
		ctrlPane2.add(htmlTextField);
		
		mainPane.add(ctrlPane);
		mainPane.add(ctrlPane3);
		mainPane.add(ctrlPane2);
		
		
		this.getContentPane().add(mainPane, BorderLayout.CENTER);
		
		JPanel buttomPane = new JPanel();
		JButton saveButton = new JButton(MENU_SAVE, new ImageIcon(MainFrame.class.getResource("/save.png")));
		saveButton.addActionListener(this);
		buttomPane.add(saveButton);
		JButton cancelButton = new JButton(MENU_CANCEL, new ImageIcon(MainFrame.class.getResource("/close.png")));
		cancelButton.addActionListener(this);
		buttomPane.add(cancelButton);
		this.getContentPane().add(buttomPane, BorderLayout.SOUTH);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Utils.log(e.getActionCommand());
		switch (e.getActionCommand()) {
		case MENU_SAVE:
			
			if (nameTextField.getText() == null || "".equals(nameTextField.getText())) {
				Utils.showAlert("請輸入正確Mod壓縮檔名稱");
				return;
			}
			
			//修改Mod壓縮檔名稱
			File zipFile = new File(Utils.getModZipDir(zip));
			File newFile = new File(Utils.getModZipDir(), nameTextField.getText());
			if (zipFile.renameTo(newFile)) {
				
				zip.setName(nameTextField.getText());
				zip.setVersion(versionTextField.getText());
				zip.setHtml(htmlTextField.getText());
				zip.setFileUrl(newFile.getAbsolutePath());
				
				Utils.log("修改: " + zip.toString());
				
				//存回xml
				DefaultListModel<ModZipVO> listModel = (DefaultListModel<ModZipVO>) modList.getModel();
				//listModel.addElement(zip);
				ModZipVO[] array = new ModZipVO[listModel.size()];
				listModel.copyInto(array);
				Utils.saveModZipList(array);
				
				//修改Mod xml
				ModTableModel tableModel = (ModTableModel) modTable.getModel();
				List<ModVO> mods = tableModel.getModList();
				for (ModVO mod : mods) {
					if (mod.getZipName().equals(zipFile.getAbsolutePath())) {
						mod.setZipName(newFile.getAbsolutePath());
					}
				}
				Utils.saveModTable(mods);
				
				Utils.showAlert("修改成功!");
				
				this.dispose();
			} else {
				Utils.showAlert("修改Mod壓縮檔名稱失敗!");
			}
			
			break;
			
		case MENU_CANCEL:
			this.dispose();
		}
	}
}
