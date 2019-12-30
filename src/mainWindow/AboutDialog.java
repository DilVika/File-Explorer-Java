package mainWindow;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JDialog;
import javax.swing.UIManager;
import javax.swing.JTextPane;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.BoxLayout;
import javax.swing.JButton;

public class AboutDialog extends JDialog {

	/**
	 * Launch the application.
	 */
	// public static void main(String[] args) {
	// 	EventQueue.invokeLater(new Runnable() {
	// 		public void run() {
	// 			try {
	// 				AboutDialog dialog = new AboutDialog();
	// 				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	// 				dialog.setVisible(true);
	// 			} catch (Exception e) {
	// 				e.printStackTrace();
	// 			}
	// 		}
	// 	});
	// }

	/**
	 * Create the dialog.
	 */
	public AboutDialog() {
		getContentPane().setBackground(UIManager.getColor("InternalFrame.inactiveTitleGradient"));
		getContentPane().setLayout(null);

		setModal(true);

		JLabel info2Label = new JLabel("--- 1753127 ---");
		info2Label.setHorizontalAlignment(SwingConstants.CENTER);
		info2Label.setFont(new Font("Tahoma", Font.PLAIN, 14));
		info2Label.setBounds(125, 50, 217, 38);
		getContentPane().add(info2Label);
		
		JLabel info1Label = new JLabel(" Cao \u0110\u00ECnh V\u0129 - 17KTPM");
		info1Label.setHorizontalAlignment(SwingConstants.CENTER);
		info1Label.setFont(new Font("Tahoma", Font.PLAIN, 14));
		info1Label.setBounds(125, 0, 203, 68);
		getContentPane().add(info1Label);
		
		JLabel infor3Label = new JLabel("--- HCMUS ---");
		infor3Label.setHorizontalAlignment(SwingConstants.CENTER);
		infor3Label.setFont(new Font("Tahoma", Font.PLAIN, 14));
		infor3Label.setBounds(163, 95, 136, 49);
		getContentPane().add(infor3Label);
		setBackground(UIManager.getColor("FormattedTextField.selectionBackground"));
		//set
		setUndecorated(true);
		setResizable(false);
		setPreferredSize(new Dimension(100, 100));
		//setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setAlwaysOnTop(true);
		setBounds(100, 100, 450, 300);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if (e.getClickCount() == 1) {
					setVisible(false);
					dispose();
				}
			}
		});
	}

	public void showDialog() {
		setVisible(true);
	}
}
