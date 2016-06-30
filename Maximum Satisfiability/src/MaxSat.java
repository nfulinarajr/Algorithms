import java.util.HashMap;
import java.io.FileReader;
import java.util.Map.Entry;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.io.BufferedReader;

/**
 * @author napoleonfulinarajr
 */
public class MaxSat {
	
	/**
	 * @Class: Literal
	 * @Description: Creates a Literal object to store a literal and a boolean.
	 */
	public static class Literal {
		int literal;
		boolean truth;

		public Literal(int literal, boolean truth) {
			this.literal = literal;
			this.truth = truth;
		}
	}

	/**
	 * @Method: initiate
	 * @Description: Reads a file of clauses and parses all literals and their
	 *               respective line numbers into an appropriate data structure.
	 */
	public static void initiate(HashMap<Integer, ArrayList<Literal>> clauses, HashMap<Integer, Boolean> satClauses) {
		
		Literal literals;
		ArrayList<Literal> clause;
		BufferedReader bufferedReader;
		String currentLine, fileIn = "instance.txt";

		try {
			clauses = new HashMap<Integer, ArrayList<Literal>>();
			bufferedReader = new BufferedReader(new FileReader(fileIn));

			int lineNumber = 0;
			
			while ((currentLine = bufferedReader.readLine()) != null) {
				int tempLiteral;
				clause = new ArrayList<Literal>();
				String[] line = currentLine.split(",");

				for (int i = 0; i < line.length; i++) {
					tempLiteral = Integer.parseInt(line[i]);
					literals = new Literal(Math.abs(tempLiteral), tempLiteral > 0);
					clause.add(literals);
				}
				clauses.put(lineNumber, clause);
				lineNumber++;
			}
			bufferedReader.close();
		} catch (IOException e) {
			System.out.println(e);
		}

		solve(satClauses, clauses);
	}

	/**
	 * @Method: solve
	 * @Description: Solves the maximum satisfiability problem. All satisfiable
	 *               clauses are initialized to true. Compare current amount of
	 *               satisfiable clauses with the negation and keep the largest.
	 */
	public static void solve(HashMap<Integer, Boolean> satClauses, HashMap<Integer, ArrayList<Literal>> clauses) {
		
		int totalClauses = 500;
		satClauses = new HashMap<Integer, Boolean>();

		// Initialize all clauses to true
		for (int i = 1; i <= totalClauses; i++) {
			satClauses.put(i, true);
		}

		// Compare current amount of satisfiable clauses with the negation.
		int currentValue = getMaxSat(satClauses, clauses);
		
		for (Entry<Integer, Boolean> entry : satClauses.entrySet()) {
			entry.setValue(!entry.getValue().booleanValue());
			int comparedValue = getMaxSat(satClauses, clauses);

			if (currentValue < comparedValue) {
				currentValue = comparedValue;
			} else {
				entry.setValue(!entry.getValue().booleanValue());
			}
		}

		writeToFile(satClauses);
	}

	/**
	 * @return value as the greatest amount of satisfied clauses.
	 * @Method: getMaxSat
	 * @Description: Compares the boolean value of each literal with its
	 *               respective index within satClauses. If at least one literal
	 *               is true, it means the entire clause is true.
	 */
	public static int getMaxSat(HashMap<Integer, Boolean> satClauses, HashMap<Integer, ArrayList<Literal>> clauses) {
		
		int counter = 0;

		// Iterate through all clauses
		for (Entry<Integer, ArrayList<Literal>> entry : clauses.entrySet()) {

			// Iterate through all literals of every clause
			for (Literal literal : entry.getValue()) {

				// Compare boolean values
				if (literal.truth == satClauses.get(literal.literal)) {
					counter++;
					break;
				}
			}
		}
		return counter;
	}

	/**
	 * @Method: writeToFile
	 * @Description: Writes the contents of satClauses to a file.
	 */
	public static void writeToFile(HashMap<Integer, Boolean> satClauses) {
		
		String fileOut = "output_fulinara.txt";

		try {
			PrintWriter writer = new PrintWriter(fileOut, "UTF-8");

			// Convert back to an integer
			for (Entry<Integer, Boolean> entry : satClauses.entrySet()) {
				if (entry.getValue().booleanValue() == true) {
					writer.print(1);
				} else {
					writer.print(0);
				}
			}
			writer.close();
		} catch (IOException e) {
			System.out.println(e);
		}

	}

	/**
	 * @param args
	 * 
	 * @Method: Main
	 * @Description: User interface for MaxSat.
	 */
	public static void main(String[] args) {
		
		HashMap<Integer, Boolean> satClauses = null;
		HashMap<Integer, ArrayList<Literal>> clauses = null;

		initiate(clauses, satClauses);
	}
}