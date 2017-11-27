import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ktest {
	static Map<String, Integer> count = new HashMap<String, Integer>();

	public static void main(String[] args) throws IOException {

		if (args.length != 1) {
			System.err.println("Usage: java ktest <filename>");
			return;
		}

		try (BufferedReader reader = new BufferedReader(new FileReader(args[0]))) {

			String line;
			while ((line = reader.readLine()) != null) {
				line = line.replace(" ", "");
				line = line.substring(0, line.lastIndexOf(','));
				//System.out.println(line);
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
			//System.out.printf("Key [%s], count [%d]\n", s, count.get(s));
		}
		System.out.println("Satisfies " + min + "-anonymity");
		System.exit(min);
	}
}
