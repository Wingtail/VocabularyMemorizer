import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;

public class JsonEncoder {
    transient Gson gson;
    File save;
    FileWriter writer;


    public JsonEncoder (String name)
    {
        gson = new Gson();
        save = new File(name+".dic");
    }

    public Dictionary load(String dir, GUI gui) throws IOException
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

        Dictionary dictionary = new Dictionary("", gui);


        JsonReader reader = new JsonReader(new StringReader(builder.toString()));
        reader.setLenient(true);


        dictionary.start = new Gson().fromJson(reader, new TypeToken<DictionaryElement>(){}.getType());
        dictionary.name = new Gson().fromJson(reader, new TypeToken<String>(){}.getType());
        dictionary.totalWords = new Gson().fromJson(reader, new TypeToken<ArrayList<Word>>(){}.getType());
        dictionary.numWords = new Gson().fromJson(reader, new TypeToken<Integer>(){}.getType());


        return dictionary;
    }

    public void save(Dictionary dictionary, String dir) throws IOException
    {
        save = new File(dir);
        writer = new FileWriter(save);
        StringBuilder builder = new StringBuilder();
        builder.append(gson.toJson(dictionary.start));
        builder.append(gson.toJson(dictionary.name));
        builder.append(gson.toJson(dictionary.totalWords));
        builder.append(gson.toJson(dictionary.numWords));
        writer.write(builder.toString());
        writer.flush();
        writer.close();
    }
}
