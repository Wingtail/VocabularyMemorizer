import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Dictionary {
    DictionaryElement start;
    int numWords;
    transient JsonEncoder encoder;

    public Dictionary()
    {
        start = new DictionaryElement('\0');
        numWords = 0;
        encoder = new JsonEncoder();
    }

    public void save()
    {
        try {
            encoder.save(this);
        }catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public Dictionary load()
    {
        try {
            return encoder.load();
        }catch(IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public void addSearch(String word)
    {
        if(searchWord(word) != null)
        {
            DataScraper scraper = new DataScraper(word, this, false);
            scraper.start();
            if(!scraper.isAlive())
            {
                addWord(scraper.w);
            }
        }
    }

    public void searchWordsInFile(String directory)
    {
        try {
            File file = new File(directory);
            if(file.exists()) {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String line;

                int count = 0;
                        while ((line = bufferedReader.readLine()) != null) {
                            Thread t = new DataScraper(line, this, true);
                            t.start();
                            DataScraper.threads.add(t);
                        }
                    for(Thread thread:DataScraper.threads)
                    {
                        try {
                            thread.join(200000);
                        }catch(InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }

                    DataScraper.threads.clear();

            }
            save();
        }catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public void addWord(Word word)
    {
        char[] words = word.toString().toCharArray();

        short i;
        DictionaryElement current = start;

        for(i=0;i<words.length;i++)
        {
            int index = words[i];

            if(index >= 65) {
                if (index > 90) {
                    index -= 71;
                } else {
                    index -= 65;
                }

                if (current.links[index] == null) {
                    current.links[index] = new DictionaryElement(words[i]);
                    current = current.links[index];
                }
            }
        }

        boolean addWord = true;
        for(i =0;i<current.words.size();i++)
        {
            if(current.words.get(i).getWord().equals(word.getWord()))
            {
                addWord = false;
                break;
            }
        }
        if(addWord) {
            numWords++;
            current.words.add(word);
        }
    }

    public Word[] searchWord(String word)
    {
        char[] words = word.toCharArray();
        short i;
        DictionaryElement current = start;

        for(i=0;i<words.length;i++)
        {
            char index = words[i];
            if(index > 90)
            {
                index -= 71;
            }else{
                index -= 65;
            }

            if(current.links[index] == null)
            {
                return null;
            }else
            {
                current = current.links[index];
            }

            if(i == words.length-1)
            {
                return current.getWords();
            }
        }

        return null;
    }
}
