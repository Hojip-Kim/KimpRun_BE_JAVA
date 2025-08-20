package kimp.scrap.dto.bithumb;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
public class BithumbNotice {
    private int id;
    private String boardType;
    private String categoryName1;
    private String categoryName2;
    private String title;
    private String topFixYn;
    private String publicationDateTime;
    private String modifyDateTime;
    private String modifyDateTimeExposureYn;

    public BithumbNotice(int id, String boardType, String categoryName1, String categoryName2, String title, String topFixYn, String publicationDateTime, String modifyDateTime, String modifyDateTimeExposureYn) {
        this.id = id;
        this.boardType = boardType;
        this.categoryName1 = categoryName1;
        this.categoryName2 = categoryName2;
        this.title = title;
        this.topFixYn = topFixYn;
        this.publicationDateTime = publicationDateTime;
        this.modifyDateTime = modifyDateTime;
        this.modifyDateTimeExposureYn = modifyDateTimeExposureYn;
    }

    public LocalDateTime getPublicationDateTime() {
        return LocalDateTime.parse(publicationDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public LocalDateTime getModifyDateTime() {
        return LocalDateTime.parse(modifyDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
