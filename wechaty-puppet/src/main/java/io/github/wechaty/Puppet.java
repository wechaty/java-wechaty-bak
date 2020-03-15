package io.github.wechaty;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.base.Preconditions;
import io.github.wechaty.schemas.*;
import io.github.wechaty.schemas.Room.RoomMemberPayload;
import io.github.wechaty.schemas.Room.RoomPayload;
import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.lambda.function.Function1;
import org.jooq.lambda.function.Function2;
import org.jooq.lambda.function.Function3;
import org.jooq.lambda.function.Function4;
import sun.awt.FullScreenCapable;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
public abstract class Puppet {

    protected Cache<String, Contact.ContactPayload> cacheContactPayload;
    protected Cache<String, Friendship.FriendshipPayload> cacheFriendshipPayload;
    protected Cache<String, Message.MessagePayload> cacheMessagePayload;

    protected Cache<String,RoomPayload> cacheRoomPayload;
    protected Cache<String,RoomMemberPayload> cacheRoomMemberPayload;

    protected Cache<String,RoomInvitationPayload> cacheRoomInvitationPayload;

    private Vertx vertx;
    private EventBus eb;
    private AtomicLong count = new AtomicLong();
    private String id;
    private PuppetOptions puppetOptions;


    private static Map<String, Method> methodMap = new ConcurrentHashMap<>();
    private List<RoomPayload> roomPayloadList;

