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
	private JTextField outputField;
	private int inputType;
	
	JPanel panel = new JPanel();
	JPanel formPanel = new JPanel();
	JPanel inputPanel = new JPanel();
	JPanel outputPanel = new JPanel();
	JLabel lblInput = new JLabel("Input: ");
	JLabel lblOutput = new JLabel("Output: ");
	
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
		panel.add(outputPanel, BorderLayout.SOUTH);
		formPanel.setLayout(new BorderLayout(2, 5));
		outputPanel.setLayout(new GridLayout(2, 2));
		
		formPanel.add(inputPanel, BorderLayout.NORTH);
		inputPanel.setLayout(new GridLayout(2, 2));
		inputPanel.add(lblInput);
		
		inputField = new JTextField();
		inputField.setHorizontalAlignment(SwingConstants.LEFT);
		inputPanel.add(inputField);
		inputField.setColumns(50);
		
		outputField = new JTextField();
		outputPanel.add(lblOutput);
		outputField.setHorizontalAlignment(SwingConstants.LEFT);
		outputField.setColumns(50);
		outputPanel.add(outputField);
		
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
			String input = inputField.getText().toLowerCase();
			//Validates input
			if (input.length() < 8) //anything below 8 is invalid, could be 0x12345678 or 12345678(Shortest valid length) or Add t1 t2 t3
			{
				JOptionPane.showMessageDialog(getRootPane(), "Invalid Input!", "Input Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			else if (inputType == Manager.HEX_TO_INSTRUCTION && input.length() > 10) //invalid Hex
			{
				JOptionPane.showMessageDialog(getRootPane(), "Invalid Hex code!", "Input Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			Manager manager = new Manager(inputType, input, getRootPane());
			if (manager.initialize())
			{
				System.out.println("Successfully initialized manager");
				outputField.setText(manager.convert());
			}
			else
			{
				JOptionPane.showMessageDialog(getRootPane(), "Please make sure 'instructions.txt' and 'registers.txt' are valid.",
						"Error Reading Files!", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		else //Radio buttons clicked
		{
			if (rdbtnHexToInstruction.equals(e.getSource()))
				inputType = Manager.HEX_TO_INSTRUCTION;
			else
				inputType = Manager.INSTRUCTION_TO_HEX;
		}
	}
}
