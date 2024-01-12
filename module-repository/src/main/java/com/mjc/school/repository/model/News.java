package com.mjc.school.repository.model;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "news")
public class News extends AuditingEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "content", nullable = false)
    private String content;
    @JoinColumn(name = "author_id")
    @ManyToOne
    private Author author;
    @Builder.Default
    @ManyToMany
    @JoinTable(name = "news_tag", joinColumns = @JoinColumn(name = "news_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<Tag> tags = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "news")
    private List<Comment> comments = new ArrayList<>();

    public void setAuthor(Author authorModel) {
        this.author = authorModel;
        authorModel.getNews().add(this);
    }

    public void addTag(Tag tag) {
        this.tags.add(tag);
        tag.getNews().add(this);
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
        comment.setNews(this);
    }
}