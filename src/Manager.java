/**
 * Manager.java
 * Manages all the conversions
 *
 */

import java.util.*;

import javax.swing.JOptionPane;
import javax.swing.JRootPane;

import java.io.*;

public class Manager
{	
	public static final int HEX_TO_INSTRUCTION = 0;
	public static final int INSTRUCTION_TO_HEX = 1;
	
	private String input;
	private int inputType;
	private JRootPane rootPane;
	
	public Manager(int _inputType, String _input, JRootPane pane)
	{
		inputType = _inputType;
		input = _input;
		rootPane = pane;
	}
	
	//Reads and initializes all instructions and registers
	public boolean initialize()
	{
		File tableFile = new File("instructions.txt");
		File registersFile = new File("registers.txt");
		Scanner scan = null;
		Scanner scan2 = null;
		boolean status = true;
		
		try 
		{
			scan = new Scanner(tableFile);
			scan2 = new Scanner(registersFile);
			
			while (scan.hasNextLine()) //reads in all instructions in MIPS table
			{
				String temp = scan.nextLine();
				String[] instruction = temp.split(" ");
				
				if (instruction != null && instruction.length == 4)
				{
					MIPS.InstructionsFromMnemonic.put(instruction[0], 
							new MIPS.Instruction(instruction[0], instruction[1], Integer.parseInt(instruction[2], 16), 
									instruction[3].equalsIgnoreCase("null") ? 0 : Integer.parseInt(instruction[3], 16)));
				}
				else
					status = false;
			}
			
			while (scan2.hasNextLine()) //reads in all registers
			{
				String temp = scan2.nextLine();
				String[] register = temp.split(" ");
				
				if (register != null && register.length == 2)
				{
					MIPS.RegisterFromString.put(register[0], Integer.parseInt(register[1], 16));
					MIPS.RegisterFromInt.put(Integer.parseInt(register[1], 16), register[0]);
				}
				else
					status = false;
			}
			scan.close();
			scan2.close();
		} 
		catch (FileNotFoundException e) 
		{
			return false;
		}
		return status;
	}
	
	
	public String convert()
	{
		String output = "";
		if (inputType == HEX_TO_INSTRUCTION)
		{
			output = convertHexToInst();
		}
		else
			output = convertInstToHex();
		
		return output;
	}
	
	private String convertHexToInst()
	{
		if (input.length() == 10 && input.startsWith("0x")) //Cuts off the '0x' in 0x12345678
		{
			input = input.substring(2);
		}
		long instruction = Long.parseLong(input, 16);
		int opCode = MIPS.getOpCode(instruction);
		String instructionText = "";
		String binaryText = Long.toString(instruction, 2);
		
		if (opCode == 0)
		{
			int func = MIPS.getFunc(instruction);
			int rs = MIPS.getRS(instruction);
			int rt = MIPS.getRT(instruction);
			int rd = MIPS.getRD(instruction);
			int sa = MIPS.getSA(instruction);
			
			if (MIPS.rTypeFormats.containsKey(func))
			{
				instructionText = String.format(MIPS.rTypeFormats.get(func), MIPS.RegisterFromInt.get(rs),
						MIPS.RegisterFromInt.get(rt), MIPS.RegisterFromInt.get(rd), "0x" + Integer.toString(sa, 16));
			}
			else
			{	
				JOptionPane.showMessageDialog(rootPane, "Unsupported R-Type function", "Error", JOptionPane.ERROR_MESSAGE);
				return null;
			}
		}
		else if (opCode == 2)
		{
			int target = MIPS.getTarget(instruction);
			instructionText = "j 0x" + Integer.toString(target, 16);
		}
		else if (opCode == 3)
		{
			int target = MIPS.getTarget(instruction);
			instructionText = "jal 0x" + Integer.toString(target,16);
		}
		else 
		{
			int rs = MIPS.getRS(instruction);
			int rt = MIPS.getRT(instruction);
			int im = MIPS.getImmediate(instruction);
			
			if (MIPS.iTypeFormats.containsKey(opCode))
			{
				instructionText = String.format(MIPS.iTypeFormats.get(opCode), MIPS.RegisterFromInt.get(rs),
						MIPS.RegisterFromInt.get(rt), Integer.toString(im, 16));
			}
			else
			{
				JOptionPane.showMessageDialog(rootPane, "Unsuppported I-Type Opcode", "Error", JOptionPane.ERROR_MESSAGE);
				return null;
			}
		}
		
		JOptionPane.showMessageDialog(rootPane,  instructionText + "\n\nBinary: " + binaryText + "\n\nHexadecimal: 0x" + input,
				"Your MIPS Instruction", JOptionPane.INFORMATION_MESSAGE);
		return instructionText;
	}
	
	private String convertInstToHex()
	{	
		String output = "";
		String[] s= input.split(" ");
		
		//***Need to check if it's 't1' or '$t1' for example. Spews out nullExceptionError if '$' is missing.***
		
		if (MIPS.InstructionsFromMnemonic.containsKey(s[0]))
		{
			MIPS.Instruction i = MIPS.InstructionsFromMnemonic.get(s[0]);
			long instruction = MIPS.setOpCode(0, i.opcode);
			if (i.type.equalsIgnoreCase("r_type"))
			{
				int numOfOperands = MIPS.numberOfOperandsRTypes(i.funcCode);
				instruction = MIPS.setFunc(instruction, i.funcCode);
				if (numOfOperands == s.length - 1)
				{
					if (i.funcCode <= 0x3)
					{
						instruction = MIPS.setRD(instruction, MIPS.RegisterFromString.get(s[1]));
						instruction = MIPS.setRT(instruction, MIPS.RegisterFromString.get(s[2]));
						
						if (s[3].startsWith("0x"))
							s[3] = s[3].substring(2);
						
						instruction = MIPS.setSA(instruction, Integer.parseInt(s[3], 16));
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
					JOptionPane.showMessageDialog(rootPane, "Incorrect number of operands for mnemonic: " + i.name);
					return null;
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
					return null;	
				}
			}
			else
			{
				JOptionPane.showMessageDialog(null, "Unsupported instruction type: " + i.type);
				return null;
			}
			
			String binaryText = Long.toString(instruction, 2);
			String hexText = Long.toString(instruction, 16);
			JOptionPane.showMessageDialog(rootPane, input + "\n\n" + "Binary: " + binaryText + "\n\n" + "Hexadecimal: 0x" + hexText,
		 		"Your MIPS Instruction", JOptionPane.INFORMATION_MESSAGE);
			output = hexText;
		}
		else
		{
			JOptionPane.showMessageDialog(rootPane, "Unsupported mnemonic: " + s[0]);
			return null;
		}
		return output;
	}
}