package ru.ifmo.se.commands;

import ru.ifmo.se.dto.request.Request;
import ru.ifmo.se.dto.response.Response;

public class UnknownCommand extends Command {

    public UnknownCommand() {
        super("unknown",
                "вызывается автоматически при вводе команды, "
                        + "которая не поддерживается программой", false
        );
    }

    @Override
    public Response execute(Request request) {
        return new Response(
                true,
                String.format(
                        "Передана неизвестная команда: %s", request.getCommandName()
                )
        );
    }
}
