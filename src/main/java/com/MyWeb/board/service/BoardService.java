package com.MyWeb.board.service;

import com.MyWeb.board.dto.BoardSummaryDTO;
import com.MyWeb.board.entity.Board;
import com.MyWeb.board.repository.BoardRepository;
import com.MyWeb.boardComment.dto.BoardCommentDTO;
import com.MyWeb.boardComment.entity.BoardComment;
import com.MyWeb.boardComment.repository.BoardCommentRepository;
import com.MyWeb.user.entity.User;
import com.MyWeb.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardCommentRepository boardCommentRepository;
    private final UserRepository userRepository;

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String VIEW_KEY_PREFIX = "post:view:";

    public BoardService(BoardRepository boardRepository, BoardCommentRepository boardCommentRepository, UserRepository userRepository, RedisTemplate<String, Object> redisTemplate) {
        this.boardRepository = boardRepository;
        this.boardCommentRepository = boardCommentRepository;
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
    }

    public List<Board> getBoardsWithPaging(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page-1,size);
        return boardRepository.findAll(pageRequest).getContent();
    }

    public int getBoardCount() {
        return (int)boardRepository.count();
    }

    //게시물 전체 가져오기 좋아요 & 댓글 수 포함
    public Page<BoardSummaryDTO> getBoardsWithCommentAndLikeCount(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return boardRepository.findAllWithCommentAndLikeCount(pageRequest);
    }

    //게시물에서 게시물 눌렀을때 게시글 정보가져오기
    public Optional<Board> getOneBoard(Long id) {

//        String redisKey = VIEW_KEY_PREFIX + boardId + ":" + userId;


        return boardRepository.findById(id);
    }

    //게시판에서 게시물 눌렀을때 전체댓글불러오기
    public List<BoardCommentDTO> getAllComment(Long boardNo) {
        List<BoardComment> comments = boardCommentRepository.findByBoard_Id(boardNo);
        List<BoardCommentDTO> commentDTOs = new ArrayList<>();


        for(BoardComment comment : comments){
            BoardCommentDTO dto = new BoardCommentDTO();
            dto.setId(comment.getId());


            dto.setUserNo(comment.getUser().getId());
            dto.setBoardNo(comment.getBoard().getId());

            dto.setComContent(comment.getComContent());
            dto.setComDate(comment.getComDate());
            dto.setComParentNo(comment.getComParentNo());
            dto.setComRef(comment.getComRef());
            dto.setRefOrder(comment.getRefOrder());
            dto.setDepth(comment.getDepth());
            dto.setComChild(comment.getComChild());

            commentDTOs.add(dto);

        }

        return commentDTOs;
    }

    public int getMaxRef(Long boardNo) {
        return boardCommentRepository.findMaxRef(boardNo);
    }

    //댓글 insert
    @Transactional
    public BoardComment insertComment(Long boardNo, Long userNo, String comContent, int maxRef) {
        Optional<Board> boardOpt = boardRepository.findById(boardNo);
        Optional<User> userOpt = userRepository.findById(userNo);

        if (boardOpt.isEmpty() || userOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid board or user ID");
        }

        BoardComment bc = new BoardComment();
        bc.setBoard(boardOpt.get());
        bc.setUser(userOpt.get());
        bc.setComContent(comContent);
        bc.setComRef(maxRef);
        return boardCommentRepository.save(bc);
    }

    @Transactional
    public BoardComment insertReComment(Long boardId, Long userNo, int refOrder,
                                        int comChild, String comContent, int comParentNo, int refOrderResult, int depth, int comRef) {
        Optional<Board> boardOpt = boardRepository.findById(boardId);
        Optional<User> userOpt = userRepository.findById(userNo);

        System.out.println("recomment boardOPT => "+boardOpt);

        System.out.println("recomment userOpt => "+userOpt);
        if (boardOpt.isEmpty() || userOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid board or user ID");
        }

        BoardComment bc = new BoardComment();
        bc.setBoard(boardOpt.get());
        bc.setUser(userOpt.get());
        bc.setRefOrder(refOrderResult);
        bc.setComContent(comContent);
        bc.setComParentNo(comParentNo);
        bc.setDepth(depth);
        bc.setComRef(comRef);

        return boardCommentRepository.save(bc);
    }

    @Transactional
    public void updateComChild(int comParentNo, Long boardId) {
        Long boardCommentId = (long) comParentNo;
        System.out.println(" updateComChild boardCommentId => "+boardCommentId );

        Optional<BoardComment> bcOpt = boardCommentRepository.findById(boardCommentId);
        System.out.println(" updateComChild bcOpt => "+bcOpt );
        boardCommentRepository.updateComChild(boardCommentId,boardId);

    }

    public Optional<Board> getOneBoard(Long id, Long userId) {
        // Redis에 저장된 조회 기록 확인
        String redisKey = VIEW_KEY_PREFIX + id + ":" + userId;

        // Redis에서 키가 없으면 조회수 증가 처리
        if (Boolean.FALSE.equals(redisTemplate.hasKey(redisKey))) {
            // 조회수 증가
            Optional<Board> board = boardRepository.findById(id);
            if (board.isPresent()) {
                Board currentBoard = board.get();
                currentBoard.setBoardCount(currentBoard.getBoardCount() + 1);
                boardRepository.save(currentBoard);

                // Redis에 조회 기록 저장 (TTL 1시간)
                redisTemplate.opsForValue().set(redisKey, true, 1, TimeUnit.HOURS);
            }
        }

        // 조회수 증가 여부와 관계없이 게시물 반환
        return boardRepository.findById(id);
    }


    public Board saveBoard(Board saveBoard) {
        return boardRepository.save(saveBoard);
    }
}
