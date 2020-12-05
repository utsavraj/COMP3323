import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

// Multi-Map 
class MultiMap<K, V> {
	private Map<K, Collection<V>> map = new HashMap<>();

	public void put(K key, V value) {
		if (map.get(key) == null)
			map.put(key, new ArrayList<V>());

		map.get(key).add(value); // Add the specified value with the specified key
	}

	public void putIfAbsent(K key, V value) {
		if (map.get(key) == null)
			map.put(key, new ArrayList<>()); // Associate the specified key with the given value if not already
												// associated with a value

		// if value is absent, insert it
		if (!map.get(key).contains(value)) {
			map.get(key).add(value);
		}
	}

	public Collection<V> get(Object key) {
		return map.get(key); // Collection of values to which the specified key is mapped or null if doesnt
								// exist
	}

	public Set<K> keySet() {
		return map.keySet(); // Returns a Set view of the keys contained in this multimap.
	}

	public Set<Map.Entry<K, Collection<V>>> entrySet() {
		return map.entrySet(); // Returns a Set view of the mappings contained in this multimap.
	}

	public Collection<Collection<V>> values() {
		return map.values(); // Collection view of Collection of the values present
	}

	public boolean containsKey(Object key) {
		return map.containsKey(key); // true if this multimap contains a mapping for the specified key.
	}

	public Collection<V> remove(Object key) {
		return map.remove(key); // Removes the mapping for the specified key from this multimap and returns the
								// Collection of previous values associated with key, or null if there was no
								// mapping for key.
	}

	public int size() {
		int size = 0;
		for (Collection<V> value : map.values()) {
			size += value.size(); // the number of key-value mappings
		}
		return size;
	}

	public boolean isEmpty() {
		return map.isEmpty(); // true if this multimap contains no key-value mappings.
	}

	public void clear() {
		map.clear(); // Removes all of the mappings from this multimap.
	}

	public boolean remove(K key, V value) {
		if (map.get(key) != null) // key exists
			return map.get(key).remove(value); // Removes the entry for the specified key only if it is currently mapped
												// to the specified value and return true if removed

		return false;
	}

	public boolean replace(K key, V oldValue, V newValue) {

		if (map.get(key) != null) {
			if (map.get(key).remove(oldValue))
				return map.get(key).add(newValue); // Replaces the entry for the specified key only if currently mapped
													// to the specified value and return true if replaced
		}
		return false;
	}
}

public class makeIndex {

	public static void duplicate_elimination(String data_path, String data_path_new) {
		// read the original dataset from data_path
		// eliminate duplicates by deleting the corresponding lines
		// write the dataset without duplicates into data_path_new

		// Number of rows
		int n = 6442892;

		// Array to store latitude values
		Double[] latitude = new Double[n + 1];

		// Array to store longitude values
		Double[] longitude = new Double[n + 1];

		// Array to store location id values
		Double[] location_id = new Double[n + 1];

		try {
			BufferedReader all_data = new BufferedReader(new FileReader(data_path));//
			int index = 0;
			for (String line = all_data.readLine(); line != null; line = all_data.readLine()) {

				String[] values = line.split("	");

				// minimum and maximum longitude/y-values and latitude/x-values
				// x_min=-90.0, x_max=90.0, y_min=-176.3, y_max=177.5.
				// Note that the nodes with error coordinates should also be deleted, e.g., the
				// nodes whose latitude is bigger than 400.

				if (!(Double.parseDouble(values[2]) > 90.0 || Double.parseDouble(values[2]) < -90.0)) { // 29 here as
																										// bigger than
																										// 400

					if (!(Double.parseDouble(values[3]) > 177.5 || Double.parseDouble(values[3]) < -176.3)) { // 1 here
																												// as
																												// longitude
																												// is
																												// smaller
						// 3rd, the 4th, and the 5th columns (i.e., [latitude], [longitude], [location
						// id]).

						// Latitude is third column
						latitude[index] = Double.parseDouble(values[2]);

						// Longitude is fourth column
						longitude[index] = Double.parseDouble(values[3]);

						// Location ID is fifth column
						location_id[index] = Double.parseDouble(values[4]);
					}

				}

				index++;
			}

			all_data.close();
		}

		catch (IOException e) {
			System.out.println("ERROR: Please put the path to Gowalla_totalCheckins.txt correctly.");
			e.printStackTrace();
		}

		// Removing any null values from the arrays
		location_id = Arrays.stream(location_id).filter(Objects::nonNull).toArray(Double[]::new);
		longitude = Arrays.stream(longitude).filter(Objects::nonNull).toArray(Double[]::new);
		latitude = Arrays.stream(latitude).filter(Objects::nonNull).toArray(Double[]::new);
		// System.out.println(location_id.length); // 6442862

		// Please delete the lines whose locations are same
		// (longitude and latitude value), and only keep the one with the smallest
		// location id

		Double[][] temp_file = new Double[location_id.length][3];

		for (int i = 0; i < location_id.length; i++) {
			temp_file[i][2] = location_id[i];
			temp_file[i][0] = latitude[i];
			temp_file[i][1] = longitude[i];
		}

		ArrayList<String> dataframe = new ArrayList<String>();
		Arrays.stream(temp_file)
				.collect(Collectors.groupingBy(t -> Arrays.asList(t[0], t[1]), LinkedHashMap::new,
						Collectors.minBy(Comparator.comparingDouble(t -> t[2]))))

				.forEach((k, v) -> dataframe
						.add(Arrays.toString(v.get()).replaceAll("^\\[|\\]$", "").replaceAll(",", " ")));

		// System.out.println(dataframe.size()); // 1256679

		try {

			FileWriter writer = new FileWriter(data_path_new);
			BufferedWriter buffer = new BufferedWriter(writer);

			for (int i = 0; i < dataframe.size(); i++) {
				String[] row_values = dataframe.get(i).split(" ");
				buffer.write(row_values[0] + " " + row_values[2] + " " + row_values[4].split("\\.")[0]);
				buffer.newLine();
			}

			buffer.close();

		} catch (IOException e) {
			System.out.println("Error: Modified Gowalla_totalCheckins.txt file creation unsuccesful");
			e.printStackTrace();
		}

	}

