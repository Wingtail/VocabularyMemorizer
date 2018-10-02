import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Driver {

    public static void main(String[] args)
    {
        for(int j = 10;j < 100; j+=10) {
            ArrayList<Thread> threads = new ArrayList<Thread>();
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < j; i++) {
                DataScraper scrape = new DataScraper("apple", new Dictionary("", new GUI()), false);
                scrape.dummy = true;
                scrape.start();
                threads.add(scrape);
            }

            for (Thread thread : threads) {
                try {
                    thread.join(100000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            double timeTook = ((double) (System.currentTimeMillis() - startTime)) / 1000;
            System.out.println(timeTook);
        }
    }
}
