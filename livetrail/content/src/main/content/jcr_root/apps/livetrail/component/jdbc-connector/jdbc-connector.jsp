<%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" contentType="text/html; charset=utf-8" 
	pageEncoding="UTF-8"
    import="org.apache.sling.api.resource.*,
    com.day.commons.datasource.poolservice.DataSourcePool,
    javax.sql.*,
    java.sql.*,
    java.util.*,
    javax.jcr.*,
    com.day.cq.search.*,
    com.day.cq.wcm.api.*,
    com.day.cq.dam.api.*,
    com.adobe.ags.livetrial.*"%><%
 
   ImportService importService = sling.getService(ImportService.class);
   if(importService != null) {
       log.info("yah got import service");
       importService.importPages(resourceResolver, "5");
       importService.importAssets(resourceResolver, "5");

   } else {
       log.info("oh nos");
   }
  
  
  %>

<h2> Content MigrationX </h2>