package io.github.wechaty.grpc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import io.github.wechaty.Puppet;
import io.github.wechaty.schemas.Contact;
import io.github.wechaty.schemas.Friendship;
import io.github.wechaty.schemas.Room;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.core.json.jackson.JacksonCodec;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Slf4j
public class GrpcClient extends Puppet {

    private static final String CHATIE_ENDPOINT = "wss://api.chatie.io/v0/websocket/token/";
    private static final String PROTOCOL = "puppet-hostie|0.0.1";

    private Future<String> discoverHostieIp(String token) {


        OkHttpClient client = new OkHttpClient.Builder()
            .build();
        //构造request对象
        Request request = new Request.Builder()
            .url(CHATIE_ENDPOINT + token)
            .build();

        Map<String, String> hostieMap = new HashMap<>();
        hostieMap.put("name", "hostie");

        client.newWebSocket(request, new WebSocketListener() {

            @Override
            public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
                webSocket.send(Json.encode(hostieMap));
            }

            @Override
            public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                Map<String, String> map = JacksonCodec.decodeValue(text, new TypeReference<Map<String, String>>() {});
            }

            @Override
            public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                log.info("code {}, reason {}",code,reason);
            }

            @Override
            public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
                log.error("response {}",response,t);
                super.onFailure(webSocket, t, response);
            }
        });

        return CompletableFuture.completedFuture("test");
    }


    @Override
    public Future<Void> start() {
        return null;
    }

    @Override
    public Future<Void> end() {
        return null;
    }

    @Override
    public Future<Void> logout() {
        return null;
    }

    @Override
    public void ding(String data) {

    }

    @Override
    public Future<Void> contractSelfName(String name) {
        return null;
    }

    @Override
    public Future<String> contactSelfQRCode() {
        return null;
    }

    @Override
    public Future<Void> contactSelfSignature(String signature) {
        return null;
    }

    @Override
    public Future<Void> tagContactAdd(String tagId, String contactId) {
        return null;
    }

    @Override
    public Future<Void> tagContactDelete(String tagId) {
        return null;
    }

    @Override
    public Future<List<String>> tagContactList(String contactId) {
        return null;
    }

    @Override
    public Future<List<String>> tagContactList() {
        return null;
    }

    @Override
    public Future<Void> tagContactRemove(String tagId, String contactId) {
        return null;
    }

    @Override
    public Future<String> contactAlias(String contactId) {
        return null;
    }

    @Override
    public Future<Void> contactAlias(String contactId, String alias) {
        return null;
    }

    @Override
    public Future<File> contactAvatar(String contactId) {
        return null;
    }

    @Override
    public Future<Void> contactAvatar(String contactId, File file) {
        return null;
    }

    @Override
    public Future<List<String>> contactList() {
        return null;
    }

    @Override
    protected Future<Object> contactRawPayload(String contractId) {
        return null;
    }

    @Override
    protected Future<Contact.ContactPayload> contactRawPlayloadParser(Object rawPayload) {
        return null;
    }

    @Override
    public Future<Void> roomAdd(String roomId, String contactId) {
        return null;
    }

    @Override
    public Future<File> roomAvatar(String roomId) {
        return null;
    }

    @Override
    public Future<String> roomCreate(List<String> contactIdList, String topic) {
        return null;
    }

    @Override
    public Future<String> roomDel(String roomId, String contactId) {
        return null;
    }

    @Override
    public Future<List<String>> roomList() {

        CompletableFuture<List<String>> future = CompletableFuture.supplyAsync(()->{
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return Lists.newArrayList("1","2");
        });

        return future;
    }

    @Override
    public Future<String> roomQRCode(String roomId) {
        return null;
    }

    @Override
    public Future<Void> roomQuit(String roomId) {
        return null;
    }

    @Override
    public Future<String> roomTopic(String roomId) {
        return null;
    }

    @Override
    public Future<Void> roomTopic(String roomId, String topic) {
        return null;
    }

    @Override
    public Future<Object> roomRawPayload(String roomId) {
        return null;
    }

    @Override
    public Future<Room.RoomPayload> roomRawPayloadParser(Object any) {
        return null;
    }

    @Override
    public Future<Void> friendshipAccept(String friendshipId) {
        return null;
    }

    @Override
    public Future<Void> friendshipAdd(String contractId, String hello) {
        return null;
    }

    @Override
    public Future<String> friendshipSearchPhone(String phone) {
        return null;
    }

    @Override
    public Future<String> friendshipSearchWeixin(String weixin) {
        return null;
    }

    @Override
    public Future<Object> friendshipRwaPayload(String friendshipId) {
        return null;
    }

    @Override
    public Future<Friendship.FriendshipPayload> friendshipRawPayloadParser(Object rwwPayload) {
        return null;
    }
}
