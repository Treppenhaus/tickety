package eu.treppi.codingschule.ticekty.core;

import eu.treppi.codingschule.ticekty.core.transcript.TicketMessage;
import eu.treppi.codingschule.ticekty.core.transcript.Transcript;
import eu.treppi.codingschule.ticekty.helper.GuildSettings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.File;
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


                // create ticket channel
                // setup permissions for ticket category with moderator role
                category.createTextChannel(default_channelName).addRolePermissionOverride(g.getPublicRole().getIdLong(), default_channelPermissions_yes, default_channelPermissions_no).queue(channel -> {

                    // send ticket embed
                    channel.sendMessageEmbeds(Embeds.success("**Ticket Support**\nClick the Button below to open a Ticket!")
                            .setAuthor(g.getSelfMember().getNickname() == null ? g.getSelfMember().getNickname() : g.getSelfMember().getUser().getName(), Tickety.imageUrl)
                            .build())
                            .setActionRow(Button.secondary("tickety-create-ticket", "Open Ticket"))
                            .queue(message -> {




                                category.createTextChannel(default_transkriptlName)
                                        .addRolePermissionOverride(g.getPublicRole().getIdLong(), null, default_channelPermissions_yes)
                                        .addRolePermissionOverride(role.getIdLong(), default_channelPermissions_yes, null)
                                        .queue(logschannel -> logschannel.sendMessageEmbeds(Embeds.success(
                                                "** Ticket Archieve \n**" +
                                                        "This is the channel where closed ticket logs will be sent to.\n" +
                                                        "It can only be seen by "+role.getAsMention()
                                        ).build()).queue( tickettranskriptmessage -> {

                                                    log.sendMessageEmbeds(Embeds.success("**success!**\n" +
                                                    "- created moderation role: "+role.getAsMention()+"\n" +
                                                    "- created ticket-creation channel: "+channel.getAsMention()+"\n" +
                                                    "- set up permissions for "+channel.getAsMention() + "\n" +
                                                    "- sent ticket-creation embed: "+channel.getAsMention()+"\n" +
                                                    "- created ticket category ("+category.getName()+")\n" +
                                                    "- set up permissions for ticket category\n" +
                                                    "- created channel for ticket logs:"+ logschannel.getAsMention()+" \n" +
                                                    "- set up permissions for "+logschannel.getAsMention()).build()).queue();



                                                    // store guilddata
                                                    JSONObject guildData = new JSONObject();
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



                    });
                });


            });
        });
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

                    // send ticket embed message with close button
                    EmbedBuilder b = Embeds.success("**Ticket Support**\n" +
                            "Press the Button to Close your Ticket!\n\n" +
                            "" +
                            "Ticket by: "+member.getAsMention());
                    b.setTimestamp(event.getTimeCreated());

                    ticketChannel.sendMessageEmbeds(b.build())
                            .setActionRow(Button.secondary("tickety-close-ticket-"+amount, "Close Ticket"))
                            .queue();


                    // save ticket information
                    JSONObject ticketOptions = new JSONObject();
                    ticketOptions.put("userid", member.getId());
                    ticketOptions.put("ticketid", amount);
                    ticketOptions.put("title", ticketTitle);
                    ticketOptions.put("channelid", ticketChannel.getId());

                    JSONArray tickets = GuildSettings.getTickets(guild);
                    tickets.put(ticketOptions);

                    guildSettings.put("tickets", tickets);
                    GuildSettings.saveGuildSettings(guild, guildSettings);
                });
    }

    public static void closeTicket(Member closer, int ticketid) {
        Guild guild = closer.getGuild();
        JSONObject ticketSettings = GuildSettings.getTicketSettingsById(guild, ticketid);
        if(ticketSettings == null) return;

        // send closing embed
        TextChannel ticketChannel = guild.getTextChannelById(ticketSettings.getString("channelid"));
        ticketChannel.sendMessageEmbeds(Embeds.error("**Ticket closed by "+closer.getAsMention()+"**\nChannel will be deleted in a few Seconds!").build()).queue(
                message -> {


                    // archieve logs
                    ticketChannel.getHistoryBefore(message, 100).queue(
                            messageHistory -> {

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

                                Transcript tscript = new Transcript(
                                        ticketid,
                                        ticketSettings.getString("userid"),
                                        closer.getId(),
                                        ticketMessages,
                                        guild
                                );

                                File f = tscript.generate();
                                closer.getUser().openPrivateChannel().queue(channel -> channel.sendFile(f).setEmbeds(tscript.getEmbed().build()).queue());

                                Member user = guild.getMemberById(ticketSettings.getString("userid"));
                                if(user != null) {
                                    if(!user.getId().equals(closer.getId())) {
                                        user.getUser().openPrivateChannel().queue(channel -> channel.sendFile(f).setEmbeds(tscript.getEmbed().build()).queue());
                                    }
                                }

                                TextChannel logchannel = guild.getTextChannelById(GuildSettings.getGuildSettings(guild).getString("logchannel"));
                                if(logchannel != null) {
                                    logchannel.sendFile(f).setEmbeds(tscript.getEmbed().build()).queue();
                                }


                                // delete channel
                                ticketChannel.delete().queue();
                            }
                    );




                    // delete from guild data
                    GuildSettings.removeTicketFromDataByTicketid(guild, ticketid);
                }
        );

    }
}
