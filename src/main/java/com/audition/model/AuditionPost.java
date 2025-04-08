package com.audition.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Audition Post object")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuditionPost implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "User Id")
    private int userId;
    @Schema(description = "Post Id")
    private int id;
    @Schema(description = "Title")
    private String title;
    @Schema(description = "Body")
    private String body;
    @Schema(description = "Comments")
    List<Comment> comments = new ArrayList<>();

    public AuditionPost(final int userId, final int id, final String title, final String body,
        final List<Comment> comments) {
        this.userId = userId;
        this.id = id;
        this.title = title;
        this.body = body;
        if (Objects.isNull(comments)) {
            this.comments = List.copyOf(comments);
        }
    }

    public void setAuditionComments(final List<Comment> auditionComments) {
        if (auditionComments != null) {
            this.comments = new ArrayList<>(auditionComments);
        } else {
            this.comments.clear();
        }
    }

    public List<Comment> getComments() {
        return List.copyOf(comments);
    }

    @Override
    public String toString() {
        return "AuditionPost{" + "userId=" + userId + ", id=" + id + ", title='" + title + '\'' + ", body='" + body
            + '\'' + ", comments=" + comments + '}';
    }
}
