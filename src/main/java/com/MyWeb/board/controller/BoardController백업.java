package com.MyWeb.board.controller;

import com.MyWeb.File.dto.FileDTO;
import com.MyWeb.File.entity.File;
import com.MyWeb.File.repository.FileRepository;
import com.MyWeb.File.service.FileService;
import com.MyWeb.board.dto.BoardDTO;
import com.MyWeb.board.dto.BoardSummaryDTO;
import com.MyWeb.board.entity.Board;
import com.MyWeb.board.service.BoardService;
import com.MyWeb.board.service.SseEmitterService;
import com.MyWeb.boardComment.dto.BoardCommentDTO;
import com.MyWeb.boardComment.entity.BoardComment;
import com.MyWeb.boardComment.service.BoardCommentService;
import com.MyWeb.boardLike.dto.BoardLikeDTO;
import com.MyWeb.boardLike.entity.BoardLike;
import com.MyWeb.boardLike.service.BoardLikeService;
import com.MyWeb.common.FileRename;
import com.MyWeb.common.SseEmitters;
import com.MyWeb.user.entity.User;
import com.MyWeb.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
//@Controller
public class BoardController백업 {
    private final BoardService boardService;
    private final UserService userService;
    private final BoardCommentService boardCommentService;
    private final FileRename fileRename;
    private final FileService fileService;
    private final FileRepository fileRepository;
    private final BoardLikeService boardLikeService;
    private final SseEmitterService sseEmitterService;
    private final SseEmitters sseEmitters;

    @Value("${file.uploads}")
    private String uploadDir;


    public BoardController백업(BoardService boardService, UserService userService,
                             BoardCommentService boardCommentService, FileRename fileRename,
                             FileService fileService, FileRepository fileRepository, BoardLikeService boardLikeService, SseEmitterService sseEmitterService, SseEmitters sseEmitters) {
        this.boardService = boardService;
        this.userService = userService;
        this.boardCommentService = boardCommentService;
        this.fileRename = fileRename;
        this.fileService = fileService;
        this.fileRepository = fileRepository;
        this.boardLikeService = boardLikeService;
        this.sseEmitterService = sseEmitterService;
        this.sseEmitters = sseEmitters;
    }

    /**
     *  기존에 사용하던목록 아래 댓글 갯수 + 좋아요 가져오는거로 변경
     */
//    @GetMapping("/boards")
//    public String listBoards(Model model,
//                             @RequestParam(name="page",defaultValue = "1")int page,
//                             @RequestParam(name="size",defaultValue = "10")int size){
//        List<Board> boards = boardService.getBoardsWithPaging(page,size);
//        log.info("boards => {} ",boards);
//        int boardCount = boardService.getBoardCount();
//        int maxPage = (int) Math.ceil((double) boardCount / size);
//
//        // 현재 페이지 그룹 계산
//        int currentGroup = (page - 1) / 5;
//        int startPage = currentGroup * 5 + 1;
//        int endPage = Math.min(startPage + 4, maxPage);
//
//        model.addAttribute("boards", boards);
//        model.addAttribute("size", size);
//        model.addAttribute("maxPage", maxPage);
//        model.addAttribute("page", page);
//        model.addAttribute("startPage", startPage);
//        model.addAttribute("endPage", endPage);
//
//        return "board/boards";
//    }

