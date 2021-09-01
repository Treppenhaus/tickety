

#**this readme is not finished yet**
---
---
---
# tickety
ticket bot for .gg/schule bot-jam

![test](https://github.com/treppenhaus/tickety/actions/workflows/maven.yml/badge.svg) [![CodeFactor](https://www.codefactor.io/repository/github/treppenhaus/tickety/badge)](https://www.codefactor.io/repository/github/treppenhaus/tickety)


# contents
- [Features](#features)
- [Commands](#commands)
- [Transcripts](#transcripts)
- [Setup](#setup)
- [Support](#support)

---
# features
Tickety offers a variety of features other ticket bots do.
- Supports Embeds & Buttons
- Customizable (Messages, Roles, Channel-Names, ...)
- Generates Transcripts
  - Transcripts are sent as a .html-File and can easily be viewed in your browser on PC and mobile.
  - You can also search for transcripts
- Autosetup: Be lazy and let the bot set up roles, channels, permissions and even categories! Use `t!autosetup`

---
# commands
Below is a list of commands (+ description) that are currently available

`t!autosetup`
> - generates a role @ticket-moderator
> - generates a category and sets up the permissions for the created role
> - also adds two channels to the category: 
>   - support-ticket: the channel where users can react to open a ticket
>   - support-transcripts: the channel where transcripts from closed tickets are sent to
> - sets up permissions for the @everyone and @ticket-moderator roles

`t!open`
> Opens a new Ticket (Same as reacting in the create-ticket channel).
> The message gets deleted so this command can be used by anyone in any channel

`t!close`
> Closes a Ticket, can only be used in a ticket-channel of a user
> Can be used by the ticket-creator, a person with the ticket-moderator role (the one that is set up) or someone with ADMINISTRATOR-Permissions

`t!settings`
> Is used to change the guilds settings. Can only be used by Administrators. Described here: [Setup](#setup)

`t!sendmessage <channel>`
> Resends the create-ticket-embed with a button to the provided channel (Also sets the channel as the ticket-creation channel.)

`t!help`
> Sends a help Embed with all commands and a [Support](#support) link

`t!invite`
> Provides a Link to invite the bot


---
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


