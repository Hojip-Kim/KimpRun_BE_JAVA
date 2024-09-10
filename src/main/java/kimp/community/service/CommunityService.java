package kimp.community.service;


import kimp.community.entity.Board;

import java.util.List;

// BoardService, CommentService를 통합 및 상위 레이어(Application layer)에서 의존성 결합을 줄이기 위한 파사드-패턴 적용
public interface CommunityService {

    public List<Board> getBoard(int page, int size);

}
