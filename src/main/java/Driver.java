import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Driver {

    public static void main(String[] args)
    {
        //new DataScraper("at",new Dictionary()).run();
        Dictionary dictionary = new Dictionary("Test");
        dictionary.searchWordsInFile("data/Words.txt");
        System.out.println("Complete");
        dictionary.save();
        /*DictionaryElement element = new DictionaryElement('0');
        element.makeMaster();
        System.out.println("YAY");*/
        /*try {
            File file = new File("data/Words.txt");
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;

            int count = 0;

            while((line = bufferedReader.readLine())!=null)
            {
                new DataScraper().run(line);
            }
        }catch(IOException e)
        {
            e.printStackTrace();
        }*/
    }
}
