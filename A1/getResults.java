
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class getResults{
	public static double estimate_eqWidth(int LEFT, int RIGHT, String histogram_path) throws NumberFormatException, IOException{
		// to get the estimated result using the equi-width histogram
		
		//Array to store tuples values
		ArrayList<Integer> tuples_num = new ArrayList<Integer>();

		
		try {
			//Reading the contents of eq_depth.dat file
			BufferedReader all_data = new BufferedReader(new FileReader(histogram_path));// + "/eq_width.dat"));
			String row;

			// Adding the age value from each row to an array
			while ((row = all_data.readLine()) != null) {
				tuples_num.add(Integer.parseInt(row));
	     
			}
			all_data.close(); 

		}
		
		catch (IOException e) 
		{
			System.out.println("ERROR");
			e.printStackTrace();
		}
		
		// Temporary storing LEFT & RIGHT	
		double left_temp = LEFT;
		double right_temp = RIGHT;
		
		
		//Index in the arrayList of age. 
		// Example: If age is 70 - The bin is 7th hence, 70/10.
		
		int right_pos_list = (int)right_temp/10;
		int left_pos_list = (int)left_temp/10;
		
		
		// Total number of tuples to return for the given range 
		
		double total_tuples = 0.0;
		
		//LEFT and RIGHT are in the same bin then eg. both in the first bin [0-10)
		// Adding one as bins are [0,9], [10,19] ..... 
		if(right_pos_list == left_pos_list) {
			return (right_temp - left_temp+1)/10 * tuples_num.get((left_pos_list));
		}
		
		else {
		
		//Otherwise, if the LEFT and RIGHT are in different bins
		//Calculting lEFT first
		total_tuples = ((left_pos_list+1)*10 - left_temp)/10 * tuples_num.get((left_pos_list));
		
		//Calculting RIGHT
		total_tuples = total_tuples + (right_temp - right_pos_list * 10 + 1)/10 * tuples_num.get((right_pos_list));		
		
		
		// Adding any remaining bin values 
		// eg. if LEFT is in Bin0 and RIGHT is in Bin2, add Bin1
		if ( (right_pos_list - left_pos_list) > 1) {
			for (int i = left_pos_list+1; i < right_pos_list; i++ ) {
				total_tuples = total_tuples + tuples_num.get(i);
			}
			
		}
		return total_tuples; }
		
	}


	public static double estimate_eqDepth(int LEFT, int RIGHT, String histogram_path) throws NumberFormatException, IOException{
		// to get the estimated result using the equi-depth histogram
		
		//Array to store tuples values
		ArrayList<Integer> tuples_num = new ArrayList<Integer>();
		
		Integer total_rel = 0;
				
		try {
			//Reading the contents of eq_depth.dat file
			BufferedReader all_data = new BufferedReader(new FileReader(histogram_path));// + "/eq_depth.dat"));
			total_rel = Integer.parseInt(all_data.readLine());
			String row;

			// Adding the age value from each row to an array
			while ((row = all_data.readLine()) != null) {
				tuples_num.add(Integer.parseInt(row));
	     
			}
			all_data.close(); 

		}
		
		catch (IOException e) 
		{
			System.out.println("ERROR");
			e.printStackTrace();
		}
	
		
		// Temporary storing LEFT & RIGHT	
		double left_temp = LEFT;
		double right_temp = RIGHT;
		
		
		// Average rel in each bin
		double avg_rel = total_rel/8;
		
		double total_tuples = 0.0;
		
		
		// upper and lower bins of LEFT & RIGHT
		int lower_left_bin = LEFT;
		int upper_right_bin = RIGHT;
		int upper_left_bin = LEFT;
		int lower_right_bin = RIGHT;
		
		//Calculating LEFT bin first
		// If it is LEFT is smaller than 19.
		if (left_temp < tuples_num.get(0) ) {
			lower_left_bin = 0;
		}
		else {
			while(tuples_num.indexOf(lower_left_bin) == -1 || (lower_left_bin == upper_left_bin)) {
				lower_left_bin--;
			}
		}
		
		if (right_temp > tuples_num.get(6) ) {
			upper_right_bin = 80;
		}
		else {
			while(tuples_num.indexOf(upper_right_bin) == -1 || (lower_right_bin == upper_right_bin)) {
				upper_right_bin++;
			}
		}
		

		
		
		//Calculating current index
		
		if (left_temp > tuples_num.get(6) ) {
			upper_left_bin = 80;
		}
		else {
			while((lower_left_bin == upper_left_bin) || tuples_num.indexOf(upper_left_bin) == -1 ) {
				upper_left_bin++;
			
			}
		}
	
		if (right_temp < tuples_num.get(0) ) {
			lower_right_bin = 0;
		}
		else {
			while(tuples_num.indexOf(lower_right_bin) == -1 || (lower_right_bin == upper_right_bin)) {
				lower_right_bin--;
				}
			}
		
//		System.out.println("upper_right" + upper_right_bin );
//		System.out.println("upper_left" + upper_left_bin );
//		System.out.println("lower_left" + lower_left_bin );
//		System.out.println("lower_right" + lower_right_bin );
		
		// If in same BIN
		if ((upper_left_bin == upper_right_bin) && (lower_right_bin == lower_left_bin)) {
			return ( right_temp - left_temp + 1 )/(upper_right_bin - lower_right_bin)* avg_rel ;
		}
		else {
		//Calculating LEFT first
		total_tuples = (upper_left_bin - left_temp )/(upper_left_bin - lower_left_bin) * avg_rel ; 
		
		//Calculating RIGHT
		total_tuples = total_tuples + (right_temp - lower_right_bin + 1)/(upper_right_bin - lower_right_bin) * avg_rel ; 
		
		// Adding any remaining bin values 
		// eg. if LEFT is in Bin0 and RIGHT is in Bin2, add Bin1
	
		if((tuples_num.indexOf(lower_right_bin) - tuples_num.indexOf(upper_left_bin) + 1 ) > 1  ) {
			total_tuples = total_tuples + (tuples_num.indexOf(lower_right_bin) - tuples_num.indexOf(upper_left_bin))* avg_rel;
		}
		
		
		return total_tuples;
		}
	}


		
	public static int get_result(int LEFT, int RIGHT, String dat_path) throws NumberFormatException, IOException{
		// to get the real result, using the actual data
		
		ArrayList<Integer> age = new ArrayList<Integer>();
		
		
	try {
		//Reading the contents of final_general.dat file
		BufferedReader all_data = new BufferedReader(new FileReader(dat_path));// + "/final_general.dat"));
		String row;
		

		// Adding the age value from each row to an array
		while ((row = all_data.readLine()) != null) {
            String[] values = row.split(" ");
            age.add(Integer.parseInt(values[1]));
     
		}
		all_data.close();
	}
	catch (IOException e) 
	{
		System.out.println("ERROR: Please put the path to final_general.dat correctly.");
		e.printStackTrace();
	}
	
	//Sort the data in acsending order 
	Collections.sort(age);

	
	// Temporary storing LEFT & RIGHT
	int left_temp = LEFT;
	int right_temp = RIGHT;

	//Assumption is that all numbers are in the data.
	//Get the last index of RIGHT's value subtract the first index of occurence of LEFT
	//NOTE: as Index start from zero, add 1
	return age.lastIndexOf(right_temp) - age.indexOf(left_temp) + 1;
	
	}

	public static void main(String args[]) throws NumberFormatException, IOException{
  		if(args.length != 5){
  			System.out.println("Usage: java getResults LEFT RIGHT EQ_WIDTH_PATH EQ_DEPTH_PATH DATA_PATH");
  			/*
			LEFT(int): the lower bound of the interval
			RIGHT(int): the upper bound of the interval
			EQ_WIDTH_PATH(String): the file path of the equal-width histogram
			EQ_DEPTH_PATH(String): the file path of the equal-depth histogram
			DATA_PATH(String): the file path of final_general.dat
  			*/
  			return;
  		}
  		System.out.println(estimate_eqWidth(Integer.parseInt(args[0]), Integer.parseInt(args[1]), args[2]));
  		System.out.println(estimate_eqDepth(Integer.parseInt(args[0]), Integer.parseInt(args[1]), args[3]));
  		System.out.println(get_result(Integer.parseInt(args[0]), Integer.parseInt(args[1]), args[4]));
  	}
}
