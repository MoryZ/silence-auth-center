package com.old.silence.auth.center.client;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author moryzang
 */
@FeignClient(name = SilenceAuthCenterClientConstants.SERVICE_NAME,
        contextId = "silence-auth-center-client", path = "/internal/v1")
public interface SilenceAuthCenterClient {

}
