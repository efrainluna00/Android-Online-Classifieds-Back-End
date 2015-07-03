/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshop.maven2.service;

import eshop.maven2.jpa.entities.Ad;
import eshop.maven2.jpa.entities.controllers.AdJpaController;
import eshop.maven2.util.Constants;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.transaction.UserTransaction;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * REST Web Service
 *
 * @author Efraín
 */
@Path("listAds")
public class ListAds {

  

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of ListAdsResource
     */
    public ListAds() {
    }
    
    /**
     * Retrieves representation of an instance of eshoprestm.eshoprestmav.service.ListAdsResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson(
            @DefaultValue("44.046455") @QueryParam("lati") double lati,
            @DefaultValue("-79.453159") @QueryParam("longi") double longi
        ) {
      
       
       JSONArray jarr = new JSONArray(); 
       JSONObject jobject = new JSONObject();
       Constants c = new Constants();
       EntityManager entityManager = Persistence.createEntityManagerFactory("eshop_eshop_war_1.0-SNAPSHOTPU").createEntityManager(); 
       Query query = entityManager.createNamedQuery("Native.Ad.findCloseBy").setParameter(1,lati).setParameter(2,longi).setParameter(3,c.PROXIMITY);
       //SELECT a from Ad a where earth_distance(ll_to_earth(a.lat,a.long), ll_to_earth(?,?)) <= ?", resultClass = Ad.class)}
       //Query query = entityManager.createNativeQuery("SELECT * from ad  where earth_distance(ll_to_earth(?,?), ll_to_earth(ad.lati,ad.longi) ) <= ? ",  Ad.class);
       //rth(:yourlat,:yourlong)) <= :proximity
       List<Ad> listAd = new ArrayList<Ad>();
       //query.setParameter(1,44.046455).setParameter(2,-79.453159).setParameter(3,50000);
       //query.setParameter(1,2);
       
       System.out.println("*****************QUERY ****************" +query.toString());
       listAd = query.getResultList(); 
       try{
       
       for(Ad ltemp : listAd){
           jobject = new JSONObject();
           jobject.put("id", ltemp.getId());
           jobject.put("string_thumbnail", ltemp.getStringThumbnail());
           jobject.put("string_image", ltemp.getStringImage());
           jobject.put("name", ltemp.getName());
           jobject.put("description", ltemp.getDescription());
           jobject.put("cost", ltemp.getCost());
           jobject.put("status", ltemp.getStatus());
           jobject.put("posting_date", ltemp.getPostingDate());
           jobject.put("views", ltemp.getViews());
           jobject.put("id_user", ltemp.getIdUser().getId());
           jobject.put("lat", ltemp.getLati());
           jobject.put("long", ltemp.getLongi());
           jarr.put(jobject);
       }}catch(JSONException je){
           je.printStackTrace();
       }
        //TODO return proper representation object
      
        return jarr.toString();
        
        //throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of ListAdsResource
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
    }
}
