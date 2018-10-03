import java.util.ArrayList;

public class Word {
    private String word;
    private String pronounciation;
    private ArrayList<String> keywords;
    private String[] definitions;
    private String[] exampleSentences;
    private String etymology;

    public int consecRight = 0;
    public int consecWrong = 0;

    public int totConsecRight = 0;
    public int dataPoints = 0;
    public double avgConsecRight = 0.0;

    public double time = -1;

    public boolean mastered = false;

    public Word(String word)
    {
        this.word = word;
        keywords = new ArrayList<String>();
    }

    public String toString()
    {
        return word;
    }

    public void setWord(String word)
    {
        this.word = word;
    }

    public String getWord()
    {
        return word;
    }

    public void setKeywords(ArrayList<String> keys)
    {
        keywords = keys;
    }

    public ArrayList<String> getKeywords()
    {
        return keywords;
    }

    public void pronounciation(String p)
    {
        pronounciation = p;
    }

    public String getPronounciation()
    {
        return pronounciation;
    }

    public void addKeyword(String key)
    {
        for(String keyword : keywords())
        {
            if(keyword.equals(key))
            {
                return;
            }
        }

        keywords.add(key);
    }

    public String[] keywords()
    {
        String[] keyword = new String[keywords.size()];

        int i = 0;
        for(String key : keywords)
        {
            keyword[i] = key;
            i++;
        }

        return keyword;
    }

    public void setDefinitions(String[] definitions)
    {
        this.definitions = definitions;
    }

    public String[] definitions()
    {
        return definitions;
    }

    public String getDefinition(int index)
    {
        return definitions[index];
    }

    public void setExampleSentences(String[] sentences)
    {
        this.exampleSentences = sentences;
    }

    public String[] getExampleSentences()
    {
        return exampleSentences;
    }

    public String getExampleSentence(int index)
    {
        return exampleSentences[index];
    }

    public void setEtymology(String etymology)
    {
        this.etymology = etymology;
    }

    public String etymology()
    {
        return etymology;
    }

}
