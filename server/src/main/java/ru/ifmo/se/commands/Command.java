package ru.ifmo.se.commands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.ifmo.se.dto.request.Request;
import ru.ifmo.se.dto.response.Response;

@Getter
@RequiredArgsConstructor
public abstract class Command {

    protected final String commandSignature;
    protected final String commandDescription;
    protected final boolean clientAccess;

    public abstract Response execute(Request request);
}
