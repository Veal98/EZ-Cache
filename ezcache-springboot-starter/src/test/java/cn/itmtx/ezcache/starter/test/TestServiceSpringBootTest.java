package cn.itmtx.ezcache.starter.test;

import cn.itmtx.ezcache.starter.bo.TestBo;
import cn.itmtx.ezcache.starter.service.TestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class) // 使用JUnit 5的Spring测试扩展
@SpringBootTest // 启动SpringBootTest
public class TestServiceSpringBootTest {

    @Autowired
    private TestService testService;

    /**
     * 测试表达式错误
     */
    @Test
    public void testGetTestBos_0() {
        try {
            // 调用方法
            List<TestBo> result = testService.getTestBos_0();
        } catch (Exception e) {
            Assertions.assertNotNull(e);
        }
    }

    @Test
    public void testGetTestBos_1() {
        // 调用方法
        List<TestBo> result = testService.getTestBos_1("test1", 1L, "test2", 1L);
        // 验证结果
        Assertions.assertNotNull(result);

        // 第二次调用方法，可以 debug 看下这次调用会直接读到缓存中的数据
        result = testService.getTestBos_1("test1", 1L, "test2", 1L);
        Assertions.assertNotNull(result);
    }
}
