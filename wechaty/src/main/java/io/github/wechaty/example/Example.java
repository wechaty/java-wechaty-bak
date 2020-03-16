package io.github.wechaty.example;

import io.github.wechaty.Wechaty;
import io.github.wechaty.listener.FriendshipListener;

public class Example {

    public static void main(String[] args) {

        Wechaty.instance()
            .on("test",(FriendshipListener) friendshipId1 -> System.out.println(friendshipId1))
            .start();

    }

}
