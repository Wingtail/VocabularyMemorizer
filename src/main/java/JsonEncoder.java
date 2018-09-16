import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

public class JsonEncoder {
    Gson gson;
    File save = new File("save.json");
    FileWriter writer;


    public JsonEncoder ()
    {
        gson = new Gson();
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
