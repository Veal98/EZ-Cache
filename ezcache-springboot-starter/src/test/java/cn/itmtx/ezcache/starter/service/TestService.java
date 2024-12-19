package cn.itmtx.ezcache.starter.service;

import cn.itmtx.ezcache.common.annotation.EzCache;
import cn.itmtx.ezcache.starter.bo.TestBo;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class TestService {

    /**
     * 测试表达式错误
     * @return
     */
    @EzCache(key = "'getTestBos' + #args[0] + '-' + #args[1] + #args[2] + '-' + #args[3]", expireTimeMillis = 10 * 1000)
    public List<TestBo> getTestBos_0() {
        return Arrays.asList(new TestBo("test1", 1L));
    }

    @EzCache(key = "'getTestBos' + #args[0] + '-' + #args[1] + #args[2] + '-' + #args[3]", expireTimeMillis = 10 * 1000)
    public List<TestBo> getTestBos_1(String name1, Long id1, String name2, Long id2) {
        return Arrays.asList(new TestBo(name1, id1), new TestBo(name2, id2));
    }

}
