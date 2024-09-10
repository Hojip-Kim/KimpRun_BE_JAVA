package kimp.community.controller;

import kimp.community.service.CommunityService;
import org.springframework.web.bind.annotation.RestController;

@RestController("/board")
public class CommunityController {

    private final CommunityService communityService;

    public CommunityController(CommunityService communityService) {
        this.communityService = communityService;
    }
}
