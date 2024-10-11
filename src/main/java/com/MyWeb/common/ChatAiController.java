package com.MyWeb.common;

import lombok.extern.log4j.Log4j2;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("ai")
@RestController
@Log4j2
public class ChatAiController {
    private final OpenAiChatModel openAiChatModel;
    //    private final VertexAiGeminiChatModel vertexAiGeminiChatModel;
    public ChatAiController(OpenAiChatModel openAiChatModel) {
        this.openAiChatModel = openAiChatModel;
    }

    @GetMapping("/chat")
    @ResponseBody
    public Map<String, String> chat(@RequestParam("message") String message){
        Map<String,String> response = new HashMap<>();
        log.info("Message => {} ",message);
        String openAiResponse = openAiChatModel.call(message);
        log.info("openAiResponse => {} ",openAiResponse);

        response.put("openAI 응답 ", openAiResponse);


        return response;
    }
}
