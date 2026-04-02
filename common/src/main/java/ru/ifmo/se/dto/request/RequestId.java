package ru.ifmo.se.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ifmo.se.dto.entity.UserDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestId extends Request {

    public RequestId(String commandName,
                     Long id,
                     UserDto userDto) {
        super(commandName, userDto);
        this.id = id;
    }

    private Long id;
}
