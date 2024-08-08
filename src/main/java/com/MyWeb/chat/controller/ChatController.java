package com.MyWeb.chat.controller;

import com.MyWeb.chat.entity.Chat;
import com.MyWeb.chat.service.ChatService;
import com.MyWeb.common.FileRename;
import com.MyWeb.user.entity.CustomUserDetails;
import com.MyWeb.user.entity.CustomUserOAuth;
import com.MyWeb.user.entity.User;
import com.MyWeb.user.service.UserService;
import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
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
import java.time.LocalDateTime;
import java.util.*;

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
                String enterMessage= "<p class='chatwrap text-primary'>"+userNick+"님이 입장하였습니다.</p>";
                messagingTemplate.convertAndSend("/topic/" + roomNo, enterMessage);

                //지난 대화 출력
                List<Chat> crList = chatService.getChatReord(roomNo);
                String oldGetMember = "";

                if(!crList.isEmpty()){

                    for(Chat cr : crList){

                        StringBuilder sendMsg = new StringBuilder();
                        String newGetMember = cr.getChatMember();
                        String currentUserId = String.valueOf(currentUser.getId());
                        String formmatedTime = cr.getChatDate().getHour()+ ": "+cr.getChatDate().getMinute();

                        if (cr.getChatMember().equals(String.valueOf(userId))) {
//                            sendMsg.append("<div data-id='").append(cr.getChatMember()).append("'>").append("시간").append("</div>")
//                                    .append("<div class='chat right'>").append(cr.getChatContent()).append("</div>");
//                            if (cr.getFilepath() != null) {
//                                sendMsg.append("<div class='chat right'>").append("<img alt='사진' src='/uploads/chat/").append(cr.getFilepath()).append("'>").append("</div>");
//                            }

//                            sendMsg.append("<div class='chatwrap' data-id='").append(cr.getChatMember()).append("'>")
//                                    .append("<div class='chat right'>").append(cr.getChatContent()).append("</div>");
//                            if (cr.getFilepath() != null) {
//                                sendMsg.append("<div class='chat right'>").append("<img alt='사진' src='/uploads/chat/").append(cr.getFilepath()).append("'></div>");
//                            }
//                            sendMsg.append("<span class='time right'>").append(formmatedTime).append("</span>")
//                                    .append("</div>");


//                            sendMsg.append("<div class='chatwrap chatWrap-right' data-id='").append(cr.getChatMember()).append("'>")
//                                    .append("<div class='chat right'>").append(cr.getChatContent()).append("</div>");
//                            if (cr.getFilepath() != null) {
//                                sendMsg.append("<div class='chat right'>").append("<img alt='사진' src='/uploads/chat/").append(cr.getFilepath()).append("'></div>");
//                            }
//                            sendMsg.append("<span class='time time-right'>").append(formmatedTime).append("</span>")
//                                    .append("</div>");
                            if(cr.getFilepath() == null){
                                sendMsg.append("<div class='chatwrap chatWrap-right' data-id='").append(cr.getChatMember()).append("'>")
                                        .append("<div class='div-right'>").append("<p class='pClass2'>").append(cr.getChatContent()).append("</p>")
                                        .append("<span class='time time-right'>").append(formmatedTime).append("</span>")
                                        .append("</div></div>");
                            }else{
                                sendMsg.append("<div class='chatwrap chatWrap-right' data-id='").append(cr.getChatMember()).append("'>")
                                        .append("<div class='div-right'>")
                                        .append("<div class='pClass2'>")
                                        .append("<img src='/uploads/chat/").append(cr.getFilepath()).append("'>")
                                        .append("</div>")
                                        .append("<span class='time time-right'>").append(formmatedTime).append("</span>")
                                        .append("</div></div>");
                            }


                        } else {

                            if (!currentUserId.equals(cr.getChatMember()) && !newGetMember.equals(oldGetMember)) {
                                log.info("어쩌고저쩌고 1번상황 !!!!");
                                String findGetProfilePhoto = findGetProfilePhoto(cr.getChatMember());
                                sendMsg.append("<div class='chatwrap chatWrap-left' data-id='").append(cr.getChatMember()).append("'>")
                                        .append("<div class='chat left leftBox'><span class='chatId' style='display: flex;'><img src='/uploads/user/").append(findGetProfilePhoto)
                                        .append("' alt='Profile Photo' style='width: 30px; height: 30px; border-radius: 50%; margin-right: 10px;'>")
                                        .append(cr.getChatMember()).append("</span>")
                                        .append("<div class='div-left' style='padding-left: 40px;'><p class='pClass'>").append(cr.getChatContent()).append("</p>")
                                        .append("<span class='time time-left'>").append(formmatedTime).append("</span>")
                                        .append("</div></div></div>");
                            } else {
                                log.info("어쩌고저쩌고 2번상황 !!!!");

                                sendMsg.append("<div class='chatwrap chatWrap-left' data-id='").append(cr.getChatMember()).append("'>")
                                        .append("<div class='chat left leftBox'><span class='chatId' style='display: flex;'></span>")
                                        .append("<div class='div-left' style='padding-left: 40px;'><p class='pClass'>").append(cr.getChatContent()).append("</p>")
                                        .append("<span class='time time-left'>").append(formmatedTime).append("</span>")
                                        .append("</div></div></div>");

                            }
                            if (cr.getFilepath() != null) {

                                sendMsg.append("<div class='chatwrap chatWrap-left' data-id='").append(cr.getChatMember()).append("'>")
                                        .append("<div class='chat left leftBox'><span class='chatId' style='display: flex;'></span></div>")
                                        .append("<div class='div-left' style='padding-left: 40px;'>")
                                        .append("<div class='pClass'>")
                                        .append("<img src='/uploads/chat/").append(cr.getFilepath()).append("'>")
                                        .append("</div>")
                                        .append("<span class='time time-left'>").append(formmatedTime).append("</span></div>")

                                        .append("</div></div></div>");
                            }


                        }

                        messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/reply", sendMsg.toString());
                        oldGetMember = newGetMember;
                    }
                    messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/reply", "<p class='chatwrap text-primary'>여기까지 지난 대화입니다.</p>");

                }

                break;
            case "leave":
                log.info("TYPE :: {}", type);

                String leaveMessage= "<p class='text-primary'>"+userNick+"님이 퇴장하였습니다.</p>";

                messagingTemplate.convertAndSend("/topic/" + roomNo, leaveMessage);
                break;

            case "chat":
                log.info("TYPE :: {}",type);
                Chat chat = recordChat(senderId, roomNo, msg, LocalDateTime.now());
                StringBuilder sendMsg = new StringBuilder();
                String formmatedTime = chat.getChatDate().getHour()+ ": "+chat.getChatDate().getMinute();

