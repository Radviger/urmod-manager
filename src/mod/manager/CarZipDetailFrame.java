package mod.manager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import mod.manager.constant.ModIcon;
import mod.manager.util.Utils;
import mod.manager.vo.CarZipVO;

/**
 * 車輛Mod壓縮檔內容視窗
 */
public class CarZipDetailFrame extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;

	private static final String MENU_INSTALL = "安裝車輛Mod";
	private static final String MENU_CANCEL = "返回";
	
	private CarZipVO zip;
	private JTable modTable;
	private JTextField zipPathTextField, versionTextField, callTextField, dlcTextField;
	
	public CarZipDetailFrame(CarZipVO zip, JTable modTable) {
		super();
		this.zip = zip;
		this.modTable = modTable;
		this.setBounds(100, 100, 800, 350);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		this.getContentPane().setLayout(new BorderLayout(0, 0));
		this.setTitle("車輛Mod壓縮檔內容");
		this.setIconImage(ModIcon.ZIP.getImage());
		
		JPanel mainPane = new JPanel();
		mainPane.setLayout(new GridLayout(5, 1));
		
		JPanel ctrlPane = new JPanel();
		JLabel zipDirLabel = new JLabel("壓縮檔位置:");
		zipPathTextField = new JTextField(40);
		zipPathTextField.setText(zip.getFileUrl());
		zipPathTextField.setEditable(false);
		ctrlPane.add(zipDirLabel);
		ctrlPane.add(zipPathTextField);
		
		JPanel ctrlPane3 = new JPanel();
		JLabel versionLabel = new JLabel("版本:");
		versionTextField = new JTextField(40);
		versionTextField.setText(zip.getVersion());
		versionTextField.setEditable(false);
		ctrlPane3.add(versionLabel);
		ctrlPane3.add(versionTextField);
		
		JPanel ctrlPane2 = new JPanel();
		JLabel htmlLabel = new JLabel("來源網址:");
		
		JButton button = new JButton();
	    button.setText("<HTML><FONT color=\"#000099\"><U>" + zip.getHtml() + "</U></FONT></HTML>");
	    button.setHorizontalAlignment(SwingConstants.LEFT);
	    button.setBorderPainted(false);
	    button.setOpaque(false);
	    button.setBackground(Color.WHITE);
	    button.setToolTipText(zip.getHtml());
	    button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (zip.getHtml() != null && !"".equals(zip.getHtml())) {
						Utils.open(new URI(zip.getHtml()));
					}
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
					Utils.log(e1.getMessage());
				}
			}
	    });
		
		ctrlPane2.add(htmlLabel);
		ctrlPane2.add(button);
		
		JPanel ctrlPane4 = new JPanel();
		JLabel callLabel = new JLabel("車輛呼叫名稱:");
		callTextField = new JTextField(40);
		callTextField.setText(zip.getCallName());
		callTextField.setEditable(false);
		ctrlPane4.add(callLabel);
		ctrlPane4.add(callTextField);
		
		JPanel ctrlPane5 = new JPanel();
		JLabel dirLabel = new JLabel("車輛dlc目錄:");
		dlcTextField = new JTextField(40);
		dlcTextField.setText(zip.getDlcName());
		dlcTextField.setEditable(false);
		ctrlPane5.add(dirLabel);
		ctrlPane5.add(dlcTextField);
		
		mainPane.add(ctrlPane);
		mainPane.add(ctrlPane3);
		mainPane.add(ctrlPane2);
		mainPane.add(ctrlPane4);
		mainPane.add(ctrlPane5);
		
		
		this.getContentPane().add(mainPane, BorderLayout.CENTER);
		
		JPanel buttomPane = new JPanel();
		JButton saveButton = new JButton(MENU_INSTALL, ModIcon.CAR_ADD);
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
		case MENU_INSTALL:
			InstallCarFrame installFrame = new InstallCarFrame(zip, modTable);
			Utils.showCenterFrame(installFrame);
			this.dispose();
			break;
			
		case MENU_CANCEL:
			this.dispose();
		}
	}
}
