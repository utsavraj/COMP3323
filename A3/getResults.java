import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class getResults {
	public static String knn_grid(double x, double y, String index_path, int k, int n) {
		// to get the k-NN result with the help of the grid index
		// Please store the k-NN results by a String of location ids, like "11, 789,
		// 125, 2, 771"

		// Latitude(x-value) of q
		double q_x = x;

		// Longitude(y-value) of q
		double q_y = y;

		// Although, covered in threshold, still good practice
		if (q_x < -90.0 || q_x > 90.0 || q_y > 177.5 || q_y < -176.3) {
			return "Invalid value of q (x,y)";
		}

		int grid_size = n;

		// x-axis - hence latitude
		double cell_width = 180.0 / grid_size;

		// y-axis - hence longitude
		double cell_height = (177.5 + 176.3) / grid_size;

		// lower bound of latitude and longitude
		double low_bound_x = -90.0;
		double low_bound_y = -176.3;
		double up_bound_x = 90;
		double up_bound_y = 177.5;
		// x column in double
		double temp_q_index_x = (q_x - low_bound_x) / cell_width;

		// y column in double
		double temp_q_index_y = (q_y - low_bound_y) / cell_height;

		// column x ( Grid[][x] ) for 'q'
		int q_column_x;

		// column y ( Grid[y][] ) for 'q'
		int q_column_y;

		// Notice that if p is located on boundaries but not on maxBox, then it should
		// belong to the
		// cell on its left or Check for max value (n) which should be (n-1) as index
		// start from zero.
		if (temp_q_index_x % 1 == 0 && ((int) temp_q_index_x != 0)) {
			q_column_x = (int) (temp_q_index_x - 1);
		} else {
			q_column_x = (int) (temp_q_index_x);
		}

		// or its top or Check for max value
		if (temp_q_index_y % 1 == 0 && ((int) temp_q_index_y != 0)) {
			q_column_y = (int) (temp_q_index_y - 1);
		} else {
			q_column_y = (int) (temp_q_index_y);
		}

		// y starts from top 0 to bottom (n-1)
		q_column_y = (n - 1) - q_column_y;

		int k_neighbours = k;

		ArrayList<String>[][] Grid = new ArrayList[grid_size][grid_size];

		try {
			BufferedReader all_data = new BufferedReader(new FileReader(index_path));//
			for (String line = all_data.readLine(); line != null; line = all_data.readLine()) {

				// the whole line containing the Grid x,y as well as the points
				String[] values = line.split(":");

				// Co-ordinates of Grid[y][x]
				String[] grid_xywc = values[0].split(" ");

				// also removing the comma
				int y_grid = Integer.valueOf(grid_xywc[1].substring(0, grid_xywc[1].length() - 1));
				int x_grid = Integer.valueOf(grid_xywc[2]);

				Grid[y_grid][x_grid] = new ArrayList<String>();

				// each a.id_a.x_a.y
				try {
					String[] elements = values[1].split(" ");

					// Conversion from String array to list of String to an ArrayList
					List<String> fixedLenghtListxy = Arrays.asList(elements);
					ArrayList<String> listOfxy = new ArrayList<String>(fixedLenghtListxy);
					// Remove Starting Empty Character
					listOfxy.remove(0);

					Grid[y_grid][x_grid].addAll(listOfxy);

					// if not point in cell of the grid
				} catch (ArrayIndexOutOfBoundsException e) {
					Grid[y_grid][x_grid].add("null");
				}

			}

			all_data.close();
		}

		catch (IOException e) {
			System.out.println("ERROR: Path to file index_path.txt is incorrect");
			e.printStackTrace();
		}

		// KNN_Grid Search

		/*
		 * //Creating the layer as an arrayList structure //first column
		 * 
		 */
		List<List<String>> Layers = new ArrayList<List<String>>();

		// intilize layer one (0 for index) - which the cell containing the q's (y,x
		// values)
		Layers.add(new ArrayList<String>());
		Layers.get(0).add(Integer.toString(q_column_y) + ',' + Integer.toString(q_column_x));

		for (int b = 1; b < grid_size; b++) {
			Layers.add(new ArrayList<String>());
			List<Integer> list = new ArrayList<Integer>();
			// Top-Left
			int tl_y = q_column_y - b;
			int tl_x = q_column_x - b;

			// Top-Right
			int tr_y = q_column_y - b;
			int tr_x = q_column_x + b;

			// Bottom-Left
			int bl_y = q_column_y + b;
			int bl_x = q_column_x - b;

			// Bottom-Right
			int br_y = q_column_y + b;
			int br_x = q_column_x + b;

			// Top Row/width
			list = IntStream.rangeClosed(tl_x, tr_x).boxed().collect(Collectors.toList());
			for (int u = 0; u < list.size(); u++) {
				if (list.get(u) > -1 && list.get(u) < grid_size && tl_y > -1 && tl_y < grid_size) {
					Layers.get(b).add(Integer.toString(tl_y) + ',' + Integer.toString(list.get(u)));
				}
			}
			list.clear();

			// Bottom Row/width
			list = IntStream.rangeClosed(bl_x, br_x).boxed().collect(Collectors.toList());
			for (int u = 0; u < list.size(); u++) {
				if (list.get(u) > -1 && list.get(u) < grid_size && bl_y > -1 && bl_y < grid_size) {
					Layers.get(b).add(Integer.toString(bl_y) + ',' + Integer.toString(list.get(u)));
				}
			}
			list.clear();

			// Left Column/Height (excluding corners)
			list = IntStream.rangeClosed(tl_y, bl_y).boxed().collect(Collectors.toList());
			list.remove(0);
			list.remove(list.size() - 1);
			for (int u = 0; u < list.size(); u++) {
				if (list.get(u) > -1 && list.get(u) < grid_size && tl_x > -1 && tl_x < grid_size) {
					Layers.get(b).add(Integer.toString(list.get(u)) + ',' + Integer.toString(tl_x));
				}
			}
			list.clear();

			// Right Column/Height (excluding corners)
			list = IntStream.rangeClosed(tr_y, br_y).boxed().collect(Collectors.toList());
			list.remove(0);
			list.remove(list.size() - 1);
			for (int u = 0; u < list.size(); u++) {
				if (list.get(u) > -1 && list.get(u) < grid_size && tr_x > -1 && tr_x < grid_size) {
					Layers.get(b).add(Integer.toString(list.get(u)) + ',' + Integer.toString(tr_x));
				}
			}
			list.clear();

		}

		// Removing null elements left due to negative or higher than n
		for (int u = 0; u < Layers.size(); u++) {
			if (Layers.get(u).isEmpty()) {
				Layers.remove(u);
				u--; // u decremented
				// since the indices of the elements that follow the removed element are
				// decremented.
			}
		}

		// Stores all the k neighbours of q
		// First column contains distance from q and second contains the location id

		String[][] resulting_neighbours = new String[k_neighbours][2];

		// t is distance of highest distant neighbour
		double t = -1.0;

		// -k_neighbours when null are present in our array - unnecessary when array
		// filled
		double null_handler = -Double.valueOf(k_neighbours) - 1;
		boolean array_filled = false;

		boolean prune_allayers = true;

		for (int b = 0; b < Layers.size(); b++) {

			// System.out.println(" -------- Layer " + b);
			prune_allayers = true;
			for (int u = 0; u < Layers.get(b).size(); u++) {
				String[] temp_values = Layers.get(b).get(u).split(",");

				// shortest distance between q and cell
				double dlow = -2.0;

				if (q_column_x == Integer.valueOf(temp_values[1]) && q_column_y == Integer.valueOf(temp_values[0])) {
					dlow = -2.0;
				}
				// Directly Top/Bottom
				else if (q_column_x == Integer.valueOf(temp_values[1])) {
					// Top
					if (q_column_y - Integer.valueOf(temp_values[0]) > 0) {

						// Boxes + Remaining
						dlow = (q_column_y - 1 - Integer.valueOf(temp_values[0])) * cell_height
								+ ((grid_size - q_column_y) * cell_height + low_bound_y) - q_y;

					}
					// Bottom
					else {
						// Boxes + Remaining
						dlow = (Integer.valueOf(temp_values[0]) - 1 - q_column_y) * cell_height + cell_height
								- (((grid_size - q_column_y) * cell_height + low_bound_y) - q_y);

					}

				}
				// or Directly Left/Right
				else if (q_column_y == Integer.valueOf(temp_values[0])) {

					// Left
					if (q_column_x - Integer.valueOf(temp_values[1]) > 0) {
						// Boxes + Remaining
						dlow = (q_column_x - 1 - Integer.valueOf(temp_values[1])) * cell_width
								+ (q_x - (q_column_x * cell_width + low_bound_x));

					}
					// Right
					else {
						// Boxes + Remaining
						dlow = (Integer.valueOf(temp_values[1]) - 1 - q_column_x) * cell_width
								+ (cell_width - (q_x - (q_column_x * cell_width + low_bound_x)));

					}
				}

				// L-shape or diagonal
				else {

					// Top diagonal

					if (q_column_y > Integer.valueOf(temp_values[0])) {
						// Closest distance between two points
						// Point(y) - y.x Point(x) - x.y

						// Right
						if (q_column_x < Integer.valueOf(temp_values[1])) {
							dlow = Math.sqrt(Math
									.pow((grid_size - Integer.valueOf(temp_values[0]) - 1) * cell_height + low_bound_y
											- q_y, 2)
									+ Math.pow((Integer.valueOf(temp_values[1])) * cell_width + low_bound_x - q_x, 2));
							// dlow = -2.0;

						}

						else {// Left {
							dlow = Math.sqrt(Math
									.pow((grid_size - Integer.valueOf(temp_values[0]) - 1) * cell_height + low_bound_y
											- q_y, 2)
									+ Math.pow((Integer.valueOf(temp_values[1]) + 1) * cell_width + low_bound_x - q_x,
											2));
						} // dlow = -2.0; }
					}

//		  			
//		  			//Bottom diagonal
					else if (q_column_y < Integer.valueOf(temp_values[0])) {

						// Right
						if (q_column_x < Integer.valueOf(temp_values[1])) {
							dlow = Math.sqrt(Math.pow(
									(grid_size - Integer.valueOf(temp_values[0])) * cell_height + low_bound_y - q_y, 2)
									+ Math.pow((Integer.valueOf(temp_values[1]) * cell_width + low_bound_x - q_x), 2));
						}
						// Left
						else {
							dlow = Math.sqrt(Math.pow(
									(grid_size - Integer.valueOf(temp_values[0])) * cell_height + low_bound_y - q_y, 2)
									+ Math.pow(((Integer.valueOf(temp_values[1]) + 1) * cell_width + low_bound_x - q_x),
											2));
							// dlow = -2.0;//
						}
					}
				}

//				System.out.println(
//						t + " " + dlow + " x : " + temp_values[1] + " y: " + temp_values[0] + " " + !(dlow > t));

				// if cell c is closer to q than the furtherest away point in
				// resulting_neighbours
				// OR array is not yet filled.
				if (!(dlow > t) || !array_filled) {
					// Layer has a point that can be close
					prune_allayers = false;
					for (int j = 0; j < Grid[Integer.valueOf(temp_values[0])][Integer.valueOf(temp_values[1])]
							.size(); j++) {
						// Ignore Cell if it is blank
						if (Grid[Integer.valueOf(temp_values[0])][Integer.valueOf(temp_values[1])].get(j) == "null") {

							break;
						} else {
							String[] values = Grid[Integer.valueOf(temp_values[0])][Integer.valueOf(temp_values[1])]
									.get(j).split("_");

							double point_x = Double.parseDouble(values[1]);
							double point_y = Double.parseDouble(values[2]);

							double euclidean_distance = Math
									.sqrt(Math.pow((q_x - point_x), 2) + Math.pow((q_y - point_y), 2));

							// Add to array the newest point if it does not have k values
							for (int i = 0; i < k_neighbours; i++) {
								if (resulting_neighbours[i][0] == null && null_handler < -1.0) {

									resulting_neighbours[i][0] = String.valueOf(euclidean_distance);
									resulting_neighbours[i][1] = values[0];
									t = Math.max(t, euclidean_distance);
									null_handler++;
									break;

								}
							}

							// Replace the furtherest away point with the new point

							if (Double.compare(euclidean_distance, t) <= 0 && null_handler == -1 && array_filled) {
								for (int o = 0; o < k_neighbours; o++) {
									if (resulting_neighbours[o][0].equals(String.valueOf(t))) {
										resulting_neighbours[o][0] = String.valueOf(euclidean_distance);
										resulting_neighbours[o][1] = values[0];
									}
								}

							}
							// find the highest distance point again
							if (null_handler == -1) {
								// needs to be reset
								t = -1.0;
								array_filled = true;
								for (int g = 0; g < k_neighbours; g++) {

									t = Math.max(t, Double.parseDouble(resulting_neighbours[g][0]));
								}
							}

						}
					}
				}
			}

			if (prune_allayers) {
				break;
			}
		}

		// System.out.println(Grid[0][2].size());//.get(0));
		// System.out.println(Arrays.deepToString(resulting_neighbours));

		ArrayList<String> Result_kIDs = new ArrayList<String>();
		for (int i = 0; i < resulting_neighbours.length; i++) {
			{
				Result_kIDs.add(resulting_neighbours[i][1].split("\\.")[0]);
				// System.out.println(resulting_neighbours[i][0]);
			}
		}
		// Sorts the location ID
		Result_kIDs.sort(Comparator.comparing(Double::parseDouble));
		String Result_kIDs_listString = String.join(", ", Result_kIDs);

		return Result_kIDs_listString;

	}

	public static String knn_linear_scan(double x, double y, String data_path_new, int k) {
		// to get the k-NN result by linear scan
		// Please store the k-NN results by a String of location ids, like "11, 789,
		// 125, 2, 771"

		// Latitude(x-value) of q
		double q_x = x;

		// Longitude(y-value) of q
		double q_y = y;

		// Although, covered in threshold, still good practice
		if (q_x < -90.0 || q_x > 90.0 || q_y > 177.5 || q_y < -176.3) {
			return "Invalid value of q (x,y)";
		}

		int k_neighbours = k;

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

		// KNN Linear Search

		// Stores all the k neighbours of q
		// First column contains distance from q and second contains the location id
		String[][] resulting_neighbours = new String[k_neighbours][2];

		// t is distance of highest distant neighbour
		double t = -1.0;
		// -k_neighbours when null are present in our array
		double null_handler = -Double.valueOf(k_neighbours) - 1;
		boolean array_filled = false;

		for (int j = 0; j < location_id.size(); j++) {

			double point_x = latitude.get(j);
			double point_y = longitude.get(j);

			double euclidean_distance = Math.sqrt(Math.pow((q_x - point_x), 2) + Math.pow((q_y - point_y), 2));

			// Add to array the newest point if it does not have k values
			for (int i = 0; i < k_neighbours; i++) {

				if (resulting_neighbours[i][0] == null && null_handler < -1.0) {
					resulting_neighbours[i][0] = String.valueOf(euclidean_distance);
					resulting_neighbours[i][1] = String.valueOf(location_id.get(j));
					t = Math.max(t, euclidean_distance);

					null_handler++;
					break;

				}
			}

			// Replace the furtherest away point with the new point

			if (Double.compare(euclidean_distance, t) < 0 && null_handler == -1.0 && array_filled) {

				for (int o = 0; o < k_neighbours; o++) {

					if (resulting_neighbours[o][0].equals(String.valueOf(t))) {
						resulting_neighbours[o][0] = String.valueOf(euclidean_distance);
						resulting_neighbours[o][1] = String.valueOf(location_id.get(j));

					}
				}

			}
			// find the highest distance point again
			if (null_handler == -1) {
				array_filled = true;
				// needs to be reset
				t = 0.0;
				for (int g = 0; g < k_neighbours; g++) {

					t = Math.max(t, Double.parseDouble(resulting_neighbours[g][0]));
				}
			}

		}

		ArrayList<String> Result_kIDs = new ArrayList<String>();
		for (int i = 0; i < resulting_neighbours.length; i++) {
			{
				Result_kIDs.add(resulting_neighbours[i][1].split("\\.")[0]);
				// System.out.println(resulting_neighbours[i][1] + " dis " +
				// resulting_neighbours[i][0]);
			}
		}

		// Sorts the location ID
		Result_kIDs.sort(Comparator.comparing(Double::parseDouble));

		String Result_kIDs_listString = String.join(", ", Result_kIDs);
		return Result_kIDs_listString;
	}

	public static void main(String args[]) {
		if (args.length != 6) {
			System.out.println("Usage: java getResults X Y DATA_PATH_NEW INDEX_PATH K N");
			/*
			 * X(double): the latitude of the query point q Y(double): the longitude of the
			 * query point q DATA_PATH_NEW(String): the file path of dataset you generated
			 * without duplicates INDEX_PATH(String): the file path of the grid index
			 * K(integer): the k value for k-NN search N(integer): the grid index size
			 */
			return;
		}
		long s = System.currentTimeMillis();
		System.out.println("Linear scan results: " + knn_linear_scan(Double.parseDouble(args[0]),
				Double.parseDouble(args[1]), args[2], Integer.parseInt(args[4])));
		long t = System.currentTimeMillis();
		System.out.println("Linear scan time: " + (t - s));
		s = System.currentTimeMillis();
		System.out.println("Grid index search results: " + knn_grid(Double.parseDouble(args[0]),
				Double.parseDouble(args[1]), args[3], Integer.parseInt(args[4]), Integer.parseInt(args[5])));
		t = System.currentTimeMillis();
		System.out.println("Grid index search time: " + (t - s));
	}
}