//                if(userId == senderId){
//                    sendMsg.append("<div class='chatwrap chatWrap-right' data-id='").append(chat.getChatMember()).append("'>")
//                            .append("<div class='div-right'>").append("<p class='pClass2'>").append(chat.getChatContent()).append("</p>")
//                            .append("<span class='time time-right'>").append(formmatedTime).append("</span>")
//                            .append("</div></div>");
//                }else{
//                    sendMsg.append("<div class='chatwrap chatWrap-left' data-id='").append(chat.getChatMember()).append("'>")
//                            .append("<div class='chat left leftBox'><span class='chatId' style='display: flex;'></span>")
//                            .append("<div class='div-left' style='padding-left: 40px;'><p class='pClass'>").append(chat.getChatContent()).append("</p>")
//                            .append("<span class='time time-left'>").append(formmatedTime).append("</span>")
//                            .append("</div></div></div>");
//                }


//                String chatMessage = String.format("{\"msg\":\"%s\", \"senderId\":\"%s\"}", msg, senderId);
                String chatMessage = String.format("{\"msg\":\"%s\", \"senderId\":\"%s\", \"profilePhoto\":\"%s\", \"time\":\"%s\"}",
                        msg, senderId, currentUser.getProfilePhoto(),formmatedTime);


                messagingTemplate.convertAndSend("/topic/" + roomNo, chatMessage);
                break;
            case "file":
                log.info("TYPE :: {}",type);
                JsonArray testObj = element.getAsJsonObject().get("files").getAsJsonArray();
                log.info("테스트 오브제 => {}", testObj);
