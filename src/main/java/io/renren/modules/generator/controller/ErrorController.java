package io.renren.modules.generator.controller;

import io.renren.modules.generator.utils.BaseResp;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ErrorController {
    @RequestMapping(path = "/401")
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public BaseResp unauthorized() {
        return BaseResp.error(-3, "token invalid.");
    }
}
