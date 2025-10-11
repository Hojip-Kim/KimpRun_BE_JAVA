package kimp.notice.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@NoArgsConstructor
public class NoticeParsedData {

    String title;
    String alink;
    LocalDateTime date;

    public NoticeParsedData(String title,  String alink, LocalDateTime date) {
        this.title = title;
        this.alink = alink;
        this.date = date;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        NoticeParsedData noticeParsedData =  (NoticeParsedData) obj;
        return Objects.equals(title, noticeParsedData.title) && Objects.equals(alink, noticeParsedData.alink) && Objects.equals(date, noticeParsedData.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, alink, date);
    }
}
