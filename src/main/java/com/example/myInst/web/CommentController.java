package com.example.myInst.web;

import com.example.myInst.dto.CommentDTO;
import com.example.myInst.dto.PostDTO;
import com.example.myInst.entity.Comment;
import com.example.myInst.entity.Post;
import com.example.myInst.facade.CommentFacade;
import com.example.myInst.payload.response.MessageResponse;
import com.example.myInst.services.CommentService;
import com.example.myInst.validations.ResponseErrorValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/comment")
@CrossOrigin
public class CommentController {

    private CommentFacade commentFacade;
    private CommentService commentService;
    private ResponseErrorValidation responseErrorValidation;

    @Autowired
    public CommentController(CommentFacade commentFacade, CommentService commentService, ResponseErrorValidation responseErrorValidation) {
        this.commentFacade = commentFacade;
        this.commentService = commentService;
        this.responseErrorValidation = responseErrorValidation;
    }

    @PostMapping("/{postId}/create")
    public ResponseEntity<Object> creatComment(@Valid @RequestBody CommentDTO commentDTO,
                                               @PathVariable("postId") String postId,
                                               BindingResult bindingResult,
                                               Principal principal){
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationsService(bindingResult);
        if(!ObjectUtils.isEmpty(errors)) return errors;

        Comment comment = commentService.saveComment(Long.parseLong(postId), commentDTO, principal);
        CommentDTO createdCommentDTO = commentFacade.commentToCommentDTO(comment);

        return new ResponseEntity<>(createdCommentDTO, HttpStatus.OK);
    }

    @GetMapping("/{postId}/all")
    public ResponseEntity<List<CommentDTO>> getAllCommentToPost(@PathVariable("postId") String postId){
        List<CommentDTO> commentDTOList = commentService.getAllCommentsForPost(Long.parseLong(postId))
                .stream()
                .map(commentFacade::commentToCommentDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(commentDTOList, HttpStatus.OK);
    }

    @PostMapping("/{commentId}/delete")
    public ResponseEntity<MessageResponse> deleteComment(@PathVariable("commentId") String commentId){
        commentService.deleteComment(Long.parseLong(commentId));
        return new ResponseEntity<>(new MessageResponse("Comment was delete"), HttpStatus.OK);
    }
}
