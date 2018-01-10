package mod.manager;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;

import lombok.Getter;
import mod.manager.constant.ModIcon;
import mod.manager.util.Utils;
import mod.manager.vo.ModVO;

/**
 *
 */
public class ModDetailFrame extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;

	private static final String MENU_INSTALL = "安裝MOD";
	private static final String MENU_UNINSTALL = "移除MOD";
	private static final String MENU_CANCEL = "關閉";
	
	private ModVO mod;
	private JTable modTable;
	private JButton inButton, unButton;
	@Getter
	private GameTreePanel gameTreePanel;
	@Getter
	private ModTreePanel modTreePanel;
	
	public ModDetailFrame(ModVO mod, JTable modTable) {
		super();
		this.mod = mod;
		this.modTable = modTable;
		this.setBounds(100, 100, 1000, 600);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		this.getContentPane().setLayout(new BorderLayout(0, 0));
		this.setTitle("Mod內容");
		this.setIconImage(ModIcon.MOD.getImage());
		
		JPanel mainPane = new JPanel();
		mainPane.setLayout(new GridLayout(1, 2));
		
		modTreePanel = new ModTreePanel(mod);
		gameTreePanel = new GameTreePanel(mod, this);
		mainPane.add(modTreePanel);
		mainPane.add(gameTreePanel);
		this.getContentPane().add(mainPane, BorderLayout.CENTER);
		
		
		JPanel buttomPane = new JPanel();
		inButton = new JButton(MENU_INSTALL, ModIcon.MOD_ADD);
		inButton.addActionListener(this);
		buttomPane.add(inButton);
		unButton = new JButton(MENU_UNINSTALL, ModIcon.MOD_DEL);
		unButton.addActionListener(this);
		buttomPane.add(unButton);
		JButton cancelButton = new JButton(MENU_CANCEL, ModIcon.CLOSE);
		cancelButton.addActionListener(this);
		buttomPane.add(cancelButton);
		this.getContentPane().add(buttomPane, BorderLayout.SOUTH);
		
		refreshButton();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Utils.log(e.getActionCommand());
		switch (e.getActionCommand()) {
		case MENU_INSTALL:
			if (Utils.showComfirm("確定安裝Mod至遊戲目錄?", "確認")) {
				Utils.copyModDir(mod);
				mod.setEnable(true);
				
				ModTableModel tableModel = (ModTableModel) modTable.getModel();
				Utils.saveModTable(tableModel.getModList());
			}
			modTable.updateUI();
			modTreePanel.refresh();
			gameTreePanel.refresh();
			refreshButton();
			break;
			
		case MENU_UNINSTALL:
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
			modTable.updateUI();
			modTreePanel.refresh();
			gameTreePanel.refresh();
			refreshButton();
			break;
			
		case MENU_CANCEL:
			this.dispose();
		}
	}
	
	public void refresh() {
		mod.setStatus(Utils.checkModStatus(mod));
		modTable.updateUI();
		ModTableModel tableModel = (ModTableModel) modTable.getModel();
		Utils.saveModTable(tableModel.getModList());
	}
	
	private void refreshButton() {
		unButton.setEnabled(mod.isEnable());
		unButton.updateUI();
		inButton.setEnabled(!mod.isEnable());
		inButton.updateUI();
	}
	
}
