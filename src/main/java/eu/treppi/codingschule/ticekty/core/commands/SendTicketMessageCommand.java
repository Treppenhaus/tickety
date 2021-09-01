package eu.treppi.codingschule.ticekty.core.commands;

import eu.treppi.codingschule.ticekty.core.Embeds;
import eu.treppi.codingschule.ticekty.core.Tickety;
import eu.treppi.codingschule.ticekty.helper.GuildSettings;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SendTicketMessageCommand extends ListenerAdapter {
    public static final String command = "sendmessage";
    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if(Tickety.ignoreBots && e.getAuthor().isBot()) return;
        String content = e.getMessage().getContentRaw();
        String prefix = GuildSettings.getPrefix(e.getGuild());

        if(content.startsWith(prefix + command)) {
            if(e.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                SettingsCommand.sendMessage(e, content.split(" "), prefix, true);
            }
            else {
                e.getMessage().replyEmbeds(Embeds.error("For security reasons, only members with the `administrator`-permission are allowed to use `"+prefix+"settings`.").build()).queue();
            }
        }
    }
}
