package urlshortener.service;

import com.blueconic.browscap.Capabilities;
import com.blueconic.browscap.UserAgentParser;
import com.blueconic.browscap.ParseException;
import com.blueconic.browscap.UserAgentService;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import urlshortener.domain.ShortURL;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@EnableScheduling
@Service
public class ServiceAgents {
    private static final Logger log = LoggerFactory
    .getLogger(ShortURLService.class);

    Map<String, Integer> infoUserAgents;
    Map<String, Integer> top5;
    
    private UserAgentParser userAgentParser = null;

    public ServiceAgents() {    
        try{
          this.userAgentParser = new UserAgentService().loadParser(); // handle IOException and ParseException
        }
        catch(IOException e){}
        catch(ParseException e){} 
    
        this.infoUserAgents = new HashMap<String, Integer>();
        this.top5 = new HashMap<String, Integer>();
    }

    @Async
    public void processAgents(String userAgent){
      log.info("Execute Agents method asynchronously.Thread name: " + Thread.currentThread().getName());
        Capabilities capabilities = userAgentParser.parse(userAgent);
        String browser = capabilities.getBrowser();
        String os = capabilities.getPlatform();
    
        int veces_B = infoUserAgents.getOrDefault(browser, -1);
        int veces_SO = infoUserAgents.getOrDefault(os,-1);
    
        // Verify if there is an entry in the MAP, elwhise create it
        if(veces_B == -1){
          infoUserAgents.put(browser,1);
        }
        else{
          infoUserAgents.put(browser,veces_B+1);
        }
    
        if(veces_SO == -1){
          infoUserAgents.put(os,1);
        }
        else{
          infoUserAgents.put(os,veces_SO+1);
        }
    }

    @Async
    @Scheduled(fixedDelay = 5000)
    public void calculateTop5(){
      log.info("Execute Calculate Top 5 method asynchronously.Thread name: " + Thread.currentThread().getName());
      //try{ Thread.sleep(5000);} catch (InterruptedException e) {};
      Map<String, Integer> aux = new HashMap<String, Integer>();
      aux.putAll(infoUserAgents);
      Map<String, Integer> aux_top =  new HashMap<String, Integer>();
      
      for(int i = 0; i < 5; i++){
        if(aux.isEmpty()) break; 

        String max_K = "";
        int max_V = -1;
        
        for(Map.Entry<String,Integer> entry : aux.entrySet()){
          if(entry.getValue() > max_V){
            max_K = entry.getKey();
            max_V = entry.getValue();
          }
        }
        aux.remove(max_K);
        aux_top.put(max_K, max_V);
      }
      top5.clear();
      top5.putAll(aux_top);
    }
    

    public Map<String, Integer> getAgentsInfo(){
        return top5;
    }
    
}