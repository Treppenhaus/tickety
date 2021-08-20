package eu.treppi.codingschule.ticekty.core.listeners;

import eu.treppi.codingschule.ticekty.helper.GuildSettings;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class TicketChannelDelete extends ListenerAdapter {
    @Override
    public void onTextChannelDelete(TextChannelDeleteEvent e) {
        Guild guild = e.getGuild();
        TextChannel channel = e.getChannel();

        GuildSettings.removeTicketFromDataByChannelid(guild, channel.getId());
    }
}
