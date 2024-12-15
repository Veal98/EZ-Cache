package cn.itmtx.ezcache.starter.controller;

import cn.itmtx.ezcache.starter.bo.TestBo;
import cn.itmtx.ezcache.starter.service.TestService;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private TestService testService;

    @GetMapping("test1")
    public void testGetTestBos() {
        List<TestBo> testBos = testService.getTestBos_1();
        Assertions.assertNotNull(testBos);
    }
}
