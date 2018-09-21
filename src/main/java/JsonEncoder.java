import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.io.*;
import java.util.LinkedList;

public class JsonEncoder {
    Gson gson;
    File save;
    FileWriter writer;


    public JsonEncoder (String name)
    {
        gson = new Gson();
        save = new File(name+".dic");
    }

    public Dictionary load(String dir) throws IOException
    {
        if(!save.exists())
        {
            save.createNewFile();
        }
        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(dir)));
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
        save = new File(dictionary.name+".dic");
        writer = new FileWriter(save);
        String json = gson.toJson(dictionary);
        writer.write(json);
        writer.flush();
        writer.close();
    }
}
