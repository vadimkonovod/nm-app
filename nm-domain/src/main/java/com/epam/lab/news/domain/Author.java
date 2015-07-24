package com.epam.lab.news.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Formula;

@Entity
@Table(name = "AUTHOR")
@SequenceGenerator(name = "PK", sequenceName = "AUTHOR_SEQ")
@NamedQueries({ @NamedQuery(name = "getAllAuthor", query = "SELECT a FROM Author a") })
public class Author implements Serializable {
    private static final long serialVersionUID = 3584662828996434856L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "PK")
    @Column(name = "AUTHOR_ID")
    @Min(value = 1, message = "error.author.id")
    private Long authorId;

    @Column(name = "NAME")
    @NotNull(message = "error.author.name.notNull")
    @Size(min = 1, max = 30, message = "error.author.name.size")
    private String name;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    @JoinTable(name = "NEWS_AUTHORS", joinColumns = { @JoinColumn(name = "AUTHOR_ID") },
            inverseJoinColumns = { @JoinColumn(name = "NEWS_ID") }, uniqueConstraints = { @UniqueConstraint(
                    columnNames = { "NEWS_ID", "AUTHOR_ID" }) })
    private List<News> news;

    @Formula("(SELECT COUNT(*) FROM NEWS_AUTHORS WHERE NEWS_AUTHORS.AUTHOR_ID = AUTHOR_ID)")
    private Long newsCount;

    public Author() {
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<News> getNews() {
        return news;
    }

    public void setNews(List<News> news) {
        this.news = news;
    }

    public Long getNewsCount() {
        return newsCount;
    }

    public void setNewsCount(Long newsCount) {
        this.newsCount = newsCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.authorId, this.name);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Author))
            return false;
        Author other = (Author) obj;
        if (authorId == null) {
            if (other.authorId != null)
                return false;
        } else if (!authorId.equals(other.authorId))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Author.class.getCanonicalName() + "[");
        sb.append("id=").append(authorId).append(", ");
        sb.append("name=").append(name);
        sb.append("]");
        return sb.toString();
    }
}
