package kimp.user.dao;

import kimp.user.entity.AnnonyMousMember;

import java.util.List;

public interface AnnonyMousMemberDao {

    public AnnonyMousMember createAnnonymousMember(String uuid, String ip);

    public AnnonyMousMember getAnnonymousMemberByUuid(String uuid);

    public AnnonyMousMember getAnnonymousMemberByIp(String ip);

    public void deleteAnnonymousMember(String uuid);

    public List<AnnonyMousMember> getAllAnnonymousMember();

    public List<AnnonyMousMember> getAllAnnonymousMemberBeforeExpireTime(Long expireTime);

}
