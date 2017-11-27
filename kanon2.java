import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

class Const {
	public static final int MAX_AGE = 100;
	public static final int MAX_HEIGHT = 200;
	public static final int MAX_WEIGHT = 200;
	public static final int MAX_WIDTH = 100;
	public static final int MAX_SHOE_SIZE = 50;
	public static final int MAX_CHILDREN = 10;
}

class Record6d implements Comparable<Record6d> {
	int age, height, weight, width, shoeSize, numChildren;
	int hasPhage;
	int _id;

	public Record6d(int age, int height, int weight, int width, int shoeSize,
			int numChildren, int hasPhage, int _id) {
		super();
		this.age = age;
		this.height = height;
		this.weight = weight;
		this.width = width;
		this.shoeSize = shoeSize;
		this.numChildren = numChildren;
		this.hasPhage = hasPhage;
		this._id = _id;
	}

	@Override
	public String toString() {
		return String.format("%d,%d,%d,%d,%d,%d,%d", age, height, weight,
				width, shoeSize, numChildren, hasPhage);
	}

	public int distTo(Record6d other) {
		return Math.abs(this.age - other.age)
				+ Math.abs(this.height - other.height)
				+ Math.abs(this.weight - other.weight)
				+ Math.abs(this.width - other.width)
				+ Math.abs(this.shoeSize - other.shoeSize)
				+ Math.abs(this.numChildren - other.numChildren);
	}

	@Override
	public int compareTo(Record6d arg0) {
		return new Integer(this._id).compareTo(arg0._id);
	}
}

public class kanon2 {

	private static Set<Set<Record6d>> greedyKMemberClustering(Set<Record6d> s,
			int k) {
		if (s.size() <= k) {
			Set<Set<Record6d>> ans = new HashSet<>();
			ans.add(s);
			return ans;
		}
		Set<Set<Record6d>> result = new HashSet<>();
		Record6d r = getRandomRecord(s);

		while (s.size() >= k) {
			r = getFurthestRecordAwayFromRecord(s, r);
			s.remove(r);
			Set<Record6d> c = new HashSet<>();
			c.add(r);

			while (c.size() < k) {
				r = findBestRecord(s, c);
				s.remove(r);
				c.add(r);
			}
			result.add(c);
		}
		while (s.size() > 0) {
			r = getRandomRecord(s);
			s.remove(r);
			Set<Record6d> c = findBestCluster(result, r);
			c.add(r);
		}

		return result;
	}

	private static Set<Record6d> findBestCluster(Set<Set<Record6d>> clusters,
			Record6d r) {
		int min = Integer.MAX_VALUE;
		Set<Record6d> best = null;

		for (Set<Record6d> c : clusters) {
			int originalCost = infoLossCost(c);
			c.add(r);
			int newCost = infoLossCost(c);
			c.remove(r);

			int diff = newCost - originalCost;

			if (diff < min) {
				min = diff;
				best = c;
			}
		}

		return best;
	}

	private static Record6d getRandomRecord(Set<Record6d> set) {
		int size = set.size();
		int item = new Random().nextInt(size);
		int i = 0;
		for (Record6d pr : set) {
			if (i == item) {
				return pr;
			}
			i++;
		}
		throw new RuntimeException("Shouldn't be reachable");
	}

	private static Record6d getFurthestRecordAwayFromRecord(Set<Record6d> all,
			Record6d origin) {

		int maxDist = Integer.MIN_VALUE;
		Record6d furthest = origin;
		for (Record6d pr : all) {
			if (pr.distTo(origin) > maxDist) {
				maxDist = pr.distTo(origin);
				furthest = pr;
			}
		}

		return furthest;
	}

	private static Record6d findBestRecord(Set<Record6d> s, Set<Record6d> c) {
		int n = s.size();
		int min = Integer.MAX_VALUE;
		Record6d best = null;

		Set<Record6d> tempCluster = new HashSet<>();
		tempCluster.addAll(c);

		int originalCost = infoLossCost(c);

		for (Record6d r : s) {

			tempCluster.add(r);
			int newCost = infoLossCost(tempCluster);
			tempCluster.remove(r);
			int diff = newCost - originalCost;

			if (diff < min) {
				min = diff;
				best = r;
			}
		}
		return best;
	}

	private static int infoLossCost(Set<Record6d> s) {
		Record6d throwawayMedian = findMedian(s);

		int infoLossCost = 0;
		for (Record6d r : s) {
			infoLossCost += throwawayMedian.distTo(r);
		}
		return infoLossCost;

	}

