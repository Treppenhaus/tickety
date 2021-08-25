package eu.treppi.codingschule.ticekty.core.transcript;

import eu.treppi.codingschule.ticekty.core.Embeds;
import eu.treppi.codingschule.ticekty.helper.FileHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.io.File;
import java.util.ArrayList;

public class Transcript {
    Member user, closer;
    ArrayList<TicketMessage> messages;
    Guild guild;
    int ticketid;


    public Transcript(int ticketid, Member user, Member closer, ArrayList<TicketMessage> messages, Guild guild) {
        this.user = user;
        this.closer = closer;
        this.messages = messages;
        this.ticketid = ticketid;
        this.guild = guild;
    }

    public EmbedBuilder getEmbed() {

        return Embeds.success("" +
            "**__" + ticketid+" closed __**\n" +
            "**User: **"+ (user != null ? user.getAsMention() : "") + "\n" +
            "**Closed by: **"+ (closer != null ? closer.getAsMention() : "") + "\n");
    }


    public File generate() {

        String message_preset = FileHelper.readFile(new File("message.html"));
        String ticket_preset = FileHelper.readFile(new File("ticket.html"));
        StringBuilder messagesGenerated = new StringBuilder();




        for(TicketMessage message : messages) {

            String buildingMessage = message_preset
                    .replace("{message.id}", message.getMessageid())
                    .replace("{user.id}", message.getUserid())
                    .replace("{message.contentdisplay}", message.getMessagecontent())
                    .replace("{user.name}", message.getUsername())
                    .replace("{user.avatar}", message.getUseravatar());

            messagesGenerated.append(buildingMessage).append("\n");
        }

        File file = new File("data/guilds/"+guild.getId()+"/transcripts/"+ticketid+".html");
        FileHelper.writeToFile(file, ticket_preset
                .replace("{ticket.id}", Integer.toString(ticketid))
                .replace("{guild.url}", guild.getIconUrl() != null ? guild.getIconUrl() : guild.getSelfMember().getUser().getAvatarUrl())
                .replace("{guild.name}", guild.getName())
                .replace("{messages}", messagesGenerated));

        return file;
    }
}
