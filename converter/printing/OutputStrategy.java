package converter.printing;

import converter.parsing.*;

import java.util.Map;

public interface OutputStrategy {
    String getElement(KeyValuePair keyValuePair);
    String getElement(NullElementary elementary);
    String getElement(StringElementary elementary);
    String getElement(Tree tree);
    String getElement(PairListObj pairListObj);
    String getElement(Map.Entry<String, String> attribute);
    String getElement(ArrayObj arrayObj);
}