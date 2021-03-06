package ws.restful;

import datamodel.ws.rest.CreateReviewReq;
import datamodel.ws.rest.ErrorRsp;
import datamodel.ws.rest.ReplyToStaffReplyReq;
import datamodel.ws.rest.ReplyToStaffReplyRsp;
import datamodel.ws.rest.ReviewChainRsp;
import datamodel.ws.rest.ReviewRsp;
import datamodel.ws.rest.UpdateReviewReq;
import ejb.stateless.CustomerEntityControllerLocal;
import ejb.stateless.ReviewEntityControllerLocal;
import entity.CustomerEntity;
import entity.ReviewEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import jdk.nashorn.internal.runtime.JSONFunctions;
import util.exception.CustomerNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.ProductNotFoundException;
import util.exception.ReviewNotFoundException;

@Path("Review")
public class ReviewResource {

    CustomerEntityControllerLocal customerEntityControllerLocal = lookupCustomerEntityControllerLocal();

    @Context
    private UriInfo context;

    ReviewEntityControllerLocal reviewEntityControllerLocal;

    public ReviewResource() {
        reviewEntityControllerLocal = lookupReviewEntityControllerLocal();
    }

    @Path("retrieveReviewChain")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveReviewChain(@QueryParam("reviewId") Long reviewId) {
        try {

            System.out.println("hello");

            List<ReviewEntity> reviewChain = reviewEntityControllerLocal.getReviewChain(reviewId);
            System.out.println("hello2");
            for (ReviewEntity re : reviewChain) {
                if (re.getCustomerEntity() != null) {
                    re.getCustomerEntity().getReviewEntities().clear();
                    re.getCustomerEntity().setDiscountCodeEntities(null);
                    re.getCustomerEntity().setSaleTransactionEntities(null);
                }
//                re.setParentReviewEntity(null);
                re.setProductEntity(null);
                re.setReplyReviewEntity(null);
                if (re.getStaffEntity() != null) {
                    re.getStaffEntity().getReviewEntities().clear();
                    re.getStaffEntity().getCommunityGoalEntities().clear();
                }
            }
            reviewChain.remove(0);
            System.out.println("hello3");

            return Response.status(Status.OK).entity(new ReviewChainRsp(reviewChain)).build();

        } catch (Exception ex) {

            System.out.println("hello4");
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }

    @Path("getReviewsForCustomer")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReviewsForCustomer(@QueryParam("customerId") Long customerId) {
        try {
            List<ReviewEntity> reviews = reviewEntityControllerLocal.retrieveAllRootReviewsForCustomer(customerId);
            for (ReviewEntity re : reviews) {
                System.out.print(re.getReviewId());
            }
            for (ReviewEntity re : reviews) {
                re.getCustomerEntity().setDiscountCodeEntities(null);
                re.getCustomerEntity().setReviewEntities(null);
                re.getCustomerEntity().setSaleTransactionEntities(null);
                re.setParentReviewEntity(null);
                re.getProductEntity().setCategoryEntity(null);
                re.getProductEntity().setDiscountCodeEntities(null);
                re.getProductEntity().setReviewEntities(null);
                re.getProductEntity().setTagEntities(null);
                re.setReplyReviewEntity(null);
                re.setStaffEntity(null);

            }
            return Response.status(Status.OK).entity(new ReviewChainRsp(reviews)).build();
        } catch (Exception ex) {
            System.out.print("EXCEPTION");
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }

    @Path("replyToStaffReply")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response replyToStaffReply(ReplyToStaffReplyReq req) {
        if (req != null) {
            try {
                ReviewEntity customerReply = reviewEntityControllerLocal.customerReplyToStaffReply(req.getStaffReplyId(), req.getCustomerReply(), req.getCustomerId());

                return Response.status(Status.OK).entity(new ReplyToStaffReplyRsp(customerReply.getReviewId())).build();

            } catch (CustomerNotFoundException | InputDataValidationException | ReviewNotFoundException ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
            }

        } else {
            ErrorRsp errorRsp = new ErrorRsp("Invalid register customer request");

            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        }

    }

    @Path("updateReview")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateReview(UpdateReviewReq reviewReq) {
        Long reviewId = reviewReq.getReviewId();
        String reviewContent = reviewReq.getNewContent();
        Integer productRating = reviewReq.getNewProductRating();

        try {
            ReviewEntity re = reviewEntityControllerLocal.updateReview(reviewId, reviewContent, productRating);
            if (re.getCustomerEntity() != null) {
                re.getCustomerEntity().getReviewEntities().clear();
                re.getCustomerEntity().setDiscountCodeEntities(null);
                re.getCustomerEntity().setSaleTransactionEntities(null);
            }
            re.setParentReviewEntity(null);
            re.setProductEntity(null);
            re.setReplyReviewEntity(null);
            if (re.getStaffEntity() != null) {
                re.getStaffEntity().getReviewEntities().clear();
                re.getStaffEntity().getCommunityGoalEntities().clear();
            }
            return Response.status(Status.OK).entity(new ReviewRsp(re)).build();
        } catch (ReviewNotFoundException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }

    }

    @Path("createReview")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createReview(CreateReviewReq createReviewReq) {
        Long customerId = createReviewReq.getCustomerId();
        Long productId = createReviewReq.getProductId();
        String newContent = createReviewReq.getNewContent();
        Integer productRating = createReviewReq.getNewProductRating();

        try {
            ReviewEntity re = reviewEntityControllerLocal.createNewReview(customerId, newContent, productRating, productId);
            if (re.getCustomerEntity() != null) {
                re.getCustomerEntity().getReviewEntities().clear();
                re.getCustomerEntity().setDiscountCodeEntities(null);
                re.getCustomerEntity().setSaleTransactionEntities(null);
            }
            re.setParentReviewEntity(null);
            re.setProductEntity(null);
            re.setReplyReviewEntity(null);
            if (re.getStaffEntity() != null) {
                re.getStaffEntity().getReviewEntities().clear();
                re.getStaffEntity().getCommunityGoalEntities().clear();
            }
            return Response.status(Status.OK).entity(new ReviewRsp(re)).build();
        } catch (CustomerNotFoundException | InputDataValidationException | ProductNotFoundException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }

    private ReviewEntityControllerLocal lookupReviewEntityControllerLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (ReviewEntityControllerLocal) c.lookup("java:global/StoreCraft/StoreCraft-ejb/ReviewEntityController!ejb.stateless.ReviewEntityControllerLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private CustomerEntityControllerLocal lookupCustomerEntityControllerLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (CustomerEntityControllerLocal) c.lookup("java:global/StoreCraft/StoreCraft-ejb/CustomerEntityController!ejb.stateless.CustomerEntityControllerLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

}
