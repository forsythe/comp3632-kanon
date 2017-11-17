class Record {
	public int age;
	public int hasPhage;

	public Record(int age, int hasPhage) {
		if (hasPhage != 0 && hasPhage != 1) {
			throw new IllegalArgumentException();
		}

		this.age = age;
		this.hasPhage = hasPhage;

	}

	@Override
	public String toString() {
		return String.format("Age[%d], Phage[%d]", age, hasPhage);
	}
}

public class kanon {
	public static final int K = 3; // K-anonymity

	private int numData;
	private int numCols;
	private int[][] T; // cost array
	private int[][] startOfSet; // for reconstructing solution
	private Record[] records;

	public kanon(Record[] records) {
		this.numData = records.length;
		this.numCols = numData / K;
		this.T = new int[numData][numCols];
		this.startOfSet = new int[numData][numCols];
		this.records = records;
	}

	public void printRecords() {
		for (Record r : records) {
			System.out.println(r);
		}
	}

	private int getCost(int start, int end) {

		int cost = 0;
		int median = records[(start + end) / 2].age;
		for (int j = start; j <= end; j++) {
			cost += Math.abs(median - records[j].age);
		}

		return cost;
	}

	public void solve() {
		// initialize the invalid rows
		for (int row = 0; row < K - 1; row++) {
			for (int col = 0; col < numCols; col++) {
				T[row][col] = Integer.MAX_VALUE;
				startOfSet[row][col] = Integer.MAX_VALUE;
			}
		}

		// initialize base case (1 set)
		for (int row = K - 1; row < numData; row++) {
			T[row][0] = getCost(0, row);
			startOfSet[row][0] = 0;
		}

		// apply recurrence
		for (int col = 1; col < numCols; col++) {
			for (int row = K - 1; row < numData; row++) {
				T[row][col] = T[row][col - 1];
				startOfSet[row][col] = startOfSet[row][col - 1];

				for (int z = K; z <= row + 1 - K; z++) {
					if (T[z - 1][col - 1] + getCost(z, row) < T[row][col]) {
						T[row][col] = T[z - 1][col - 1] + getCost(z, row);
						startOfSet[row][col] = z;
					}

				}

			}
		}

		System.out.printf("Minimum change to achieve %d-anonymity: ", K);
		System.out.println(T[numData - 1][numCols - 1]);
	}

	public Record[] reconstructSolution() {
		int curRow = numData - 1;
		int curCol = numCols - 1;

		int startIndex = startOfSet[curRow][curCol];
		int endIndex = numData - 1;
		while (startIndex > 0) {
			int median = records[(startIndex + endIndex) / 2].age;
			for (int z = startIndex; z <= endIndex; z++) {
				records[z].age = median;
			}
			curRow -= (endIndex - startIndex);
			curCol--;
			endIndex = startIndex - 1;
			startIndex = startOfSet[curRow][curCol];
		}
		return records;
	}

	private void print(int[][] arr) {
		for (int r = 0; r < numData; r++) {
			for (int c = 0; c < numCols; c++) {
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

	public static void main(String[] args) {
		Record[] testData = {
				//
				new Record(20, 1),//
				new Record(20, 0),//
				new Record(20, 0),//
				new Record(25, 0),//
				new Record(25, 0),//
				new Record(29, 1),//
				new Record(29, 1),//
		};
		kanon anonymizer = new kanon(testData);
		System.out.println("Original data");
		anonymizer.printRecords();
		anonymizer.solve();

		anonymizer.reconstructSolution();
		System.out.println("k-anonymized data");
		anonymizer.printRecords();

	}
}
