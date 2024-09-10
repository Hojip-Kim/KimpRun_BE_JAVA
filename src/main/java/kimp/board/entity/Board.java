package kimp.board.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import kimp.common.entity.TimeStamp;

@Entity
public class Board extends TimeStamp {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String content;



}
