package mz.co.hi.web.users;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by Mario Junior.
 */
public interface UDetailsProvider {

    public Map getDetails(String username);

}
