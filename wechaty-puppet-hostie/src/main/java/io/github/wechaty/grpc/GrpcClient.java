package io.github.wechaty.grpc;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.wechaty.Puppet;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.core.json.jackson.JacksonCodec;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class GrpcClient extends Puppet {

    private static final String CHATIE_ENDPOINT = "wss://api.chatie.io/v0/websocket/token/";
    private static final String PROTOCOL = "puppet-hostie|0.0.1";

    private Future<String> discoverHostieIp(String token) {

        Promise<String> promise = Promise.promise();

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
                    promise.complete(map.get("payload"));
            }

            @Override
            public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                log.info("code {}, reason {}",code,reason);
            }

            @Override
            public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
                log.error("response {}",response,t);
                promise.fail(t);
                super.onFailure(webSocket, t, response);
            }
        });

        return promise.future();
    }


    @Override
    public Promise<Void> start() {
        return null;
    }

    @Override
    public Promise<Void> end() {
        return null;
    }

    @Override
    public Promise<Void> logout() {
        return null;
    }

    public static void main(String[] args) {

        GrpcClient grpcClient = new GrpcClient();

        Future<String> future = grpcClient.discoverHostieIp("test");

        System.out.println(future.result());

    }
}
