package kimp.user.service.member;

import kimp.user.dto.response.AnnonymousMemberResponseDto;
import kimp.user.vo.*;

public interface AnnonyMousService {

    public AnnonymousMemberResponseDto createAnnonymousMember(String uuid, String ip);

    public AnnonymousMemberResponseDto updateAnnonymousMemberIp(UpdateAnonNicknameVo vo);

    public AnnonymousMemberResponseDto getAnnonymousMemberByUuidOrIp(GetAnnonymousMemberInfoVo vo);

    public void applicationBanMember(ApplicationBanMemberVo vo);

    public void applicationUnBanMember(ApplicationUnBanMemberVo vo);

    public void cdnBanMember(CdnBanMemberVo vo);

    public void cdnUnBanMember(CdnUnbanMemberVo vo);

    public void deleteAnnonymousMember(String uuid);

}
