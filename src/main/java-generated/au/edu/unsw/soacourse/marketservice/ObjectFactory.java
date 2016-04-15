
package au.edu.unsw.soacourse.marketservice;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the au.edu.unsw.soacourse.marketservice package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ImportMarketFault_QNAME = new QName("http://marketservice.soacourse.unsw.edu.au", "importMarketFault");
    private final static QName _DownloadFileFault_QNAME = new QName("http://marketservice.soacourse.unsw.edu.au", "downloadFileFault");
    private final static QName _ConvertMarketFault_QNAME = new QName("http://marketservice.soacourse.unsw.edu.au", "convertMarketFault");
    private final static QName _ShowMarketDataFault_QNAME = new QName("http://marketservice.soacourse.unsw.edu.au", "showMarketDataFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: au.edu.unsw.soacourse.marketservice
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ImportMarketDataRequest }
     * 
     */
    public ImportMarketDataRequest createImportMarketDataRequest() {
        return new ImportMarketDataRequest();
    }

    /**
     * Create an instance of {@link ImportMarketDataResponse }
     * 
     */
    public ImportMarketDataResponse createImportMarketDataResponse() {
        return new ImportMarketDataResponse();
    }

    /**
     * Create an instance of {@link DownloadFileRequest }
     * 
     */
    public DownloadFileRequest createDownloadFileRequest() {
        return new DownloadFileRequest();
    }

    /**
     * Create an instance of {@link DownloadFileResponse }
     * 
     */
    public DownloadFileResponse createDownloadFileResponse() {
        return new DownloadFileResponse();
    }

    /**
     * Create an instance of {@link ConvertMarketDataRequest }
     * 
     */
    public ConvertMarketDataRequest createConvertMarketDataRequest() {
        return new ConvertMarketDataRequest();
    }

    /**
     * Create an instance of {@link ConvertMarketDataResponse }
     * 
     */
    public ConvertMarketDataResponse createConvertMarketDataResponse() {
        return new ConvertMarketDataResponse();
    }

    /**
     * Create an instance of {@link ShowMarketDataRequest }
     * 
     */
    public ShowMarketDataRequest createShowMarketDataRequest() {
        return new ShowMarketDataRequest();
    }

    /**
     * Create an instance of {@link ShowMarketDataResponse }
     * 
     */
    public ShowMarketDataResponse createShowMarketDataResponse() {
        return new ShowMarketDataResponse();
    }

    /**
     * Create an instance of {@link ServiceFaultType }
     * 
     */
    public ServiceFaultType createServiceFaultType() {
        return new ServiceFaultType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ServiceFaultType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://marketservice.soacourse.unsw.edu.au", name = "importMarketFault")
    public JAXBElement<ServiceFaultType> createImportMarketFault(ServiceFaultType value) {
        return new JAXBElement<ServiceFaultType>(_ImportMarketFault_QNAME, ServiceFaultType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ServiceFaultType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://marketservice.soacourse.unsw.edu.au", name = "downloadFileFault")
    public JAXBElement<ServiceFaultType> createDownloadFileFault(ServiceFaultType value) {
        return new JAXBElement<ServiceFaultType>(_DownloadFileFault_QNAME, ServiceFaultType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ServiceFaultType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://marketservice.soacourse.unsw.edu.au", name = "convertMarketFault")
    public JAXBElement<ServiceFaultType> createConvertMarketFault(ServiceFaultType value) {
        return new JAXBElement<ServiceFaultType>(_ConvertMarketFault_QNAME, ServiceFaultType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ServiceFaultType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://marketservice.soacourse.unsw.edu.au", name = "showMarketDataFault")
    public JAXBElement<ServiceFaultType> createShowMarketDataFault(ServiceFaultType value) {
        return new JAXBElement<ServiceFaultType>(_ShowMarketDataFault_QNAME, ServiceFaultType.class, null, value);
    }

}
