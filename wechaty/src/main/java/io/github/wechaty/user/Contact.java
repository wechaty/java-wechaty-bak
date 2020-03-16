package io.github.wechaty.user;

import io.github.wechaty.type.Sayable;

import java.util.concurrent.Future;

public class Contact implements Sayable {
    @Override
    public Future<Void> say(String text, Contact contact) {
        return null;
    }
}
