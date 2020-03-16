package io.github.wechaty;

import lombok.Data;
import lombok.ToString;

/**
 * @author Zhengxin
 */
@Data
@ToString
public class PuppetOptions {

    private String endPoint;
    private Long timeout;
    private String token;

    private String puppetOptionKey;

}
