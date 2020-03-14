package io.github.wechaty.schemas;

import lombok.Data;
import lombok.ToString;

public class MinProgram {

    @Data
    @ToString
    static class MiniProgramPayload{

        private String appId;
        private String description;
        private String pagePath;

        private String thumbUrl;
        private String title;

        private String username;
        private String thumbKey;

    }

}
