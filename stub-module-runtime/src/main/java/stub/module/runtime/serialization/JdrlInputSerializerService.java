package stub.module.runtime.serialization;

import com.fasterxml.jackson.databind.JsonSerializer;
import org.kie.efesto.common.core.serialization.SerializerService;
import stub.module.runtime.model.JdrlInput;

public class JdrlInputSerializerService implements SerializerService<JdrlInput> {

    @Override
    public Class<JdrlInput> type() {
        return JdrlInput.class;
    }

    @Override
    public JsonSerializer<? extends JdrlInput> ser() {
        return new JdrlInputSerializer();
    }
}
