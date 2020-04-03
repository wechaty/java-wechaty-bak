package io.github.wechaty.schemas;

import java.util.List;

import org.checkerframework.checker.units.qual.s;

import io.vertx.core.Vertx;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
public class Friendship {

    enum FriendshipType{
        Unkonwn(0),
        Confirm(1),
        Receive(2),
        Verify(3);
        private int code;
        private FriendshipType(int code){
            this.code = code;
        }
    }

    enum FriendshipSceneType{

        QQ(1),
        Email(2),
        Weixin(3),
        QQtbd (12),
        Room(14),
        Phone(15),
        Card(17),
        Location(18),
        Bottle(25),
        Shaking(29),
        QRCode(30);

        private int code;
        private FriendshipSceneType(int code){
            this.code = code;
        }

    }

    public interface FriendshipPayload{

    }

    @Data
    @ToString
    public static class FriendshipPayloadBase{
        private String id;
        private String contactId;
        private String hello;
        private Long timestamp;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @ToString
    public static class FriendshipPayloadConfirm extends FriendshipPayloadBase implements FriendshipPayload{
        private FriendshipType type = FriendshipType.Confirm;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @ToString
    public static class FriendshipPayloadReceive extends FriendshipPayloadBase implements FriendshipPayload{
        private FriendshipSceneType scene;
        private String stranger;
        private String ticket;
        private FriendshipType type = FriendshipType.Receive;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @ToString
    public static class FriendshipPayloadVerify extends FriendshipPayloadBase implements FriendshipPayload{
        private FriendshipType type = FriendshipType.Verify;
    }

    @Data
    @ToString
    public static class FriendshipSearchCondition{
        private String phone;
        private String weixin;
    }

    @Data
    @ToString
    public static class FriendshipQueryFilter{
        private List<FriendshipSearchCondition> list;
    }
}
