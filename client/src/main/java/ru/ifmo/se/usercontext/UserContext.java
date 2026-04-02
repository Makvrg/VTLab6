package ru.ifmo.se.usercontext;

import ru.ifmo.se.dto.entity.UserDto;

public class UserContext {

    private static UserType currentUserType = UserType.GUEST;
    private static UserDto currentUser;

    public static UserType getCurrentUserType() {
        return currentUserType;
    }

    public static UserDto getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(UserDto user) {
        if (user != null) {
            currentUser = user;
            currentUserType = UserType.AUTH_USER;
        } else {
            currentUser = null;
            currentUserType = UserType.GUEST;
        }
    }
}
