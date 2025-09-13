package com.old.silence.auth.center.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author moryzang
 */
@FeignClient(name = SilenceAuthCenterClientConstants.SERVICE_NAME,
        contextId = "silence-auth-center-client", path = "/internal/permission")
public interface SilenceAuthCenterPermissionClient {

    void findPrivileges(@RequestParam String username, @RequestParam String appCode);

    void findResources(@RequestParam String appCode, @RequestParam String um,
                       @RequestParam(required = false) String regionFlag);
}