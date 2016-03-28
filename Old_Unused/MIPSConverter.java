//MIPSConverter.java
//Opens up a gui and stuff

import java.io.*;
import java.util.*;
import javax.swing.*;

public class MIPSConverter 
{
	public static void main(String[] args) 
	{
		@SuppressWarnings("unused")
		String errorMessage = readRegisters();
		if(errorMessage != null)
		{
			JOptionPane.showMessageDialog(null, errorMessage);
			return;
		}
		errorMessage = readInstructions();
		if(errorMessage != null)
		{
			JOptionPane.showMessageDialog(null, errorMessage);
			return;
		}		
		MainFrame main = new MainFrame();
	
	}
	
	public static String readRegisters()
	{
		String errorMessage = null;
		File file = null;
		try
		{
			file = new File("registers.txt");
			Scanner in = new Scanner(file);
			String line = "";
			while(in.hasNextLine())
			{
				line = in.nextLine();
				String[] s = line.split(" ");
				if(s != null && s.length == 2)
				{
					MIPS.RegisterFromString.put(s[0], Integer.parseInt(s[1],16));
					MIPS.RegisterFromInt.put(Integer.parseInt(s[1],16), s[0]);
				}
				else
				{
					errorMessage = "Register file formatting error";
					break;
				}
			}
		}
		catch(FileNotFoundException e)
		{
			errorMessage = "File not found : " + file.getPath();
		}
		catch(Exception e)
		{
			errorMessage = "Unhandled exception : " + e.toString();
		}
		
		return errorMessage;
	}
	
	public static String readInstructions()
	{
		String errorMessage = null;
		File file = null;
		try
		{
			file = new File("instructions.txt");
			Scanner in = new Scanner(file);
			String line = "";
			while(in.hasNextLine())
			{
				line = in.nextLine();
				String[] s = line.split(" ");
				if(s != null && s.length == 4)
				{
					MIPS.InstructionsFromMnemonic.put(s[0], new MIPS.Instruction(s[0],s[1],Integer.parseInt(s[2],16),
								 s[3].equalsIgnoreCase("null") ? 0 : Integer.parseInt(s[3],16)));
				}
				else
				{
					errorMessage = "Instruction file formatting error";
					break;
				}
			}
		}
		catch(FileNotFoundException e)
		{
			errorMessage = "File not found : " + file.getPath();
		}
		catch(Exception e)
		{
			errorMessage = "Unhandled exception : " + e.toString();
		}
		
		return errorMessage;
	}
}