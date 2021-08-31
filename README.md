

#**this readme is not finished yet**
---
---
---
# tickety
ticket bot for .gg/schule bot-jam

![test](https://github.com/treppenhaus/tickety/actions/workflows/maven.yml/badge.svg) [![CodeFactor](https://www.codefactor.io/repository/github/treppenhaus/tickety/badge)](https://www.codefactor.io/repository/github/treppenhaus/tickety)


## contents
- feature list
- command list
- set up
- support


# feature list
a


# setup
**Below is a brief tutorial on how to set up tickety for your Discord Guild. There is an easy way and another if you already have channels and roles set up:**
### The Easy Way:
Use `t!autosetup`. Tickety will **automatically create**
- a category where ticket channels are stored
- a ticket channel with an embed message and a button to open new tickets (people can still use the `t!open` command in any channel.)
- a ticket channel where transcripts are sent to (also under the ticket category)
- a role @ticket-moderator, this role will be granted permissions to see the transcript channel as well as every new ticket a user creates.

> You can change channel/role/category names as well as the role color, tickety should not be impacted. If you delete anything, use the `t!settings` command to provide tickety with a new role/channel

> You can change all settings using the `t!settings`-command or looking into the _configurable way_

### The configurable way:
First, look up your guild settings using `t!settings`. Commands with the * are required to do / stuff is required in order for tickety to work.
Now you can change everything using the below commands:

- `t!settings maxtickets <numer>` - changes the number of tickets a user can have at the same time (default: 2)
- `t!settings modrole <roleid/@role>` - this role is automatically granted permission to see and write in all ticket channels as well as to see the channels where transcripts are sent to. If you have not set up a modrole, the ticket channels will only be seen by people with administrator permissions
- `t!settings logchannel <channel/#channel>` - sets the channel where transcripts are sent to whenever a ticket is closed
- `t!settings category <categoryid/name>` - sets the category under which new tickets will be opened. If you have multiple categories with the same name, use the ID. Otherwise, the first one will be selected.
- \*`t!settings sendticketmessage` - sets the ticket channel where a message with an embed + button is sent to. **TODO: make message configurable!**

You can also check if tickety is set up correctly using the `t!settings` or `t!setup` command.
todo: add screenshot

































