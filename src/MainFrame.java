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
				System.out.println("Converting input type: " + inputType);
				//OR managerClass.convert(inputType);
				if (inputType == 0)
				{
					//Convert from Hex to Instruction
				}
				else //inputType == 1
				{
					//Convert from Instruction to Hex
				}
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
