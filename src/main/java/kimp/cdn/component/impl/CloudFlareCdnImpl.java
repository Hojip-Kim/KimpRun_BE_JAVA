package kimp.cdn.component.impl;

import kimp.cdn.component.Cdn;
import kimp.cdn.component.dto.request.CfConfiguration;
import kimp.cdn.component.dto.request.CfCreateAccessRuleReqeustDto;
import kimp.cdn.component.dto.response.CfAccessRuleResult;
import kimp.cdn.component.dto.response.CfCommonResponseDto;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Qualifier("cloudflare")
public class CloudFlareCdnImpl implements Cdn {

    private final RestClient cdnClient;

    @Value("${cdn.cloudflare.scope}")
    private String scope;
    @Value("${cdn.cloudflare.scope_id}")
    private String scopeId;

    public CloudFlareCdnImpl(RestClient cdnClient) {
        this.cdnClient = cdnClient;
    }

    @Override
    public String requestIpBan(String ip, String note) {
        CfCreateAccessRuleReqeustDto cfCreateAccessRuleReqeustDto = CfCreateAccessRuleReqeustDto.builder()
                .mode("block")
                .configurations(new CfConfiguration("ip", ip))
                .notes(note)
                .build();

        CfCommonResponseDto<CfAccessRuleResult> response = cdnClient.post()
                .uri("/{scope}/{id}/firewall/access_rule/rules", scope, scopeId)
                .body(cfCreateAccessRuleReqeustDto)
                .retrieve()
                .body(new ParameterizedTypeReference<CfCommonResponseDto<CfAccessRuleResult>>() {
                });

        if(response == null || !response.isSuccess()) {
            throw new KimprunException(KimprunExceptionEnum.IP_BAN_FAILED, "ip ban failed", HttpStatus.INTERNAL_SERVER_ERROR, "CloudFlareCdnImpl.requestIpBan");
        }

        return response.getResult().getId();
    }

    @Override
    public Optional<String> findRuleIdByIp(String ip) {
        Map<String, Object> maps =  cdnClient.get()
                .uri("/{scope}/{id}/firewall/access_rules/rules", scope, scopeId)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});
        if(maps == null){
            return Optional.empty();
        }

        List<Map<String, Object>> accessRules = (List<Map<String, Object>>) maps.get("result");
        if(accessRules == null) {
            return Optional.empty();
        }

        return accessRules.stream()
                .filter(it -> {
                    Map<String, Object> cloudflareGroups = (Map<String, Object>) it.get("configuration");
                    return cloudflareGroups != null && ip.equals(String.valueOf(cloudflareGroups.get("value")));
                })
                .map(it -> String.valueOf(it.get("id")))
                .findFirst();
    }

    @Override
    public void deleteCloudflareRule(String ruleId) {
        cdnClient.delete()
                .uri("/{scope}/{id}/firewall/access_rules/rules/{ruleId}", scope, scopeId, ruleId)
                .retrieve()
                .toBodilessEntity();
    }
}
