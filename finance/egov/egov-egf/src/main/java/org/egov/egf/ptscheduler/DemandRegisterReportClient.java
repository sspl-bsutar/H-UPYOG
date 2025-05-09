package org.egov.egf.ptscheduler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.egov.egf.masters.model.PropertyTaxDemandRegister;
import org.egov.egf.masters.repository.PropertyTaxDemandRegisterRepository;
import org.egov.infstr.services.PersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@Component
public class DemandRegisterReportClient {
	
	@Autowired
	private Mdmstenants mdmstenants;
	@Autowired
    @Qualifier("persistenceService")
    private PersistenceService persistenceService;
	
	@PersistenceContext
    private EntityManager entityManager; 
	
	@Qualifier("propertyTaxDemandRegisterRepository")
	@Autowired
	private PropertyTaxDemandRegisterRepository propertyTaxDemandRegisterRepository;
	
	 private static final String API_URL = "https://nagarsewa.uk.gov.in/report/rainmaker-pt/DemandRegisterReport/_get";

	    public String getDemandRegisterData(long fromDate, long toDate) throws IOException {
	    	 Mdmstenants mdmstenants = new Mdmstenants();
	    	    String extractedCodes = mdmstenants.consumeApi();
	    	    String[] codesArray = extractedCodes.split(", ");

	    	 // Iterate through each code
	    	    for (String code : codesArray) {
	    	        if ("uk.doiwala".equals(code.trim())) {
	    	            CloseableHttpClient httpClient = HttpClients.createDefault();
	    	            HttpPost postRequest = new HttpPost(API_URL);
	    	            postRequest.addHeader("Content-Type", "application/json");

	    	            Map<String, Object> requestBody = new HashMap<>();
	    	            requestBody.put("tenantId", code);
	    	            requestBody.put("reportName", "DemandRegisterReport");
	    	            requestBody.put("searchParams", new Object[]{
	    	                    new HashMap<String, Object>() {
	    	                        private static final long serialVersionUID = 1L;

	    	                        {
	    	                            put("name", "fromDate");
	    	                            put("input", fromDate);
	    	                        }
	    	                    },
	    	                    new HashMap<String, Object>() {
	    	                        private static final long serialVersionUID = 1L;

	    	                        {
	    	                            put("name", "toDate");
	    	                            put("input", toDate);
	    	                        }
	    	                    }
	    	            });
	    	            requestBody.put("RequestInfo", new HashMap<String, Object>() {{
	    	                put("apiId", "emp");
	    	                put("ver", "1.0");
	    	                put("ts", System.currentTimeMillis());
	    	                put("action", "create");
	    	                put("did", "1");
	    	                put("key", "abcdkey");
	    	                put("msgId", "20170310130900");
	    	                put("requesterId", "");
	    	                put("authToken", "ebd1eadd-dd0c-4c9d-9759-caffd28a6331");
	    	            }});

	    	            ObjectMapper mapper = new ObjectMapper();
	    	            String jsonRequestBody = mapper.writeValueAsString(requestBody);
	    	            StringEntity entity = new StringEntity(jsonRequestBody);
	    	            postRequest.setEntity(entity);

	    	            try (CloseableHttpResponse response = httpClient.execute(postRequest)) {
	    	                if (response.getStatusLine().getStatusCode() == 200) {
	    	                    InputStream inputStream = response.getEntity().getContent();
	    	                    String responseData = org.apache.commons.io.IOUtils.toString(inputStream, StandardCharsets.UTF_8);
	    	                    System.out.println("API response data: " + responseData);
	    	                    savePropertyTaxDemandRegister(responseData);
	    	                    return responseData;
	    	                } else {
	    	                    throw new RuntimeException("API request failed with status code: " + response.getStatusLine().getStatusCode());
	    	                }
	    	            }
	    	        }
	    	    }
	    	    
	    	    return null;
	    	}

