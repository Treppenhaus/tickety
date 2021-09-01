package eu.treppi.codingschule.ticekty.core.commands;

import eu.treppi.codingschule.ticekty.core.Embeds;
import eu.treppi.codingschule.ticekty.core.Setup;
import eu.treppi.codingschule.ticekty.core.Tickety;
import eu.treppi.codingschule.ticekty.helper.GuildSettings;
import net.dv8tion.jda.api.Permission;
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
        String prefix = GuildSettings.getPrefix(e.getGuild());

        if(content.startsWith(prefix+"settings")) {

            if(e.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                if(args.length == 1) {
                    showSettings(e.getTextChannel(), prefix);
                }
                else if(args.length >= 2) {
                    switch (args[1].toLowerCase()) {
                        case "maxtickets":
                            setMaxTickets(e, args, prefix);
                            return;
                        case "modrole":
                            setModRole(e, args, prefix);
                            return;
                        case "logchannel":
                            setChannel("logchannel", "The channel where ticket transcripts will be sent to.", e, args, prefix);
                            return;
                        case "ticketchannel":
                            setChannel("ticketchannel", "The channel with the 'create-ticket'-embed and button.", e, args, prefix);
                            return;
                        case "sendticketmessage":
                            sendMessage(e, args, prefix, false);
                            return;
                        case "category":
                            setCategory(e, args, prefix);
                    }
                }
            }
            else {
                e.getMessage().replyEmbeds(Embeds.error("For security reasons, only members with the `administrator`-permission are allowed to use `"+prefix+"settings`.").build()).queue();
            }
        }
    }

    public static void sendMessage(MessageReceivedEvent e, String[] args, String prefix, boolean cmd) {
        final String syntax = "Syntax: `"+prefix+"settings sendticketmessage <channelid>` or\n" +
                "Syntax: `"+prefix+"sendmessage <channelid>`\nThe category under which new ticket channels are created.";

        if(args.length >= 3 || (args.length == 2 && cmd)) {
            Guild guild = e.getGuild();
            String channelid = cmd ? args[1] : args[2];

            TextChannel channel = null;
            try {
                channel = guild.getTextChannelById(channelid);
            }catch (NumberFormatException exception) {
                if(e.getMessage().getMentionedChannels().size() == 1) {
                    channel = e.getMessage().getMentionedChannels().get(0);
                    channelid = channel.getId();
                }
            }

            if(channel == null) {
                e.getChannel().sendMessageEmbeds(Embeds.error(syntax + "\n\n-> `"+channelid+"` could not find a chatnnel with that id.\n" +
                        "[How to get IDs](https://ozonprice.com/blog/discord-get-role-id/) <- also applies to channels/categories").build()).queue();
                return;
            }

            JSONObject guildSettings = GuildSettings.getGuildSettings(guild);
            guildSettings.put("ticketchannel", channelid);
            GuildSettings.saveGuildSettings(guild, guildSettings);

            Setup.sendTicketCreationMessage(channel, null, null, null);
            e.getChannel().sendMessageEmbeds(Embeds.success("Send Ticket-Creation Embed to "+channel.getAsMention() +
                    "\nNew ticketchannel: "+channel.getAsMention() + "(`"+channelid+"`)").build()).queue();

        }
        else {
            e.getChannel().sendMessageEmbeds(Embeds.error(syntax).build()).queue();
        }

    }

    private static void setCategory(MessageReceivedEvent e, String[] args, String prefix) {
        final String syntax = "Syntax: `"+prefix+"settings category <categoryid>`\nThe category under which new ticket channels are created.";

        if(args.length >= 3) {
            Guild guild = e.getGuild();
            String categoryid = args[2];


            Category category = null;
            try {
                category = guild.getCategoriesByName(categoryid, false).get(0);
                categoryid = category.getId();
            }catch (NumberFormatException | IndexOutOfBoundsException ignored) {}
            try {
                if(category == null) {
                    category = guild.getCategoryById(categoryid);
                }
            }catch (Exception ignored) {}


            if(category == null) {
                e.getChannel().sendMessageEmbeds(Embeds.error(syntax + "\n\n-> `"+categoryid+"` could not find a category with that id.\n" +
                        "[How to get IDs](https://ozonprice.com/blog/discord-get-role-id/) <- also applies to channels/categories").build()).queue();
                return;
            }


            JSONObject guildSettings = GuildSettings.getGuildSettings(guild);
            guildSettings.put("ticket-category", categoryid);
            GuildSettings.saveGuildSettings(guild, guildSettings);

            e.getChannel().sendMessageEmbeds(Embeds.success("New Category: "+category.getAsMention() + "(`"+categoryid+"`)").build()).queue();
        }
        else {
            e.getChannel().sendMessageEmbeds(Embeds.error(syntax).build()).queue();
        }

    }

    private static void setChannel(String configname, String description, MessageReceivedEvent e, String[] args, String prefix) {
        final String syntax = "Syntax: `"+prefix+"settings "+configname+" <channelid/#mention>`\n"+description;

        if(args.length >= 3) {
            Guild guild = e.getGuild();
            String channelid = args[2];

            TextChannel channel = null;
            try {
                channel = guild.getTextChannelById(channelid);
            }catch (NumberFormatException exception) {
                if(e.getMessage().getMentionedChannels().size() == 1) {
                    channel = e.getMessage().getMentionedChannels().get(0);
                    channelid = channel.getId();
                }
            }

            if(channel == null) {
                e.getChannel().sendMessageEmbeds(Embeds.error(syntax + "\n\n-> `"+channelid+"` could not find a channel with that id.\n" +
                        "[How to get IDs](https://ozonprice.com/blog/discord-get-role-id/) <- also applies to channels/categories").build()).queue();
                return;
            }

            JSONObject guildSettings = GuildSettings.getGuildSettings(guild);
            guildSettings.put(configname, channelid);
            GuildSettings.saveGuildSettings(guild, guildSettings);

            e.getChannel().sendMessageEmbeds(Embeds.success("New "+configname+": "+channel.getAsMention() + "(`"+channelid+"`)").build()).queue();
        }
        else {
            e.getChannel().sendMessageEmbeds(Embeds.error(syntax).build()).queue();
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
                e.getChannel().sendMessageEmbeds(Embeds.error(syntax + "\n\n-> `"+roleid+"` could not find a role with that id.\n" +
                        "[How to get IDs](https://ozonprice.com/blog/discord-get-role-id/) <- also applies to channels/categories").build()).queue();
                return;
            }


            JSONObject guildSettings = GuildSettings.getGuildSettings(g);
            guildSettings.put("moderation-role", roleid);
            GuildSettings.saveGuildSettings(g, guildSettings);

            e.getChannel().sendMessageEmbeds(Embeds.success("New Modrole: "+role.getAsMention() + "(`"+roleid+"`)").build()).queue();
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
                e.getChannel().sendMessageEmbeds(Embeds.error(syntax + "\n\n-> `<amount>` must be a Number.").build()).queue();
                return;
            }

            // save
            Guild g = e.getGuild();
            JSONObject guildSettings = GuildSettings.getGuildSettings(g);
            guildSettings.put("maxperuser", amount);
            GuildSettings.saveGuildSettings(g, guildSettings);

            e.getChannel().sendMessageEmbeds(Embeds.success("Set max. amount of tickets per user to `"+amount+"`!").build()).queue();

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

        channel.sendMessageEmbeds(Embeds.tickety("" +
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
                "`"+prefix+"settings modrole <roleid/@mention>`\n" +
                "`"+prefix+"settings logchannel <channelid/#mention>`\n" +
                "`"+prefix+"settings ticketchannel <channelid/#mention>`\n" +
                "`"+prefix+"settings category <categoryid>`\n" +
                "`"+prefix+"settings sendticketmessage <channelid>` (Also sets the ticket channel with the button)\n\n" +
                "[How do i get an ID?](https://ozonprice.com/blog/discord-get-role-id/) <- Same applies to channels and categories" +
                "\n").build()).queue();
    }
}
