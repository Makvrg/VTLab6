package ru.ifmo.se.mapper;

import ru.ifmo.se.dto.entity.UserDto;
import ru.ifmo.se.entity.User;

public class UserMapper {

    public static UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getUsername(), null);
    }

    public static User toEntity(UserDto userDto) {
        return new User(null, userDto.getUsername(), null, null);
    }
}
