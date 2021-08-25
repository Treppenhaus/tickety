package eu.treppi.codingschule.ticekty.core.listeners;

import eu.treppi.codingschule.ticekty.core.Setup;
import eu.treppi.codingschule.ticekty.helper.GuildSettings;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class TicketCreation extends ListenerAdapter {

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        if (event.getComponentId().equals("tickety-create-ticket")) {
            if(canCreateTicket(event.getMember()))
                Setup.setupNewTicket(event);
            else
                event.reply("You already have too many open Tickets. Maybe close one?")
                        .setEphemeral(true)
                        .queue();
        }
    }

    public static boolean canCreateTicket(Member member) {
        return GuildSettings.getTicketSettingsByUser(member).length() < GuildSettings.getMaxTicketsPerUser(member.getGuild());
    }
}
