package com.cicssvy.kafka.cicsjson.ProducerListener;


import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;


import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
//import org.apache.kafka.clients.CommonClientConfigs;

import java.util.Properties;

public class ProducerFactory extends BasePooledObjectFactory<KafkaProducer<String, String>> {
	
    //zlinux
	//public static final String KAFKA_BROKER_LIST = "129.40.119.72:9092";
	public static final String KAFKA_BROKER_LIST = "9.119.156.201:9092";
	
    public static final String MAX_BLOCK_MS = "10000"; 
    public static final String CLIENT_ID = "CICSProducer"; 	
    
   

	@Override
	public KafkaProducer<String, String> create() throws Exception {
		
		//System.out.println("ProducerFactory  create");
		
        Properties properties = new Properties();

        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKER_LIST);
        properties.put(ProducerConfig.MAX_BLOCK_MS_CONFIG,MAX_BLOCK_MS);
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, CLIENT_ID);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");  
        
       // properties.put(ProducerConfig.METADATA_MAX_AGE_CONFIG,"60000");


        // SSL
        //properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
        //properties.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, "/u/eileen/BBL/KAFKACA/client.truststore.jks");
        //properties.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG,  "test1234");

        return new KafkaProducer<>(properties);
	}
	

	@Override
	public PooledObject<KafkaProducer<String, String>> wrap(KafkaProducer<String, String> obj) {
		return new DefaultPooledObject<KafkaProducer<String, String>>(obj);
			
	}
	
    @Override
    public void destroyObject(PooledObject<KafkaProducer<String, String>> p) throws Exception { 
    	
    //	System.out.println("ProducerFactory  destroyObject");
    	
    	p.getObject().close();

    }
	
}

