package Weather;

import java.util.Map;

/**
 * Created by jklei on 5/29/2017.
 */
public interface Weather {
    Map<String, Double> getUpdate();
    String getName();
}