    /**
     *
     */
    public Puppet(){
        PuppetOptions puppetOptions = new PuppetOptions();
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

    public Puppet on(String event, Function2 function1){
        return this;
    }


    public Puppet on(String event, Function3 function1){
        return this;
    }

    public Puppet on(String event, Function1 function1){
        return this;
    }

    public Puppet on(String event, Function4 function1){
        return this;
    }

    public abstract Future<Void> start();

    public abstract Future<Void> end();

    protected Future<Void> login(String userId) {
        log.info("Puppet login in ({})",userId);
        Promise<Void> promise = Promise.promise();
        if(StringUtils.isNotBlank(userId)){
            throw new RuntimeException("must logout first before login again!");
        }
        this.id = userId;
        this.emit("login",userId);
        promise.complete();
        return promise.future();
    }
    public abstract Future<Void> logout();

    public String selfId(){
        return id;
    }

    public Vertx getVertx(){
        return vertx;
    }

    private void initCache(){

        cacheContactPayload = Caffeine.newBuilder().build();




    }

    /**
     * 抽象方法
     *
     */

    public abstract void ding(String data);

    /**
     * contactSelf
     */

    public abstract Future<Void> contractSelfName(String name);
    public abstract Future<String> contactSelfQRCode();
    public abstract Future<Void> contactSelfSignature(String signature);

    /**
     *
     * Tag
     *  tagContactAdd - add a tag for a Contact. Create it first if it not exist.
     *  tagContactRemove - remove a tag from the Contact
     *  tagContactDelete - delete a tag from Wechat
     *  tagContactList(id) - get tags from a specific Contact
     *  tagContactList() - get tags from all Contacts
     *
     */

    public abstract Future<Void> tagContactAdd(String tagId,String contactId);
    public abstract Future<Void> tagContactDelete(String tagId);
    public abstract Future<List<String>> tagContactList(String contactId);
    public abstract Future<List<String>> tagContactList();
    public abstract Future<Void> tagContactRemove(String tagId,String contactId);

    /**
     *
     * Contact
     *
     */

    public abstract Future<String> contactAlias(String contactId);
    public abstract Future<Void> contactAlias(String contactId,String alias);

    public abstract Future<File> contactAvatar(String contactId);
    public abstract Future<Void> contactAvatar(String contactId,File file);

    public abstract Future<List<String>> contactList();

    protected abstract Future<Object> contactRawPayload(String contractId);
    protected abstract Future<Contact.ContactPayload> contactRawPlayloadParser(Object rawPayload);

    public Future<List<String>> contactRoomList(String contactId){

        log.info("contractId is {}",contactId);

        Promise<List<String>> promise = Promise.promise();

        Future<List<String>> future = roomList();

        List<String> roomIdList = future.result();

        List<Future<RoomPayload>> collect = roomIdList.stream().map(this::roomPayload).collect(Collectors.toList());

        CompositeFuture all = CompositeFuture.all(new ArrayList<>(collect));

        List<RoomPayload> roomPayloadList = all.list();

        List<String> collect1 = roomPayloadList.stream().filter(t -> {
            List<String> memberIdList = t.getMemberIdList();
            return memberIdList.contains(contactId);
        }).map(RoomPayload::getId).collect(Collectors.toList());

        promise.complete(collect1);
        return promise.future();
    }

    public Future<Void> contactPayloadDirty(String contactId){
        Promise<Void> promise = Promise.promise();
        log.info("contractId is {}",contactId);
        cacheRoomPayload.invalidate(contactId);
        promise.complete();
        return promise.future();
    }

    public Future<List<String>> contactSearch(String query,List<String> searchIdList){
        log.info("query {},searchIdList {}",query,searchIdList);

        Promise<List<String>> promise = Promise.promise();

        if(CollectionUtils.isEmpty(searchIdList)){
            Future<List<String>> listFuture = contactList();
            searchIdList= listFuture.result();
        }

        log.info("searchIdList length {}",searchIdList.size());

        if(query == null){
            promise.complete(searchIdList);
            return promise.future();
        }

        Contact.ContactQueryFilter nameFilter = new Contact.ContactQueryFilter();
        Contact.ContactQueryFilter aliasFilter = new Contact.ContactQueryFilter();
        nameFilter.setName(query);
        aliasFilter.setAlias(query);
        Future<List<String>> future = contactSearch(nameFilter, searchIdList);
        Future<List<String>> future1 = contactSearch(aliasFilter, searchIdList);

        CompositeFuture compositeFuture = CompositeFuture.all(future,future1);

        List<List<String>> list = compositeFuture.list();
        List<String> collect = list.stream().flatMap(Collection::stream).collect(Collectors.toList());

        promise.complete(collect);
        return promise.future();
    }

    //TODO
    public Future<List<String>> contactSearch(Contact.ContactQueryFilter query,List<String> searchIdList){
        log.info("query {},searchIdList {}",query,searchIdList);

        Promise<List<String>> promise = Promise.promise();

        if(CollectionUtils.isEmpty(searchIdList)){
            Future<List<String>> listFuture = contactList();
            searchIdList= listFuture.result();
        }

        log.info("searchIdList length {}",searchIdList.size());

        if(query == null){
            promise.complete(searchIdList);
            return promise.future();
        }

        return promise.future();
    }

    public Future<Boolean> contactValidate(String contactId){
        log.info("contactValidate {} base class just return `true`", contactId);
        Promise<Boolean> promise = Promise.promise();
        promise.complete(true);
        return promise.future();
    }

    protected Future<Contact.ContactPayload> contactPayloadCache(String contactId){

        Preconditions.checkNotNull(contactId);

        Promise<Contact.ContactPayload> promise = Promise.promise();

        Contact.ContactPayload contactPayload = cacheContactPayload.get(contactId, key -> {
            Future<Contact.ContactPayload> contactPayloadFuture = contactPayload(key);
            return contactPayloadFuture.result();
        });


        if(contactPayload == null){
            promise.complete();
            log.info("contactPayload {} cache MISS",contactId);
        }else {
            promise.complete(contactPayload);
        }

        return promise.future();
    }

    protected Future<Contact.ContactPayload> contactPayload(String contactId){
        Preconditions.checkNotNull(contactId);

        Promise<Contact.ContactPayload> promise = Promise.promise();

        Future<Object> future = contactRawPayload(contactId);
        return contactRawPlayloadParser(future);
    }

    /**
     *
     * Room
     *
     */
    public abstract Future<Void> roomAdd(String roomId,String contactId);
    public abstract Future<File> roomAvatar(String roomId);

    public abstract Future<String> roomCreate(List<String> contactIdList,String topic);
    public abstract Future<String> roomDel(String roomId,String contactId);

    public abstract Future<List<String>> roomList();
    public abstract Future<String> roomQRCode(String roomId);
    public abstract Future<Void> roomQuit(String roomId);

    public abstract Future<String> roomTopic(String roomId);
    public abstract Future<Void> roomTopic(String roomId,String topic);

    public abstract Future<Object> roomRawPayload(String roomId);
    public abstract Future<RoomPayload> roomRawPayloadParser(Object any);


    public Future<RoomPayload> roomPayload(String roomId){
        Preconditions.checkNotNull(roomId);
        Promise<RoomPayload> promise = Promise.promise();
        RoomPayload roomPayload = roomPayloadCache(roomId);
        promise.complete(roomPayload);
        return promise.future();
    }

    protected RoomPayload roomPayloadCache(String roomId){

        Preconditions.checkNotNull(roomId);
        return cacheRoomPayload.get(roomId, key -> {
            Future<Object> future = roomRawPayload(key);
            Object result = future.result();
            Future<RoomPayload> roomPayloadFuture = roomRawPayloadParser(result);
            return roomPayloadFuture.result();
        });
    }






}
