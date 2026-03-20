package ru.ifmo.se.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestId extends Request {

    public RequestId(String commandName,
                     Long id) {
        super(commandName);
        this.id = id;
    }

    private Long id;
}
