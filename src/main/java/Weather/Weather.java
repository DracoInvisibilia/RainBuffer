package Weather;

import java.util.Date;
import java.util.Map;

/**
 * Created by jklei on 5/29/2017.
 */
public interface Weather {
    Map<Date, Double> getUpdate();
    String getName();
}
