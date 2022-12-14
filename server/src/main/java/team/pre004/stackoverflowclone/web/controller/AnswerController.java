package team.pre004.stackoverflowclone.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.pre004.stackoverflowclone.domain.post.entity.Answer;
import team.pre004.stackoverflowclone.domain.post.entity.Question;
import team.pre004.stackoverflowclone.domain.user.entity.Users;
import team.pre004.stackoverflowclone.domain.user.repository.UsersRepository;
import team.pre004.stackoverflowclone.dto.common.*;
import team.pre004.stackoverflowclone.dto.post.PostType;
import team.pre004.stackoverflowclone.dto.post.request.AnswerCommentDto;
import team.pre004.stackoverflowclone.dto.post.response.AnswerInfoDto;
import team.pre004.stackoverflowclone.dto.post.request.AnswerPostDto;
import team.pre004.stackoverflowclone.dto.post.response.LikesDto;
import team.pre004.stackoverflowclone.handler.ExceptionMessage;
import team.pre004.stackoverflowclone.handler.ResponseCode;
import team.pre004.stackoverflowclone.handler.exception.CustomNotAccessItemsException;
import team.pre004.stackoverflowclone.handler.exception.CustomNotContentItemException;
import team.pre004.stackoverflowclone.mapper.AnswerMapper;
import team.pre004.stackoverflowclone.mapper.CommentMapper;
import team.pre004.stackoverflowclone.web.config.auth.PrincipalDetails;
import team.pre004.stackoverflowclone.service.AnswerCommentService;
import team.pre004.stackoverflowclone.service.AnswerService;
import team.pre004.stackoverflowclone.service.CommonService;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/answers")
public class AnswerController {
    private final AnswerService answerService;
    private final AnswerMapper answerMapper;
    private final CommentMapper commentMapper;
    private final CommonService commonService;
    private final AnswerCommentService answerCommentService;
    private final UsersRepository usersRepository;

    Users users = Users.builder()
            .password("4321")
            .name("??????ward")
            .email("ward@ward.net")
            .bio("???????????????????????????.")
            .build();

