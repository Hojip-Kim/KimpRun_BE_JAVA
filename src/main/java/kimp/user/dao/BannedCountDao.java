package kimp.user.dao;

import kimp.user.entity.BannedCount;
import kimp.user.entity.UserAgent;

public interface BannedCountDao {

    public BannedCount createBannedCount(UserAgent userAgent);
}
