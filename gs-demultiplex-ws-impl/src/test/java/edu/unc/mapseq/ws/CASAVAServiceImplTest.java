package edu.unc.mapseq.ws;

import java.io.File;
import java.net.MalformedURLException;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.junit.Test;

import edu.unc.mapseq.ws.gs.demultiplex.GSDemultiplexService;

public class CASAVAServiceImplTest {

    @Test
    public void testUpload() {

        QName serviceQName = new QName("http://casava.gs.ws.mapseq.unc.edu", "GSCASAVAService");
        Service service = Service.create(serviceQName);
        QName portQName = new QName("http://casava.gs.ws.mapseq.unc.edu", "GSCASAVAPort");
        service.addPort(portQName, SOAPBinding.SOAP11HTTP_BINDING, String.format("http://%s:%d/cxf/GSCASAVAService", "152.54.3.109", 8181));
        GSDemultiplexService casavaService = service.getPort(GSDemultiplexService.class);

        Client cl = ClientProxy.getClient(casavaService);
        HTTPConduit httpConduit = (HTTPConduit) cl.getConduit();
        httpConduit.getClient().setReceiveTimeout(5 * 60 * 1000L);

        Binding binding = ((BindingProvider) service.getPort(portQName, GSDemultiplexService.class)).getBinding();
        ((SOAPBinding) binding).setMTOMEnabled(true);

        try {
            // File f = new File("/home/jdr0887", "140912_UNC17-D00216_0247_BC4G46ANXX.csv");
            // File f = new File("/home/jdr0887", "141006_UNC17-D00216_0249_BC4G45ANXX.csv");
            File f = new File("/home/jdr0887", "151123_UNC16-SN851_0629_AH5FG2ADXX.csv");
            DataHandler handler = new DataHandler(f.toURI().toURL());
            Long id = casavaService.uploadSampleSheet(handler, "151123_UNC16-SN851_0629_AH5FG2ADXX");
            System.out.println(id);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testAssertDirectoryExists() {
        QName serviceQName = new QName("http://ws.mapseq.unc.edu", "SequencerRunService");
        QName portQName = new QName("http://ws.mapseq.unc.edu", "SequencerRunPort");
        Service service = Service.create(serviceQName);
        service.addPort(portQName, SOAPBinding.SOAP11HTTP_MTOM_BINDING,
                String.format("http://%s:%d/cxf/SequencerRunService", "biodev2.its.unc.edu", 8181));
        GSDemultiplexService casavaService = service.getPort(GSDemultiplexService.class);
        System.out.println(casavaService.assertDirectoryExists("121212_UNC13-SN749_0207_BD1LCDACXX"));
        System.out.println(casavaService.assertDirectoryExists("130116_UNC16-SN851_0202_AD1TP9ACXX"));
        System.out.println(casavaService.assertDirectoryExists("130117_UNC14-SN744_0297_BD1RDUACXX"));
        System.out.println(casavaService.assertDirectoryExists("130129_UNC15-SN850_0257_BC1M1EACXX"));
        System.out.println(casavaService.assertDirectoryExists("130201_UNC15-SN850_0258_AD1RW7ACXX"));
        System.out.println(casavaService.assertDirectoryExists("130201_UNC16-SN851_0210_BC1NN7ACXX"));
        System.out.println(casavaService.assertDirectoryExists("130220_UNC13-SN749_0232_AD1VEJACXX"));
        System.out.println(casavaService.assertDirectoryExists("130118_UNC16-SN851_0205_BC1HHAACXX"));

    }

}
