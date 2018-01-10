package mod.manager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import mod.manager.constant.ModIcon;
import mod.manager.util.Utils;
import mod.manager.vo.CarTrainerVO;

/**
 *
 */
public class CarPriceFrame extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	private static final String MENU_CANCEL = "關閉";

	private List<CarTrainerVO> cars;
	
	public CarPriceFrame(List<CarTrainerVO> cars) {
		super();
		
		this.cars = cars;
		this.setBounds(100, 100, 1000, 700);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.getContentPane().setLayout(new BorderLayout(0, 0));
		this.setTitle("車商Mod車價ini內容");
		this.setIconImage(ModIcon.TRAINER_EDIT.getImage());
		
		JTextArea content = new JTextArea(20, 100);
		StringBuffer output = new StringBuffer();
		for (CarTrainerVO car : this.cars) {
			//[name]2015 Flavio Manzoni Ferrari FXX-K[price]1000000[model]fxxk
			if ("1".equals(car.getEnable())) {
				output.append("[name]").append(car.getDisplayName())
					.append("[price]100[model]").append(car.getModelName()).append("\r\n");
			}
		}
		content.setText(output.toString());
		
		JScrollPane tableScrollPane = new JScrollPane(content);
		tableScrollPane.setPreferredSize(new Dimension(250, 200));
		
		Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder title = BorderFactory.createTitledBorder(loweredetched, "車商Mod車價ini內容");
		tableScrollPane.setBorder(title);
		this.getContentPane().add(tableScrollPane, BorderLayout.CENTER);
		
		
		JPanel pane1 = new JPanel();
		JButton cancelButton = new JButton(MENU_CANCEL, ModIcon.CLOSE);
		cancelButton.addActionListener(this);
		pane1.add(cancelButton);
		
		this.getContentPane().add(pane1, BorderLayout.SOUTH);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Utils.log(e.getActionCommand());
		switch (e.getActionCommand()) {
		case MENU_CANCEL:
			this.dispose();
			break;
		}
	}
	
}
