package eu.treppi.codingschule.ticekty.core.listeners;

import eu.treppi.codingschule.ticekty.core.Setup;
import eu.treppi.codingschule.ticekty.core.Tickety;
import eu.treppi.codingschule.ticekty.helper.GuildSettings;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.JSONObject;

public class TicketClose extends ListenerAdapter {
    @Override
    public void onButtonClick(ButtonClickEvent e) {
        if (e.getComponentId().startsWith("tickety-close-ticket-")) {
            JSONObject channelSettings = GuildSettings.getTicketSettingsByChannelId(e.getGuild(), e.getChannel().getId());
            Setup.closeTicket(e.getMember(), e.getTextChannel(), channelSettings);
        }
    }


    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {

        if(Tickety.ignoreBots && e.getAuthor().isBot()) return;

        String content = e.getMessage().getContentRaw();
        if(content.startsWith(Tickety.prefix+"close")) {
            Guild guild = e.getGuild();

            for(Role role : e.getMember().getRoles()) {
                if(role.getId().equals(GuildSettings.getGuildSettings(guild).getString("moderation-role")) || e.getMember().hasPermission(Permission.ADMINISTRATOR)) {

                    JSONObject channelSettings = GuildSettings.getTicketSettingsByChannelId(guild, e.getChannel().getId());
                    Setup.closeTicket(e.getMember(), e.getChannel(), channelSettings);
                    return;
                }
            }
        }
    }
}
