import java.io.PrintStream;
import java.text.NumberFormat;

public class CAconsole {
	
	public enum State {OOO, OOI, OIO, OII, IOO, IOI, IIO, III};
	
	public static void main(String[] args){
		final PrintStream out = System.out;
		CAconsole console = new CAconsole();
		char[] data = null;
		int steplim = 0;
		int rulenum = 0;
		
		// Print out and read data.
		out.println();
		out.println("Welcome to The Cellular Automata");
		out.println();
		out.print("Processing data string: ");
		try {
			out.println(args[0]);
			data = args[0].toCharArray();
		}
		catch (Exception e) {
			out.println();
			out.println("Error: No data string entered.");
			console.quit(1);
		}
		out.print("Processing step limit:  ");
		try {
			out.println(args[1]);
			steplim = Integer.parseInt(args[1]);
		}
		catch (Exception e) {
			out.println();
			out.println("Error: No step limit entered.");
			console.quit(1);
		}
		out.print("Processing rule number: ");
		try {
			out.println(args[2]);
			rulenum = Integer.parseInt(args[2]);
		}
		catch (Exception e) {
			out.println();
			out.println("Error: No rule number entered.");
			console.quit(1);
		}
		out.println();
		
		// Initialise.
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMinimumIntegerDigits(8);
		nf.setMaximumIntegerDigits(8);
		nf.setGroupingUsed(false);
		char [] printdata = new char[data.length];

		// Show initial state.
		out.print("Initial state: ");
		//  Make the data easier to read when printed out.
		//   Convert 0/1 to empty spaces and full blocks. 
		printdata = data.clone();
		for (int j = 0; j < printdata.length; j++){
			if (printdata[j] == '0') {printdata[j] = ' ';} else {printdata[j] = 'X';}
		}
		out.println(printdata);
		
		// Start "timer".
		long starttime = System.currentTimeMillis();
		
		// Step through and print each new stage.
		for (int i = 1; i < steplim + 1; i++) {
			out.print("Step " + nf.format(i) + ": ");
			data = console.step(data, rulenum);
			
			// Make the data easier to read when printed out.
			printdata = data.clone();
			for (int j = 0; j < printdata.length; j++){
				if (printdata[j] == '0') {printdata[j] = ' ';} else {printdata[j] = 'X';}
			}
			out.println(printdata);
		}
		// Stop "timer".
		long stoptime = System.currentTimeMillis();
		long delta = stoptime - starttime;
		out.println();
		out.println("Finished.");
		out.println(steplim + " steps completed in " + delta + "ms, at ~" + (delta/steplim) + "ms per step.");
		console.quit(0);
	}
	
	void quit (int result){
		final PrintStream out = System.out;
		out.println();
		if (result == 0) {
			out.println("Quitting...");
		} else {
			out.println();
			out.println();
			out.println("Help using The Cellular Automata");
			out.println();
			out.println("  1. Run from the command line using the format:");
			out.println("     java CAconsole <initial data> <step limit> <rule number>");
			out.println();
			out.println("  2. <initial data> must be a string of 0s and 1s (of any length)");
			out.println("     where a 0 indicates empty space, and a 1 is a full cell.");
			out.println();
			out.println("  3. <step limit> must be an integer indicating the number of");
			out.println("     time steps you wish to run the simulation for.");
			out.println();
			out.println("  4. <rule number> must be an integer that represents the");
			out.println("     Wolfram code that will determine the cell evolution.");
			out.println("     Currently, rules 30 and 110 are implemented.");
			out.println();
			out.println("  Now try running The Cellular Automata again, for example:");
			out.println("     java CAconsole 00000000100000000 8 30");
			out.println();
			out.println("Aborting...");
		}
		out.println();
		System.exit(result);
	}
	
	char[] step(char[] data, int rulenum) {
		char[] newdata = new char[data.length];
		// Scan left-to-right, processing each character using rules.
		for (int x = 0; x < data.length; x++) {
			newdata[x] = evolve(data, x, rulenum);
		}
		return newdata;
	}
	
	char evolve(char[] data, int loc, int rulenum) {
		final PrintStream out = System.out;
		// Check for problems first
		CAconsole cac = new CAconsole();
		if (data[loc] != '0' && data[loc] != '1') {
			out.println("Invalid data character encountered.");
			cac.quit(1);
		}
		
		// Initialise.
		char leftcell = '0'; // Default
		char thiscell = data[loc];
		char rghtcell = '0'; // Default
		State curstate = State.OOO; // Default
		
		// Get left and right data cells.
		try {
			leftcell = data[loc-1];
		} catch (Exception e) {
			leftcell = '0';
		}
		try {
			rghtcell = data[loc+1];
		} catch (Exception e) {
			rghtcell = '0';
		}
		
		// Mapping.
		//  Ugly, poor code. But cleaner methods like hashmaps don't seem to work.
		if (leftcell == '0' && thiscell == '0' && rghtcell == '0') {curstate = State.OOO;}
		if (leftcell == '0' && thiscell == '0' && rghtcell == '1') {curstate = State.OOI;}
		if (leftcell == '0' && thiscell == '1' && rghtcell == '0') {curstate = State.OIO;}
		if (leftcell == '0' && thiscell == '1' && rghtcell == '1') {curstate = State.OII;}
		if (leftcell == '1' && thiscell == '0' && rghtcell == '0') {curstate = State.IOO;}
		if (leftcell == '1' && thiscell == '0' && rghtcell == '1') {curstate = State.IOI;}
		if (leftcell == '1' && thiscell == '1' && rghtcell == '0') {curstate = State.IIO;}
		if (leftcell == '1' && thiscell == '1' && rghtcell == '1') {curstate = State.III;}
		
		// Rules.
		switch (rulenum) { //TODO Make more rules!
			case 30 : switch (curstate) {
		        	case OOO : thiscell = '0'; break;
		        	case OOI : thiscell = '1'; break;
		        	case OIO : thiscell = '1'; break;
		        	case OII : thiscell = '1'; break;
		        	case IOO : thiscell = '1'; break;
		        	case IOI : thiscell = '0'; break;
		        	case IIO : thiscell = '0'; break;
		        	case III : thiscell = '0'; break;
		        	default:   out.println("Invalid state."); cac.quit(1); break;
				} break;
			case 110 : switch (curstate) {
			    	case OOO : thiscell = '0'; break;
			    	case OOI : thiscell = '1'; break;
			    	case OIO : thiscell = '1'; break;
			    	case OII : thiscell = '1'; break;
			    	case IOO : thiscell = '0'; break;
			    	case IOI : thiscell = '1'; break;
			    	case IIO : thiscell = '1'; break;
			    	case III : thiscell = '0'; break;
			    	default:   out.println("Invalid state."); cac.quit(1); break;
				} break;
			default : out.println("Invalid rule."); cac.quit(1); break;
		}
		return thiscell;
	}
}
