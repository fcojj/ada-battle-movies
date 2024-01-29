package br.com.ada.moviesbatle.match.entity;

import br.com.ada.moviesbatle.quiz.entity.Quiz;
import br.com.ada.moviesbatle.security.entity.AccessToken;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="match")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(columnDefinition = "int default 0")
    private int amountHits;

    @Column(name="is_finished", columnDefinition = "boolean default false")
    private boolean isFinished;

    @Column(name="is_pending_quiz", columnDefinition = "boolean default false")
    private boolean isPendingQuiz;

    @Column(columnDefinition = "integer default 0")
    private int amountError;

    @CreatedDate
    @Column(nullable = false, name="created_at")
    private Date createdAt;

    @LastModifiedDate
    @Column(nullable = false, name="modified_at")
    private Date modifiedAt;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "token_id", referencedColumnName = "id")
    private AccessToken token;

    @OneToMany(mappedBy="match")
    private Set<Quiz> quizzes;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Match match = (Match) o;
        return id.equals(match.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

