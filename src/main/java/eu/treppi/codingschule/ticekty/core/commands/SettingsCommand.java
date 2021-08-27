package eu.treppi.codingschule.ticekty.core.commands;

import eu.treppi.codingschule.ticekty.core.Embeds;
import eu.treppi.codingschule.ticekty.core.Tickety;
import eu.treppi.codingschule.ticekty.helper.GuildSettings;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.JSONObject;

public class SettingsCommand extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if(Tickety.ignoreBots && e.getAuthor().isBot()) return;
        String content = e.getMessage().getContentRaw();
        String[] args = e.getMessage().getContentRaw().split(" ");

        if(content.startsWith(Tickety.prefix+"settings")) {

            if(args.length == 1) {
                showSettings(e.getTextChannel(), Tickety.prefix);
            }
            else if(args.length >= 2) {
                switch (args[1].toLowerCase()) {
                    case "maxtickets": setMaxTickets(e, args, Tickety.prefix);
                    case "modrole": setModRole(e, args, Tickety.prefix);
                }
            }
        }
    }

    private static void setModRole(MessageReceivedEvent e, String[] args, String prefix) {
        final String syntax = "Syntax: `"+prefix+"settings modrole <roleid/@mention>`\nSets the role of people who are granted permission to see tickets by default";

        if(args.length >= 3) {
            Guild g = e.getGuild();

            String roleid = args[2];
            Role role = null;

            try {
                role = g.getRoleById(roleid);
            }catch (NumberFormatException exception) {
                if(e.getMessage().getMentionedRoles().size() == 1) {
                    role = e.getMessage().getMentionedRoles().get(0);
                    roleid = role.getId();
                }
            }

            if(role == null) {
                e.getChannel().sendMessage(Embeds.error(syntax + "\n\n-> `"+roleid+"` could not find a role with that id.\n" +
                        "[How to get IDs](https://ozonprice.com/blog/discord-get-role-id/) <- also applies to channels/categories").build()).queue();
                return;
            }


            JSONObject guildSettings = GuildSettings.getGuildSettings(g);
            guildSettings.put("moderation-role", roleid);
            GuildSettings.saveGuildSettings(g, guildSettings);

            e.getChannel().sendMessage(Embeds.success("New Modrole: "+role.getAsMention() + "(`"+roleid+"`)").build()).queue();
        }
        else {
            e.getChannel().sendMessageEmbeds(Embeds.error(syntax).build()).queue();
        }
    }

    public static void setMaxTickets(MessageReceivedEvent e, String[] args, String prefix) {
        final String syntax = "Syntax: `"+prefix+"settings maxtickets <amount>`\nSets the max. ticket amount per user.";

        if(args.length >= 3) {
            int amount;
            try { amount = Integer.parseInt(args[2]); }
            catch (NumberFormatException exception) {
                e.getChannel().sendMessage(Embeds.error(syntax + "\n\n-> `<amount>` must be a Number.").build()).queue();
                return;
            }

            // save
            Guild g = e.getGuild();
            JSONObject guildSettings = GuildSettings.getGuildSettings(g);
            guildSettings.put("maxperuser", amount);
            GuildSettings.saveGuildSettings(g, guildSettings);

            e.getChannel().sendMessage(Embeds.success("Set max. amount of tickets per user to `"+amount+"`!").build()).queue();

        }
        else {
            e.getChannel().sendMessageEmbeds(Embeds.error(syntax).build()).queue();
        }
    }

    private static void showSettings(TextChannel channel, String prefix) {
        Guild guild = channel.getGuild();
        JSONObject guildSettings = GuildSettings.getGuildSettings(guild);

        Role modRole = guildSettings.has("moderation-role") ? guild.getRoleById(guildSettings.getString("moderation-role")) : null;
        TextChannel logchannel = guildSettings.has("logchannel") ? guild.getTextChannelById(guildSettings.getString("logchannel")) : null;
        TextChannel ticketchannel = guildSettings.has("ticketchannel") ? guild.getTextChannelById(guildSettings.getString("ticketchannel")) : null;
        Category category = guildSettings.has("ticket-category") ? guild.getCategoryById(guildSettings.getString("ticket-category")) : null;

        int maxtickets = guildSettings.has("maxperuser") ? guildSettings.getInt("maxperuser") : 2;

        channel.sendMessage(Embeds.tickety("" +
                "**Current Guild Settings**\n" +
                "\n" +
                "**Mod-Role: **" + (modRole == null ? "`NOT SET UP`" : modRole.getAsMention()) + "\n" +
                "**Logchannel: **" + (logchannel == null ? "`NOT SET UP`" : logchannel.getAsMention()) + "\n" +
                "**Ticketchannel: **" + (ticketchannel == null ? "`NOT SET UP`" : ticketchannel.getAsMention()) + "\n" +
                "**Ticket-Category: **" + (category == null ? "`NOT SET UP`" : category.getName()) + "\n" +
                "**Max-Ticketamount: **" + (maxtickets == 2 ? maxtickets + " `(default)`" : maxtickets) + "\n" +
                "\n" +
                "You can change settings using the following commands:\n" +
                "`"+prefix+"settings maxtickets <amount>`\n" +
                "`"+prefix+"settings modrole <roleid>`\n" +
                "`"+prefix+"settings logchannel <channelid>`\n" +
                "`"+prefix+"settings category <categoryid>`\n" +
                "`"+prefix+"settings sendticketmessage <channelid>` (Also sets the ticket channel with the button)\n\n" +
                "[How do i get an ID?](https://ozonprice.com/blog/discord-get-role-id/) <- Same applies to channels and categories" +
                "\n").build()).queue();
    }
}
