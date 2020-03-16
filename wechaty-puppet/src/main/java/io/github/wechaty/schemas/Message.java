package io.github.wechaty.schemas;

import lombok.Data;
import lombok.ToString;

import java.util.List;

public class Message {


    enum MessageType{

        Unknown (0),
        Attachment(1),     // Attach(6),
        Audio(2),          // Audio(1), Voice(34)
        Contact(3),        // ShareCard(42)
        ChatHistory(4),    // ChatHistory(19)
        Emoticon(5),       // Sticker: Emoticon(15), Emoticon(47)
        Image(6),          // Img(2), Image(3)
        Text(7),           // Text(1)
        Location(8),       // Location(48)
        MiniProgram(9),    // MiniProgram(33)
        Transfer(10),       // Transfers(2000)
        RedEnvelope(11),    // RedEnvelopes(2001)
        Recalled(12),       // Recalled(10002)
        Url(13),            // Url(5)
        Video(14);          // Video(4), Video(43)

        private int code;

        MessageType(int code) {
            this.code = code;
        }
    }

    @Data
    @ToString
    public static class MessagePayload{

        private String id;
        private List<String> mentionIdList;
        private String text;
        private Long timestamp;
        private MessageType type;

        private String fromId;
        private String roomId;
        private String toId;
    }

    @Data
    @ToString
    public static class MessageQueryFilter{

        private String fromId;
        private String id;

        private String roomId;
        private String text;
        private String toId;

        private MessageType type;

    }

}
