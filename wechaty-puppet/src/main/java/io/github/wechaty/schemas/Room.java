package io.github.wechaty.schemas;

import lombok.Data;
import lombok.ToString;

import java.util.List;

public class Room {

    @Data
    @ToString
    public static class RoomMemberQueryFilter {
        private String name;
        private String roomAlias;
        private String contactAlias;
    }

    @Data
    @ToString
    public static class RoomQueryFilter{
        private String id;
        private String topic;
    }


    @Data
    @ToString
    public static class RoomPayload{
        private String id;
        private String topic;
        private String avatar;
        private List<String> memberIdList;
        private String ownerId;
        private List<String> adminIdList;
    }

    @Data
    @ToString
    public static class RoomMemberPayload{
        private String id;
        private String roomAlias;
        private String inviterId;
        private String avatar;
        private String name;

    }

}
