/**
 * Manager.java
 * Manages all the conversions
 *
 */

import java.util.*;
import java.io.*;

public class Manager
{
	ArrayList<MIPSElement> HEX_MIPS_TABLE = new ArrayList<MIPSElement>();
	ArrayList<Register> registers = new ArrayList<Register>(); 
	
	public Manager(int inputType, String input)
	{
		if (initialize())
		{
			for (Register r : registers)
			{
				System.out.println("Register Name: " + r.getName() + "\nNumber: " + r.getNumber());
			}
		}
		else
		{
			System.out.println("COULDN'T FIND FILES");
		}
	}
	
	public boolean initialize()
	{
		File tableFile = new File("table.txt");
		File registersFile = new File("registers.txt");
		Scanner scan = null;
		Scanner scan2 = null;
		
		try 
		{
			scan = new Scanner(tableFile);
			scan2 = new Scanner(registersFile);
			
			while (scan.hasNextLine()) //reads in all elements in MIPS table
			{
				String temp = scan.nextLine();
				String[] element = temp.split(" ");
				
				String name = element[0];
				String format = element[1];
				String opcode = element[2];
				String function = element[3];
				
				HEX_MIPS_TABLE.add(new MIPSElement(name, format, opcode, function));
			}
			
			while (scan2.hasNextLine()) //reads in all registers
			{
				String temp = scan2.nextLine();
				String[] register = temp.split(" ");
				
				String name = register[0];
				String number = register[1];
				
				registers.add(new Register(name, number));
			}
			scan.close();
			scan2.close();
		} 
		catch (FileNotFoundException e) 
		{
			return false;
		}
		return true;
		
	}
}

class MIPSElement
{
	String name, format, opcode, function;
	
	public MIPSElement(String n, String f, String op, String fn)
	{
		name = n;
		format = f;
		opcode = op;
		function = fn;
	}
	
	public String getName()
	{
		return name;
	}
	public String getFormat()
	{
		return format;
	}
	public String getOpcode()
	{
		return opcode;
	}
	public String getFunction()
	{
		return function;
	}
}

class Register
{
	String name, number;
	
	public Register(String n, String num)
	{
		name = n;
		number = num;
	}
	
	String getName()
	{
		return name;
	}
	
	String getNumber()
	{
		return number;
	}
}