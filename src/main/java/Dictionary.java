public class Dictionary {
    DictionaryElement start;
    int numWords;

    public Dictionary()
    {
        start = new DictionaryElement('\0');
        numWords = 0;
    }

    public void addWord(Word word)
    {
        char[] words = word.toString().toCharArray();

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
                current.links[index] = new DictionaryElement(words[i]);
                current = current.links[index];
            }
        }

        current.words.add(word);
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
