import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.io.*;
import java.util.LinkedList;

public class JsonEncoder {
    Gson gson;
    File save = new File("save.json");
    FileWriter writer;


    public JsonEncoder ()
    {
        gson = new Gson();
    }

    public Dictionary load() throws IOException
    {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(save));
        String line;
        StringBuilder builder = new StringBuilder();
        while((line = bufferedReader.readLine()) != null)
        {
            builder.append(line);
        }

        Dictionary dictionary = gson.fromJson(builder.toString(),Dictionary.class);
        return dictionary;
    }

    public void save(Dictionary dictionary) throws IOException
    {
        save.createNewFile();
        writer = new FileWriter(save);
        String json = gson.toJson(dictionary);
        writer.write(json);
        writer.flush();
        writer.close();
    }
}
