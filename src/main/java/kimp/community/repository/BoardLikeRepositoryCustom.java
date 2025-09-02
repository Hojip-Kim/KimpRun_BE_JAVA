package kimp.community.repository;

public interface BoardLikeRepositoryCustom {
    boolean existsByBoardIdAndMemberId(Long boardId, Long memberId);
    void addLikeIfNotExists(Long boardId, Long memberId);
}