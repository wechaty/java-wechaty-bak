package io.github.wechaty.listener;


@FunctionalInterface
public interface LogoutListener {
    void execute(String contactId,String reason);
}
