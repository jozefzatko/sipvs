package sk.fiit.sipvs.ar.ui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.Font;

public class UI extends JFrame {

	/**
	 * UI Mock
	 * TODO: Replace temporary GUI with final version
	 * 
	 */
	private static final long serialVersionUID = -8337220584677354655L;

	private JPanel contentPane;
	
	private JButton btnSave;
	private JButton btnValidate;
	private JButton btnTransform;

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
		
		btnSave = new JButton("Ulož");
		btnSave.setFont(new Font("Tahoma", Font.PLAIN, 22));
		btnSave.setBounds(10, 10, 200, 40);
		contentPane.add(btnSave);
		
		btnValidate = new JButton("Validuj");
		btnValidate.setFont(new Font("Tahoma", Font.PLAIN, 22));
		btnValidate.setBounds(10, 60, 200, 40);
		contentPane.add(btnValidate);
		
		btnTransform = new JButton("Transformuj");
		btnTransform.setFont(new Font("Tahoma", Font.PLAIN, 22));
		btnTransform.setBounds(10, 110, 200, 40);
		contentPane.add(btnTransform);
	}

	public JButton getBtnSave() {
		
		return this.btnSave;
	}
	
	public JButton getBtnValidate() {
		
		return this.btnValidate;
	}
	
	public JButton getBtnTransform() {
		
		return this.btnTransform;
	}
	
}