    //게시판 목록 불러오기
    @GetMapping("/boards")
    public String listBoards(Model model,
                             @RequestParam(name="page", defaultValue = "1") int page,
                             @RequestParam(name="size", defaultValue = "10") int size) {
        Page<BoardSummaryDTO> boards = boardService.getBoardsWithCommentAndLikeCount(page, size);
        log.info("boards => {} ", boards);

        int boardCount = boardService.getBoardCount();
        int maxPage = (int) Math.ceil((double) boardCount / size);

        // 현재 페이지 그룹 계산
        int currentGroup = (page - 1) / 5;
        int startPage = currentGroup * 5 + 1;
        int endPage = Math.min(startPage + 4, maxPage);

        model.addAttribute("boards", boards.getContent());
        model.addAttribute("size", size);
        model.addAttribute("maxPage", maxPage);
        model.addAttribute("page", page);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "board/boards";
    }

//    //게시물 하나 불러오기
//    @GetMapping("/getBoard")
//    public String getOneBoard(@RequestParam("boardNo")Long id,
//                              Principal principal,
//                              Model model){
//        System.out.println(
//                "currentUSER" + model.getAttribute("currentUser"));
//
//        Optional<Board> board = boardService.getOneBoard(id);
//        if (board.isPresent()) {
//            model.addAttribute("board", board);
//        }
//
//        List<BoardCommentDTO> bcList = boardService.getAllComment(id);
//        if (!bcList.isEmpty()) {
//            model.addAttribute("bcList", bcList);
//            System.out.println("bcList 여기니 => " + bcList);
//        }
//        return "board/boardDetail";
//    }

    //게시물 하나 불러오기
    @GetMapping("/getBoard")
    public String getOneBoard(@RequestParam("boardNo")Long id,
                              Principal principal,
                              Model model){


        User user = (User) model.getAttribute("currentUser");
//        log.info(" userID ? {}",user.getId());
        Long userId = user.getId();

        Optional<Board> board = boardService.getOneBoard(id,userId);
        if (board.isPresent()) {
            model.addAttribute("board", board);
        }

        List<File> files = fileService.getAllFile(id);
        if (!files.isEmpty()) {
            model.addAttribute("files", files);
//            System.out.println("bcList 여기니 => " + bcList);
//            log.info(" GetBoard의 Files {} ",files);
        }

        List<BoardCommentDTO> bcList = boardService.getAllComment(id);
        if (!bcList.isEmpty()) {
            model.addAttribute("bcList", bcList);
//            System.out.println("bcList 여기니 => " + bcList);
        }
        return "board/boardDetail";
    }

    //게시물에서 댓글 작성
    @PostMapping("/boards/{boardNo}/comments")
    @ResponseBody
    public String insertComment(@PathVariable("boardNo") Long boardNo,
                                @RequestBody BoardCommentDTO boardCommentDto
    ) {
        Long userNo = boardCommentDto.getUserNo();

        String comContent = boardCommentDto.getComContent();


        // 댓글의 Ref값 최대구해서 +1씩해서 전달
        int maxRef = boardService.getMaxRef(boardNo) + 1;
//        System.out.println("컨트롤러 maxRef => "+maxRef);
        BoardComment result = boardService.insertComment(boardNo, userNo, comContent, maxRef);
//        System.out.println("컨트롤러 result => "+result);


        if (result != null) {
            return "1";
        } else {
            return "0";
        }
    }
    
    //댓글 작성 후 새롭게 전체목록 가져와서 다시보여주기
    @GetMapping("/boards/{boardNo}/comments")
    @ResponseBody
    public List<BoardCommentDTO> insertComment(@PathVariable("boardNo") Long boardNo, Model model) {
        // 새로운 댓글 목록을 조회하여 반환
        List<BoardCommentDTO> bcList = boardService.getAllComment(boardNo);
//        System.out.println("bcList => "+bcList);
        //댓글 작성 후 기존 bcList덮어쓰기
        model.addAttribute("bcList", bcList);
        return bcList;
    }


