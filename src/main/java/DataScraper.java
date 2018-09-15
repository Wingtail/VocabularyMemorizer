

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataScraper extends Thread{

    String[] dictionary;
    String[] thesaurusKeywords;
    String[] etymology;

    String actualWord;
    String pronounciation = "";

    DataScraper(String word)
    {
        actualWord = word;
    }

    public void run()
    {
        dictionary(actualWord);
        thesaurus(actualWord);
        etymology(actualWord);
    }

    public void thesaurus(String word)
    {

    }

    public void etymology(String word)
    {

    }

    public boolean containsWithinSentence()
    {
        return true;
    }


    public void dictionary(String word)
    {
        try{
            Document doc = Jsoup.connect("https://www.dictionary.com/browse/"+word+"?s=t").get();
            actualWord = word;
            if(doc.select("span.css-1m7ldyv.e6aw9qa0").clone().size() > 0)
            {
                String switchWord = doc.selectFirst("h2.css-6gthty.e19m0k9k0").select("a").first().attr("abs:href");
                doc = Jsoup.connect(switchWord).get();
            }
            actualWord = doc.select("section.css-0.e1rg2mtf0").select("h1.css-1qbmdpe.e1rg2mtf5").text();

            if(doc.select("span.css-1khtv86.e1rg2mtf2").first() != null) {
                pronounciation = doc.select("span.css-1khtv86.e1rg2mtf2").first().text();
            }

            Elements masthead = doc.select("section.css-1748arg.e1wu7xq20").clone();
            List<String> strings = masthead.select("li.css-2oywg7.e10vl5dg5").eachText();
            dictionary = new String[strings.size()];
            for(int i = 0;i<strings.size();i++)
            {
                dictionary[i] = strings.get(i);
            }
            System.out.println(actualWord);
            System.out.println(pronounciation);
        }catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}
