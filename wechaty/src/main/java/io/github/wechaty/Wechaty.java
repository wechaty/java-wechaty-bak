package io.github.wechaty;

import io.github.wechaty.grpc.GrpcClient;
import io.github.wechaty.listener.DongListener;
import io.github.wechaty.listener.FriendshipListener;


public class Wechaty {

    private Wechaty() {
        puppet = new GrpcClient();
    }

    private static Puppet puppet;

    public static Wechaty instance(){
        return new Wechaty();
    }

    public Wechaty on(String event, DongListener listener){
        puppet.on(event,listener);
        return this;
    }

    public Wechaty on(String event, FriendshipListener listener){
        puppet.on(event,listener);
        return this;
    }

    public Wechaty start(){
        return this;
    }

}
