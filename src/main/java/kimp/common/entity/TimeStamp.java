package kimp.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = null;

    @CreatedDate
    @Column(name = "registed_at", updatable = false)
    private LocalDateTime registedAt;


    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