	public static void create_index(String data_path_new, String index_path, int n) {

		// x-axis - hence latitude
		double cell_width = 180.0 / n;

		// y-axis - hence longitude
		double cell_height = (177.5 + 176.3) / n;

		// lower bound of latitude and longitude
		double low_bound_x = -90.0;
		double low_bound_y = -176.3;

		// multiMap - contains Cell 'x,y' values as keys and list of value that are
		// under it.
		MultiMap<String, String> Grid = new MultiMap();

		// ArrayList to store latitude values
		ArrayList<Double> latitude = new ArrayList<Double>();

		// ArrayList to store longitude values
		ArrayList<Double> longitude = new ArrayList<Double>();

		// ArrayList to store location id values
		ArrayList<Double> location_id = new ArrayList<Double>();

		try {
			BufferedReader all_data = new BufferedReader(new FileReader(data_path_new));//
			for (String line = all_data.readLine(); line != null; line = all_data.readLine()) {

				String[] values = line.split(" ");

				// Latitude is first column - in new file
				latitude.add(Double.parseDouble(values[0]));

				// Longitude is second column - in new file
				longitude.add(Double.parseDouble(values[1]));

				// Location ID is last column - in new file
				location_id.add(Double.parseDouble(values[2]));

			}

			all_data.close();
		}

		catch (IOException e) {
			System.out.println("ERROR: Please put the file path of the dataset without duplicates correctly.");
			e.printStackTrace();
		}

		// To create a grid index
		for (int i = 0; i < latitude.size(); i++) {

			// x column in double
			double temp_cell_index_x = (latitude.get(i) - low_bound_x) / cell_width;

			// y column in double
			double temp_cell_index_y = (longitude.get(i) - low_bound_y) / cell_height;

			// column x ( Cell[][x] )
			int cell_index_x;

			// column y ( Cell[y][] )
			int cell_index_y;

			// Notice that if p is located on boundaries but not on maxBox, then it should
			// belong to the
			// cell on its left or Check for max value (n) which should be (n-1) as index
			// start from zero.
			if (temp_cell_index_x % 1 == 0 && ((int) temp_cell_index_x != 0)) {
				cell_index_x = (int) (temp_cell_index_x - 1);
			} else {
				cell_index_x = (int) (temp_cell_index_x);
			}

			// or its top or Check for max value
			if (temp_cell_index_y % 1 == 0 && ((int) temp_cell_index_y != 0)) {
				cell_index_y = (int) (temp_cell_index_y - 1);
			} else {
				cell_index_y = (int) (temp_cell_index_y);
			}

			// y starts from top 0 to bottom (n-1)
			cell_index_y = (n - 1) - cell_index_y;

			// Convention of naming = y, x NOT x, y
			// String row_key = "Cell " + String.valueOf(cell_index_y) + ", " +
			// String.valueOf(cell_index_x) + ":";

			String row_key = "Cell " + String.valueOf(cell_index_y) + ", " + String.valueOf(cell_index_x) + ":";

			// row_key = "Cell " + String.valueOf(cell_index_y) + ", " +
			// String.valueOf(cell_index_x) + ":";

			// a.id_a.x_a.y
			Grid.put(row_key, String.valueOf(location_id.get(i)).split("\\.")[0] + "_"
					+ Double.toString(latitude.get(i)) + "_" + Double.toString(longitude.get(i)));

		}

		try {

			// and save it to file on "index_path".
			FileWriter writer = new FileWriter(index_path);
			BufferedWriter buffer = new BufferedWriter(writer);
			// The output file should contain exactly n*n lines.
			for (int i = 0; i < n; i++) {

				for (int j = 0; j < n; j++) {

					String temp_key = "Cell " + String.valueOf(i) + ", " + String.valueOf(j) + ":";

					// If there is no point in the cell, just leave it empty after ":".
					if (Grid.get(temp_key) == null) {
						buffer.write(temp_key);
					} else {
						buffer.write(temp_key + " "
								+ Grid.get(temp_key).toString().replaceAll("^\\[|\\]$", "").replaceAll(",", ""));

					}
					buffer.newLine();
				}

			}

			buffer.close();

		} catch (IOException e) {
			System.out.println("Error: index_path.txt file creation unsuccesful");
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		if (args.length != 4) {
			System.out.println("Usage: java makeIndex DATA_PATH INDEX_PATH DATA_PATH_NEW N");
			/*
			 * DATA_PATH(String): the file path of Gowalla_totalCheckins.txt
			 * INDEX_PATH(String): the output file path of the grid index
			 * DATA_PATH_NEW(String): the file path of the dataset without duplicates
			 * N(integer): the grid index size
			 */
			return;
		}
		duplicate_elimination(args[0], args[2]);
		long s = System.currentTimeMillis();
		create_index(args[2], args[1], Integer.parseInt(args[3]));
		long t = System.currentTimeMillis();
		System.out.println("Index construction time: " + (t - s));
	}
}