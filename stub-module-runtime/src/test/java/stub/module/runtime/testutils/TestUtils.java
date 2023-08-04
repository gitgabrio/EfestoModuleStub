package stub.module.runtime.testutils;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.drools.example.Applicant;
import org.drools.example.LoanApplication;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.runtimemanager.api.model.EfestoMapInputDTO;
import stub.module.runtime.api.model.JdrlInput;

import java.util.*;

import static org.kie.efesto.common.core.utils.JSONUtils.getObjectMapper;

public class TestUtils {
    public static JdrlInput getJdrlInput() throws JsonProcessingException {
        List<Object> inserts = Arrays.asList(new LoanApplication("ABC10001", new Applicant("John", 45), 2000, 1000),
                new LoanApplication("ABC10002", new Applicant("Paul", 25), 5000, 100),
                new LoanApplication("ABC10015", new Applicant("George", 12), 1000, 100));
        List<LoanApplication> approvedApplications = new ArrayList<>();
        final Map<String, Object> globals = new HashMap<>();
        globals.put("approvedApplications", approvedApplications);
        globals.put("maxAmount", 5000);
        String modelLocalUriIdString = "{\"model\":\"jdrl\",\"basePath\":\"/LoanRules\",\"fullPath\":\"/jdrl/LoanRules\"}";
        ModelLocalUriId modelLocalUriId = getObjectMapper().readValue(modelLocalUriIdString, ModelLocalUriId.class);
        EfestoMapInputDTO darMapInputDTO = new EfestoMapInputDTO(inserts, globals,
                Collections.emptyMap(),
                Collections.emptyMap(),
                "modelname",
                "packageName");
        return new JdrlInput(modelLocalUriId, darMapInputDTO);
    }
}
