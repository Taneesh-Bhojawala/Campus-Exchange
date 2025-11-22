package com.campus.exchange.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class JsonUtils
{
    /*Used to convert json to java object and vice versa*/
    private final ObjectMapper mapper = new ObjectMapper();

    /*Locks object for synchronization, only 1 can change file at a time*/
    private final Object filelock = new Object();

    /*Just like the templates in cpp, T is the place holder for the type*/
    public <T> List<T> readList(String filePath, TypeReference<List<T>> typeRef)
    {
        synchronized (filePath)
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
        synchronized (filePath)
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

            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
