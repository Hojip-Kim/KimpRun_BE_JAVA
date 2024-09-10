package kimp.community.service.impl;

import kimp.community.entity.Board;
import kimp.community.service.CommunityService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommunityServiceImpl implements CommunityService {
    @Override
    public List<Board> getBoard(int page, int size) {
        return List.of();
    }
}
