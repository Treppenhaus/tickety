package eu.treppi.codingschule.ticekty.core.commands;

import eu.treppi.codingschule.ticekty.core.Embeds;
import eu.treppi.codingschule.ticekty.core.Tickety;
import eu.treppi.codingschule.ticekty.helper.GuildSettings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class InviteCommand extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if(Tickety.ignoreBots && e.getAuthor().isBot()) return;
        String prefix = GuildSettings.getPrefix(e.getGuild());
        String content = e.getMessage().getContentRaw();

        if(content.startsWith(prefix+"invite")) {
            EmbedBuilder b = Embeds.success("Click [here]("+Tickety.INVITE+") to invite the bot to your Guild!\nThank you for supporting Tickety ✨");
            e.getChannel().sendMessageEmbeds(b.build()).queue();
        }
    }
}
