package kimp.community.controller;

import jakarta.servlet.http.HttpServletRequest;
import kimp.common.dto.PageRequestDto;
import kimp.community.entity.Board;
import kimp.community.entity.Category;
import kimp.community.service.CommunityService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/community")
public class CommunityController {

    private final CommunityService communityService;

    public CommunityController(CommunityService communityService) {
        this.communityService = communityService;
    }


}
