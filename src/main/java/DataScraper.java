

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class DataScraper {



    public static void main(String[] args)
    {

        String word = "conflagration";

        try{
            Document doc = Jsoup.connect("https://www.dictionary.com/browse/"+word+"?s=t").get();
            
            System.out.println(doc);
        }catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}
