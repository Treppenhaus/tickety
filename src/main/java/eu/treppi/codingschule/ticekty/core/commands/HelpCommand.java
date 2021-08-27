package eu.treppi.codingschule.ticekty.core.commands;

import eu.treppi.codingschule.ticekty.core.Embeds;
import eu.treppi.codingschule.ticekty.core.Tickety;
import eu.treppi.codingschule.ticekty.helper.GuildSettings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class HelpCommand extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if(Tickety.ignoreBots && e.getAuthor().isBot()) return;
        String content = e.getMessage().getContentRaw();

        String prefix = GuildSettings.getPrefix(e.getGuild());

        if(content.startsWith(prefix+"help")) {
            EmbedBuilder b = Embeds.tickety("" +
                    "**Tickety**\n" +
                    "Tickety is an easy and professional way to manage tickets in your discord server\n" +
                    "Current prefix: `"+prefix+"`\n" +
                    "\n" +
                    "**Commands**\n" +
                    "You can find a full List of Commands below:\n" +
                    "`"+prefix+"help`: this help embed\n" +
                    "`"+prefix+"settings`: see all guild settings\n" +
                    "`"+prefix+"settings maxtickets <amount>`: sets the max ticket amount for users.\n" +
                    "`"+prefix+"autosetup`: automatically set up EVERYTHING for your guild. Just one command.\n" +
                    "`"+prefix+"open`: opens a new ticket for you (can be used in any channel for faster support).\n" +
                    "`"+prefix+"close`: to close a ticket.\n" +
                    "\n" +
                    "**Support**\n" +
                    "For fast support message `treppi#9999` or join the [Support Server](https://discord.gg/cNJ6s3S6DM)\n" +
                    "You can also [Submit an Issue](https://github.com/Treppenhaus/tickety/issues/new) on the public [Tickety-repository](https://github.com/Treppenhaus/tickety/)\n" +
                    "\n" +
                    "Invite the bot [here]("+Tickety.INVITE+")");
            e.getChannel().sendMessageEmbeds(b.build()).queue();
        }
    }
}
