package io.github.wechaty.listener;

import java.util.List;

@FunctionalInterface
public interface RoomJoinListener {

    void execute(String roomId, List<String> inviteeIdList,String inviterId,Long number);

}