    @PostMapping("/boards/{boardNo}/reComments")
    @ResponseBody
    public String insertReComment(@PathVariable("boardNo") Long boardNo,
                                @RequestBody BoardCommentDTO boardCommentDTO) {
//        System.out.println(boardCommentDto);
//
        Long userNo = boardCommentDTO.getUserNo();
        String comContent = boardCommentDTO.getComContent();
        int comParentNo= boardCommentDTO.getComParentNo();
        int comRef= boardCommentDTO.getComRef();
        int refOrder=boardCommentDTO.getRefOrder();
        int depth=boardCommentDTO.getDepth();
        int comChild=boardCommentDTO.getComChild();
        Long boardId = boardCommentDTO.getBoardNo();
        log.info("boardNo : {} ",boardNo);
        log.info("boardCommentDTO : {} ",boardCommentDTO);

        BoardComment bc = new BoardComment();

        // 댓글 참조번호
        Optional<BoardComment> optionalBc = boardCommentService.getOneboardComment(comParentNo);

        if(optionalBc.isPresent()){
            bc = optionalBc.get();
        }
        log.info("bc  : {}",bc);
//
        int refOrderResult = refOrderAndUpdate(bc);

        System.out.println("112줄 refOrder => "+refOrderResult);
        if(refOrderResult == 0) {
            return null;
        }
        depth = depth+1;


        BoardComment result = boardService.insertReComment(
                boardId, userNo, refOrder,comChild,
                comContent, comParentNo,
                refOrderResult, depth, comRef);

        if (result != null) {
            //대댓글 성공적으로 저장 후 부모 댓글 child+1
            boardService.updateComChild(comParentNo,boardId);
            return "1";
        } else {
            return "0";
        }
    }

    private int refOrderAndUpdate(BoardComment bc){
        Long boardCommentNo = bc.getId();
        int saveStep = bc.getDepth() + 1;
        int refOrder = bc.getRefOrder();
        int answerNum = bc.getComChild();
        int ref = bc.getComRef();
        Long boardNo = bc.getBoard().getId();

        // 부모 댓글그룹의 answerNum(자식수)
        int answerNumSum = boardCommentService.findBySumAnswerNum(ref, boardCommentNo);
        /*
         * SELECT SUM(com_child) FROM board_comment WHERE com_ref = #{comRef} and
         * board_no = #{boardNo}
         */
        System.out.println("answerNumSum => "+answerNumSum);
        // 부모 댓글그룹의 최댓값 step
        int maxStep = boardCommentService.findMaxStep(ref, boardCommentNo);
        /**
         * SELECT MAX(depth) FROM board_comment WHERE com_ref = #{comRef} AND board_no =
         * #{boardNo}
         */

//        System.out.println("===refOrderAndUpdate===");
//        System.out.println("saveStep=> "+saveStep);
//        System.out.println("maxStep=> "+maxStep);
//        System.out.println("refOrder=> "+refOrder);
//        System.out.println("ref=> "+ref);
//        System.out.println("answerNum=> "+answerNum);
//        System.out.println("boardNo=> "+boardNo);


        if (saveStep < maxStep) {
            return answerNumSum + 1;
        } else if (saveStep == maxStep) {
            boardCommentService.updateRefOrderPlus(ref, refOrder + answerNum,boardNo);
            return refOrder + answerNum + 1;
        } else {
            boardCommentService.updateRefOrderPlus(ref, refOrder, boardNo);
            return refOrder + 1;
        }
    }

