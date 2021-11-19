package com.example.myInst.web;

import com.example.myInst.dto.PostDTO;
import com.example.myInst.entity.Post;
import com.example.myInst.facade.PostFacade;
import com.example.myInst.payload.response.MessageResponse;
import com.example.myInst.services.PostService;
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
@RequestMapping("api/post")
@CrossOrigin
public class PostController {

    private PostFacade postFacade;
    private PostService postService;
    private ResponseErrorValidation responseErrorValidation;

    @Autowired
    public PostController(PostFacade postFacade, PostService postService, ResponseErrorValidation responseErrorValidation) {
        this.postFacade = postFacade;
        this.postService = postService;
        this.responseErrorValidation = responseErrorValidation;
    }

    @PostMapping("/create")
    public ResponseEntity<Object> creatPost(@Valid @RequestBody PostDTO postDTO,
                                            BindingResult bindingResult,
                                            Principal principal){
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationsService(bindingResult);
        if(!ObjectUtils.isEmpty(errors)) return errors;

        Post post = postService.createPost(postDTO, principal);
        PostDTO createdPostDTO = postFacade.postToPostDTO(post);

        return new ResponseEntity<>(createdPostDTO, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<PostDTO>> getAllPost(){
        List<PostDTO> postDTOList = postService.getAllPost()
                .stream()
                .map(postFacade::postToPostDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(postDTOList, HttpStatus.OK);
    }

    @GetMapping("/user/posts")
    public ResponseEntity<List<PostDTO>> getAllPostForUser(Principal principal){
        List<PostDTO> postDTOList = postService.getAllPostForUser(principal)
                .stream()
                .map(postFacade::postToPostDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(postDTOList, HttpStatus.OK);
    }

    @PostMapping("/{postId}/{username}/like")
    public ResponseEntity<PostDTO> likePost(@PathVariable("postId") String postId,
                                            @PathVariable("username") String username){
        Post post = postService.likePost(Long.parseLong(postId),username);
        PostDTO postDTO = postFacade.postToPostDTO(post);

        return new ResponseEntity<>(postDTO, HttpStatus.OK);
    }

    @PostMapping("/{postId}/delete")
    public ResponseEntity<MessageResponse> deletePost(@PathVariable("postId") String postId, Principal principal){
        postService.deletePost(Long.parseLong(postId), principal);
        return new ResponseEntity<>(new MessageResponse("Post was delete"), HttpStatus.OK);
    }
}
