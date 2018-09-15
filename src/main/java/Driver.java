import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Driver {

    public static void main(String[] args)
    {
        try {
            File file = new File("data/Words.txt");
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;

            int count = 0;

            while((line = bufferedReader.readLine())!=null)
            {
                new DataScraper(line).run();
            }
        }catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}
