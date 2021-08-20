package eu.treppi.codingschule.ticekty.helper;

import net.dv8tion.jda.api.entities.Guild;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

public class GuildSettings {

    public static JSONArray getTickets(Guild g) {
        JSONObject settings = getGuildSettings(g);
        return settings.has("tickets") ? settings.getJSONArray("tickets") : new JSONArray();
    }

    public static JSONObject getGuildSettings(Guild g) {
        return FileHelper.getFileAsObject(new File("data/guilds/"+g.getId()+"/settings.json"));
    }

    public static void saveGuildSettings(Guild g, JSONObject guildData) {
        FileHelper.writeToFile(new File("data/guilds/"+g.getId()+"/settings.json"), guildData.toString());
    }

    public static JSONObject getTicketSettingsById(Guild g, int ticketid) {
        for(int i = 0; i < getTickets(g).length(); i++) {
            JSONObject obj = getTickets(g).getJSONObject(i);

            if(obj.has("ticketid"))
                if(obj.getInt("ticketid") == ticketid)
                    return obj;
        }
        return null;
    }

    public static JSONObject getTicketSettingsByChannelId(Guild g, String channelid) {
        for(int i = 0; i < getTickets(g).length(); i++) {
            JSONObject obj = getTickets(g).getJSONObject(i);

            if(obj.has("channelid"))
                if(obj.getString("channelid").equals(channelid))
                    return obj;
        }
        return null;
    }
}
