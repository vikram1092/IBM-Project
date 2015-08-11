// Extract list of ATMS and their info 

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Extract_ATM {

	public static String extractHTML(String site) throws IOException {

		StringBuffer html = new StringBuffer();
		try
		{
			URL url = new URL(site);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			String line;
			while ((line = br.readLine()) != null)
			{
				html.append(line);
				html.append('\n');
			}
			br.close();
			// Not giving complete html text
			/*Connection conn = Jsoup.connect(site);
			String s = conn.get().html();
			return s;*/
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		return html.toString();
	}

	public static void main(String[] args) throws Exception
	{
		//String city = "Chennai";
		String[] cities = {"Delhi", "Chennai", "Kolkata", "Bangalore", "Mumbai", "Hyderabad"};
		List<String> cityList = Arrays.asList(cities);

		for(String city : cityList)
		{
			String link = "http://www.justdial.com/" + city + "/ATM-Centres";
			int page_no = 1;
			int noofatms = 0;

			String dir = "C:\\Users\\IBM_ADMIN\\Desktop\\Edge Analytics\\codes\\ATM List\\" + city;
			File saveDir = new File(dir);
			if(!saveDir.exists())
				saveDir.mkdir();
			
			FileWriter fw = new FileWriter("C:\\Users\\IBM_ADMIN\\Desktop\\Edge Analytics\\codes\\ATM List\\" + city + "\\extracted_list.txt");
			BufferedWriter bw = new BufferedWriter(fw);

			while(true)
			{
				String html_text = Extract_ATM.extractHTML(link);

				FileWriter fw1 = new FileWriter("C:\\Users\\IBM_ADMIN\\Desktop\\Edge Analytics\\codes\\ATM List\\" + city + "\\page" + page_no + ".html");
				BufferedWriter bw1 = new BufferedWriter(fw1);
				bw1.write(html_text);
				bw1.close();
				fw1.close();

				// Parse page.html
				File just_dial_html = new File("C:\\Users\\IBM_ADMIN\\Desktop\\Edge Analytics\\codes\\ATM List\\" + city + "\\page" + page_no + ".html");
				Document doc = Jsoup.parse(just_dial_html, "UTF-8");

				noofatms = noofatms + Pattern.extract_atm_info(doc, bw);

				link = Pattern.extract_next_page_link(doc);

				if(link.equals(""))
					break;
				else
					page_no = page_no + 1;
			}
			bw.close();
			fw.close();

			System.out.println("Number of ATMS in " + city + " : " + noofatms);
		}
	}

}
