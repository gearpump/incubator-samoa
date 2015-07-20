package org.apache.samoa.topology.impl;

import com.github.javacliparser.ClassOption;
import org.apache.samoa.tasks.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Created by keweisun on 7/13/2015.
 */
public class GearpumpSamoaUtils {

    public static String piConf = "processingItem";
    public static String entrancePiConf = "entranceProcessingItem";

    private static final Logger logger = LoggerFactory.getLogger(GearpumpSamoaUtils.class);

    public static byte[] objectToBytes(Object object) {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            oos.flush();
            byte[] bytes = baos.toByteArray();
            baos.close();
            oos.close();
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object bytesToObject(byte[] bytes) {
        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;
        try {
            bais = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bais);
            Object object = ois.readObject();
            bais.close();
            ois.close();
            return object;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static GearpumpTopology argsToTopology(String[] args) {
        StringBuilder cliString = new StringBuilder();
        for (String arg : args) {
            cliString.append(" ").append(arg);
        }
        logger.info("Command line string = {}", cliString.toString());

        Task task = getTask(cliString.toString());

        // TODO: remove setFactory method with DynamicBinding
        task.setFactory(new GearpumpComponentFactory());
        task.init();

        return (GearpumpTopology) task.getTopology();
    }

    public static Task getTask(String cliString) {
        Task task = null;
        try {
            logger.debug("Providing task [{}]", cliString);
            task = ClassOption.cliStringToObject(cliString, Task.class, null);
        } catch (Exception e) {
            logger.warn("Fail in initializing the task!");
            e.printStackTrace();
        }
        return task;
    }
}
