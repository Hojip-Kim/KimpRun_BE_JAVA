package kimp.util;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public class MumurHashUtil {
    private static final HashFunction HASH_FUNCTION = Hashing.murmur3_128();

    public static long stringTo64bitHashCode(String str){
        return HASH_FUNCTION.hashString(str, StandardCharsets.UTF_8).asLong();
    }

    public static String stringTo128bitHashCode(String str){
        return HASH_FUNCTION.hashString(str, StandardCharsets.UTF_8).toString();
    }

}
