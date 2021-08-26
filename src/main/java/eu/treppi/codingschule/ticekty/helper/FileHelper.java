package eu.treppi.codingschule.ticekty.helper;

import net.dv8tion.jda.api.entities.Guild;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class FileHelper {
    public static String readFile(File f) {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(f.getAbsolutePath()), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }
        catch (IOException ignored) { }
        return contentBuilder.toString();
    }

    public static JSONObject getFileAsObject(File f) {
        String content = readFile(f).equals("") ? "{}" : readFile(f);
        return new JSONObject(content);
    }

    public static void writetoFile(String path, String newcontent) {
        //create file if it doesnt exist (wtf)
        File file = new File(path);
        if(!file.exists()) {
            try {
                String[] x = path.split("\\.");
                String[] y = x[0].split("/");

                StringBuilder dirpath = new StringBuilder();
                for(int i = 0; i < y.length - 1; i++) {
                    dirpath.append(y[i]);
                }

                new File(dirpath.toString()).mkdir();
                file.createNewFile();
            }catch (Exception ignored) {}
        }


        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            writer.write(newcontent);
            writer.close();

        }catch (FileNotFoundException ex1) {
            try {
                new File(new File(path).getParent()).mkdirs();
                new File(path).createNewFile();
                writeToFile(new File(path), newcontent);

            }catch (Exception e1) {}
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void writeToFile(File f, String newcontent) {
        String absolutepath = f.getAbsolutePath();
        writetoFile(absolutepath, newcontent);
    }
}
