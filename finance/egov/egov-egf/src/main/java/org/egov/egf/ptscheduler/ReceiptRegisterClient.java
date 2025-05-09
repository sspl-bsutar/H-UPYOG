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

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.egov.egf.masters.model.PropertyTaxReceiptRegister;
import org.egov.egf.masters.repository.PropertyTaxReceiptRegisterRepository;
import org.egov.infstr.services.PersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ReceiptRegisterClient {
	@Qualifier("propertyTaxReceiptRegisterRepository")
	@Autowired
	private PropertyTaxReceiptRegisterRepository propertyTaxReceiptRegisterRepository;
	@Autowired
	private Mdmstenants mdmstenants;
	
	@Autowired
    @Qualifier("persistenceService")
    private PersistenceService persistenceService;
	
	@PersistenceContext
    private EntityManager entityManager; 
	
	private static final String API_URL = "https://nagarsewa.uk.gov.in/report/rainmaker-pt/ReceiptRegister/_get";

    @SuppressWarnings("serial")
	public String getReceiptRegisterData(long fromDate, long toDate) throws IOException {
    	
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
    	            requestBody.put("reportName", "ReceiptRegister");
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
    	                    savePropertyTaxReceiptRegister(responseData);
    	                    return responseData;
    	                } else {
    	                    throw new RuntimeException("API request failed with status code: " + response.getStatusLine().getStatusCode());
    	                }
    	            }
    	        }
    	    }
    	    
    	    return null;
    	}

        public void savePropertyTaxReceiptRegister(String responseData) {
            ObjectMapper mapper = new ObjectMapper();
            Logger logger = LoggerFactory.getLogger(getClass());

            try {
                JsonNode root = mapper.readTree(responseData);
                JsonNode reportDataNode = root.get("reportData");
                if (reportDataNode != null && reportDataNode.isArray()) {
                    for (JsonNode dataNode : reportDataNode) {
                        String propertyId = dataNode.get(1).asText(); // Assuming propertyId is at index 1
                        List<PropertyTaxReceiptRegister> existingRecords = propertyTaxReceiptRegisterRepository.findByPropertyid(propertyId);
                        if (existingRecords.isEmpty()) {
                            PropertyTaxReceiptRegister receiptRegister = createReceiptRegisterFromJson(dataNode);
                            propertyTaxReceiptRegisterRepository.save(receiptRegister);
                        } else {
                            logger.info("Receipt Property ID " + propertyId + " already exists. Skipping save.");
                        }
                    }
                }
            } catch (IOException e) {
                logger.error("Error occurred while parsing and saving report data", e);
            }
        }
        
        private PropertyTaxReceiptRegister createReceiptRegisterFromJson(JsonNode dataNode) {
        	PropertyTaxReceiptRegister receiptRegister = new PropertyTaxReceiptRegister();
            try {
            	receiptRegister.setTenantid(dataNode.get(0).asText());
                receiptRegister.setPropertyid(dataNode.get(1).asText());
                receiptRegister.setReceiptnumber(dataNode.get(2).asText());
                receiptRegister.setReceiptdate(dataNode.get(3).asText());
                receiptRegister.setPaymentmode(dataNode.get(4).asText());
                receiptRegister.setTransactionnumber(dataNode.get(5).asText());
                receiptRegister.setInterest(dataNode.get(6).asText());
                receiptRegister.setPenalty(dataNode.get(7).asText());
                receiptRegister.setSwatchathatax(dataNode.get(8).asText());
                receiptRegister.setPropertytax(dataNode.get(9).asText());
                receiptRegister.setTotalcollection(dataNode.get(10).asText());
                receiptRegister.setUsername(dataNode.get(11).asText());
                receiptRegister.setMohallaname(dataNode.get(12).asText());
                receiptRegister.setDoorno(dataNode.get(13).asText());
                receiptRegister.setName(dataNode.get(14).asText());
                receiptRegister.setUser_uuid(dataNode.get(15).asText());
                receiptRegister.setFlag(0);
                receiptRegister.setNote(null);
                receiptRegister.setSystem_createddate(new Date());
                receiptRegister.setSystem_updateddate(new Date());
                receiptRegister.setCreateddate(new Date());
                receiptRegister.setUpdateddate(new Date());
                receiptRegister.setVoucherid(null);
                // Set other properties as needed
            } catch (Exception e) {
                // Handle exceptions or log errors
                e.printStackTrace();
            }
            return receiptRegister;
        }
	
    // Now Scheduler code start
    
   
	@Scheduled(cron = "0 0 0 * * *") 
    public void fetchAndProcessData() throws IOException {
        long fromDate = calculatePreviousDayMidnight(); 
        long toDate = System.currentTimeMillis(); 

        String data = getReceiptRegisterData(fromDate, toDate);
        
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
