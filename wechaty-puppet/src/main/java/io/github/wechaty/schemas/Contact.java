package io.github.wechaty.schemas;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Contact {

    enum ContractGender{

        Unknown(0),
        Male(1),
        Female(2);

        int code;

        ContractGender(int code) {
            this.code = code;
        }
    }

    enum ContractType {
        Unknow(0),
        Personal(1),
        Official(2);
        int code;

        ContractType(int code) {
            this.code = code;
        }
    }

    @Data
    @ToString
    public static class ContactQueryFilter{

        private String alias;
        private String oid;
        private String name;
        private String weixin;

    }

    @Data
    @ToString
    public static class ContactPayload{

        private String id;
        private ContractGender gender;
        private ContractType type;
        private String name;
        private String avatar;

        private String address;
        private String alias;
        private String city;
        private Boolean friend;
        private String province;
        private String signature;
        private Boolean star;

        private String weixin;

    }

}
