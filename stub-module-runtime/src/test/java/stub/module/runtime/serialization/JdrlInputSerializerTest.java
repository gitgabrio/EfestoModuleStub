package stub.module.runtime.serialization;

import org.junit.jupiter.api.Test;
import stub.module.runtime.api.model.JdrlInput;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.efesto.common.core.utils.JSONUtils.getObjectMapper;
import static stub.module.runtime.testutils.TestUtils.getJdrlInput;

class JdrlInputSerializerTest {

    @Test
    void serializeTest() throws IOException {
        JdrlInput jdrlInput = getJdrlInput();
        String retrieved = getObjectMapper().writeValueAsString(jdrlInput);
        String expected = "{\"modelLocalUriId\":{\"model\":\"jdrl\",\"basePath\":\"/LoanRules\",\"fullPath\":\"/jdrl/LoanRules\"},\"inputData\":{\"modelName\":\"modelname\",\"packageName\":\"packageName\",\"inserts\":[{\"kind\":\"org.drools.example.LoanApplication\",\"value\":{\"id\":\"ABC10001\",\"applicant\":{\"name\":\"John\",\"age\":45},\"amount\":2000,\"deposit\":1000,\"approved\":false}},{\"kind\":\"org.drools.example.LoanApplication\",\"value\":{\"id\":\"ABC10002\",\"applicant\":{\"name\":\"Paul\",\"age\":25},\"amount\":5000,\"deposit\":100,\"approved\":false}},{\"kind\":\"org.drools.example.LoanApplication\",\"value\":{\"id\":\"ABC10015\",\"applicant\":{\"name\":\"George\",\"age\":12},\"amount\":1000,\"deposit\":100,\"approved\":false}}],\"globals\":{\"approvedApplications\":[],\"maxAmount\":5000}}}";
        assertThat(retrieved).isEqualTo(expected);
    }


}