package kimp.user.dao.impl;

import kimp.user.dao.BannedCountDao;
import kimp.user.entity.BannedCount;
import kimp.user.entity.UserAgent;
import kimp.user.repository.user.BannedCountRepository;
import org.springframework.stereotype.Repository;

@Repository
public class BannedCountDaoImpl implements BannedCountDao {

    private final BannedCountRepository bannedCountRepository;

    public BannedCountDaoImpl(BannedCountRepository bannedCountRepository) {
        this.bannedCountRepository = bannedCountRepository;
    }


    @Override
    public BannedCount createBannedCount(UserAgent userAgent) {
        if(userAgent == null){
            throw new IllegalArgumentException("userAgent is null");
        }
        BannedCount bannedCount = new BannedCount(userAgent);

        return bannedCountRepository.save(bannedCount);

    }
}
