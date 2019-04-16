/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.restful;

import datamodel.ws.rest.ErrorRsp;
import datamodel.ws.rest.ScavengerHuntRsp;
import ejb.stateless.ScavengerHuntEntityControllerLocal;
import entity.CustomerEntity;
import entity.ScavengerHuntEntity;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.exception.ScavengerHuntNotFoundException;

/**
 * REST Web Service
 *
 * @author Alastair
 */
@Path("ScavengerHunt")
public class ScavengerHuntResource {

    ScavengerHuntEntityControllerLocal scavengerHuntEntityControllerLocal = lookupScavengerHuntEntityControllerLocal();
    
    @Context
    private UriInfo context;

    /**
     * Creates a new instance of ScavengerHuntResource
     */
    public ScavengerHuntResource() {
    }

    @Path("retrieveScavengerHuntForTheDay")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveScavengerHuntForTheDay() {
        Date date = new Date();
        try {
            ScavengerHuntEntity scavengerHuntEntity = scavengerHuntEntityControllerLocal.retrieveScavengerHuntEntityByDate(date);
            
            for(CustomerEntity ce: scavengerHuntEntity.getCustomerEntities()) {
                ce.getDiscountCodeEntities().clear();
                ce.getReviewEntities().clear();
                ce.getSaleTransactionEntities().clear();     
            }
            ScavengerHuntRsp scavengerHuntRsp = new ScavengerHuntRsp(scavengerHuntEntity);
            return Response.status(Response.Status.OK).entity(scavengerHuntRsp).build();
        } catch (Exception ex) {
            
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }
    
    
//    @PUT
//    @Consumes(MediaType.APPLICATION_JSON)
//    public void putJson(String content) {
//    }

    private ScavengerHuntEntityControllerLocal lookupScavengerHuntEntityControllerLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (ScavengerHuntEntityControllerLocal) c.lookup("java:global/StoreCraft/StoreCraft-ejb/ScavengerHuntEntityController!ejb.stateless.ScavengerHuntEntityControllerLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}
