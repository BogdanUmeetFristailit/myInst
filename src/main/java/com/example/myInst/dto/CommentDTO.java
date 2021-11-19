package com.example.myInst.dto;

import com.example.myInst.entity.Post;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;

@Data
public class CommentDTO {

    private Long id;
    private String username;
    @NotEmpty
    private String message;

}
