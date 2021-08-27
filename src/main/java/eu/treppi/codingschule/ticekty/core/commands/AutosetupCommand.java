package eu.treppi.codingschule.ticekty.core.commands;

import eu.treppi.codingschule.ticekty.core.Embeds;
import eu.treppi.codingschule.ticekty.core.Setup;
import eu.treppi.codingschule.ticekty.core.Tickety;
import eu.treppi.codingschule.ticekty.helper.GuildSettings;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AutosetupCommand extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if(Tickety.ignoreBots && e.getAuthor().isBot()) return;
        String prefix = GuildSettings.getPrefix(e.getGuild());
        String content = e.getMessage().getContentRaw();


        if(content.startsWith(prefix+"autosetup")) {
            if(e.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                Setup.autoSetupGuild(e.getGuild(), e.getTextChannel());
            }
            else {
                e.getMessage().replyEmbeds(Embeds.error("For security reasons, only members with the `administrator`-permission are allowed to use `"+prefix+"autosetup`.").build()).queue();
            }
        }

    }
}