    @ResponseBody
    @PostMapping(value="/board/uploadImages",produces = "application/json;charset=utf-8")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile[] file){
        try {
            String fileName = null;
            if (!file[0].isEmpty()) {
                String webPath = null;
                for (MultipartFile file1 : file) {
                    String tempUploadDir = uploadDir+"board";
                    Path uploadPath = Paths.get(tempUploadDir);
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }

                    fileName = file1.getOriginalFilename();
                    fileName = fileRename.fileRename(uploadPath, fileName);

                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(file1.getInputStream(), filePath);

                    log.info("filePath {} fileName {} ",filePath, fileName);
                    // 파일 경로를 응답에 추가

                    return ResponseEntity.ok().body(fileName);

                }

            }else{
                System.out.println("비었음");
            }

        }catch (Exception e){
            System.out.println("error -> " +e);
            return ResponseEntity.badRequest().build();
        }
        return null;
    }



    @GetMapping("/writeForm")
    public String writeForm(){
        return "board/boardWrite";
    }

    @PostMapping("/boardWrite")
    public ResponseEntity<?> handleFileUpload(@RequestParam("memberNo") Long memberNo,
                                              @RequestParam("boardTitle") String boardTitle,
                                              @RequestParam("boardContent") String boardContent,
                                              @RequestParam("boardFile") MultipartFile[] files) {

        Optional<User> users = userService.findById(memberNo); // 예시: memberNo로 User를 찾는 서비스 메소드
        User user = users.get();

//        Long userId = user.getId();

        // BoardDTO 생성
        BoardDTO saveBoard =
                new BoardDTO(
                null, memberNo, boardTitle, boardContent, 0, LocalDate.now()
        );

        // Board 엔티티로 변환
        Board board = saveBoard.toEntity(user);
        Board savedBoard = boardService.saveBoard(board);

        log.info("SavedBoard : {} ",savedBoard);

        log.info("MemberNo : {}, boardTitle : {} ",memberNo, boardTitle);
        log.info("boardContent : {}, boardFile : {} ",boardContent, Arrays.toString(files));

        try{
            String fileName = null;
            String originFileName = null;
            if(!files[0].isEmpty()){
                String webPath = null;
                Board b = new Board();
                for (MultipartFile file1 : files) {
                    String tempUploadDir = uploadDir+"board";
                    Path uploadPath = Paths.get(tempUploadDir);
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }

                    originFileName = file1.getOriginalFilename();
                    fileName = file1.getOriginalFilename();
                    fileName = fileRename.fileRename(uploadPath, fileName);

                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(file1.getInputStream(), filePath);

                    System.out.println("originFileName => "+originFileName);
                    System.out.println("fileName => "+fileName);

                    FileDTO saveFile = FileDTO.builder()
                            .fileName(fileName)
                            .filePath(String.valueOf(filePath))
                            .type("board") // 파일 타입 어디서 저장되었는지
                            .refId(savedBoard.getId()) // 참조아이디 현재 기준으론 board Id
                            .user(user)
                            .build();

                    File entityFile = saveFile.toEntity();
                    File savedFile = fileService.save(entityFile);

                }

                return ResponseEntity.ok("getBoard?boardNo="+savedBoard.getId());
            }else{
                System.out.println("파일이없으요");
            }

        }catch (Exception e ){
            System.out.println("Exception => "+e);
            return null;
        }
        return ResponseEntity.ok("getBoard?boardNo="+savedBoard.getId());
    }

    @PostMapping("/boardEdit")
    public ResponseEntity<?> boardUpdate(
                            @RequestParam("boardCount") int boardCount,
                            @RequestParam("boardNo") Long boardNo,
                            @RequestParam("memberNo") Long memberNo,
                            @RequestParam("boardTitle") String boardTitle,
                            @RequestParam("boardContent") String boardContent,
                            @RequestParam("boardFile") MultipartFile[] files,
                            @RequestParam("delValues") String delValues) {
            log.info("memberNo : {} , boardTitle : {} , boardContent : {} ," +
                            " Files : {} , delValues : {} , boardCount : {}",
                    memberNo,boardTitle,boardContent,files,delValues, boardCount);


        /**
         * 해야할꺼 delValues에 값 있는것들은 delete 해주고(완료)
         * files에 값있으면 insert해주고
         * 지금 게시물은 최종적으로 update
         */

        log.info("files SIZE => {} ",files.length);
        fileService.delFiles(delValues);
        Optional<User> users = userService.findById(memberNo); // 예시: memberNo로 User를 찾는 서비스 메소드
        User user = users.get();

        Long userId = user.getId();

        // BoardDTO 생성
        BoardDTO saveBoard =
                new BoardDTO(
                        boardNo, memberNo, boardTitle, boardContent, boardCount ,
                         LocalDate.now()
                );

        // Board 엔티티로 변환
        Board board = saveBoard.toEntity(user);
        Board savedBoard = boardService.saveBoard(board);
//
//        log.info("SavedBoard : {} ",savedBoard);

//        log.info("MemberNo : {}, boardTitle : {} ",memberNo, boardTitle);
//        log.info("boardContent : {}, boardFile : {} ",boardContent, Arrays.toString(files));

        try{
            String fileName = null;
            String originFileName = null;
            if(!files[0].isEmpty()){
                String webPath = null;
                Board b = new Board();
                for (MultipartFile file1 : files) {
                    String tempUploadDir = uploadDir+"board";
                    Path uploadPath = Paths.get(tempUploadDir);
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }

                    originFileName = file1.getOriginalFilename();
                    fileName = file1.getOriginalFilename();
                    fileName = fileRename.fileRename(uploadPath, fileName);

                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(file1.getInputStream(), filePath);

                    System.out.println("originFileName => "+originFileName);
                    System.out.println("fileName => "+fileName);

                    FileDTO saveFile = FileDTO.builder()
                            .fileName(fileName)
                            .filePath(String.valueOf(filePath))
                            .type("board") // 파일 타입 어디서 저장되었는지
                            .refId(savedBoard.getId()) // 참조아이디 현재 기준으론 board Id
                            .user(user)
                            .build();

                    File entityFile = saveFile.toEntity();
                    File savedFile = fileService.save(entityFile);

                }

                return ResponseEntity.ok("getBoard?boardNo="+savedBoard.getId());
            }else{
                System.out.println("파일이없으요");
            }

        }catch (Exception e ){
            System.out.println("Exception => "+e);
            return null;
        }
        return ResponseEntity.ok("getBoard?boardNo="+savedBoard.getId());
    }


    @GetMapping("/boardFileDown/{id}")
    public ResponseEntity<Resource>  downloadBoardFile(@PathVariable("id") Long id) throws IOException {
        Optional<File> opFile = fileRepository.findById(id);
        File file = opFile.get();

        Path filePath = Paths.get(file.getFilePath()).toAbsolutePath().normalize();
        UrlResource resource = new UrlResource(filePath.toUri());

        log.info(" File ID : {} , filePath : {} , UrlResource : {} ",id,filePath,resource);

        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        String encodedUploadFile = UriUtils.encode(file.getFileName(), StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedUploadFile + "\"")
                .body(resource);
    }

    @GetMapping("/boardEdit/{id}")
    public String downloadBoardFile(@PathVariable("id") Long id,Model model){
        Optional<Board> board = boardService.getOneBoard(id);
        User user = (User) model.getAttribute("currentUser");
        Long currentUserId = user.getId();

        if(board.isPresent()){
//            if(board.get().getUser())
            Long ownerId = board.get().getUser().getId();
//            log.info("BoardGetUser : {}, CurrentUser {} ", ownerId , currentUserId);
            if(!ownerId.equals(currentUserId)){
                throw new AccessDeniedException("수정 권한이 없습니다." );
            }
            Board b = board.get();
            model.addAttribute("board",b);

            List<File> files = fileService.getAllFile(b.getId());
            if (!files.isEmpty()) {
                model.addAttribute("files", files);
            }
        }
        return "board/boardWrite";
    }

    @GetMapping("/boardDel/{id}")
    public String delBoard(@PathVariable("id") Long id,Model model){
        Optional<Board> board = boardService.getOneBoard(id);
        User user = (User) model.getAttribute("currentUser");
        Long currentUserId = user.getId();

        if(board.isPresent()){
            Long ownerId = board.get().getUser().getId();

            if(!ownerId.equals(currentUserId)){
                throw new AccessDeniedException("수정 권한이 없습니다." );
            }else{
                boardService.deleteOneBoard(id);
                fileService.multiDelete(id,"board");
            }
        }

        return "redirect:../boards";
    }

    @PostMapping("/board/changeLike")
    @ResponseBody
    public String boardLikes(@RequestParam("userId")Long userId,
                           @RequestParam("boardId")Long boardId,
                           @RequestParam("status")String status,
                           Model model){

        log.info("BoardLike UserId : {}, boardId : {}, Status : {} ",userId,boardId,status);
        Optional<Board> getBoard = boardService.getOneBoard(boardId);
        Optional<BoardLike> blList = boardLikeService.getCheck(userId,boardId);
        User getUser = (User) model.getAttribute("currentUser");
        BoardLike saveBoardLike = null;
        BoardLike updateBoardLike = null;
        String resultVal;
        if(blList.isEmpty()){
            //빈값이므로 무조건 좋아요 누르기

            BoardLikeDTO newbLike = BoardLikeDTO.builder()
                    .likeStatus(1)
                    .board(getBoard.get())
                    .user(getUser)
                    .build();
            BoardLike entityBoardLike = newbLike.toEntity();

            log.info("entityBoardLike : {} ",entityBoardLike);

            saveBoardLike = boardLikeService.save(entityBoardLike);

            log.info("SaveBoardLike SIZE : {} ",saveBoardLike);
            resultVal = "1";
            return resultVal;
        }else{
//            log.info("BLLIST : {} ",blList);
            int resultStatus = blList.get().getLikeStatus();
            int setStatus = resultStatus == 0 ? 1 : 0;

            // 좋아요 버튼을눌렀을때
            if(status.equals("g")){

                if(resultStatus == -1){
                    resultVal = "2";
                    return resultVal;
                }

                    BoardLikeDTO updateLike = BoardLikeDTO.builder()
                            .id(blList.get().getId())
                            .likeStatus(setStatus)
                            .board(getBoard.get())
                            .user(getUser)
                            .build();
                    BoardLike entityBoardLike = updateLike.toEntity();

                    updateBoardLike = boardLikeService.update(entityBoardLike);

                    log.info("UpdateBoardLike : {} ",updateBoardLike);

                    resultVal = "3";
                    return resultVal;

            }else if(status.equals("f")){
                if(resultStatus == 1){
                    resultVal = "4";
                    return resultVal;
                }

                //싫어요 버튼 눌렀을때
                setStatus = resultStatus == 0 ? -1 : 0;

                BoardLikeDTO updateLike = BoardLikeDTO.builder()
                        .id(blList.get().getId())
                        .likeStatus(setStatus)
                        .board(getBoard.get())
                        .user(getUser)
                        .build();
                BoardLike entityBoardLike = updateLike.toEntity();

                updateBoardLike = boardLikeService.update(entityBoardLike);

                log.info("UpdateBoardLike : {} ",updateBoardLike);

                resultVal = "5";
                return resultVal;
            }
        }
        return "defaultValue";
    }


    /**
     * SSE로 게시물 조회수 스트림 연결
     */