//                String files = element.getAsJsonObject().get("files").getAsString();
                Chat c = new Chat();
                List<String> filePaths = new ArrayList<>();

                for (JsonElement fileElement : testObj) {
                    String fileName = fileElement.getAsString();
                    log.info("파일명 => {}", fileName);

                    // 각 파일명에 대해 개별적으로 recordChat 메서드 호출
                    c = recordChat(senderId, roomNo, msg, fileName, LocalDateTime.now());
                    filePaths.add(c.getFilepath()); // 각 파일 경로를 리스트에 추가
                }

//                Chat c = recordChat(senderId, roomNo, msg, files,LocalDateTime.now());

                String imagePath = uploadDir+c.getFilepath();
                formmatedTime = c.getChatDate().getHour()+ ": "+c.getChatDate().getMinute();

//                String chatMessages = "<div class='chat " + (userId.equals(senderId) ? "right" : "left") + "'>" + msg + "</div>";
//                chatMessages += "<div class='chat right'> <img src='"+imagePath+"' alt='사진' style='max-width:180px; max-height:340px;'></div>";

                log.info("249줄 Chat C => {}",c);

//                chatMessage = String.format("{\"msg\":\"%s\", \"senderId\":\"%s\", " +
//                                "\"profilePhoto\":\"%s\", \"filePath\":\"%s\" ,\"time\":\"%s\"}",
//                        msg, senderId, currentUser.getProfilePhoto(), c.getFilepath() ,formmatedTime);

                chatMessage = String.format("{\"msg\":\"%s\", \"senderId\":\"%s\", \"profilePhoto\":\"%s\", \"filePaths\":%s, \"time\":\"%s\"}",
                        msg, senderId, currentUser.getProfilePhoto(), new Gson().toJson(filePaths), formmatedTime);

                messagingTemplate.convertAndSend("/topic/" + roomNo, chatMessage);
                break;
            default:
                throw new IllegalStateException("Unexcepted value : "+type);

        }
    }



