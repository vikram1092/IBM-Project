import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class JustDialResults {
	
	String[] addParam = new String[3];
	
	public JustDialResults() {}
	
	public boolean startClass(String s)
	{
		if(s.contains("jgbg")) return true;
		return false;
	}

	public boolean endClass(String s)
	{
		if(s.contains("class=\"etl\"")) return true;
		return false;
	}
	
	public String getTitle(String s)
	{
		int ind = s.indexOf('>')+1;
		if(s.contains("</a>"))return s.substring(ind, s.length()-4);
		return "MISMATCH";
	}
	
	public String getAddress(String s)
	{
		//int ind = s.substring(0, s.length()-7).lastIndexOf('>') + 1;
		if(s.contains("</a><br>")) return s.substring(7, s.indexOf('|')-16); //<-------------------------------->PROBLEM
		return "MISMATCH";
	}
	
	public String[] splitAddress(String s)
	{
		
		//Find pin code
		if(!s.isEmpty() && addParam[0] == "?" && (s.lastIndexOf(' ')+1) > 0)
		{
			if(s.length() > 9 && (s.substring(s.lastIndexOf(' '))+1).compareTo("999999") <= 0)
			{
				addParam[0] = s.substring(s.lastIndexOf(' ')+1);
				return splitAddress(s.substring(0, s.length()-9));
			}
			else
			{
				addParam[0] = "-";
				return splitAddress(s.substring(0, s.length()-1));
				
			}
		}
		//Find city
		else if(!s.isEmpty() && addParam[1].equals("?"))
		{
			String element;
			String checker;
			boolean match = false;
			
			//Initialize element - part of string being considered
			//
			//Basis: Part of string is city separated by a comma, whole string is a city with no comma
			//or string doesn't have a city.
			if(s.contains(","))  {  element = s.substring(s.lastIndexOf(',')+2); }
			else                 {  element = s; }
			
			try
			{
				BufferedReader reader = new BufferedReader(new FileReader("C:/Users/IBM_ADMIN/Documents/SystemText-0.3.2/system-t-userdata/dictionaries/cities.dict"));
				while((checker = reader.readLine()) != null)
				{
					if(element.compareTo(checker) == 0){
						addParam[1] = element;
						match = true;
					}
				}
				reader.close();
				
				if(addParam[1].equals("?"))
				{
					addParam[1] = "-";
				}
				
				//Situation handling for cases where the string contains the city, is entirely the city or has no city.
				if(!s.equals(element))
					if(!match)
						return splitAddress(s.substring(0,s.lastIndexOf(",")));
					else
						return splitAddress(s.substring(0, s.lastIndexOf(element, s.length())-2));
				else if(s.equals(element))
					if(!match)
						return splitAddress(s);
				
			} catch (IOException e){	e.printStackTrace(); }
		}
		//Find location
		else if (!s.isEmpty() && addParam[2].equals("?"))
		{
			//Change the commas in the string so as to distinguish the three Arraylists when they are displayed/stored
			addParam[2] = s.replace(',', '|');
		}
		
		if(addParam[2]=="?") addParam[2] = "-";
		return addParam;
	}

	public String changePage(String s, int number)
	{
		if(s.contains(">" + Integer.toString(number) + "<")){
			String cut = s.substring(0, s.indexOf(">" + Integer.toString(number) + "<"));
			return cut.substring(cut.lastIndexOf("http"), cut.length()-1);
		}
		return "INVALID";
	}

	/**
public static void main(String[] args) throws Exception {  

	//justdial.com API usage
	
	//Initialized variables
	String justdial = "www.justdial.com";  
    String searchCity = "Chennai";
    String searchThing = "ATM-Centres";
    String charset = "UTF-8";
    String input;
    
    ArrayList<String> titles = new ArrayList<String>();
    ArrayList<String> fullAddress = new ArrayList<String>();
    ArrayList<String> location = new ArrayList<String>();
    ArrayList<String> city = new ArrayList<String>();
    ArrayList<String> pin = new ArrayList<String>();
    
    boolean temp = false;
    boolean pageTemp = false;
    int count = 0;
    int pageCount = 1;
    boolean t = false;     //counter between searching for title and address
    JustDialResults results = new JustDialResults();
    
    //HTML page reading
    URL url = new URL("http", justdial, "/" + searchCity + "/" + searchThing.toLowerCase() + "/");
    URLConnection urlc = url.openConnection();
    urlc.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
    BufferedReader reader = new BufferedReader(new InputStreamReader(urlc.getInputStream(), charset));
    FileWriter filer = new FileWriter("C:/Users/IBM_ADMIN/Desktop/ATMs/" + searchCity + "ATMs.csv");
    BufferedWriter writer = new BufferedWriter(filer);
    
    System.out.println(url.toString());
    
    while((input = reader.readLine()) != null)
    {
    	
        //--------------Arraylist Building------------------
    	    	
    	//If correct element, extract title first
    	if(temp == true && t == false) {
    		String thing = results.getTitle(input);
    		if(!(thing.equals("MISMATCH"))){
    			titles.add(thing);
    			t = true;
    		}
    	}
    	
    	//Then, extract address
    	if(temp == true && t == true)
    	{
    		String thing = results.getAddress(input);
    		if(!(thing.equals("MISMATCH"))){
    			fullAddress.add(thing);

    			//Initialize parameters to be added and send address string for splitting
    			results.addParam[0] = results.addParam[1] = results.addParam[2] = "?";
    	    	String[] placebo = results.splitAddress(thing);
    	    	
    	    	//Assigning Split Address
    	    	location.add(placebo[2]);
    	    	city.add(placebo[1]);
    	    	pin.add(placebo[0]);

    	    	writer.write(titles.get(count) + ", " + location.get(count) + ", " + city.get(count) + ", " + pin.get(count) + System.getProperty("line.separator"));
    	    	count++;
    		}
    	}
    	
    	//Switch off temp variable when reader exits HTML class
    	if(results.endClass(input)){ temp = false; t = false; }
    	

    	//Check if right HTML class element - last thing to do on the while loop for technicality reasons. 
    	//Reason: The first "reslt" block has a </div> on the line so java confirms the beginning of the 
    	//class as well as the end of the class on the same line. Glitch is controlled in this method.
    	if (results.startClass(input)) temp = true;
    	

        //--------------Page Advancing------------------

    	
    	if (pageTemp == false && input.contains("class=\"jpag\""))
    		pageTemp = true;
    	
    	if(pageTemp)
    	{
    		String nextPage = results.changePage(input, pageCount+1);
    		if(!nextPage.equals("INVALID"))
    		{
    			url = new URL(nextPage);
    			urlc = url.openConnection();
    		    urlc.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
    		    reader = new BufferedReader(new InputStreamReader(urlc.getInputStream(), charset));
    			pageTemp = false;
    			pageCount++;
    			System.out.println(nextPage);
    	        System.out.print("Titles size: " + titles.size());
    		}
    	}
    }

    reader.close();
    writer.close();
    
	System.out.println();
    
    //print out Arraylist size and elements
    System.out.println("Titles size: " + titles.size());
    System.out.println("Address size: " + fullAddress.size());
    System.out.println("Location size: " + location.size());
    System.out.println("City size: " + city.size());
    System.out.println("Pin size: " + pin.size());

	System.out.println();
	
    	while(count < fullAddress.size()){
    		
    		//Aesthetics
    		int spacing = 50 - titles.get(count).length();
    		
    		System.out.print(titles.get(count));
    		for(int i = 0; i<spacing; i++)
    			System.out.print("-");
    		System.out.println(location.get(count) + ", " + city.get(count) + ", " + pin.get(count));
    		count++;
    	}
    	
    //Write onto a file
    count = 0;
    while(count < location.size()){
    	count++;
    }
    
}**/
}
