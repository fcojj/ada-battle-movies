package br.com.ada.moviesbatle.quiz.entity;

import br.com.ada.moviesbatle.match.entity.Match;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="quiz")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String movieId1;

    @Column(nullable = false)
    private String movieId2;

    @Column(nullable = false)
    private String movieWinId;

    @Column(nullable = false)
    private boolean isActive;

    @CreatedDate
    @Column(nullable = false, name="created_at")
    private Date createdAt;

    @LastModifiedDate
    @Column(nullable = false, name="modified_at")
    private Date modifiedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="match_id", nullable=false)
    private Match match;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quiz quiz = (Quiz) o;
        return id.equals(quiz.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}