package eu.treppi.codingschule.ticekty.core;

import eu.treppi.codingschule.ticekty.helper.FileHelper;
import net.dv8tion.jda.api.JDABuilder;
import org.json.JSONObject;

import java.io.File;

public class Tickety {
    private static final JSONObject CONFIG = FileHelper.getFileAsObject(new File("config.json"));
    private static final String TOKEN = CONFIG.getString("token");
    public static final String INVITE = CONFIG.getString("invite");

    public static void main(String[] args) {
        try {
            JDABuilder.createDefault(TOKEN).build();
        }catch (Exception e) {
            //test
        }
    }
}