//    @GetMapping("/board/view/stream")
//    public SseEmitter streamBoardView(@RequestParam(value="boardId") Long boardId) {
//        log.info("여기되니 확인 {} ",boardId);
//        return sseEmitterService.createEmitter(boardId);
//    }

//    @GetMapping("/board/view/stream")
//    public ResponseEntity<SseEmitter> streamBoardView(@RequestParam(value="boardId") Long boardId) {
//        log.info("여기되니 확인 {} ",boardId);
//        SseEmitter emitter = new SseEmitter();
//        sseEmitters.add(emitter);
//        try {
//            emitter.send(SseEmitter.event()
//                    .name("sse")
//                    .data("하이요"));
//        }catch(IOException e){
//            throw new RuntimeException(e);
//        }
////        return sseEmitterService.createEmitter(boardId);
//        return ResponseEntity.ok(emitter);
//
//    }


    /**
     * SSE로 조회수 전송 (조회수 증가 로직 없이 조회만)
     */
    @GetMapping("/board/view")
    public ResponseEntity<Void> viewBoard(@RequestParam(value="boardId") Long boardId) {
        // 조회수 상태를 쿼리하여 SSE로 전송
        log.info("컨트롤러 ViewDate : {} ",boardId);
//         boardService.sendViewUpdate(boardId);

        return ResponseEntity.ok().build();
    }


}
