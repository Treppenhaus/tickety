package eu.treppi.codingschule.ticekty.core;

import eu.treppi.codingschule.ticekty.core.transcript.TicketMessage;
import eu.treppi.codingschule.ticekty.core.transcript.Transcript;
import eu.treppi.codingschule.ticekty.helper.GuildSettings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.File;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Setup {

    private static final String default_categoryName = "TICKETS";
    private static final String default_channelName = "support-ticket";
    private static final String default_transkriptlName = "archieved-tickets";
    private static final String default_rolename = "ticket-moderator";
    private static final Color default_rolecolor = new Color(217, 89, 89);
    private static final List<Permission> default_permissions_mod = Arrays.asList(Permission.MESSAGE_READ, Permission.MANAGE_CHANNEL, Permission.MESSAGE_EMBED_LINKS);

    private static final List<Permission> default_channelPermissions_yes = Arrays.asList(Permission.MESSAGE_READ, Permission.VIEW_CHANNEL);
    private static final List<Permission> default_channelPermissions_no = Arrays.asList(Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION);
    private static final List<Permission> default_categoryPermissions_no = Arrays.asList(Permission.VIEW_CHANNEL);

    private static final List<Permission> default_ownticket_yes = Arrays.asList(Permission.VIEW_CHANNEL, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_WRITE);
    private static final List<Permission> default_ownticket_no = Arrays.asList(Permission.MANAGE_CHANNEL, Permission.MANAGE_THREADS, Permission.MESSAGE_MENTION_EVERYONE);

    public static void autoSetupGuild(Guild g, TextChannel log) {
        if(!g.getSelfMember().hasPermission(Permission.ADMINISTRATOR)) {
            log.sendMessage(Embeds.error("Administrator Permissions Missing").build()).queue();
        }

        // create ticket moderator role
        g.createRole().setName(default_rolename).setColor(default_rolecolor).queue(role -> {

            // create ticket category
            g.createCategory(default_categoryName)
                    .addRolePermissionOverride(role.getIdLong(), default_permissions_mod, null)
                    .addRolePermissionOverride(g.getPublicRole().getIdLong(), null, default_categoryPermissions_no)
                    .queue(category -> {

                category.createTextChannel(default_channelName).addRolePermissionOverride(g.getPublicRole().getIdLong(), default_channelPermissions_yes, default_channelPermissions_no)
                        .queue(channel -> sendTicketCreationMessage(channel, category, role, log));

            });
        });
    }

    public static void sendTicketCreationMessage(TextChannel channel, Category category, Role role, TextChannel log) {
        Guild g = channel.getGuild();
        channel.sendMessageEmbeds(Embeds.success("**Ticket Support**\nClick the Button below to open a Ticket!")
                .setAuthor(g.getSelfMember().getNickname() == null ? g.getSelfMember().getNickname() : g.getSelfMember().getUser().getName(), Tickety.imageUrl)
                .build())
                .setActionRow(Button.secondary("tickety-create-ticket", "Open Ticket"))
                .queue(message -> {
                    if(category != null && role != null && log != null)
                        setupChannel(category, role, channel, log, message);
                });
    }

    public static void setupChannel(Category category, Role role, TextChannel channel, TextChannel log, Message message) {
        Guild g = category.getGuild();
        category.createTextChannel(default_transkriptlName)
                .addRolePermissionOverride(g.getPublicRole().getIdLong(), null, default_channelPermissions_yes)
                .addRolePermissionOverride(role.getIdLong(), default_channelPermissions_yes, null)
                .queue(logschannel -> logschannel.sendMessageEmbeds(Embeds.success(
                        "** Ticket Archieve \n**" +
                                "This is the channel where closed ticket logs will be sent to.\n" +
                                "It can only be seen by "+role.getAsMention()
                ).build()).queue( tickettranskriptmessage -> {

                            log.sendMessageEmbeds(Embeds.success("**All set up!**\n" +
                                    "- created moderation role: "+role.getAsMention()+"\n" +
                                    "- created ticket-creation channel: "+channel.getAsMention()+"\n" +
                                    "- set up permissions for "+channel.getAsMention() + "\n" +
                                    "- sent ticket-creation embed: "+channel.getAsMention()+"\n" +
                                    "- created ticket category ("+category.getName()+")\n" +
                                    "- set up permissions for ticket category\n" +
                                    "- created channel for ticket logs:"+ logschannel.getAsMention()+" \n" +
                                    "- set up permissions for "+logschannel.getAsMention()).build()).queue();



                            // store guilddata
                            JSONObject guildData = GuildSettings.getGuildSettings(g);
                            guildData.put("ticket-category", category.getId());
                            guildData.put("moderation-role", role.getId());
                            guildData.put("ticketchannel", channel.getId());
                            guildData.put("message", message.getId());
                            guildData.put("logchannel", logschannel.getId());

                            JSONArray tickets = GuildSettings.getTickets(g);
                            guildData.put("tickets", tickets);


                            GuildSettings.saveGuildSettings(g, guildData);
                        }
                ));
    }

    public static void setupNewTicket(ButtonClickEvent event) {

        Member member = event.getMember();
        Guild guild = member.getGuild();

        final int amount = GuildSettings.runningNumber(guild);
        final String ticketTitle = "ticket-"+amount;

        JSONObject guildSettings = GuildSettings.getGuildSettings(guild);
        Category category = guild.getCategoryById(guildSettings.getString("ticket-category"));

        // create text channel
        category.createTextChannel(ticketTitle)
                .addMemberPermissionOverride(member.getIdLong(), default_ownticket_yes, default_ownticket_no)
                .queue(ticketChannel -> {

                    event.reply("Ticket Created! "+ticketChannel.getAsMention())
                            .setEphemeral(true)
                            .queue();

                    sendSuccessEmbed(member, ticketChannel, amount, event.getTimeCreated());
                    saveTicketInformation(member, amount, ticketTitle, ticketChannel, guild, guildSettings);
                });
    }


    public static void setupNewTicket(MessageReceivedEvent event) {

        Member member = event.getMember();
        assert member != null;
        Guild guild = member.getGuild();

        final int amount = GuildSettings.runningNumber(guild);
        final String ticketTitle = "ticket-"+amount;

        JSONObject guildSettings = GuildSettings.getGuildSettings(guild);
        Category category = guild.getCategoryById(guildSettings.getString("ticket-category"));

        if(category != null) {
            // create text channel
            category.createTextChannel(ticketTitle)
                    .addMemberPermissionOverride(member.getIdLong(), default_ownticket_yes, default_ownticket_no)
                    .queue(ticketChannel -> {

                        event.getAuthor().openPrivateChannel().queue(channel ->
                                channel.sendMessage("Ticket in "+guild.getName()+" Created! " + ticketChannel.getAsMention())
                                        .queue());

                        sendSuccessEmbed(member, ticketChannel, amount, event.getMessage().getTimeCreated());
                        saveTicketInformation(member, amount, ticketTitle, ticketChannel, guild, guildSettings);
                    });
        }
        else {
            User creator = event.getAuthor();
            creator.openPrivateChannel().queue( channel -> channel.sendMessage(Embeds.error("**The Ticket-Category was not found!**\n" +
                    "please tell your server administrator to\n" +
                    "- set their ticket-category with `t!settings category <categoryid>`\n" +
                    "- review their guild settings with `t!settings`").build()).queue());
        }


    }

    public static void saveTicketInformation(Member member, int amount, String ticketTitle, TextChannel ticketChannel, Guild guild, JSONObject guildSettings) {
        JSONObject ticketOptions = new JSONObject();
        ticketOptions.put("userid", member.getId());
        ticketOptions.put("ticketid", amount);
        ticketOptions.put("title", ticketTitle);
        ticketOptions.put("channelid", ticketChannel.getId());

        JSONArray tickets = GuildSettings.getTickets(guild);
        tickets.put(ticketOptions);

        guildSettings.put("tickets", tickets);
        GuildSettings.saveGuildSettings(guild, guildSettings);
    }

    public static void sendSuccessEmbed(Member member, TextChannel ticketChannel, int amount, OffsetDateTime time) {
        EmbedBuilder b = Embeds.success("**Ticket Support**\n" +
                "Press the Button to Close your Ticket!\n\n" +
                "" +
                "Ticket by: "+member.getAsMention());
        b.setTimestamp(time);

        ticketChannel.sendMessageEmbeds(b.build())
                .setActionRow(Button.secondary("tickety-close-ticket-"+amount, "Close Ticket"))
                .queue();
    }

    public static void closeTicket(Member closer, TextChannel ticketChannel, JSONObject channelSettings) {
        Guild guild = closer.getGuild();
        int ticketid = channelSettings == null ? -1 : channelSettings.has("ticketid") ? channelSettings.getInt("ticketid") : -1;
        ticketid = ticketid == -1 ? getIDByChannelName(ticketChannel) : ticketid;

        // send closing embed
        int finalTicketid = ticketid;
        ticketChannel.sendMessageEmbeds(Embeds.closingTicket(closer).build()).queue(
                message -> ticketChannel.getHistoryBefore(message, 100).queue(
                        messageHistory -> {

                            Member member = null;
                            if(channelSettings != null) {
                                String userid = channelSettings.getString("userid");
                                member = guild.getMemberById(userid);
                            }

                            Transcript tscript = createTranscript(messageHistory, channelSettings, guild, member, closer, finalTicketid);
                            handleTranscript(tscript, ticketChannel, guild, member, closer);


                            // delete channel & guild data
                            ticketChannel.delete().queue();
                            GuildSettings.removeTicketFromDataByTicketid(guild, finalTicketid);
                        }
                )
        );

    }

    public static void handleTranscript(Transcript tscript, TextChannel ticketChannel, Guild guild, Member member, Member closer) {
        File f = tscript.generate();
        closer.getUser().openPrivateChannel().queue(channel -> channel.sendFile(f).setEmbeds(tscript.getEmbed().build()).queue());

        if(member != null)
            if(!member.getId().equals(closer.getId()))
                member.getUser().openPrivateChannel().queue(channel -> channel.sendFile(f).setEmbeds(tscript.getEmbed().build()).queue());

        else
            ticketChannel.sendMessageEmbeds(Embeds.error("Could not find user.").build()).queue();


        TextChannel logchannel = guild.getTextChannelById(GuildSettings.getGuildSettings(guild).getString("logchannel"));
        if(logchannel != null) {
            logchannel.sendFile(f).setEmbeds(tscript.getEmbed().build()).queue();
        }
    }

    public static Transcript createTranscript(MessageHistory messageHistory, JSONObject channelSettings, Guild guild, Member member, Member closer, int finalTicketid) {
        ArrayList<TicketMessage> ticketMessages = new ArrayList<>();
        for(int i = messageHistory.getRetrievedHistory().size() - 1; i >= 0; i--) {
            Message msg = messageHistory.getRetrievedHistory().get(i);
            ticketMessages.add(new TicketMessage(
                    msg.getAuthor().getAsTag(),
                    msg.getContentDisplay(),
                    msg.getId(),
                    msg.getAuthor().getId(),
                    msg.getAuthor().getAvatarUrl()
            ));
        }

        return new Transcript(
                finalTicketid,
                member,
                closer,
                ticketMessages,
                guild
        );
    }

    public static int getIDByChannelName(TextChannel channel) {
        String[] args = channel.getName().split("-");

        try {
            return Integer.parseInt(args[args.length -1]);
        } catch (NumberFormatException ignored) {
            return -1;
        }
    }
}
