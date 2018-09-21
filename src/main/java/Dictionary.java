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
    String name;
    int numWords;
    transient JsonEncoder encoder; //prevent stack overflow error

    ArrayList<Word> totalWords;

    public Dictionary(String name)
    {
        this.name = name;
        start = new DictionaryElement('\0');
        numWords = 0;
        encoder = new JsonEncoder(name);

        totalWords = new ArrayList<Word>();
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

    public Dictionary load(String dir)
    {
        try {
            return encoder.load(dir);
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

    public ArrayList<Word> searchRelativeWords(String word)
    {
        word = word.replaceAll("\\s","");
        if(word != null) {
            char[] words = word.toCharArray();
            int i;
            DictionaryElement current = start;

            ArrayList<Word> wordList = new ArrayList<Word>();

            for (i = 0; i < words.length; i++) {
                char index = words[i];
                if (index > 90) {
                    index -= 71;
                } else {
                    index -= 65;
                }
                System.out.println(words[i]);
                if (current.links[index] != null) {
                    current = current.links[index];
                }
                if (i == words.length - 1) {
                    wordList.addAll(current.words);
                }
            }
            wordList = searchAllWords(current, wordList);
            return wordList;
        }

        return null;
    }

    public ArrayList<Word> searchAllWords(DictionaryElement element, ArrayList<Word> words)
    {
        words.addAll(element.words);
        for(DictionaryElement e : element.links)
        {
            if(e != null)
            {
                searchAllWords(e,words);
            }
        }
        return words;
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
                }
                current = current.links[index];
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
            totalWords.add(word);
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
