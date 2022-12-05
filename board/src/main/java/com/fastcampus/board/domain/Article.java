package com.fastcampus.board.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@ToString
@Table(indexes = {
        @Index(columnList = "title"),
        @Index(columnList = "hashtag"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 기본적으로 column 어노테이션을 안넣어도 column으로 만들어진다.
    // 대신 default 가 nullable = true 이므로 nullable = false를 만들때 어노테이션을 넣어준다
    @Setter @Column(nullable = false) private String title;
    @Setter @Column(nullable = false, length = 10000) private String content;
    @Setter private String hashtag;

    @ToString.Exclude // articleComments 에도 tostring이 있기 떄문에 서로 양방향으로 참조하다가 무한 루프로 가버림. 그래서 한쪽에서 끊어줘야함ㅑ
    @OrderBy("id ")
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private final Set<ArticleComment> articleComments = new LinkedHashSet<>();

    @CreatedDate @Column(nullable = false) private LocalDateTime createdAt;
    @CreatedBy @Column(nullable = false, length = 100) private String createdBy;
    @LastModifiedDate @Column(nullable = false) private LocalDateTime modifiedAt;
    @LastModifiedBy @Column(nullable = false, length = 100) private String modifiedBy;

    private Article(String title, String content, String hashtag) {
        this.title = title;
        this.content = content;
        this.hashtag = hashtag;
    }

    // 생성자는 private는 안됨. public은 너무 열려있음.
    protected Article() {}

    // Article 이 private으로 닫아뒀기 때문에 new로 생성을 못한다.
    // 대신 of를 통해 생성을 하도록 유도
    public static Article of(String title, String content, String hashtag) {
        return new Article(title, content, hashtag);
    }

    // lombok 에서 equal 관련 기능을 제공하지만 그렇다면 모든 칼럼이 동일해야 같다고 판단
    // 그래서 따로 equals 정의가 필요하다.
    // return id not null -> 영속화가 아직 안되어 있는 경우
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Article article = (Article) o;
        return id != null && id.equals(article.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
