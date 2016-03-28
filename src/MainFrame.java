/**
 * MainFrame.java
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

@SuppressWarnings("serial")
public class MainFrame extends JFrame implements ActionListener
{
	private JTextField inputField;
	private int inputType;
	
	JPanel panel = new JPanel();
	JPanel formPanel = new JPanel();
	JPanel inputPanel = new JPanel();
	JLabel lblInput = new JLabel("Input: ");
	
	JButton btnConvert = new JButton("Convert");
	
	JRadioButton rdbtnHexToInstruction = new JRadioButton("Hex to Instruction");
	JRadioButton rdbtnInstructionToHex = new JRadioButton("Instruction to Hex");
	
	ButtonGroup group = new ButtonGroup();
	
	public MainFrame() 
	{
		setTitle("MIPSConverter");
		setSize(400, 200);
		
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(5, 5));
		
		panel.add(formPanel);
		formPanel.setLayout(new BorderLayout(2, 5));
		
		formPanel.add(inputPanel, BorderLayout.NORTH);
		inputPanel.setLayout(new GridLayout(2, 2));
		
		inputPanel.add(lblInput);
		
		inputField = new JTextField();
		inputField.setHorizontalAlignment(SwingConstants.LEFT);
		inputPanel.add(inputField);
		inputField.setColumns(50);
		
		formPanel.add(btnConvert, BorderLayout.SOUTH);
		
		formPanel.add(rdbtnHexToInstruction, BorderLayout.WEST);
		
		formPanel.add(rdbtnInstructionToHex, BorderLayout.EAST);
		
		//Adds radio buttons to a group
		group.add(rdbtnHexToInstruction);
		group.add(rdbtnInstructionToHex);
		rdbtnHexToInstruction.setSelected(true);
		
		//Register listeners to the buttons
		btnConvert.addActionListener(this);
		rdbtnHexToInstruction.addActionListener(this);
		rdbtnInstructionToHex.addActionListener(this);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if (btnConvert.equals(e.getSource()))
		{
			String input = inputField.getText();
			//Validates input
			if (input.length() < 8) //anything below 8 is invalid
			{
				JOptionPane.showMessageDialog(getRootPane(), "Invalid Input!", "Input Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			else if (inputType == 0 && input.length() > 10) //invalid Hex
			{
				JOptionPane.showMessageDialog(getRootPane(), "Invalid Hex code!", "Input Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			System.out.println("Converting input type: " + inputType);
			Manager manager = new Manager(inputType, input);
		}
		else //Radio buttons clicked
		{
			if (rdbtnHexToInstruction.equals(e.getSource()))
				inputType = 0;
			else
				inputType = 1;
		}
	}
}
