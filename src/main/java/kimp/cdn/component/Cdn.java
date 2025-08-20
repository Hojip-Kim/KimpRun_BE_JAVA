package kimp.cdn.component;

import java.util.Optional;

public interface Cdn {

    /**
     *
     * @param ip 클라우드 플레어 단 밴을 먹일 ip (혹은 대역폭)
     * @return String Clouflare Rule id 반환 (후에 밴 해제를 위한 rule id)
     */
    public String requestIpBan(String ip, String note);

    public Optional<String> findRuleIdByIp(String ip);

    public void deleteCloudflareRule(String ruleId);

}
