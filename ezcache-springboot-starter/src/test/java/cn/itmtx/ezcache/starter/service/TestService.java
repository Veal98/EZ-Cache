package cn.itmtx.ezcache.starter.service;

import cn.itmtx.ezcache.common.annotation.EzCache;
import cn.itmtx.ezcache.starter.bo.TestBo;
import org.junit.jupiter.api.Assertions;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class TestService {

    @EzCache(key = "'getTestBos' + #args[0] + '-' + #args[1] + '-' + #args[2]", expireTimeMillis = 10 * 1000)
    public List<TestBo> getTestBos_1() {
        return Arrays.asList(new TestBo("test1", 1L), new TestBo("test2", 2L));
    }

}
