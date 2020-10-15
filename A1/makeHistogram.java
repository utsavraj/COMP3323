
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList; // import the ArrayList class
import java.util.Collections;

public class makeHistogram{
	public static void create_histogram(String data_path, String eq_width_path, String eq_depth_path) throws NumberFormatException, IOException{
		// To create an equi-width histogram and an equi-depth histogram, and save them to files.
		// Each output file should contain exactly eight lines, and each line should contain a single integer.
		
		
		//Array to store age values
		ArrayList<Integer> age = new ArrayList<Integer>();
		
		
	try {
		//Reading the contents of final_general.dat file
		BufferedReader all_data = new BufferedReader(new FileReader(data_path));// + "/final_general.dat"));
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
	}
		
		
		//equi-width histogram
		
	int output_column_one = 0;
	int output_column_two = 0;
	int output_column_three = 0;
	int output_column_four = 0;
	int output_column_five = 0;
	int output_column_six= 0;
	int output_column_seven = 0;
	int output_column_eight = 0;



		//Equal bin
		for (int i = 0; i < age.size();i++) 
	      { 	
			if (age.get(i) >= 0 && age.get(i) < 10 ) {
				output_column_one++;
			}
			else if (age.get(i) >= 10 && age.get(i) < 20 ) {
				output_column_two++;
			}
			else if (age.get(i) >= 20 && age.get(i) < 30 ) {
				output_column_three++;
			}
			else if (age.get(i) >= 30 && age.get(i) < 40 ) {
				output_column_four++;
			}
			else if (age.get(i) >= 40 && age.get(i) < 50 ) {
				output_column_five++;
			}
			else if (age.get(i) >= 50 && age.get(i) < 60 ) {
				output_column_six++;
			}
			else if (age.get(i) >= 60 && age.get(i) < 70 ) {
				output_column_seven++;
			}
			else if (age.get(i) >= 70 && age.get(i) < 80 ) {
				output_column_eight++;
			}
			
	          	
	      }   
		
		
		try {
		FileWriter writer = new FileWriter(eq_width_path);// + "/eq_width.dat");

			
		BufferedWriter buffer = new BufferedWriter(writer);  
		buffer.write(new Integer(output_column_one).toString());
		buffer.newLine();
		buffer.write(new Integer(output_column_two).toString());
		buffer.newLine();
		buffer.write(new Integer(output_column_three).toString());
		buffer.newLine();
		buffer.write(new Integer(output_column_four).toString());
		buffer.newLine();
		buffer.write(new Integer(output_column_five).toString());
		buffer.newLine();
		buffer.write(new Integer(output_column_six).toString());
		buffer.newLine();
		buffer.write(new Integer(output_column_seven).toString());
		buffer.newLine();
		buffer.write(new Integer(output_column_eight).toString());
		buffer.close();  
		
		
		System.out.println("Success for equi-width");  
			    
		}  
		 catch (IOException e) {
		      System.out.println("Error");
		      e.printStackTrace();
		    } 
						
	
		
		//equi-depth histogram
		
		// Sort the age Arraylist in ascending order. 
		Collections.sort(age);

		int avg_bin_size = age.size()/8 + 1;

		try {

		FileWriter writer = new FileWriter(eq_depth_path);// + "/eq_depth.dat");

		BufferedWriter buffer = new BufferedWriter(writer); 

		buffer.write(new Integer(age.size()).toString());
		buffer.newLine();

		for (int bin_position = avg_bin_size; bin_position < age.size(); bin_position = bin_position + avg_bin_size)
		{
			buffer.write(new Integer(age.get(bin_position)).toString());
			buffer.newLine();
		}
			buffer.close(); 
			System.out.println("Success for equi-depth");  

		}  
		catch (IOException e) {
		System.out.println("Error");
		e.printStackTrace();
		} 
		

	}

	public static void main(String args[]) throws NumberFormatException, IOException{
  		if(args.length != 3){
  			System.out.println("Usage: java makeHistogram DATA_PATH EQ_WIDTH_PATH EQ_DEPTH_PATH");
  			/*
			DATA_PATH(String): the file path of final_general.dat
			EQ_WIDTH_PATH(String): the output file path of the equal-width histogram
  			EQ_DEPTH_PATH(String): the output file path of the equal-depth histogram
  			*/
  			return;
  		}
  		create_histogram(args[0], args[1], args[2]);
  	}
}