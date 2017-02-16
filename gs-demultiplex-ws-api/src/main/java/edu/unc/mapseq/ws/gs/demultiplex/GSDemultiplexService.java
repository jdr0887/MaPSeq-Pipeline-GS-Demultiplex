package edu.unc.mapseq.ws.gs.demultiplex;

import javax.activation.DataHandler;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.ws.BindingType;
import javax.xml.ws.soap.MTOM;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
@MTOM(enabled = true, threshold = 0)
@BindingType(value = javax.xml.ws.soap.SOAPBinding.SOAP11HTTP_MTOM_BINDING)
@WebService(targetNamespace = "http://demultiplex.gs.ws.mapseq.unc.edu", serviceName = "GSDemultiplexService", portName = "GSDemultiplexPort")
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
@Path("/GSDemultiplexService/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface GSDemultiplexService {

    @WebMethod
    public Long uploadSampleSheet(@XmlMimeType("application/octet-stream") @WebParam(name = "data") DataHandler data,
            @WebParam(name = "flowcellName") String flowcellName);

    @GET
    @Path("/assertDirectoryExists/{flowcell}")
    @WebMethod
    public Boolean assertDirectoryExists(@PathParam("flowcell") @WebParam(name = "flowcell") String flowcell);

}
