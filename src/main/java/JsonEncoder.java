import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

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

        Dictionary dictionary = new Dictionary("");


        JsonObject jobj = new Gson().fromJson(builder.toString(), JsonObject.class);
        dictionary.name = jobj.get("name").toString();
        dictionary.start = new Gson().fromJson(builder.toString(), new TypeToken<DictionaryElement>(){}.getType());
        dictionary.totalWords = new Gson().fromJson(builder.toString(), new TypeToken<ArrayList<Word>>(){}.getType());

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
        writer.write(builder.toString());
        writer.flush();
        writer.close();
    }
}
