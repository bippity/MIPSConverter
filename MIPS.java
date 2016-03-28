import java.util.*;

public class MIPS
{
	public static Hashtable<String, Integer> RegisterFromString = new Hashtable<String, Integer>();
	public static Hashtable<Integer, String> RegisterFromInt = new Hashtable<Integer, String>();
	public static Hashtable<String, Instruction> InstructionsFromMnemonic = new Hashtable<String, Instruction>();
	
	public static int getOpCode(long instruction)
	{
		return (int) (instruction >> 26);
	}
	
	public static long setOpCode(long instruction, int opcode)
	{
		opcode &= 0x3f;
		return instruction | ((long) opcode << 26);
	}
	
	public static int getFunc(long instruction)
	{
		return (int) (instruction & 0x3f);
	}
	
	public static long setFunc(long instruction, int func)
	{
		func &= 0x3f;
		return instruction | func;
	}
	
	public static int getRS(long instruction)
	{
		return (int) ((instruction >> 21) & 0x1f);
	}

	public static long setRS(long instruction, int rs)
	{
		rs &= 0x1f;
		return instruction | ((long) rs << 21);
	}
	
	public static int getRT(long instruction)
	{
		return (int) ((instruction >> 16) & 0x1f);
	}
	
	public static long setRT(long instruction, int rt)
	{
		rt &= 0x1f;
		return instruction | ((long) rt << 16);
	}
	
	public static int getRD(long instruction)
	{
		return (int) ((instruction >> 11) & 0x1f);
	}
	
	public static long setRD(long instruction, int rd)
	{
		rd &= 0x1f;
		return instruction | ((long) rd << 11);
	}	
	
	public static int getSA(long instruction)
	{
		return (int) ((instruction >> 6) & 0x1f);
	}
	
	public static long setSA(long instruction, int sa)
	{
		sa &= 0x1f;
		return instruction | ((long) sa << 6);
	}
	
	public static int getImmediate(long instruction)
	{
		return (int) (instruction & 0xffff);
	}
	
	public static long setImmediate(long instruction, int im)
	{
		im &= 0xffff;
		return instruction | im;
	}	
	
	public static int getTarget(long instruction)
	{
		return (int) (instruction & 0x3fffffff);
	}
	
	public static long setTarget(long instruction, int target)
	{
		target &= 0x3fffffff;
		return instruction | target;
	}	
	
	public static int numberOfOperandsRTypes(int func)
	{
		if(func >= 0x20 || func <= 0x7)
			return 3;
		if(func >= 0x18 || func == 0x9)
			return 2;
		if(func >= 0x10 || func == 0x8)
			return 1;
		return 0;
	}
	
	public static int numberOfOperandsITypes(int opcode)
	{
		if(opcode >= 0x8 && opcode != 0x0f)
			return 3;
		if(opcode == 0x4 || opcode == 0x5)
			return 3;
		return 2;
	}
	
	public static String padBinaryString(String s, int len)
	{
		String paddedBinary = s;
		for(int i = len-s.length(); i > 0; i--)
			paddedBinary = 0 + paddedBinary;
		
		return paddedBinary;
	}
	
	public static String padHexString(String s, int len)
	{
		String paddedHex = s;
		for(int i = len-s.length(); i > 0; i--)
			paddedHex = 0 + paddedHex;
		
		return paddedHex;
	}
	
	@SuppressWarnings("serial")
	public static Hashtable<Integer, String> rTypeFormats = new Hashtable<Integer, String>(){{
		put(0x20, "add  %3$s, %1$s, %2$s");
		put(0x21, "addu  %3$s, %1$s, %2$s");
		put(0x24, "and  %3$s, %1$s, %2$s");
		put(0x08, "jr  %1$s");
		put(0x27, "nor  %3$s, %1$s, %2$s");
		put(0x25, "or  %3$s, %1$s, %2$s");
		put(0x00, "sll  %3$s, %2$s, %4$s");
		put(0x2a, "slt  %3$s, %1$s, %2$s");
		put(0x2b, "sltu  %3$s, %1$s, %2$s");
		put(0x02, "srl  %3$s, %2$s, %4$s");
		put(0x22, "sub  %3$s, %1$s, %2$s");
		put(0x23, "subu  %3$s, %1$s, %2$s");	
	}};

	@SuppressWarnings("serial")
	public static Hashtable<Integer, String> iTypeFormats = new Hashtable<Integer, String>(){{
		put(0x08, "addi  %2$s, %1$s, %3$s");
		put(0x09, "addiu  %2$s, %1$s, %3$s");
		put(0x0c, "andi  %2$s, %1$s, %3$s");
		put(0x04, "beq  %1$s, %2$s, %3$s");
		put(0x05, "bne  %1$s, %2$s, %3$s");
		put(0x0a, "slti  %2$s, %1$s, %3$s");
		put(0x0b, "sltiu  %2$s, %1$s, %3$s");
		put(0x24, "lbu  %2$s, 0x%3$s(%1$s)");
		put(0x25, "lhu  %2$s, 0x%3$s(%1$s)");
		put(0x23, "lw  %2$s, 0x%3$s(%1$s)");
		put(0x28, "sb  %2$s, 0x%3$s(%1$s)");
		put(0x29, "sh  %2$s, 0x%3$s(%1$s)");
		put(0x2b, "sw  %2$s, 0x%3$s(%1$s)");
		put(0x30, "ll  %2$s, 0x%3$s(%1$s)");
		put(0x38, "sc  %2$s, 0x%3$s(%1$s)");
		put(0x0f, "lui  %2$s, 0x%3$s");
		put(0x0d, "lui  %2$s, %1$s, %3$s");
	}};
	
	public static class Instruction
	{
		public String name;
		public String type;
		public int opcode;
		public int funcCode;
		
		public Instruction(String n, String t, int o, int f)
		{
			name = n;
			type = t;
			opcode = o;
			funcCode = f;
		}
		
	}
}