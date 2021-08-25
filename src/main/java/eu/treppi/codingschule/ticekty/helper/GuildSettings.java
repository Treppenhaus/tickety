package eu.treppi.codingschule.ticekty.helper;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
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

    public static int runningNumber(Guild g) {
        JSONObject settings = getGuildSettings(g);
        int r = settings.has("running") ? settings.getInt("running") + 1 : 0;
        settings.put("running", r);
        GuildSettings.saveGuildSettings(g, settings);
        return r;
    }

    public static int getMaxTicketsPerUser(Guild guild) {
        JSONObject settings = getGuildSettings(guild);
        return settings.has("maxperuser") ? settings.getInt("maxperuser") : 2;
    }

    public static void removeTicketFromDataByChannelid(Guild guild, String channelid) {
        JSONObject settings = getTicketSettingsByChannelId(guild, channelid);

        if(settings != null) {
            if(settings.has("ticketid")) {
                removeTicketFromDataByTicketid(guild, settings.getInt("ticketid"));
            }
        }
    }

    public static void removeTicketFromDataByTicketid(Guild guild, int ticketid) {
        JSONObject guildSettings = GuildSettings.getGuildSettings(guild);
        JSONArray tickets = GuildSettings.getTickets(guild);

        int rem = -1;

        for(int i = 0; i < tickets.length(); i++) {
            JSONObject ticket = tickets.getJSONObject(i);
            if(ticket.has("ticketid")) {
                if(ticket.getInt("ticketid") == ticketid) {
                    rem = i;
                }
            }
        }

        if(rem >= 0) tickets.remove(rem);

        guildSettings.put("tickets", tickets);
        GuildSettings.saveGuildSettings(guild, guildSettings);
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

    public static JSONArray getTicketSettingsByUser(Member member) {
        Guild g = member.getGuild();
        JSONArray tickets = new JSONArray();

        for(int i = 0; i < getTickets(g).length(); i++) {
            JSONObject obj = getTickets(g).getJSONObject(i);

            if(obj.has("userid"))
                if(obj.getString("userid").equals(member.getUser().getId()))
                    tickets.put(obj);
        }
        return tickets;
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
