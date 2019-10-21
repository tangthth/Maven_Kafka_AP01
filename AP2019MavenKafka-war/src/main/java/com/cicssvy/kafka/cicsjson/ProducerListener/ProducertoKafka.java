package com.cicssvy.kafka.cicsjson.ProducerListener;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import org.apache.log4j.Logger;

import com.cicssvy.kafka.cicsjson.ProducerListener.ProducerListener;

public class ProducertoKafka {

	private  KafkaProducer<String, String> producer = null;	
    private  Logger logger = Logger.getLogger(this.getClass());  
	
	private static final String TOPIC = System.getProperty("com.ibm.cics.jvmserver.applid");
    
    public ProducertoKafka() {
              
    }
    
    public void sendToKafka(String sJson){
    	// logger.info("ProducertoKafka  sendToKafka");
   
         long beforeproducer = System.currentTimeMillis(); 
         
    	try {
	          
    		   producer = ProducerListener.pool.borrowObject();
    		  	        
    	        ProducerRecord<String, String> data = new ProducerRecord<String, String>(TOPIC, sJson);  	        
    	        
    	        long startTime = System.currentTimeMillis(); 
    	            	        
    		       
    	        // Send asynchronously
    	        if (producer != null)
    	        {
    	        	producer.send(data, new SimpleCallBack(startTime, 0, sJson));
    	        }
    	          	          		
    		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("borrowObject producer failed");
        } finally {
            if (producer != null) {
           // 	System.out.println("ProducertoKafka getCreatedCount = " + ProducerListener.pool.getCreatedCount()); 
            	
            	ProducerListener.pool.returnObject(producer);
            }
        }
    	      
    	   long totaltime = System.currentTimeMillis() - beforeproducer;
    	   logger.info("total time = " + totaltime + " ms");

    	
    }
    
}

class SimpleCallBack implements Callback {
	
    //private Logger messageBackupLogger = Logger.getLogger("MessageBackup");
    private  Logger logger = Logger.getLogger(this.getClass());  
    
    private final long startTime;
    private final int key;
    private final String message;
 
    public SimpleCallBack(long startTime, int key, String message) {
        this.startTime = startTime;
        this.key = key;
        this.message = message;
    }
 
    /**
     * onCompletion method will be called when the record sent to the Kafka Server has been acknowledged.
     *
     * @param metadata  The metadata contains the partition and offset of the record. Null if an error occurred.
     * @param exception The exception thrown during processing of this record. Null if no error occurred.
     */
    public void onCompletion(RecordMetadata metadata, Exception exception) {
    	
    	logger.info("onCompletion is called");
    	
        long elapsedTime = System.currentTimeMillis() - startTime;
        if (metadata != null) {

        	logger.info( "message(" + key + ") sent to partition(" + metadata.partition() +
            "), " + "offset(" + metadata.offset() + ") in " + elapsedTime + " ms");
        	
        	
        	
        } else {
            //exception.printStackTrace();
            logger.error(exception);
           // messageBackupLogger.error(message);
        }
    }

}

