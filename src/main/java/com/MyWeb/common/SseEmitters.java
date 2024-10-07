package com.MyWeb.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Slf4j
public class SseEmitters {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<Long, List<SseEmitter>> emittersByBoard = new ConcurrentHashMap<>();

    public SseEmitter add(Long boardId, SseEmitter emitter) {
        this.emittersByBoard.computeIfAbsent(boardId, key ->
                new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> {
            this.emittersByBoard.get(boardId).remove(emitter);
        });

        emitter.onTimeout(() -> {
            emitter.complete();
        });

        return emitter;
    }


    public void countLike(Long boardId, int boardSum) {
        List<SseEmitter> emitters = emittersByBoard.get(boardId);
        if (emitters != null) {
            log.info("CountLike Emitters : {}",emitters);
            emitters.forEach(emitter -> {

                try {
                    log.info("CountLike emitter : {}",emitter);

                    emitter.send(SseEmitter.event()
                            .name("count")
                            .data(boardSum));
                } catch (IOException e) {
                    emitter.complete();
                }
            });
        }
    }
}
