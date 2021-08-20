package eu.treppi.codingschule.ticekty.core.listeners;

import eu.treppi.codingschule.ticekty.core.Setup;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class TicketCreation extends ListenerAdapter {

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        if (event.getComponentId().equals("tickety-create-ticket")) {
            Setup.setupNewTicket(event);
        }
    }
}
