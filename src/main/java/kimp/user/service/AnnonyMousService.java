package kimp.user.service;

import kimp.user.dto.request.*;
import kimp.user.dto.response.AnnonymousMemberResponseDto;

public interface AnnonyMousService {

    public AnnonymousMemberResponseDto createAnnonymousMember(String uuid, String ip);

    public AnnonymousMemberResponseDto updateAnnonymousMemberIp(String uuid, String ip);

    public AnnonymousMemberResponseDto getAnnonymousMemberByUuidOrIp(AnnonymousMemberInfoRequestDto request);

    public void applicationBanMember(ApplicationBanMemberRequestDto request);

    public void applicationUnBanMember(ApplicationUnBanMemberRequestDto request);

    public void cdnBanMember(CdnBanMemberRequestDto request);

    public void cdnUnBanMember(CdnUnbanMemberRequestDto request);

    public void deleteAnnonymousMember(String uuid);

}
