package kimp.telegram.service;

import kimp.notice.dto.response.NoticeDto;

public interface TelegramService {
    
    /**
     * 텔레그램 채널에 공지사항 메시지 전송
     * @param noticeDto 공지사항 정보
     */
    void sendNoticeMessage(NoticeDto noticeDto);
}