package components;

import java.util.HashMap;
import java.util.Map;

public class Parameters extends HashMap<String, Double> {

    public void initialise() {
       globalParameters = new HashMap<>(1000);
       globalParameters.put("price_GBP",1.0);
       globalParameters.put("price_Stock", 1.0);
       globalParameters.put("price_SampleLiability", 1.0);
       globalParameters.put("bank_leverage_target", 0.05);
       globalParameters.put("bank_minimum_leverage", 0.02);

       //TODO: List all the parameters!

    }

    public Map<Object, Object> getGlobalParameters() {
        return globalParameters;
    }

    private HashMap<Object, Object> globalParameters;
}
