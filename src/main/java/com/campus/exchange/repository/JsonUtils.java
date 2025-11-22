package com.campus.exchange.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/*this is so that only 1 instance of this is class is made and Spring manages it, can be injected where ever required
* using constructor injection. if this was not done, every file in repository dir would contain individual objects of
* JsonUtils*/
@Component
public class JsonUtils
{
    /*Used to convert json to java object and vice versa*/
    private ObjectMapper mapper = new ObjectMapper();

    /*Locks object for synchronization, only 1 can change file at a time*/
    private Object fileLock = new Object();

    /*Just like the templates in cpp, T is the placeholder for the type eg User, Claim, Item
    * Reads and returns all data from the JSON file as a List<T>*/
    public <T> List<T> readList(String filePath, TypeReference<List<T>> typeRef)
    {
        synchronized (fileLock)
        {
            try
            {
                File file = new File(filePath);

                if(!file.exists())
                {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                    writeList(filePath, new ArrayList<>());
                }

                return mapper.readValue(file, typeRef);
            }

            catch (Exception e)
            {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
    }

    public <T> void writeList(String filePath, List<T> data)
    {
        synchronized (fileLock)
        {
            try
            {
                /*first load the file into a temp file, make the changes there and then update the main file so
                * that if write fails due to any problem, the original file is safe and does not get corrupted.
                * after successful write to the temp file, it is then moved to main file*/
                Path path = Path.of(filePath);
                Path tempPath = Path.of(filePath + ".tmp");

                mapper.writerWithDefaultPrettyPrinter().writeValue(tempPath.toFile(), data);

                Files.move(tempPath, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            }

            /*It is the special exception type for the input output like file not found, no permission,
            * disk space full, etc. The methods provided by java.nio.file.Path throws objects of these types.*/
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
