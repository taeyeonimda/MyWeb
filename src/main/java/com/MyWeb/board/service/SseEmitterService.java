package com.MyWeb.board.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseEmitterService {
    private final List<SseEmitter> emitters = new ArrayList<>();
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

}