//    @MessageMapping("/chat")
//    public void handleMessage(@Payload String message, Principal principal, Message<byte[]> messages){
//        if(principal == null){
//            System.out.println("User is not authenticated");
//            return;
//        }
//
//        User currentUser = null;
//        Authentication authentication = (Authentication) principal;
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
//        String userNick = currentUser.getNickName();
//        switch (type){
//            case "enter":
//                log.info("TYPE :: {}",type);
//                String enterMessage= "<p class='text-primary'>"+userNick+"님이 입장하였습니다.</p>";
//                messagingTemplate.convertAndSend("/topic/" + roomNo, enterMessage);
//
//                //지난 대화 출력
//                List<Chat> crList = chatService.getChatReord(roomNo);
//                String oldGetMember = "";
//
//                if(!crList.isEmpty()){
//                    for(Chat cr : crList){
//                        StringBuilder sendMsg = new StringBuilder();
//                        String newGetMember = cr.getChatMember();
//                        String currentUserId = String.valueOf(currentUser.getId());
//
//
//                        log.info("채팅친사람 : {} newGetMember : {} oldGetMember : {}" ,cr.getChatMember(),newGetMember,oldGetMember);
//
////                        log.debug("챗컨트롤러 USER ID {}",userId);
////                        log.debug("챗컨트롤러 getMember {}",cr.getChatMember());
//
//                        if (cr.getChatMember().equals(String.valueOf(userId))) {
//                            sendMsg.append("<div class='chat right'>").append(cr.getChatContent()).append("</div>");
//                            if (cr.getFilepath() != null) {
//                                sendMsg.append("<div class='chat right'>").append("<img alt='사진' src='/uploads/chat/").append(cr.getFilepath()).append("'>").append("</div>");
//                            }
//                        } else {
//                            if (!currentUserId.equals(cr.getChatMember()) && !newGetMember.equals(oldGetMember)) {
//                                String findGetProfilePhoto = findGetProfilePhoto(cr.getChatMember());
//                                log.info("findGetProfilePhoto : {} ",findGetProfilePhoto);
//                                sendMsg.append("<div class='chat left'><span class='chatId'><img src='/uploads/user/").append(findGetProfilePhoto).append("' alt='Profile Photo' style='width: 30px; height: 30px; border-radius: 50%; margin-right: 10px;'>")
//                                        .append(cr.getChatMember()).append(" :</span>").append(cr.getChatContent()).append("</div>");
//                            } else {
//                                sendMsg.append("<div class='chat left'><span class='chatId'>")
//                                        .append(cr.getChatMember()).append(" :</span>").append(cr.getChatContent()).append("</div>");
//                            }
//                            if (cr.getFilepath() != null) {
//                                sendMsg.append("<div class='chat left'><span class='chatId'>")
//                                        .append(cr.getChatMember()).append(" :</span>")
//                                        .append("<img src='/uploads/chat/").append(cr.getFilepath()).append("'>").append("</div>");
//                            }
//                        }
//
//                        messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/reply", sendMsg.toString());
//                        oldGetMember = newGetMember;
//                    }
//                    messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/reply", "<p class='text-primary'>여기까지 지난 대화입니다.</p>");
//                }
//
//                break;
//            case "leave":
//                log.info("TYPE :: {}", type);
//
//                String leaveMessage= "<p class='text-primary'>"+userNick+"님이 퇴장하였습니다.</p>";
//
//                messagingTemplate.convertAndSend("/topic/" + roomNo, leaveMessage);
//                break;
//
//            case "chat":
//                log.info("TYPE :: {}",type);
//                recordChat(senderId, roomNo, msg);
//
////                String chatMessage = String.format("{\"msg\":\"%s\", \"senderId\":\"%s\"}", msg, senderId);
//                String chatMessage = String.format("{\"msg\":\"%s\", \"senderId\":\"%s\", \"profilePhoto\":\"%s\"}", msg, senderId, currentUser.getProfilePhoto());
//
//                messagingTemplate.convertAndSend("/topic/" + roomNo, chatMessage);
//                break;
//            case "file":
//                log.info("TYPE :: {}",type);
//                String files = element.getAsJsonObject().get("files").getAsString();
//
//                Chat c = recordChat(senderId, roomNo, msg, files);
//
//                String imagePath = uploadDir+c.getFilepath();
//
//                String chatMessages = "<div class='chat " + (userId.equals(senderId) ? "right" : "left") + "'>" + msg + "</div>";
//                chatMessages += "<div class='chat right'> <img src='"+imagePath+"' alt='사진' style='max-width:180px; max-height:340px;'></div>";
//                messagingTemplate.convertAndSend("/topic/" + roomNo, chatMessages);
//
//                break;
//            default:
//                throw new IllegalStateException("Unexcepted value : "+type);
//
//        }
//    }



    @ResponseBody
    @PostMapping(value="/file-upload", produces = "application/json;charset=utf-8")
    public ResponseEntity<Object> uploadChatFile(
            @RequestParam("files") MultipartFile[] multiFiles)
            throws UnsupportedEncodingException {
        String fileName = "";
        List<String> fileNames = new ArrayList<>();
        try {
            log.info("파일업로드 파일 몇개들어오는지 확인 {} ",multiFiles);
//            String realPaths = uploadDir+"chat";
            String realPaths = uploadDir;

            for(MultipartFile file : multiFiles){
                Path uploadPath = Paths.get(realPaths);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

//
                fileName = fileRename.fileRename(uploadPath, file.getOriginalFilename());
                log.info("410줄 FILENAME {} ",fileName);
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(file.getInputStream(), filePath);
                System.out.println("파일 업로드된 filePath  =>"+filePath);
                fileNames.add(fileName);
            }
            return ResponseEntity.ok(fileNames);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("파일 업로드 실패");
        }
    }

    private Chat recordChat(Long memberId,String roomNo, String msg,LocalDateTime ldt ) {
        Chat cr = new Chat();
        cr.setChatMember(String.valueOf(memberId));
        cr.setChatContent(msg);
        cr.setChatNo(roomNo);
        cr.setChatDate(ldt);
        return chatService.saveChat(cr);
    }

    private Chat recordChat(Long memberId,String roomNo, String msg,String files, LocalDateTime ldt) {
        Chat cr = new Chat();
        cr.setChatMember(String.valueOf(memberId));
        cr.setChatContent(msg);
        cr.setChatNo(roomNo);
        cr.setFilepath(files);
        cr.setChatDate(ldt);
        return chatService.saveChat(cr);
    }

    private String findGetProfilePhoto(String chatMember) {
        return userService.findGetProfilePhoto(chatMember);
    }
}
