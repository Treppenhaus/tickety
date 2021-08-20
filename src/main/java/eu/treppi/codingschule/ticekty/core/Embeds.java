package eu.treppi.codingschule.ticekty.core;

import net.dv8tion.jda.api.EmbedBuilder;

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
}
