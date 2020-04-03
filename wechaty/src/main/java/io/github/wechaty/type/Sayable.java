package io.github.wechaty.type;

import io.github.wechaty.user.Contact;

import java.util.concurrent.Future;

public interface Sayable{

    Future<Void> say(String text, Contact contact);

}
