

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataScraper extends Thread{





    public void run(String word)
    {
        Word w = new Word(word);
        dictionary(w);
        thesaurus(w);
        etymology(w);
    }

    public void thesaurus(Word word) {
        try {
            Document doc = Jsoup.connect("https://www.thesaurus.com/browse/" + word.toString()).userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                    .referrer("http://www.google.com").timeout(1000*5).ignoreHttpErrors(true).get();
            if(!doc.toString().contains("no thesaurus results"))
            {
                Elements keywords = doc.select("ul.css-i32syg.e9i53te2").clone();
                if(keywords.size() > 0)
                {
                    for(Element element : keywords) {
                        keywords = element.select("strong").clone();
                    }
                    for(Element element : keywords)
                    {
                        String[] keys = element.text().split(";\\s|,\\s");
                        for(String string : keys)
                        {
                            word.addKeyword(string);
                        }
                    }
                }
            }else{
                System.out.println("Error! No Keywords available!");
            }

        }catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public void etymology(Word word)
    {
        try {
            Document doc = Jsoup.connect("https://www.etymonline.com/search?q=" + word.toString()).get();
            Elements words = doc.select("a.word--C9UPa.word_thumbnail--2DBNk");
            for(Element element : words)
            {
                String str = element.select("p.notranslate.word__name--TTbAA.word_thumbnail__name--1khEg").text().split("\\s")[0];
                if(str.equals(word.toString()))
                {
                    doc = Jsoup.connect(element.attr("abs:href")).get();
                    break;
                }
            }
            String etymology = doc.select("section.word__defination--2q7ZH").text();
            word.setEtymology(etymology);
        }catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public boolean containsWithinSentence()
    {
        return true;
    }


    public void dictionary(Word word)
    {
        String[] dictionary;
        String[] sentences;

        String actualWord;
        String pronounciation = "";

        try{
            Document doc = Jsoup.connect("https://www.dictionary.com/browse/"+word.toString()+"?s=t").get();
            actualWord = word.toString();
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
            sentences = new String[strings.size()];
            for(int i = 0;i<strings.size();i++)
            {
                String string = strings.get(i);
                if(string.contains(":")) {
                    dictionary[i] = string.substring(0, string.indexOf(":"));
                    sentences[i] = string.substring(string.indexOf(":") + 1);
                }else{
                    dictionary[i] = string;
                }
            }

            word.setWord(actualWord);
            word.pronounciation(pronounciation);
            word.setDefinitions(dictionary);
            word.setExampleSentences(sentences);

            System.out.println(actualWord);
            System.out.println(pronounciation);
        }catch(IOException e)
        {
            e.printStackTrace();
        }
    }

}
