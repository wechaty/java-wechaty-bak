package io.github.wechaty.schemas;


import lombok.Data;
import lombok.ToString;

/**
 * @author Zhengxin
 */
@Data
@ToString
public class UrlLinkPayload {

    private String description;

    private String thumbnailUrl;

    private String title;

    private String url;

}
