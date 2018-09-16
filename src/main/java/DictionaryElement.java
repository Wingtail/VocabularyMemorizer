import java.util.ArrayList;

public class DictionaryElement {
    ArrayList<Word> words;
    char letter;
    DictionaryElement[] links;

    public DictionaryElement(char letter) // A~Z and a~z
    {
        words = new ArrayList<Word>();
        links = new DictionaryElement[52];
        this.letter = letter;
    }

    public Word[] getWords()
    {
        Word[] words = new Word[this.words.size()];
        for(int i = 0;i<words.length;i++)
        {
            words[i] = this.words.get(i);
        }
        return words;
    }

    public void makeMaster()
    {
        for(short i=0;i<52;i++)
        {
            char l = (char)(i+65);
            if(i>=26)
            {
                l += 6;
            }
            links[i] = new DictionaryElement(l);
        }
    }
}
