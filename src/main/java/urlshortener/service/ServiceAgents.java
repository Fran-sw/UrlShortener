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
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Service
public class ServiceAgents {
    private static final Logger log = LoggerFactory
    .getLogger(ShortURLService.class);

    Map<String, Integer> infoUserAgents;
    private UserAgentParser userAgentParser = null;

    public ServiceAgents() {    
        try{
          this.userAgentParser = new UserAgentService().loadParser(); // handle IOException and ParseException
        }
        catch(IOException e){}
        catch(ParseException e){} 
    
        this.infoUserAgents = new HashMap<String, Integer>();
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

    public Map<String, Integer> getAgentsInfo(){
        return infoUserAgents;
    }
    
}