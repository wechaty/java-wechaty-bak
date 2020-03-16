package io.github.wechaty;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import io.github.wechaty.Utils.FutureUtils;
import io.github.wechaty.listener.DongListener;
import io.github.wechaty.listener.FriendshipListener;
import io.github.wechaty.schemas.*;
import io.github.wechaty.schemas.Friendship.FriendshipPayload;
import io.github.wechaty.schemas.Room.RoomMemberPayload;
import io.github.wechaty.schemas.Room.RoomPayload;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.Future;
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

    public Puppet on(String event, DongListener listener){
        return this;
    }

    public Puppet on(String event, FriendshipListener listener){
        eb.consumer(event,t ->{
            String friendshipId = (String) t.body();
            listener.execute(friendshipId);
        });
        return this;
    }

    public abstract Future<Void> start();

    public abstract Future<Void> end();

    protected Future<Void> login(String userId) {
        log.info("Puppet login in ({})",userId);
        return CompletableFuture.runAsync(()->{
            if(StringUtils.isNotBlank(userId)){
                throw new RuntimeException("must logout first before login again!");
            }
            this.id = userId;
            this.emit("login",userId);
        });
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

        try {
            List<String> roomList = roomList().get();

            List<CompletableFuture<RoomPayload>> collect = roomList.stream()
                .map(this::roomPayload)
                .map(FutureUtils::toCompletable)
                .collect(Collectors.toList());

            CompletableFuture<List<RoomPayload>> resultRoomIdList = FutureUtils.sequence(collect);

            List<RoomPayload> roomPayloadList = resultRoomIdList.get();

            List<String> result = roomPayloadList.stream().filter(t -> {
                List<String> memberIdList = t.getMemberIdList();
                return memberIdList.contains(contactId);
            }).map(RoomPayload::getId).collect(Collectors.toList());

            return CompletableFuture.completedFuture(result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return CompletableFuture.completedFuture(Lists.newArrayList());
    }

    public Future<Void> contactPayloadDirty(String contactId){
        cacheRoomPayload.invalidate(contactId);
        return CompletableFuture.completedFuture(null);
    }

//    public Future<List<String>> contactSearch(String query,List<String> searchIdList){
//        log.info("query {},searchIdList {}",query,searchIdList);
//
//        Promise<List<String>> promise = Promise.promise();
//
//        if(CollectionUtils.isEmpty(searchIdList)){
//            Future<List<String>> listFuture = contactList();
//            searchIdList= listFuture.get();
//        }
//
//        log.info("searchIdList length {}",searchIdList.size());
//
//        if(query == null){
//            return promise.future();
//        }
//
//        Contact.ContactQueryFilter nameFilter = new Contact.ContactQueryFilter();
//        Contact.ContactQueryFilter aliasFilter = new Contact.ContactQueryFilter();
//        nameFilter.setName(query);
//        aliasFilter.setAlias(query);
//        Future<List<String>> future = contactSearch(nameFilter, searchIdList);
//        Future<List<String>> future1 = contactSearch(aliasFilter, searchIdList);
//
//        CompositeFuture compositeFuture = CompositeFuture.all(future,future1);
//
//        List<List<String>> list = compositeFuture.list();
//        List<String> collect = list.stream().flatMap(Collection::stream).collect(Collectors.toList());
//
//        promise.complete(collect);
//        return promise.future();
//    }

    //TODO
    public Future<List<String>> contactSearch(Contact.ContactQueryFilter query,List<String> searchIdList){
//        log.info("query {},searchIdList {}",query,searchIdList);
//
//        Promise<List<String>> promise = Promise.promise();
//
//        if(CollectionUtils.isEmpty(searchIdList)){
//            Future<List<String>> listFuture = contactList();
//            searchIdList= listFuture.result();
//        }
//
//        log.info("searchIdList length {}",searchIdList.size());
//
//        if(query == null){
//            promise.complete(searchIdList);
//            return promise.future();
//        }
//
//        return promise.future();
//    }
//
//    public Future<Boolean> contactValidate(String contactId){
//        log.info("contactValidate {} base class just return `true`", contactId);
//        Promise<Boolean> promise = Promise.promise();
//        promise.complete(true);
//        return promise.future();
        return null;
    }

    protected Future<Contact.ContactPayload> contactPayloadCache(String contactId){

//        Preconditions.checkNotNull(contactId);
//
//        Promise<Contact.ContactPayload> promise = Promise.promise();
//
//        Contact.ContactPayload contactPayload = cacheContactPayload.get(contactId, key -> {
//            Future<Contact.ContactPayload> contactPayloadFuture = contactPayload(key);
//            return contactPayloadFuture.result();
//        });
//
//
//        if(contactPayload == null){
//            promise.complete();
//            log.info("contactPayload {} cache MISS",contactId);
//        }else {
//            promise.complete(contactPayload);
//        }
//
//        return promise.future();
        return null;
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
//        Preconditions.checkNotNull(roomId);
//        Promise<RoomPayload> promise = Promise.promise();
//        RoomPayload roomPayload = roomPayloadCache(roomId);
//        promise.complete(roomPayload);
//        return promise.future();
        return null;
    }

    protected RoomPayload roomPayloadCache(String roomId){

        Preconditions.checkNotNull(roomId);
        return cacheRoomPayload.get(roomId, key -> {
                Future<Object> future = roomRawPayload(key);
            Object result = null;
            try {
                result = future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            Future<RoomPayload> roomPayloadFuture = roomRawPayloadParser(result);
            try {
                return roomPayloadFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        });


    }

    /**
     *
     * Friendship
     *
     */

    public abstract Future<Void> friendshipAccept(String friendshipId);
    public abstract Future<Void> friendshipAdd(String contractId,String hello);
    public abstract Future<String> friendshipSearchPhone(String phone);
    public abstract Future<String> friendshipSearchWeixin(String weixin);

    public abstract Future<Object> friendshipRwaPayload(String friendshipId);
    public abstract Future<FriendshipPayload> friendshipRawPayloadParser(Object rwwPayload);


    public Future<String> friendshipSearch(Friendship.FriendshipSearchCondition condition){
        log.info("friendshipSearch{}",condition);

        Preconditions.checkNotNull(condition);

        if(StringUtils.isNotEmpty(condition.getPhone())){
            return friendshipSearchPhone(condition.getPhone());
        }else {
            return friendshipSearchWeixin(condition.getWeixin());
        }
    }

}
