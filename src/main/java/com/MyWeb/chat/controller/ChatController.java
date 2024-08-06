package com.MyWeb.chat.controller;

import com.MyWeb.chat.entity.Chat;
import com.MyWeb.chat.service.ChatService;
import com.MyWeb.common.FileRename;
import com.MyWeb.user.entity.CustomUserDetails;
import com.MyWeb.user.entity.CustomUserOAuth;
import com.MyWeb.user.entity.User;
import com.MyWeb.user.service.UserService;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@Slf4j
public class ChatController {
    private final Map<Integer,Long> memberList = new HashMap<>();
    private final Map<String, List<Integer>> memberMap = new HashMap<>();
    private final UserService userService;
    private Integer sessionNo= 1;
    private Long sessionId = 0L;

    @Value("${file.chatImg}")
    private String uploadDir;

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    private final FileRename fileRename;

    public ChatController(ChatService chatService, SimpMessagingTemplate messagingTemplate, FileRename fileRename, UserService userService) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
        this.fileRename = fileRename;
        this.userService = userService;
    }

//    @MessageMapping("/chat")
//    @SendTo("/topic/messages")
//    public void handleMessage(@Payload String message, Principal principal, Message<byte[]> messages){
//        if(principal == null){
//            System.out.println("User is not authenticated");
//            return;
//        }
//
//        User currentUser = null;
//
//        Authentication authentication = (Authentication) principal;
//
//        Object principalObj = authentication.getPrincipal();
//
//        if(principalObj instanceof CustomUserDetails customUserDetails){
//            currentUser = customUserDetails.getUser();
//        }else if(principalObj instanceof CustomUserOAuth customUserOAuth){
//            currentUser = customUserOAuth.getUser();
//        }
//
//
//        Long userId = Objects.requireNonNull(currentUser).getId();
//
//        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(messages);
//
//        String sessionIds = headerAccessor.getSessionId();
//
//        log.info("채팅 Message {}",message);
//        log.info("채팅 Messages {}",message);
//        log.info("챗컨트롤러 Authentication {}",authentication);
//
//        JsonParser parser = new JsonParser();
//        JsonElement element = parser.parseString(message);
//        String type = element.getAsJsonObject().get("type").getAsString();
//        String msg = element.getAsJsonObject().get("msg").getAsString();
//        String roomNo = element.getAsJsonObject().get("roomNo").getAsString();
//        Long senderId = element.getAsJsonObject().get("senderId").getAsLong();
//
//        switch (type){
//            case "enter":
//                log.info("TYPE :: {}",type);
//
//                //지난 대화 출력c
//                List<Chat> crList = chatService.getChatReord(roomNo);
//
//                if(!crList.isEmpty()){
//                    for(Chat cr : crList){
//                        StringBuilder sendMsg = new StringBuilder();
//
//                        log.debug("챗컨트롤러 USER ID {}",userId);
//                        log.debug("챗컨트롤러 getMember {}",cr.getChatMember());
//
//                        if(cr.getChatMember().equals(String.valueOf(userId))){
//                            sendMsg.append("<div class='chat right'>").append(cr.getChatContent()).append("</div>");
//                            if(cr.getFilepath() !=null){
//                                sendMsg.append("<div class='chat right'>").append("<img alt='사진' src='/uploads/chat/").append(cr.getFilepath()).append("'>").append("</div>");
//                            }
//                        }else{
//                            sendMsg.append("<div class='chat left'><span class='chatId'>")
//                                    .append(cr.getChatMember()).append(" :</span>").append(cr.getChatContent()).append("</div>");
//                            if(cr.getFilepath() !=null){
//                                sendMsg.append("<div class='chat left'><span class='chatId'>")
//                                        .append(cr.getChatMember()).append(" :</span>").append("<img src='/uploads/chat/").append(cr.getFilepath()).append("'>").append("</div>");
//                            }
//                        }
//                    messagingTemplate.convertAndSend("/topic/"+roomNo,sendMsg.toString());
//                    }
//                    messagingTemplate.convertAndSend("/topic/" + roomNo, "<p class='text-primary'>여기까지 지난 대화입니다.!!!</p>");
//                }
////                if (memberList.get(sessionNo).equals(senderId)) {
////                    messagingTemplate.convertAndSend("/topic/" + roomNo, "<div class='chat right'>본인 입장</div>");
////                } else {
////                    messagingTemplate.convertAndSend("/topic/" + roomNo, "<div class='chat left'>상대 입장</div>");
////                }
//                break;
//            case "chat":
//                log.info("TYPE :: {}",type);
//                recordChat(senderId, roomNo, msg);
//
//                String chatMessage = String.format("{\"msg\":\"%s\", \"senderId\":\"%s\"}", msg, senderId);
//                messagingTemplate.convertAndSend("/topic/" + roomNo, chatMessage);
//                break;
//            case "file":
//                log.info("TYPE :: {}",type);
//                String files = element.getAsJsonObject().get("files").getAsString();
//
//                Chat c = recordChat(senderId, roomNo, msg, files);
//
//
////                Chat c = getFiles(files);
//                String imagePath2 = "/uploads/chat/"+c.getFilepath();
//
//
//                String chatMessages = "<div class='chat " + (userId.equals(senderId) ? "right" : "left") + "'>" + msg + "</div>";
//                chatMessages += "<div class='chat right'> <img src='"+imagePath2+"' alt='사진' style='max-width:180px; max-height:340px;'></div>";
//                messagingTemplate.convertAndSend("/topic/" + roomNo, chatMessages);
//
//                break;
//            default:
//                throw new IllegalStateException("Unexcepted value : "+type);
//
//        }
//    }

    @MessageMapping("/chat")
    public void handleMessage(@Payload String message, Principal principal, Message<byte[]> messages){
        if(principal == null){
            System.out.println("User is not authenticated");
            return;
        }

        User currentUser = null;
        Authentication authentication = (Authentication) principal;
        Object principalObj = authentication.getPrincipal();

        if(principalObj instanceof CustomUserDetails customUserDetails){
            currentUser = customUserDetails.getUser();
        }else if(principalObj instanceof CustomUserOAuth customUserOAuth){
            currentUser = customUserOAuth.getUser();
        }


        Long userId = Objects.requireNonNull(currentUser).getId();

        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(messages);

        String sessionIds = headerAccessor.getSessionId();

        log.info("채팅 Message {}",message);
        log.info("채팅 Messages {}",message);
        log.info("챗컨트롤러 Authentication {}",authentication);

        JsonParser parser = new JsonParser();
        JsonElement element = parser.parseString(message);
        String type = element.getAsJsonObject().get("type").getAsString();
        String msg = element.getAsJsonObject().get("msg").getAsString();
        String roomNo = element.getAsJsonObject().get("roomNo").getAsString();
        Long senderId = element.getAsJsonObject().get("senderId").getAsLong();
        String userNick = currentUser.getNickName();
        switch (type){
            case "enter":
                log.info("TYPE :: {}",type);
                String enterMessage= "<p class='text-primary'>"+userNick+"님이 입장하였습니다.</p>";
                messagingTemplate.convertAndSend("/topic/" + roomNo, enterMessage);

                //지난 대화 출력
                List<Chat> crList = chatService.getChatReord(roomNo);
                String oldGetMember = "";

                if(!crList.isEmpty()){
                    for(Chat cr : crList){
                        StringBuilder sendMsg = new StringBuilder();
                        String newGetMember = cr.getChatMember();
                        String currentUserId = String.valueOf(currentUser.getId());


                        log.info("채팅친사람 : {} newGetMember : {} oldGetMember : {}" ,cr.getChatMember(),newGetMember,oldGetMember);

//                        log.debug("챗컨트롤러 USER ID {}",userId);
//                        log.debug("챗컨트롤러 getMember {}",cr.getChatMember());

                        if (cr.getChatMember().equals(String.valueOf(userId))) {
                            sendMsg.append("<div class='chat right'>").append(cr.getChatContent()).append("</div>");
                            if (cr.getFilepath() != null) {
                                sendMsg.append("<div class='chat right'>").append("<img alt='사진' src='/uploads/chat/").append(cr.getFilepath()).append("'>").append("</div>");
                            }
                        } else {
                            if (!currentUserId.equals(cr.getChatMember()) && !newGetMember.equals(oldGetMember)) {
                                String findGetProfilePhoto = findGetProfilePhoto(cr.getChatMember());
                                log.info("findGetProfilePhoto : {} ",findGetProfilePhoto);
                                sendMsg.append("<div class='chat left'><span class='chatId'><img src='/uploads/user/").append(findGetProfilePhoto).append("' alt='Profile Photo' style='width: 30px; height: 30px; border-radius: 50%; margin-right: 10px;'>")
                                        .append(cr.getChatMember()).append(" :</span>").append(cr.getChatContent()).append("</div>");
                            } else {
                                sendMsg.append("<div class='chat left'><span class='chatId'>")
                                        .append(cr.getChatMember()).append(" :</span>").append(cr.getChatContent()).append("</div>");
                            }
                            if (cr.getFilepath() != null) {
                                sendMsg.append("<div class='chat left'><span class='chatId'>")
                                        .append(cr.getChatMember()).append(" :</span>")
                                        .append("<img src='/uploads/chat/").append(cr.getFilepath()).append("'>").append("</div>");
                            }
                        }

                        messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/reply", sendMsg.toString());
                        oldGetMember = newGetMember;
                    }
                    messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/reply", "<p class='text-primary'>여기까지 지난 대화입니다.</p>");
                }

                break;
            case "leave":
                log.info("TYPE :: {}", type);

                String leaveMessage= "<p class='text-primary'>"+userNick+"님이 퇴장하였습니다.</p>";

                messagingTemplate.convertAndSend("/topic/" + roomNo, leaveMessage);
                break;

            case "chat":
                log.info("TYPE :: {}",type);
                recordChat(senderId, roomNo, msg);

//                String chatMessage = String.format("{\"msg\":\"%s\", \"senderId\":\"%s\"}", msg, senderId);
                String chatMessage = String.format("{\"msg\":\"%s\", \"senderId\":\"%s\", \"profilePhoto\":\"%s\"}", msg, senderId, currentUser.getProfilePhoto());

                messagingTemplate.convertAndSend("/topic/" + roomNo, chatMessage);
                break;
            case "file":
                log.info("TYPE :: {}",type);
                String files = element.getAsJsonObject().get("files").getAsString();

                Chat c = recordChat(senderId, roomNo, msg, files);

                String imagePath = uploadDir+c.getFilepath();

                String chatMessages = "<div class='chat " + (userId.equals(senderId) ? "right" : "left") + "'>" + msg + "</div>";
                chatMessages += "<div class='chat right'> <img src='"+imagePath+"' alt='사진' style='max-width:180px; max-height:340px;'></div>";
                messagingTemplate.convertAndSend("/topic/" + roomNo, chatMessages);

                break;
            default:
                throw new IllegalStateException("Unexcepted value : "+type);

        }
    }



    @ResponseBody
    @PostMapping(value="/file-upload", produces = "application/json;charset=utf-8")
    public ResponseEntity<String> uploadChatFile(
            @RequestParam("files") MultipartFile file)
            throws UnsupportedEncodingException {
        try {
//            String realPaths = uploadDir+"chat";
            String realPaths = uploadDir;

            Path uploadPath = Paths.get(realPaths);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = file.getOriginalFilename();
            fileName = fileRename.fileRename(uploadPath, fileName);

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);
            System.out.println("파일 업로드된 filePath  =>"+filePath);
            return ResponseEntity.ok(fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("파일 업로드 실패");
        }
    }

    private void recordChat(Long memberId,String roomNo, String msg) {
        Chat cr = new Chat();
        cr.setChatMember(String.valueOf(memberId));
        cr.setChatContent(msg);
        cr.setChatNo(roomNo);
        chatService.saveChat(cr);
    }

    private Chat recordChat(Long memberId,String roomNo, String msg,String files) {
        Chat cr = new Chat();
        cr.setChatMember(String.valueOf(memberId));
        cr.setChatContent(msg);
        cr.setChatNo(roomNo);
        cr.setFilepath(files);
        return chatService.saveChat(cr);
    }

    private String findGetProfilePhoto(String chatMember) {
        return userService.findGetProfilePhoto(chatMember);
    }
}
