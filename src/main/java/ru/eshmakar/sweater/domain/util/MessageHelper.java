package ru.eshmakar.sweater.domain.util;

import ru.eshmakar.sweater.domain.User;

public abstract class MessageHelper {
    public static String getAuthorName(User author) {
        return author != null ? author.getUsername() : "<none>";
    }
}