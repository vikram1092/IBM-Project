import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;


public class Cleanser {
	
	public Cleanser(){}
	
	public void addToDatabase(String searchCity)
	{
		try {

            Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/test?" + "user=root&password=passw0rd");
			Statement state = conn.createStatement();
			
			//create non-IndusInd ATM list
			state.execute("DROP TABLE IF EXISTS atms." + searchCity + "ATMs");
			state.execute("CREATE  TABLE `atms`.`" + searchCity + "ATMs` (" +
					"`Title` VARCHAR(100) NOT NULL ," +
					" `Location` VARCHAR(1000) NULL ," +
					" `City` VARCHAR(45) NULL ," + 
					"`Zip` VARCHAR(9) NULL)");
			
			
			
			//load data into database table
			state.execute("LOAD DATA LOCAL INFILE 'C:/Users/IBM_ADMIN/Desktop/ATMs/Sorted/" + searchCity + "/" + searchCity + "ATMs.csv' " +
					               "INTO TABLE atms." + searchCity + "ATMs FIELDS TERMINATED BY ',' LINES TERMINATED BY '\n';");
			
			//create IndusInd ATM list
			state.execute("DROP TABLE IF EXISTS atms." + searchCity + "IndusIndATMs");
			state.execute("CREATE  TABLE `atms`.`" + searchCity + "IndusIndATMs` (" +
					"`Title` VARCHAR(100) NOT NULL ," +
					" `Location` VARCHAR(1000) NULL ," +
					" `City` VARCHAR(45) NULL ," + 
					"`Zip` VARCHAR(9) NULL);");

			//load data into database table
			state.executeUpdate("LOAD DATA LOCAL INFILE 'C:/Users/IBM_ADMIN/Desktop/ATMs/Sorted/" + searchCity + "/IndusInd/" + searchCity + "IndusIndATMs.csv' " +
					               "INTO TABLE atms." + searchCity + "IndusIndATMs FIELDS TERMINATED BY ',' LINES TERMINATED BY '\n';");

			state.close();
			conn.close();
			
		} catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {  e.printStackTrace();}
	}
	
	public void cleanse(String searchCity)
	{
	    String atm;
		String bank;
		boolean counter = false;
	    
		try {
			
		    BufferedReader reader = new BufferedReader(new FileReader("C:/Users/IBM_ADMIN/Desktop/ATMs/" + searchCity + "ATMs.csv"));	
		    BufferedWriter writer = new BufferedWriter(new FileWriter("C:/Users/IBM_ADMIN/Desktop/ATMs/Sorted/" + searchCity + "/" + searchCity + "ATMs.csv"));
		    BufferedWriter indWriter = new BufferedWriter(new FileWriter("C:/Users/IBM_ADMIN/Desktop/ATMs/Sorted/" + searchCity + "/IndusInd/" + searchCity + "IndusIndATMs.csv"));
		    
		    while((atm = reader.readLine()) != null)
		    {	
			    BufferedReader dicter = new BufferedReader(new FileReader("C:/Users/IBM_ADMIN/workspace/Data_Mine/bin/banks.dict"));
		    	while((bank = dicter.readLine()) != null && !counter)
		    	{
		    		//write into IndusInd ATM csv file
					if(atm.toLowerCase().contains("IndusInd Bank".toLowerCase()))
					{
						indWriter.write("IndusInd Bank" + atm.substring(atm.indexOf(',')) + System.getProperty("line.separator"));
						counter = true;
					}
					//write into general ATM csv file
					else if(atm.substring(0, atm.indexOf(',')).toLowerCase().contains(bank.toLowerCase())){
		    			String s = bank + "," + atm.substring(atm.indexOf(',')+2, atm.length());
		    			writer.write(s + System.getProperty("line.separator"));
		    			counter = true;
		    		}
		    	}
		    	
		    	dicter.close();
		    	
		    	//print out unmatched entries
		    	if(!counter) 
		    	{
		    		System.out.println(atm);
		    		writer.write(atm + System.getProperty("line.separator"));
		    	}
		    	
		    	counter = false;
		    }
		    
			reader.close();
			writer.close();
			indWriter.close();
			
			addToDatabase(searchCity);
			
		} catch (IOException e) {e.printStackTrace(); }
	}
	
}
