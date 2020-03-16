package io.github.wechaty.listener;

/**
 * @author Zhengxin
 */
@FunctionalInterface
public interface FriendshipListener {

    void execute(String friendshipId);

}
