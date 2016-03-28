/**
 * MainFrame.java
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.math.*;

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
			
			System.out.println("Converting input type: " + inputType);
			//OR managerClass.convert(inputType);
			if (inputType == 0)	//Convert from Hex to Instruction
			{
				if(input.length() != 8)
				{
					JOptionPane.showMessageDialog(getRootPane(), "Invalid Hex code!", "Input Error", JOptionPane.ERROR_MESSAGE);
				}
				else
				{
					long instruction = Long.parseLong(input, 16);
					int opCode = MIPS.getOpCode(instruction);
					String instructionText = "";
					String binaryText = Long.toString(instruction,2);
					
					if(opCode == 0)
					{
						int func = MIPS.getFunc(instruction);
						int rs = MIPS.getRS(instruction);
						int rt = MIPS.getRT(instruction);
						int rd = MIPS.getRD(instruction);
						int sa = MIPS.getSA(instruction);
						if(MIPS.rTypeFormats.containsKey(func))
						{
							instructionText = String.format(MIPS.rTypeFormats.get(func), MIPS.RegisterFromInt.get(rs), MIPS.RegisterFromInt.get(rt),
															 MIPS.RegisterFromInt.get(rd), "0x" + Integer.toString(sa,16));
						}
						else
							JOptionPane.showMessageDialog(null, "Unsupported R-Type function");
					}
					else if(opCode == 2)
					{
						int target = MIPS.getTarget(instruction);
						instructionText = "j 0x" + Integer.toString(target,16);
					}
					else if(opCode == 3)
					{
						int target = MIPS.getTarget(instruction);
						instructionText = "jal 0x" + Integer.toString(target,16);
					}
					else
					{
						int rs = MIPS.getRS(instruction);
						int rt = MIPS.getRT(instruction);
						int im = MIPS.getImmediate(instruction);
						if(MIPS.iTypeFormats.containsKey(opCode))
						{
							instructionText = String.format(MIPS.iTypeFormats.get(opCode), MIPS.RegisterFromInt.get(rs), MIPS.RegisterFromInt.get(rt),
															 Integer.toString(im,16));
						}
						else
							JOptionPane.showMessageDialog(null, "Unsupported I-Type Opcode");
					}
					
					JOptionPane.showMessageDialog(null, instructionText + "\n\n" + "Binary: " + binaryText + "\n\n" + "Hexadecimal: 0x" + input,
						 "Your MIPS Instruction", JOptionPane.INFORMATION_MESSAGE);
				}
				
			}
			else if(inputType == 1) //Convert from Instruction to Hex
			{
				{
					String[] s = input.split(" ");
					if(MIPS.InstructionsFromMnemonic.containsKey(s[0]))
					{
						MIPS.Instruction i = MIPS.InstructionsFromMnemonic.get(s[0]);
						long instruction = MIPS.setOpCode(0, i.opcode);
						boolean hadError = false;
						if(i.type.equalsIgnoreCase("r_type"))
						{
							int numOfOperands = MIPS.numberOfOperandsRTypes(i.funcCode);
							instruction = MIPS.setFunc(instruction, i.funcCode);
							String sa = "";
							if(numOfOperands == s.length - 1)
							{
								if(i.funcCode <= 0x3)
								{
									instruction = MIPS.setRD(instruction, MIPS.RegisterFromString.get(s[1]));
									instruction = MIPS.setRT(instruction, MIPS.RegisterFromString.get(s[2]));
									if(s[3].startsWith("0x"))
										s[3] = s[3].substring(2);
									instruction = MIPS.setSA(instruction, Integer.parseInt(s[3],16));
								}
								else if(i.funcCode <= 0x7)
								{
									instruction = MIPS.setRD(instruction, MIPS.RegisterFromString.get(s[1]));
									instruction = MIPS.setRT(instruction, MIPS.RegisterFromString.get(s[2]));
									instruction = MIPS.setRS(instruction, MIPS.RegisterFromString.get(s[3]));
								}
								else if(i.funcCode >= 0x20)
								{
									instruction = MIPS.setRD(instruction, MIPS.RegisterFromString.get(s[1]));
									instruction = MIPS.setRS(instruction, MIPS.RegisterFromString.get(s[2]));
									instruction = MIPS.setRT(instruction, MIPS.RegisterFromString.get(s[3]));
								}
								else if(i.funcCode >= 0x18)
								{
									instruction = MIPS.setRS(instruction, MIPS.RegisterFromString.get(s[1]));
									instruction = MIPS.setRT(instruction, MIPS.RegisterFromString.get(s[2]));
								}
								else if(i.funcCode == 0x8 || i.funcCode == 0x11 || i.funcCode == 0x13)
								{
									instruction = MIPS.setRS(instruction, MIPS.RegisterFromString.get(s[1]));
								}
								else if(i.funcCode == 0x10 || i.funcCode == 0x12)
								{
									instruction = MIPS.setRD(instruction, MIPS.RegisterFromString.get(s[1]));
								}
								else if(i.funcCode == 0x9)
								{
									instruction = MIPS.setRD(instruction, MIPS.RegisterFromString.get(s[1]));
									instruction = MIPS.setRS(instruction, MIPS.RegisterFromString.get(s[2]));
								}
							}
							else
							{
								JOptionPane.showMessageDialog(null, "Incorrect number of operands for mnemonic: " + i.name);
								hadError = true;
							}
							
						}
						else if(i.type.equalsIgnoreCase("i_type"))
						{
							int numOfOperands = MIPS.numberOfOperandsITypes(i.opcode);
							if(numOfOperands == s.length - 1)
							{
								if(i.opcode >= 0x20)
								{
									instruction = MIPS.setRT(instruction, MIPS.RegisterFromString.get(s[1]));
									if(s[2].startsWith("0x"))
										s[2] = s[2].substring(2);
									instruction = MIPS.setImmediate(instruction, Integer.parseInt(s[2],16));
									instruction = MIPS.setRS(instruction, MIPS.RegisterFromString.get(s[3]));
								}
								else if(i.opcode == 0xf)
								{
									instruction = MIPS.setRT(instruction, MIPS.RegisterFromString.get(s[1]));
									if(s[2].startsWith("0x"))
										s[2] = s[2].substring(2);
									instruction = MIPS.setImmediate(instruction, Integer.parseInt(s[2],16));
								}
								else if(i.opcode >= 0x8)
								{
									instruction = MIPS.setRT(instruction, MIPS.RegisterFromString.get(s[1]));
									instruction = MIPS.setRS(instruction, MIPS.RegisterFromString.get(s[2]));
									if(s[3].startsWith("0x"))
										s[3] = s[3].substring(2);
									instruction = MIPS.setImmediate(instruction, Integer.parseInt(s[3],16));
								}
								else if(i.opcode >= 0x6)
								{
									instruction = MIPS.setRS(instruction, MIPS.RegisterFromString.get(s[1]));
									if(s[2].startsWith("0x"))
										s[2] = s[2].substring(2);
									instruction = MIPS.setImmediate(instruction, Integer.parseInt(s[2],16));
								}
								else if(i.opcode >= 0x4)
								{
									instruction = MIPS.setRS(instruction, MIPS.RegisterFromString.get(s[1]));
									instruction = MIPS.setRT(instruction, MIPS.RegisterFromString.get(s[2]));
									if(s[3].startsWith("0x"))
										s[3] = s[3].substring(2);
									instruction = MIPS.setImmediate(instruction, Integer.parseInt(s[3],16));								
								}
								else
								{
									instruction = MIPS.setRS(instruction, MIPS.RegisterFromString.get(s[1]));
									if(s[2].startsWith("0x"))
										s[2] = s[2].substring(2);
									instruction = MIPS.setImmediate(instruction, Integer.parseInt(s[2],16));
									if(i.opcode == 0x1)
										instruction = MIPS.setRT(instruction, 1);
								}
							}
							else
							{
								JOptionPane.showMessageDialog(null, "Incorrect number of operands for mnemonic: " + i.name);
								hadError = true;
							}
						}
						else if(i.type.equalsIgnoreCase("j_type"))
						{
							if(s.length == 2)
							{
								if(s[1].startsWith("0x"))
									s[1] = s[1].substring(2);
								instruction = MIPS.setTarget(instruction, Integer.parseInt(s[1],16));
							}
							else
							{
								JOptionPane.showMessageDialog(null, "Incorrect number of operands for mnemonic: " + i.name);
								hadError = true;	
							}
						}
						else
						{
							JOptionPane.showMessageDialog(null, "Unsupported instruction type: " + i.type);
							hadError = true;
						}
						
						if(!hadError)
						{
							String binaryText = Long.toString(instruction, 2);
							String hexText = Long.toString(instruction, 16);
							JOptionPane.showMessageDialog(null, input + "\n\n" + "Binary: " + binaryText + "\n\n" + "Hexadecimal: 0x" + hexText,
						 		"Your MIPS Instruction", JOptionPane.INFORMATION_MESSAGE);
						}
					}
					else
					{
						JOptionPane.showMessageDialog(null, "Unsupported mnemonic: " + s[0]);
					}
				}
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