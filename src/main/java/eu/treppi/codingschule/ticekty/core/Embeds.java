package eu.treppi.codingschule.ticekty.core;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.awt.*;

public class Embeds {
    public static EmbedBuilder error(String message) {
        EmbedBuilder b = new EmbedBuilder();
        b.setColor(new Color(217, 89, 89));
        b.setDescription(message);
        return b;
    }

    public static EmbedBuilder success(String message) {
        EmbedBuilder b = new EmbedBuilder();
        b.setColor(new Color(89, 217, 119));
        b.setDescription(message);
        return b;
    }

    public static EmbedBuilder closingTicket(Member closer) {
        return error("**Ticket closed by "+closer.getAsMention()+"**\nChannel will be deleted in a few Seconds!");
    }
}
