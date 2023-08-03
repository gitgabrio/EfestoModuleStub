package stub.module.runtime.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import stub.module.runtime.model.JdrlInput;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.efesto.common.core.utils.JSONUtils.getObjectMapper;
import static stub.module.runtime.testutils.TestUtils.getJdrlInput;

class JdrlInputDeserializerTest {

    @Test
    void deserializeTest() throws JsonProcessingException {
        String toDeserialize = "{\"modelLocalUriId\":{\"model\":\"jdrl\",\"basePath\":\"/LoanRules\",\"fullPath\":\"/jdrl/LoanRules\"},\"inputData\":{\"modelName\":\"modelname\",\"packageName\":\"packageName\",\"inserts\":[{\"kind\":\"org.drools.example.LoanApplication\",\"value\":{\"id\":\"ABC10001\",\"applicant\":{\"name\":\"John\",\"age\":45},\"amount\":2000,\"deposit\":1000,\"approved\":false}},{\"kind\":\"org.drools.example.LoanApplication\",\"value\":{\"id\":\"ABC10002\",\"applicant\":{\"name\":\"Paul\",\"age\":25},\"amount\":5000,\"deposit\":100,\"approved\":false}},{\"kind\":\"org.drools.example.LoanApplication\",\"value\":{\"id\":\"ABC10015\",\"applicant\":{\"name\":\"George\",\"age\":12},\"amount\":1000,\"deposit\":100,\"approved\":false}}],\"globals\":[{\"key\":\"approvedApplications\",\"kind\":\"java.util.ArrayList\",\"value\":[]},{\"key\":\"maxAmount\",\"kind\":\"java.lang.Integer\",\"value\":5000}]}}";
        JdrlInput retrieved = getObjectMapper().readValue(toDeserialize, JdrlInput.class);
        assertThat(retrieved).isNotNull();
        JdrlInput expected = getJdrlInput();
        assertThat(getObjectMapper().writeValueAsString(retrieved)).isEqualTo(getObjectMapper().writeValueAsString(expected));
    }
}