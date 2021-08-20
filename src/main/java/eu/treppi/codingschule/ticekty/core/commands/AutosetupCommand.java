package eu.treppi.codingschule.ticekty.core.commands;

import eu.treppi.codingschule.ticekty.core.Setup;
import eu.treppi.codingschule.ticekty.core.Tickety;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AutosetupCommand extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if(Tickety.ignoreBots && e.getAuthor().isBot()) return;

        String content = e.getMessage().getContentRaw();
        if(content.startsWith(Tickety.prefix+"autosetup")) {
            Setup.autoSetupGuild(e.getGuild(), e.getTextChannel());
        }
    }
}
