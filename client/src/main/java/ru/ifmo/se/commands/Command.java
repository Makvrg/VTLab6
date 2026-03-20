package ru.ifmo.se.commands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.ifmo.se.dto.request.Request;
import ru.ifmo.se.dto.response.Response;
import ru.ifmo.se.io.input.readers.Reader;

@Getter
@RequiredArgsConstructor
public abstract class Command {

    protected final String commandSignature;
    protected final String commandDescription;

    public abstract Request makeRequest(String[] inputArgs, Reader reader);
    public abstract void handleResponse(Response response);
    protected abstract void validateArgs(String[] inputArgs);
}
