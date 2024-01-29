package br.com.ada.moviesbatle.security.entity;

import br.com.ada.moviesbatle.match.entity.Match;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="access_token")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class AccessToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private String username;

    @Column(name="expire_at", nullable = false)
    private Date expireAt;

    @Column(name="is_BlackList", columnDefinition = "boolean default false")
    private boolean isBlackList;

    @CreatedDate
    @Column(nullable = false, name="created_at")
    private Date createdAt;

    @LastModifiedDate
    @Column(nullable = false, name="modified_at")
    private Date modifiedAt;

    @OneToOne(mappedBy = "token")
    private Match match;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccessToken accessToken = (AccessToken) o;
        return id.equals(accessToken.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