	    public void savePropertyTaxDemandRegister(String responseData) {
	        ObjectMapper mapper = new ObjectMapper();
	        Logger logger = LoggerFactory.getLogger(getClass());

	        try {
	            JsonNode root = mapper.readTree(responseData);
	            JsonNode reportDataNode = root.get("reportData");
	            if (reportDataNode != null && reportDataNode.isArray()) {
	                for (JsonNode dataNode : reportDataNode) {
	                    String propertyId = dataNode.get(0).asText();
	                    if (!propertyTaxDemandRegisterRepository.findByPropertyid(propertyId).isPresent()) {
	                        PropertyTaxDemandRegister demandRegister = createDemandRegisterFromJson(dataNode);
	                        propertyTaxDemandRegisterRepository.save(demandRegister);
	                    } else {
	                        logger.info("Demand Property ID " + propertyId + " already exists. Skipping save.");
	                    }
	                }
	            }
	        } catch (IOException e) {
	            logger.error("Error occurred while parsing and saving report data", e);
	        }
	    }

	    
	    
	    private PropertyTaxDemandRegister createDemandRegisterFromJson(JsonNode dataNode) {
	        PropertyTaxDemandRegister demandRegister = new PropertyTaxDemandRegister();
	        try {
	            demandRegister.setPropertyid(dataNode.get(0).asText());
	            demandRegister.setOldpropertyid(dataNode.get(1).asText());
	            demandRegister.setDoorno(dataNode.get(2).asText());
	            demandRegister.setMohalla(dataNode.get(3).asText());
	            demandRegister.setPropertytype(dataNode.get(4).asText());
	            demandRegister.setUsage(dataNode.get(5).asText());
	            demandRegister.setName(dataNode.get(6).asText());
	            demandRegister.setCurrentarv(dataNode.get(7).asText());
	            demandRegister.setCurrenttax(dataNode.get(8).asText());
	            demandRegister.setCurrentrebate(dataNode.get(9).asText());
	            demandRegister.setArreartax(dataNode.get(10).asText());
	            demandRegister.setPenaltytax(dataNode.get(11).asText());
	            demandRegister.setRebate(dataNode.get(12).asText());
	            demandRegister.setTotaltax(dataNode.get(13).asText());
	            demandRegister.setCurrentcollected(dataNode.get(14).asText());
	            demandRegister.setArrearcollected(dataNode.get(15).asText());
	            demandRegister.setPenaltycollected(dataNode.get(16).asText());
	            demandRegister.setTotalcollected(dataNode.get(17).asText());
	            demandRegister.setVoucherid(null);
	            demandRegister.setFlag(null);
	            demandRegister.setCreateddate(new Date());
	            demandRegister.setUpdateddate(new Date());
	            demandRegister.setNote(null);
	            demandRegister.setSystemcreateddate(new Date());
	            demandRegister.setSystemupdateddate(new Date());


	            // Set other properties as needed
	        } catch (Exception e) {
	            // Handle exceptions or log errors
	            e.printStackTrace();
	        }
	        return demandRegister;
	    }


	
	    // Now Scheduler code start for Demand API
	    
	    @Scheduled(cron = "0 0 0 * * *") 
	    public void fetchAndProcessData() throws IOException {
	        long fromDate = calculatePreviousDayMidnight(); 
	        long toDate = System.currentTimeMillis(); 

	        String data = getDemandRegisterData(fromDate, toDate);
	        
	    }

	    private long calculatePreviousDayMidnight() {
	        Calendar calendar = Calendar.getInstance();
	        calendar.add(Calendar.DAY_OF_MONTH, -1);
	        calendar.set(Calendar.HOUR_OF_DAY, 0);
	        calendar.set(Calendar.MINUTE, 0);
	        calendar.set(Calendar.SECOND, 0);
	        calendar.set(Calendar.MILLISECOND, 0);
	        return calendar.getTimeInMillis();
	    }


}