    @PostMapping("/{questionId}/add") // ?????? ?????? ??????
    public ResponseEntity<?> addAnswer(@PathVariable Long questionId, @RequestBody AnswerPostDto answerPostDto) {

        PrincipalDetails principalDetails = PrincipalDetails.builder().
                users(usersRepository.save(users))
                .build();

        Answer answer = answerService.save(
                answerMapper.answerPostDtoToAnswer(
                        principalDetails.getUsers(), questionId, answerPostDto
                )
        );

        AnswerInfoDto answerInfoDto = answerMapper.getAnswerInfo(answer);

        AnswerRespDto<?> response = AnswerRespDto.builder()
                .code(ResponseCode.SUCCESS)
                .answer(answerInfoDto)
                .message("????????? ?????????????????????.")
                .build();


        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PutMapping("/{answerId}/edit") // ?????? ?????? ??????
    public ResponseEntity<?> editAnswer(@PathVariable Long answerId, @RequestBody AnswerPostDto answerPostDto) {

        PrincipalDetails principalDetails = PrincipalDetails.builder().
                users(usersRepository.save(users))
                .build();

        if (answerService.findById(answerId).isEmpty()) //?????? ???????????? ?????? ??? ???????????????
            throw new CustomNotContentItemException(ExceptionMessage.NOT_CONTENT_ANSWER_ID);

//        if (principalDetails.getUsers().getOwnerId() != answerId) //?????? ????????? ????????????
//            throw new CustomNotAccessItemsException(ExceptionMessage.NOT_ACCESS_EDIT_ANSWER);

        Answer answer = answerService.update(answerId, answerMapper.answerPostDtoToAnswer(
                principalDetails.getUsers(), answerId, answerPostDto
        ));

        AnswerInfoDto answerInfoDto = answerMapper.getAnswerInfo(answer);

        AnswerRespDto<?> response = AnswerRespDto.builder()
                .code(ResponseCode.SUCCESS)
                .answer(answerInfoDto)
                .message("????????? ?????????????????????.")
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }



    @DeleteMapping("/{answerId}") // ?????? ?????? ??????
    public ResponseEntity<?> deleteAnswer(@PathVariable Long answerId) {

        Question question = answerService.findById(answerId).orElseThrow(
                () -> new CustomNotContentItemException(ExceptionMessage.NOT_CONTENT_ANSWER_ID)
        ).getQuestion();

        answerService.deleteById(answerId);
        Set<AnswerInfoDto> answers = answerMapper.getAnswerInfos(answerService.findAllByQuestion(question));
        AnswerRespDto<?> response = AnswerRespDto.builder()
                .code(ResponseCode.SUCCESS)
                .message("????????? ??????????????? ?????????????????????.")
                .answer(answers)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{id}/likes-up") // ?????? ????????? ??????
    public ResponseEntity<?> upAnswerLike(@PathVariable Long id) {
        Long userId = 1L;

        LikesDto likes = LikesDto.builder()
                .likes(answerService.selectLikeUp(userId, id))
                .postId(id)
                .postType(PostType.ANSWER)
                .build();

        LikeRespDto<?> response = LikeRespDto.builder()
                .code(ResponseCode.SUCCESS)
                .message("?????? ????????? ???????????????.")
                .like(likes)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{id}/likes-down") // ?????? ????????? ??????
    public ResponseEntity<?> downAnswerLike(@PathVariable Long id) {

        Long userId = 1L;

        LikesDto likes = LikesDto.builder()
                .likes(answerService.selectLikeDown(userId, id))
                .postId(id)
                .postType(PostType.ANSWER)
                .build();

        LikeRespDto<?> response = LikeRespDto.builder()
                .code(ResponseCode.SUCCESS)
                .message("?????? ????????? ???????????????.")
                .like(likes)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping("/{id}/likes-up/undo") // ?????? ????????? ????????????
    public ResponseEntity<?> upUndoAnswerLike(@PathVariable Long id) {

        Long userId = 1L;

        LikesDto likes = LikesDto.builder()
                .likes(answerService.selectLikeUpUndo(userId, id))
                .postId(id)
                .postType(PostType.ANSWER)
                .build();

        LikeRespDto<?> response = LikeRespDto.builder()
                .code(ResponseCode.SUCCESS)
                .message("?????? ????????? ?????? ???????????????.")
                .like(likes)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{id}/likes-down/undo") // ?????? ????????? ?????? ??????
    public ResponseEntity<?> downUndoAnswerLike(@PathVariable Long id) {

        Long userId = 1L;

        LikesDto likes = LikesDto.builder()
                .likes(answerService.selectLikeDownUndo(userId, id))
                .postId(id)
                .postType(PostType.ANSWER)
                .build();

        LikeRespDto<?> response = LikeRespDto.builder()
                .code(ResponseCode.SUCCESS)
                .message("?????? ????????? ?????? ???????????????.")
                .like(likes)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{id}/comments") //?????? ?????? ?????? ??????
    public ResponseEntity<?> addAnswerComment(@PathVariable Long id, @RequestBody AnswerCommentDto answerCommentDto) {

        PrincipalDetails principalDetails = PrincipalDetails.builder().
                users(usersRepository.save(users))
                .build();

        answerCommentService.save(
                commentMapper.getAnswerComment(principalDetails.getUsers(), id, answerCommentDto));

        CommentRespDto<?> response = CommentRespDto.builder()
                .code(ResponseCode.SUCCESS)
                .comment(commentMapper.getAnswerCommentInfos(answerCommentService.findAllByAnswer(id)))
                .build();


        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}/comments/{commentId}") //?????? ?????? ?????? ??????
    public ResponseEntity updateAnswerComment(
            @PathVariable Long id, @PathVariable Long commentId,
            @RequestBody AnswerCommentDto answerCommentDto) {

        PrincipalDetails principalDetails = PrincipalDetails.builder().
                users(usersRepository.save(users))
                .build();

         answerCommentService.update(id, commentId, answerCommentDto);


        CommentRespDto<?> response = CommentRespDto.builder()
                .code(ResponseCode.SUCCESS)
                .comment(commentMapper.getAnswerCommentInfos(answerCommentService.findAllByAnswer(id)))
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}/comments/{commentId}") //?????? ?????? ?????? ??????
    public ResponseEntity<?> deleteAnswerComment(@PathVariable Long id, @PathVariable Long commentId) {

        answerCommentService.deleteById(id, commentId);

        CommentRespDto<?> response = CommentRespDto.builder()
                .code(ResponseCode.SUCCESS)
                .comment(answerCommentService.findAllByAnswer(id))
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{id}/selected") // ?????? ?????? ??????
    public ResponseEntity<?> selectAnswer(@PathVariable Long id) {

        PrincipalDetails principalDetails = PrincipalDetails.builder().
                users(usersRepository.save(users))
                .build();

        boolean isAccepted = answerService.acceptAnswer(principalDetails.getUsers().getOwnerId(), id);

        AnswerRespDto<?> response = AnswerRespDto.builder()
                .code(ResponseCode.SUCCESS)
                .answer(answerMapper.getAnswerInfo(answerService.findById(id).orElseThrow(
                        () -> new CustomNotContentItemException(ExceptionMessage.NOT_CONTENT_ANSWER_ID))
                ))
                .message("????????? ?????????????????????.")
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{id}/selected/undo") //?????? ?????? ?????? ??????
    public ResponseEntity<?> undoSelectAnswer(@PathVariable Long id) {

        PrincipalDetails principalDetails = PrincipalDetails.builder().
                users(usersRepository.save(users))
                .build();

        boolean isAccepted = answerService.acceptAnswerUndo(principalDetails.getUsers().getOwnerId(), id);

        AnswerRespDto<?> response = AnswerRespDto.builder()
                .code(ResponseCode.SUCCESS)
                .answer(answerMapper.getAnswerInfo(answerService.findById(id).orElseThrow(
                        () -> new CustomNotContentItemException(ExceptionMessage.NOT_CONTENT_ANSWER_ID))
                ))
                .message("?????? ????????? ?????????????????????.")
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
