package com.easymenuplanner.backend.rest.resources;

import java.util.Collections;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oscardelgado83.easymenuplanner.pojos.ExportPOJO;
import java.util.Arrays;

@Path("/days")
@Stateless
public class DayResource extends BackendResource {

	private static final Logger logger = LoggerFactory.getLogger(DayResource.class);
    private static final int MAX = 100;
    
    //TODO: change to return only Days
    @GET
    @Path("/")
    public ExportPOJO getDays(@QueryParam("acc") String encAccName, @QueryParam("dev") String encDevId) {
    	
         logger.info("download");

         List<?> pojos = null;

         if (encDevId != null) {
             try {
                pojos = Arrays.asList((ExportPOJO) em.createQuery("SELECT e FROM ExportPOJO e "
                         + "WHERE e.accountName = :acc "
                         + "AND e.deviceId = :dev "
                         + "AND e.fromError = 0 "
                         + "ORDER BY e.updateTimestamp DESC")
                         .setParameter("acc", encAccName)
                         .setParameter("dev", encDevId)
                        .getSingleResult());
             } catch (NoResultException e) {
                 return null;
             }
         } else {
             pojos = em.createQuery("SELECT e FROM ExportPOJO e "
                     + "WHERE e.accountName = :acc "
                     + "AND e.fromError = 0 "
                     + "ORDER BY e.updateTimestamp DESC")
                     .setParameter("acc", encAccName)
                     .setMaxResults(1)
                     .getResultList();
         }
         if (pojos != null && !pojos.isEmpty()) {
             final ExportPOJO exportPOJO = (ExportPOJO) pojos.get(0);
            
 //            //FIXME: temporal fix because Proguard was changing jsonId field
 //            //Although it works, change to a regex to keep both a and jsonId. (Mixed app versions).
 //            if (exportPOJO.daysJSON != null) exportPOJO.daysJSON = exportPOJO.daysJSON.replace("\"a\":", "\"jsonId\":");
 //            if (exportPOJO.coursesJSON != null) exportPOJO.coursesJSON = exportPOJO.coursesJSON.replace("\"a\":", "\"jsonId\":");
 //            if (exportPOJO.courseIngredientJSONs != null) exportPOJO.courseIngredientJSONs = exportPOJO.courseIngredientJSONs.replace("\"a\":", "\"jsonId\":");
 //            if (exportPOJO.ingredientJSONs != null) exportPOJO.ingredientJSONs = exportPOJO.ingredientJSONs.replace("\"a\":", "\"jsonId\":");
            
             return exportPOJO;
         } else {
             return null;
         }
    }
    
     @GET
    @Path("/gallery")
    public List<ExportPOJO> getGallery(
            @QueryParam("language") String language,
            @QueryParam("start") int start,
            @QueryParam("count") int count
    ) {
    	 
         logger.info("download");
         List<ExportPOJO> pojos = null;
         try {
             String sql = "SELECT e FROM ExportPOJO e "
                     + "WHERE e.fromError = 0 ";
             if (language != null) sql += "AND e.language = :language ";
             sql += "ORDER BY e.updateTimestamp DESC";
             final TypedQuery<ExportPOJO> q = em.createQuery(sql, ExportPOJO.class);
             if (language != null) q.setParameter("language", language);
             q.setFirstResult(start);
             q.setMaxResults((count != 0)? count : MAX);
             pojos = q.getResultList();
         } catch (NoResultException e) {
             return Collections.emptyList();
         }
         return pojos;
    }

    //TODO: change to manage only Days
    @PUT
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON + "; charset=utf-8")
    public ExportPOJO updateDays(ExportPOJO pojo) {
    	
         logger.info("updateDays");
         logger.debug("pojo: {}", pojo);

         return saveOrUpdate(pojo);
    }

    private ExportPOJO saveOrUpdate(ExportPOJO pojo) {
        logger.info("saveOrUpdate");
        return em.merge(pojo);
    }
}
