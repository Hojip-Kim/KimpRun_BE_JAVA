package kimp.util;

public class IpMaskUtil {
    private IpMaskUtil() {}

    public static String mask(String ip) {
        if (ip == null || ip.isEmpty()) {
            return null;
        }

        if(ip.contains(".")) {
            // IPv4 처리
            String[] ipParts = ip.split("\\.");
            if(ipParts.length >= 2) {
                return ipParts[0] + "." + ipParts[1] + ".***.***";
            }
        }else if (ip.contains(":")) {
            // IPv6 처리
            String[] ipParts = ip.split(":");
            int keepCount = Math.min(3, ipParts.length);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < keepCount; i++) {
                if(i > 0) {
                   sb.append(":");
                }
                sb.append(ipParts[i]);
            }
            sb.append(":**:**:**:**");
            return sb.toString();
        }
        // IPv4 / IPv6 형식이 아니면 null 반환
        return null;
    }
}
