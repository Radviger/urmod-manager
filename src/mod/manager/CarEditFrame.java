package mod.manager;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

import mod.manager.constant.ModIcon;
import mod.manager.util.Utils;
import mod.manager.vo.CarVO;

/**
 * 編輯車輛Mod內容視窗
 */
public class CarEditFrame extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;

	private static final String MENU_OK = "確定";
	private static final String MENU_CANCEL = "取消";
	
	private CarVO mod;
	private JTable modTable;
	private JTextField nameTextField, versionTextField;
	
	public CarEditFrame(CarVO mod, JTable modTable) {
		super();
		
		this.mod = mod;
		this.modTable = modTable;
		
		this.setBounds(100, 100, 800, 250);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		this.getContentPane().setLayout(new BorderLayout(0, 0));
		this.setTitle("編輯車輛Mod內容");
		this.setIconImage(ModIcon.CAR_EDIT.getImage());
		
		JPanel mainPane = new JPanel();
		mainPane.setLayout(new GridLayout(2, 1));
		
		JPanel ctrlPane = new JPanel();
		JLabel nameLabel = new JLabel("Mod名稱:");
		nameTextField = new JTextField(40);
		nameTextField.setText(mod.getName());
		ctrlPane.add(nameLabel);
		ctrlPane.add(nameTextField);
		
		JPanel ctrlPane2 = new JPanel();
		JLabel versionLabel = new JLabel("版本:");
		versionTextField = new JTextField(40);
		versionTextField.setText(mod.getVersion());
		ctrlPane2.add(versionLabel);
		ctrlPane2.add(versionTextField);
		
		mainPane.add(ctrlPane);
		mainPane.add(ctrlPane2);
		this.getContentPane().add(mainPane, BorderLayout.CENTER);
		
		
		JPanel buttomPane = new JPanel();
		JButton saveButton = new JButton(MENU_OK, ModIcon.SAVE);
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
			if (nameTextField.getText() == null || "".equals(nameTextField.getText())) {
				Utils.showAlert("請輸入正確車輛Mod名稱");
				return;
			}
			
			//修改Mod目錄名稱
			File modDir = new File(Utils.getCarDir(mod));
			if (modDir.renameTo(new File(Utils.getCarDir(), nameTextField.getText()))) {
				mod.setName(nameTextField.getText());
				mod.setVersion(versionTextField.getText());
				
				Utils.log("修改: " + mod.toString());
				
				if (mod.getNewVersion() != null && mod.getNewVersion().length() > 0) {
					if (mod.getNewVersion().equals(mod.getVersion())) {
						mod.setNewVersion(null);
					}
				}
				
				//存回xml
				CarTableModel tableModel = (CarTableModel) modTable.getModel();
				Utils.saveCarTable(tableModel.getModList());
				modTable.updateUI();
				
				Utils.showAlert("修改成功!");
				this.dispose();
			} else {
				Utils.showAlert("修改車輛Mod目錄名稱失敗!");
			}
			
			break;
			
		case MENU_CANCEL:
			this.dispose();
		}
	}
	
}
