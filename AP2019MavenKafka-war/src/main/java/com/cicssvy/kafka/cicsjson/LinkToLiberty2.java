package com.cicssvy.kafka.cicsjson;

import com.ibm.cics.server.invocation.CICSProgram;
import com.cicssvy.kafka.cicsjson.ProducerListener.ProducertoKafka;
import com.ibm.cics.server.CCSIDErrorException;
import com.ibm.cics.server.Channel;
import com.ibm.cics.server.ChannelErrorException;
import com.ibm.cics.server.CicsConditionException;
import com.ibm.cics.server.CodePageErrorException;
import com.ibm.cics.server.Container;
import com.ibm.cics.server.ContainerErrorException;
import com.ibm.cics.server.InvalidRequestException;
import com.ibm.cics.server.Program;
import com.ibm.cics.server.Task;

//import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;


import org.apache.log4j.*;


public class LinkToLiberty2 {

	public static final String PayloadCon = "Payload";
	
	/** CICS local ccsid */
	//private static final String CCSID = System.getProperty("com.ibm.cics.jvmserver.local.ccsid");
	
	 //private  SimpleProducer producer;
	private  ProducertoKafka producer;
	 private  Channel jsonChan = null;
	 private  Logger logger = Logger.getLogger(this.getClass()); 	 
	
	@CICSProgram("PUTQ")
	public void putQ()  
  {	
		
		String sJson = "";
	
		try 
		{ 
			jsonChan = Task.getTask().createChannel("MyJSONChan");
			
			//Task t=Task.getTask();
			Channel currentChannel = Task.getTask().getCurrentChannel();
			if (currentChannel != null) 
			{  
				Container conHead = currentChannel.getContainer("HEAD");
				if (conHead != null)
				{			
					byte[] dataArry = conHead.get();
					
					Container conPayload = currentChannel.getContainer("PAYLOAD");
					if(conPayload != null)
					{
						byte[] playloadData = conPayload.get();
	                    Container conPayload2 = jsonChan.createContainer("PAYLOAD");
	                    conPayload2.put(playloadData);
						
					}
					
		      //-----------------------------------------
					

                    Container conJsonData = jsonChan.createContainer("DFHJSON-DATA");

                    conJsonData.put(dataArry);
                    
                    Container conTransfrm = jsonChan.createContainer("DFHJSON-TRANSFRM");
                    conTransfrm.putString("OUTP");                    
                                       
                    Program p = new Program();
                    p.setName("DFHJSON");
                                      
                    p.link(jsonChan);
                    
                    Container bitconJsonErr = jsonChan.getContainer("DFHJSON-ERROR");                    
                    if (bitconJsonErr != null)
                    {
                    	byte[] ba = bitconJsonErr.get();
                    	ByteBuffer bb = ByteBuffer.wrap(ba);                  	
                    	int cicsrc = bb.getInt();

                    	logger.error("error happens for CICS Json parser! DFHJSON-ERROR="+cicsrc);
                    	
                    	
                        Container conERRORMSG = jsonChan.getContainer("DFHJSON-ERRORMSG"); 
                        if (conERRORMSG != null)
                        {
                        	String ERRORMSG = conERRORMSG.getString();
                        	logger.error("DFHJSON-ERRORMSG="+ERRORMSG);
                        }
                  	
                    }
                    else
                    {
                        Container conJson = jsonChan.getContainer("DFHJSON-JSON"); 
                        if (conJson != null)
                        {
                        	//sJson = new String(conJson.get(),CCSID);
                        	sJson = conJson.getString();
                        }
                    }
                                   
                    conJsonData.delete();
                    conTransfrm.delete();
                    
                    
					
		      //---------------------send data to KAFKA---------------------	
					//this.producer = new SimpleProducer();
					//producer.sendToKafka(sJson);
                    
                    this.producer = new ProducertoKafka();
                    
                    long startTime = System.currentTimeMillis(); 
                    
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                    String iso8601 = sdf.format(startTime);
                    
                    //String data = iso8601 + "Z " + sJson;
                    
                    //logger.info(data);                  	

                    producer.sendToKafka(sJson);
                    
                    System.out.println(sJson);
					
				}
				
			} 
			else 
			{
				logger.error("current channel is null");
			}
			
/*		} catch(CicsConditionException e){
			e.printStackTrace();
		}*/

		
		} catch ( ContainerErrorException | InvalidRequestException
				| ChannelErrorException | CCSIDErrorException
				| CodePageErrorException e) {
			e.printStackTrace();
		}
		catch (CicsConditionException cce) 
		{
		    throw new RuntimeException(cce);
		}
				     
		
  }

  

}
