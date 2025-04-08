package com.audition.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Comments object")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "PostId")
    private int postId;
    @Schema(description = "Comment Id")
    private int id;
    @Schema(description = "Name")
    private String name;
    @Schema(description = "Email")
    private String email;
    @Schema(description = "Body")
    private String body;

    public Comment(final int postId, final int id, final String name, final String email, final String body) {
        this.postId = postId;
        this.id = id;
        this.name = name;
        this.email = email;
        this.body = body;
    }

    @Override
    public String toString() {
        return "Comment{" + "postId=" + postId + ", id=" + id + ", name='" + name + '\'' + ", email='" + email + '\''
            + ", body='" + body + '\'' + '}';
    }
}