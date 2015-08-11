import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

// Pattern : To extract ATM information (title, address, etc) from just dial pages

public class Pattern {

	// Returns no of ATMs
	public static int extract_atm_info(Document doc, BufferedWriter bw) throws IOException
	{
		String text1 = "", text2 = "";
		int noofatms = 0;
		
		Elements info_elements = doc.select("section.jgbg");
		for(Element info_element : info_elements)
		{
			Elements title_elements = info_element.select("span.jcn");
			if(!title_elements.isEmpty())
			{
				noofatms = noofatms + 1;
				bw.write(title_elements.first().text());
				//bw.newLine();
			}

			//Elements address_elements = info_element.select("section.jrcl2");
			// In some cases, it is jrcl3
			
			Elements address_elements = info_element.select("section.jbc");
			if(!address_elements.isEmpty())
			{
				Elements specific_elements = address_elements.first().select("p");
				if(!specific_elements.isEmpty())
				{
					Elements temp_elements = specific_elements.first().select("a");
					if(!temp_elements.isEmpty())
						text2 = temp_elements.first().text();
					text1 = specific_elements.first().text();
					text1 = text1.replace(text2, "");

					bw.write('\t' + text1);
					bw.newLine();
				}
			}
		}
		return noofatms;
	}
	
	
	public static String extract_next_page_link(Document doc) throws IOException
	{
		String text1 = "", text2 = "", link = "";
		Elements next_page_elements = doc.select("div.jpag");
		if(!next_page_elements.isEmpty())
		{
			Elements last_page_link = next_page_elements.first().select("a");
			if(!last_page_link.isEmpty())
			{
				text1 = last_page_link.last().text();
				link = last_page_link.last().attr("href");
				Elements last_hyperlink = last_page_link.last().select("span");
				if(!last_hyperlink.isEmpty())
					text2 = last_hyperlink.text();
				text1 = text1.replace(text2, "");
				text1 = text1.trim();
				
				if(text1.equalsIgnoreCase("Next"))
				{
					return link;
				}
			}
		}
		return "";
	}
}
