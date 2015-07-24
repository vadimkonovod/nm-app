package com.epam.lab.news.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "COMMENTS")
@SequenceGenerator(name = "PK", sequenceName = "COMMENTS_SEQ")
@NamedQueries({ @NamedQuery(name = "getAllComment", query = "SELECT c FROM Comment c ORDER BY c.creationDate DESC") })
public class Comment implements Serializable {
    private static final long serialVersionUID = 3099892340004519689L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "PK")
    @Column(name = "COMMENT_ID")
    @Min(value = 1, message = "error.comment.id")
    private Long commentId;

    @Column(name = "COMMENT_TEXT")
    @NotNull(message = "error.comment.text.notNull")
    @Size(min = 1, max = 300, message = "error.comment.text.size")
    private String commentText;

    @Column(name = "CREATION_DATE", nullable = false)
    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd, HH:mm")
    private Date creationDate;

    @Column(name = "NEWS_ID")
    private Long newsId;

    public Comment() {
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Long getNewsId() {
        return newsId;
    }

    public void setNewsId(Long newsId) {
        this.newsId = newsId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.commentId, this.commentText, this.creationDate, this.newsId);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass())
            return false;
        Comment other = (Comment) obj;
        return Objects.deepEquals(this, other);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Comment.class.getCanonicalName() + "[");
        sb.append("id=").append(commentId).append(", ");
        sb.append("commentText=").append(commentText).append(", ");
        sb.append("creationDate=").append(creationDate).append(", ");
        sb.append("newsId=").append(newsId);
        sb.append("]");
        return sb.toString();
    }
}
