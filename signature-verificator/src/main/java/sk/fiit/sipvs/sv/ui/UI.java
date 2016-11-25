package sk.fiit.sipvs.sv.ui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.Font;

/**
 * Graphical User Interface
 */
public class UI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1401058951929945624L;

	private JPanel contentPane;
	
	private JButton btnVerify;

	/**
	 * Create the frame.
	 */
	public UI() {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		btnVerify = new JButton("Verifikuj vybrané dokumenty");
		btnVerify.setFont(new Font("Tahoma", Font.PLAIN, 22));
		btnVerify.setBounds(10, 10, 400, 40);
		contentPane.add(btnVerify);
	}

	public JButton getBtnVerify() {
		
		return this.btnVerify;
	}
	
}
