package eu.treppi.codingschule.ticekty.core;

import eu.treppi.codingschule.ticekty.core.commands.AutosetupCommand;
import eu.treppi.codingschule.ticekty.core.commands.HelpCommand;
import eu.treppi.codingschule.ticekty.core.commands.InviteCommand;
import eu.treppi.codingschule.ticekty.core.commands.SettingsCommand;
import eu.treppi.codingschule.ticekty.core.listeners.TicketChannelDelete;
import eu.treppi.codingschule.ticekty.core.listeners.TicketClose;
import eu.treppi.codingschule.ticekty.core.listeners.TicketCreation;
import eu.treppi.codingschule.ticekty.helper.FileHelper;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import org.json.JSONObject;

import java.io.File;

public class Tickety {
    private static final JSONObject CONFIG = FileHelper.getFileAsObject(new File("config.json"));
    private static final String TOKEN = CONFIG.getString("token");
    public static final String INVITE = CONFIG.getString("invite");
    public static final String imageUrl = "https://i.ahegao.agency/FNHWDRm3D6.png?key=wmxV2Bt8fpGk1v";

    public static final boolean ignoreBots = true;
    private static JDA api;

    public static void main(String[] args) {
        try {

            JDABuilder builder =  JDABuilder.createDefault(TOKEN);
            builder.addEventListeners(new AutosetupCommand());
            builder.addEventListeners(new TicketCreation());
            builder.addEventListeners(new TicketClose());
            builder.addEventListeners(new TicketChannelDelete());
            builder.addEventListeners(new InviteCommand());
            builder.addEventListeners(new HelpCommand());
            builder.addEventListeners(new SettingsCommand());

            builder.setActivity(Activity.listening("t!help / @me help"));

            api = builder.build();

        }catch (Exception e) {
            //test
        }
    }

    public static boolean isPermitted(Member member) {
        return member.hasPermission(Permission.ADMINISTRATOR);
    }
}