	private static Record6d findMedian(Set<Record6d> s) {
		List<Integer[]> qidList = new ArrayList<>();
		for (int k = 0; k < NUM_QID; k++) {
			qidList.add(new Integer[s.size()]);
		}

		int i = 0;
		for (Record6d r : s) {
			qidList.get(0)[i] = r.age;
			qidList.get(1)[i] = r.height;
			qidList.get(2)[i] = r.weight;
			qidList.get(3)[i] = r.width;
			qidList.get(4)[i] = r.shoeSize;
			qidList.get(5)[i] = r.numChildren;
			i++;
		}

		for (int j = 0; j < NUM_QID; j++) {
			Arrays.sort(qidList.get(j));
		}
		int mid = s.size() / 2;
		Record6d throwawayMedian = new Record6d(qidList.get(0)[mid],
				qidList.get(1)[mid], qidList.get(2)[mid], qidList.get(3)[mid],
				qidList.get(4)[mid], qidList.get(5)[mid], -1, -1);
		return throwawayMedian;
	}

	static final int NUM_QID = 6;

	// public static void generateData(int num) throws IOException {
	//
	// try (PrintWriter printWriter = new PrintWriter(new BufferedWriter(
	// new FileWriter("output6d.txt")))) {
	// Random r = new Random();
	//
	// for (int k = 0; k < num; k++) {
	// printWriter.print(r.nextInt(Const.MAX_AGE) + ","
	// + r.nextInt(Const.MAX_HEIGHT) + ","
	// + r.nextInt(Const.MAX_WEIGHT) + ","
	// + r.nextInt(Const.MAX_WIDTH) + ","
	// + r.nextInt(Const.MAX_SHOE_SIZE) + ","
	// + r.nextInt(Const.MAX_CHILDREN) + "," + r.nextInt(2));
	// if (k < num - 1)
	// printWriter.println();
	// }
	// }
	// }

	private static boolean checkIfDataAlreadySatifiesKAnonymity(
			String filename, int k) throws FileNotFoundException, IOException {
		Map<String, Integer> count = new HashMap<String, Integer>();

		try (BufferedReader reader = new BufferedReader(
				new FileReader(filename))) {

			String line;
			while ((line = reader.readLine()) != null) {
				line = line.replace(" ", "");
				line = line.substring(0, line.lastIndexOf(','));
				// System.out.println(line);
				if (count.containsKey(line)) {
					count.put(line, 1 + count.get(line));
				} else {
					count.put(line, 1);
				}
			}
		}
		int min = Integer.MAX_VALUE;
		for (String s : count.keySet()) {
			if (count.get(s) < min) {
				min = count.get(s);
			}
			// System.out.printf("Key [%s], count [%d]\n", s, count.get(s));
		}
		return min >= k;

	}

	public static final int K = 4;

	public static void main(String[] args) throws FileNotFoundException,
			IOException {

		if (args.length != 1) {
			System.err.println("Usage: java kanon2 <filename>");
			System.exit(-1);
		}

		if (checkIfDataAlreadySatifiesKAnonymity(args[0], K)) {
			System.out.println("data already satisfies " + K + "-anonymity...");
			return;
		}

		Set<Record6d> records = new HashSet<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(args[0]))) {

			String line;
			int id = 0;
			while ((line = reader.readLine()) != null) {
				String[] vals = line.split(",");
				List<Integer> qidList = new ArrayList<>();
				for (int k = 0; k < NUM_QID + 1; k++) {
					qidList.add(Integer.parseInt(vals[k]));
				}
				records.add(new Record6d(qidList.get(0), qidList.get(1),
						qidList.get(2), qidList.get(3), qidList.get(4), qidList
								.get(5), qidList.get(6), id++));
			}
		}

		Set<Set<Record6d>> clusters = greedyKMemberClustering(records, K);
		List<Record6d> ans = new ArrayList<>();

		int totalInfoLossCost = 0;
		for (Set<Record6d> cluster : clusters) {
			Record6d throwawayMedian = findMedian(cluster);
			for (Record6d r : cluster) {
				totalInfoLossCost += r.distTo(throwawayMedian);
				r.age = throwawayMedian.age;
				r.height = throwawayMedian.height;
				r.weight = throwawayMedian.weight;
				r.width = throwawayMedian.width;
				r.shoeSize = throwawayMedian.shoeSize;
				r.numChildren = throwawayMedian.numChildren;
				ans.add(r);
			}
		}
		Collections.sort(ans);

		try (PrintWriter pw = new PrintWriter(new BufferedWriter(
				new FileWriter(args[0])))) {
			for (int j = 0; j < ans.size(); j++) {
				pw.print(ans.get(j));
				if (j < ans.size() - 1)
					pw.println();
			}
		}

		System.out.println("Converted to " + K + "-anonymity with cost: "
				+ totalInfoLossCost);

	}
}
