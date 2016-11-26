package sk.fiit.sipvs.sv.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

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
	private JTextArea textArea;

	/**
	 * Create the frame.
	 */
	public UI() {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
			e.printStackTrace();
		}

		setBounds(100, 100, 650, 800);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

		textArea = new JTextArea();
		textArea.setFont(new Font("Arial", 0, 18));
		textArea.setLineWrap(true);

		JScrollPane scroll = new JScrollPane (textArea,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		contentPane.add(scroll);

		contentPane.add(Box.createRigidArea(new Dimension(0,10)));

		btnVerify = new JButton("Vyber dokumenty na verifikáciu");
		btnVerify.setFont(new Font("Tahoma", Font.PLAIN, 22));
		btnVerify.setBounds(10, 10, 400, 40);
		contentPane.add(btnVerify);
	}

	public JButton getBtnVerify() {
		
		return this.btnVerify;
	}

	public void writeToTextArea(String s) {
		textArea.setText(s);
	}

	public void appendToTextArea(String s) {
		textArea.append(s);
	}
}
