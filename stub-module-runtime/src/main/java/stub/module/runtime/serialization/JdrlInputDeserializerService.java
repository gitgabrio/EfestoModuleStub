package stub.module.runtime.serialization;

import com.fasterxml.jackson.databind.JsonDeserializer;
import org.kie.efesto.common.core.serialization.DeserializerService;
import stub.module.runtime.model.JdrlInput;

public class JdrlInputDeserializerService implements DeserializerService<JdrlInput> {

    @Override
    public Class<JdrlInput> type() {
        return JdrlInput.class;
    }

    @Override
    public JsonDeserializer<? extends JdrlInput> deser() {
        return new JdrlInputDeserializer();
    }
}
