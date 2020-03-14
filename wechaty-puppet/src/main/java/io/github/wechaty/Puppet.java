package io.github.wechaty;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import io.github.wechaty.schemas.Contact;
import io.github.wechaty.schemas.Friendship;
import io.github.wechaty.schemas.Message;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;

import java.util.EventListener;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public abstract class Puppet {

    protected static Cache<String, Contact.ContactPayload> cacheContactPayload;
    protected static Cache<String, Friendship.FriendshipPayload> cacheFriendshipPayload;
    protected static Cache<String, Message.MessagePayload> cacheMessagePayload;


//    protected static cacheRoomInvitationPayload
    private Vertx vertx;
    private EventBus eb;
    private AtomicLong count = new AtomicLong();
    private String id;
    private PuppetOptions puppetOptions;

    /**
     *
     */
    public Puppet(){
        this.vertx = Vertx.vertx();
        this.eb = vertx.eventBus();
    }

    public Puppet(PuppetOptions puppetOptions){
        this.puppetOptions = puppetOptions;
        this.vertx = Vertx.vertx();
        this.eb = vertx.eventBus();
    }

    public void emit(String event,Object ... args){
        eb.publish(event, args);
    }
    public Puppet on(String event, EventListener eventListener){
        return this;
    }

    public abstract Promise<Void> start();

    public abstract Promise<Void> end();

    protected Promise<Void> login(String userId) {
        log.info("Puttet login in ({})",userId);
        if(StringUtils.isNotBlank(userId)){
            throw new RuntimeException("must logout first before login again!");
        }
        this.id = userId;
        this.emit("login",userId);
        return Promise.promise();
    }

    public abstract Promise<Void> logout();

    public String selfId(){
        return id;
    }

    public Vertx getVertx(){
        return vertx;
    }
}
