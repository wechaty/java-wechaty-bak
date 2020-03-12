package io.github.wechaty.schemas;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RoomMemberQueryFilter {

    private String name;

    private String roomAlias;

    private String contactAlias;

}
