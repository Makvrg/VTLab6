package ru.ifmo.se.commands;

import ru.ifmo.se.dto.request.Request;
import ru.ifmo.se.dto.response.Response;
import ru.ifmo.se.dto.response.ResponseHelpMap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class HelpCommand extends Command {

    private final Collection<Command> commands;

    public HelpCommand(Collection<Command> commands) {
        super("help", "вывести справку по доступным командам", true);
        this.commands = commands;
    }

    @Override
    public Response execute(Request ignoredRequest) {
        Map<String, String> helpMap = new HashMap<>();
        for (Command command : commands) {
            if (!command.getCommandSignature().equals("unknown") && command.isClientAccess()) {
                helpMap.put(command.getCommandSignature(), command.getCommandDescription());
            }
        }
        return new ResponseHelpMap(true, "", helpMap);
    }
}
