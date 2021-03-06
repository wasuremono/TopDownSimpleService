
package au.edu.unsw.soacourse.marketservice;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 3.0.4
 * 2016-04-15T15:23:00.575+10:00
 * Generated source version: 3.0.4
 */

@WebFault(name = "importMarketFault", targetNamespace = "http://marketservice.soacourse.unsw.edu.au")
public class ImportMarketDataFaultMsg extends Exception {
    
    private au.edu.unsw.soacourse.marketservice.ServiceFaultType importMarketFault;

    public ImportMarketDataFaultMsg() {
        super();
    }
    
    public ImportMarketDataFaultMsg(String message) {
        super(message);
    }
    
    public ImportMarketDataFaultMsg(String message, Throwable cause) {
        super(message, cause);
    }

    public ImportMarketDataFaultMsg(String message, au.edu.unsw.soacourse.marketservice.ServiceFaultType importMarketFault) {
        super(message);
        this.importMarketFault = importMarketFault;
    }

    public ImportMarketDataFaultMsg(String message, au.edu.unsw.soacourse.marketservice.ServiceFaultType importMarketFault, Throwable cause) {
        super(message, cause);
        this.importMarketFault = importMarketFault;
    }

    public au.edu.unsw.soacourse.marketservice.ServiceFaultType getFaultInfo() {
        return this.importMarketFault;
    }
}
