import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

class Record implements Comparable<Record> {
	public int age;
	public int hasPhage;
	public int _id; // used for internal tracking

	public Record(int age, int hasPhage, int id) {
		if (hasPhage != 0 && hasPhage != 1) {
			throw new IllegalArgumentException();
		}

		this.age = age;
		this.hasPhage = hasPhage;
		this._id = id;

	}

	@Override
	public String toString() {
		return String.format("Age[%d], Phage[%d]", age, hasPhage);
	}

	@Override
	public int compareTo(Record arg0) {
		return new Integer(age).compareTo(arg0.age);
	}

}

class Parser {

	public Record[] loadData(String filename) throws IOException {
		try (BufferedReader reader = new BufferedReader(
				new FileReader(filename))) {

			String line;
			ArrayList<Record> records = new ArrayList<>();

			int id = 0;
			while ((line = reader.readLine()) != null) {
				int age = Integer
						.parseInt(line.substring(0, line.indexOf(",")));
				int hasPhage = Integer.parseInt(line.substring(1 + line
						.indexOf(",")));
				records.add(new Record(age, hasPhage, id++));
			}
			return records.toArray(new Record[records.size()]);
		}
	}

	// @Deprecated
	// public void generateData(int num) throws IOException {
	// Record[] testData = new Record[num];
	// for (int k = 0; k < num; k++) {
	// testData[k] = new Record((int) (Math.random() * 100),
	// (int) Math.round(Math.random()), k);
	// }
	//
	// try (PrintWriter printWriter = new PrintWriter(new BufferedWriter(
	// new FileWriter("inputFile.txt")))) {
	//
	// for (int k = 0; k < testData.length; k++) {
	// printWriter.print(testData[k].age + "," + testData[k].hasPhage);
	// if (k < testData.length - 1)
	// printWriter.println();
	// }
	// }
	// }

	public void save(Record[] unsortedRecords, String filename)
			throws IOException {

		ArrayList<Record> sortedRecords = new ArrayList<>(
				Arrays.asList(unsortedRecords));
		sortedRecords.sort((r1, r2) -> {
			return new Integer(r1._id).compareTo(r2._id);
		});

		try (PrintWriter printWriter = new PrintWriter(new BufferedWriter(
				new FileWriter(filename)))) {
			for (int k = 0; k < sortedRecords.size(); k++) {
				printWriter.print(sortedRecords.get(k).age + ","
						+ sortedRecords.get(k).hasPhage);
				if (k < sortedRecords.size() - 1)
					printWriter.println();
			}
		}
	}
}

public class kanon {
	private int k; // k-anonymity

	private int numData;
	private int numMaxAnonSets;
	private int[][] T; // cost array
	private int[][] startOfSet; // for reconstructing solution
	private Record[] records;

	public kanon(Record[] unsortedRecords, int k) {
		if (k > unsortedRecords.length) {
			throw new IllegalArgumentException("Cannot have " + k
					+ " anonymity with only " + unsortedRecords.length
					+ " data elements");
		}
		this.k = k;
		this.numData = unsortedRecords.length;
		this.numMaxAnonSets = numData / k;
		this.T = new int[numMaxAnonSets][numData];
		this.startOfSet = new int[numMaxAnonSets][numData];
		this.records = unsortedRecords;
		Arrays.sort(this.records); // sort by age first
	}

	public void printRecords() {
		for (Record r : records)
			System.out.println(r);
	}

	private int getChangeCost(int start, int end) {

		int cost = 0;
		int median = records[(start + end) / 2].age;
		for (int j = start; j <= end; j++) {
			cost += Math.abs(median - records[j].age);
		}

		return cost;
	}

	public Record[] solve() {
		// initialize the invalid cols
		for (int row = 0; row < numMaxAnonSets; row++) {
			for (int col = 0; col < k - 1; col++) {
				T[row][col] = Integer.MAX_VALUE;
				startOfSet[row][col] = -1;
			}
		}

		// initialize base case (1 set)
		for (int col = k - 1; col < numData; col++) {
			T[0][col] = getChangeCost(0, col);
			startOfSet[0][col] = 0;
		}

		// apply recurrence
		for (int row = 1; row < numMaxAnonSets; row++) {
			for (int col = k - 1; col < numData; col++) {
				T[row][col] = T[row - 1][col];
				startOfSet[row][col] = -1; // no set initially

				for (int z = k; z <= col + 1 - k; z++) {
					if (T[row - 1][z - 1] + getChangeCost(z, col) < T[row][col]) {
						T[row][col] = T[row - 1][z - 1] + getChangeCost(z, col);
						startOfSet[row][col] = z;
					}

				}

			}
		}

		System.out.printf("Minimum change to achieve %d-anonymity: ", k);
		System.out.println(T[numMaxAnonSets - 1][numData - 1]);
		return this.reconstructSolutionArray();
	}

	public Record[] reconstructSolutionArray() {

		int curCol = numData - 1;
		int endIndex = numData - 1;

		for (int curRow = numMaxAnonSets - 1; curRow >= 0; curRow--) {
			int startIndex = startOfSet[curRow][curCol];
			if (startIndex == -1) {
				continue; // no set in this column
			}
			int median = records[(startIndex + endIndex) / 2].age;
			for (int z = startIndex; z <= endIndex; z++) {
				records[z].age = median;
			}
			endIndex = startIndex - 1;
			curCol = endIndex;
		}

		return records;
	}

	private void print(int[][] arr) {
		for (int r = 0; r < numData; r++) {
			for (int c = 0; c < numMaxAnonSets; c++) {
				System.out.print(arr[r][c] + " ");
			}
			System.out.println();
		}
	}

	public void printCost() {
		print(T);
	}

	public void printReconstruction() {
		print(startOfSet);
	}

	public static void main(String[] args) throws IOException {

		if (args.length != 1) {
			System.err.println("Usage: java kanon <filename>");
			System.exit(-1);
		}

		Parser parser = new Parser();
		// parser.generateData(30);
		Record[] testData = parser.loadData(args[0]);

		kanon anonymizer = new kanon(testData, 4);
		// System.out.println("Original data");
		// anonymizer.printRecords();

		Record[] ans = anonymizer.solve();
		// anonymizer.printReconstruction();

		// System.out.println("Anonymized data");
		// anonymizer.printRecords();

		parser.save(ans, args[0]);

	}
}
