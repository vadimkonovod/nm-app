package com.epam.lab.news.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "NEWS")
@SequenceGenerator(name = "PK", sequenceName = "NEWS_SEQ")
@NamedQueries({
        @NamedQuery(name = "getAllNews", query = "SELECT n FROM News n ORDER BY n.newsId DESC, n.modificationDate DESC"),
        @NamedQuery(name = "getNewsByAuthor",
                query = "SELECT n FROM News n WHERE :author MEMBER OF n.authors ORDER BY n.newsId DESC, n.modificationDate DESC"),
        @NamedQuery(name = "getNewsByTag",
                query = "SELECT n FROM News n WHERE :tag MEMBER OF n.tags ORDER BY n.newsId DESC, n.modificationDate DESC"),
        @NamedQuery(name = "getNewsOrderedByComments",
                query = "SELECT n FROM News n LEFT JOIN n.comments c GROUP BY n.newsId, n.title, n.shortText, n.fullText,"
                        + " n.creationDate, n.modificationDate ORDER BY COUNT(c.newsId) DESC") })
public class News implements Serializable {
    private static final long serialVersionUID = -3324369138743170603L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "PK")
    @Column(name = "NEWS_ID")
    @Min(value = 1, message = "error.news.id")
    private Long newsId;

    @Column(name = "TITLE")
    @NotNull(message = "error.news.title.notNull")
    @Size(min = 1, max = 30, message = "error.news.title.size")
    private String title;

    @Column(name = "SHORT_TEXT")
    @NotNull(message = "error.news.shortText.notNull")
    @Size(min = 1, max = 100, message = "error.news.shortText.size")
    private String shortText;

    @Column(name = "FULL_TEXT")
    @NotNull(message = "error.news.fullText.notNull")
    @Size(min = 1, max = 2000, message = "error.news.fullText.size")
    private String fullText;

    @Column(name = "CREATION_DATE", updatable = false, nullable = false)
    @CreationTimestamp
    @JsonFormat(pattern = "dd-MMM-yy")
    private Date creationDate;

    @Column(name = "MODIFICATION_DATE", nullable = false)
    @UpdateTimestamp
    @JsonFormat(pattern = "dd-MMM-yy")
    private Date modificationDate;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "NEWS_AUTHORS", joinColumns = { @JoinColumn(name = "NEWS_ID") },
            inverseJoinColumns = { @JoinColumn(name = "AUTHOR_ID") })
    private Set<Author> authors;

    @OneToMany(mappedBy = "newsId", fetch = FetchType.LAZY, cascade = { CascadeType.REMOVE })
    private List<Comment> comments;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "NEWS_TAG", joinColumns = { @JoinColumn(name = "NEWS_ID") },
                                    inverseJoinColumns = { @JoinColumn(name = "TAG") })
    private Set<Tag> tags;

    public News() {
    }

    public Long getNewsId() {
        return newsId;
    }

    public void setNewsId(Long newsId) {
        this.newsId = newsId;
    }

    public String getShortText() {
        return shortText;
    }

    public void setShortText(String shortText) {
        this.shortText = shortText;
    }

    public String getFullText() {
        return fullText;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
    }

    public Set<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<Author> authors) {
        this.authors = authors;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.newsId, this.shortText, this.fullText, this.title, this.creationDate,
                this.modificationDate);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass())
            return false;
        News other = (News) obj;
        return Objects.deepEquals(this, other);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(News.class.getCanonicalName() + "[");
        sb.append("id=").append(newsId).append(", ");
        sb.append("shortText=").append(shortText).append(", ");
        sb.append("fullText=").append(fullText).append(", ");
        sb.append("title=").append(title).append(", ");
        sb.append("creationDate=").append(creationDate).append(", ");
        sb.append("modificationDate=").append(modificationDate);
        sb.append("]");
        return sb.toString();
    }
}