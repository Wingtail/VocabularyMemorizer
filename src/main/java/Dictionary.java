import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public class Dictionary {
    DictionaryElement start;
    String name;
    int numWords;
    transient JsonEncoder encoder; //prevent stack overflow error

    ArrayList<Word> totalWords;

    int threadLimit = 300;

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

    public void searchWordsInFile(String directory)
    {
        List<Thread> tempList = new ArrayList<>();
        try {
            File file = new File(directory);
            if(file.exists()) {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String line;

                int count = 0;
                boolean noRead = false;
                while (!noRead) {
                    for (count = 0; count < threadLimit; count++) {
                        if((line = bufferedReader.readLine()) != null) {
                            Thread thread = new DataScraper(line, this, true);
                            addWord(((DataScraper)thread).w);
                            thread.start();
                            tempList.add(thread);
                        }else{
                            noRead = true;
                            break;
                        }
                    }

                    for (Thread t : tempList) {
                        t.join(100000);
                    }

                    tempList.clear();

                    System.out.println("Mark");
                }



                        /*for(Future<Word> future : wordlist)
                        {
                            try {
                                Word word = future.get();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                        }*/
            }
            save();
        }catch(IOException e)
        {
            e.printStackTrace();
        } catch (Exception e) {
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
            Word w = current.words.get(i);
            if(w.getWord().equals(word.getWord()))
            {
                addWord = false;

                w.setEtymology(word.etymology());
                w.setDefinitions(word.definitions());
                w.setKeywords(word.getKeywords());

                break;
            }
        }
        if(addWord) {
            totalWords.add(word);
            numWords++;
            current.words.add(word);
        }
    }

    public Word[] searchWord(String word) {
        char[] words = word.toCharArray();
        short i;
        DictionaryElement current = start;

        List<Callable<Word>> wordList = new ArrayList<>();

        for (i = 0; i < words.length; i++) {
            char index = words[i];
            if (index > 90) {
                index -= 71;
            } else {
                index -= 65;
            }

            if (current.links[index] == null) {
                break;
            } else {
                current = current.links[index];
            }

            if (i == words.length - 1) {
                return current.getWords();
            }
        }
        Thread thread = new DataScraper(word, this, true);
        thread.start();
        try {
            thread.join(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Word[] w = new Word[1];
        w[0] = ((DataScraper)thread).w;


        return w;
    }
}
