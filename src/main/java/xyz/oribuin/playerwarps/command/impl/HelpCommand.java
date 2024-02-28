package xyz.oribuin.playerwarps.command.impl;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import xyz.oribuin.playerwarps.manager.LocaleManager;

public class HelpCommand extends BaseRoseCommand {

    private final BaseRoseCommand parent;

    public HelpCommand(RosePlugin rosePlugin, BaseRoseCommand parent) {
        super(rosePlugin);

        this.parent = parent;
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        LocaleManager localeManager = this.rosePlugin.getManager(LocaleManager.class);

        ArgumentsDefinition argumentsDefinition = this.parent.getCommandArguments();
        if (argumentsDefinition.size() != 1)
            throw new IllegalStateException("Help command parent must have exactly 1 argument.");

        Argument argument = argumentsDefinition.get(0);
        if (!(argument instanceof Argument.SubCommandArgument subCommandArgument))
            throw new IllegalStateException("Help command parent must have a subcommand argument.");

        localeManager.sendCommandMessage(context.getSender(), "command-help-title");
        for (RoseCommand command : subCommandArgument.subCommands()) {
            if (!command.canUse(context.getSender()))
                continue;

            StringPlaceholders stringPlaceholders = StringPlaceholders.of(
                    "cmd", context.getCommandLabel().toLowerCase(),
                    "subcmd", command.getName().toLowerCase(),
                    "args", command.getParametersString(),
                    "desc", command.getDescriptionKey() == null ? "invalid-description" : localeManager.getLocaleMessage(command.getDescriptionKey())
            );

            localeManager.sendSimpleCommandMessage(context.getSender(), "command-help-list-description" + (command.getCommandArguments().size() == 0 ? "-no-args" : ""), stringPlaceholders);
        }
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("help")
                .descriptionKey("command-help-description")
                .permission("playerwarps.help")
                .build();
    }

}
