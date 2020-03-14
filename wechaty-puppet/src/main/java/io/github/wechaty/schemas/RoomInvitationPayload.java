package io.github.wechaty.schemas;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author Zhengxin
 */
@Data
@ToString
public class RoomInvitationPayload {

    private String id;

    private String inviterId;

    private String topic;

    private String avatar;

    private String invitation;

    private Integer memberCount;

    private List<String> memberIdList;

    private Long timestamp;

    private String receiverId;

}
