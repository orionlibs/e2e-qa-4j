package com.yapily.e2eqa4j;

import com.yapily.e2eqa4j.model.TestSuite2;
import com.yapily.e2eqa4j.model.YAMLNode;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import tools.jackson.dataformat.yaml.YAMLMapper;

//@SpringBootTest(args = {"run", "--path=src/test/resources/e2e", "--lib-dir=src/test/resources/lib", "--var=\"gatewayUrl=https://example.com\"", "--var=\"institutionId=mock-sandbox\""})
@SpringBootTest
@ActiveProfiles("test")
class YAMLParserTest
{
    @Autowired YAMLMapper yamlMapper;


    @BeforeEach
    void setup()
    {
    }


    @Test
    void test1() throws IOException
    {
        ClassPathResource resource = new ClassPathResource("test1.yaml");
        //System.out.println(">>>>>>>>>" + resource.getContentAsString(StandardCharsets.UTF_8));
        YAMLNode root = yamlMapper.readValue(resource.getInputStream(), YAMLNode.class);
        TestSuite2 testSuite2 = new TestSuite2(root);
        System.out.println(">>>>>>>>" + testSuite2.getVars());
        System.out.println(">>>>>>>>" + testSuite2.getArray());
        System.out.println(">>>>>>>>" + testSuite2.array);
        //System.out.println(">>>>>>>>" + testSuite2.yaml.getChildren());
        //assertThat(testSuite.yaml.getByDottedPath("vars.alpha.beta.gamma.delta").orElse("no name").toString()).isEqualTo("deltaValue");
    }
}
