package urlshortener.service;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


import org.springframework.stereotype.Service;
import urlshortener.domain.ShortURL;
import urlshortener.repository.ShortURLRepository;
import urlshortener.web.UrlShortenerController;

import org.springframework.beans.DirectFieldAccessor;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.blueconic.browscap.Capabilities;
import com.blueconic.browscap.UserAgentParser;
import com.blueconic.browscap.ParseException;
import com.blueconic.browscap.UserAgentService;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blueconic.browscap.Capabilities;
import com.blueconic.browscap.UserAgentParser;
import com.blueconic.browscap.ParseException;
import com.blueconic.browscap.UserAgentService;

@Service
public class ShortURLService {
  //private static final Logger log = LoggerFactory
      //.getLogger(ShortURLService.class);

  private final ShortURLRepository shortURLRepository;

  Map<String, Integer> info_UserAgents;
  private UserAgentParser userAgentParser = null;


  public ShortURLService(ShortURLRepository shortURLRepository) {
    this.shortURLRepository = shortURLRepository;

    try{
      this.userAgentParser = new UserAgentService().loadParser(); // handle IOException and ParseException
    }
    catch(IOException e){}
    catch(ParseException e){} 

    this.info_UserAgents = new HashMap<String, Integer>();
    //Complete with more tags
    info_UserAgents.put("Chrome",0);
    info_UserAgents.put("Firefox",0);
    info_UserAgents.put("IE",0);
    info_UserAgents.put("Win10",0);
    info_UserAgents.put("Win7",0);
    info_UserAgents.put("Android",0);
    info_UserAgents.put("IOS",0);
  }

  public void processAgents(String userAgent){
    Capabilities capabilities = userAgentParser.parse(userAgent);
    String browser = capabilities.getBrowser();
    String os = capabilities.getPlatform();

    int veces_B = info_UserAgents.get(browser);
    int veces_SO = info_UserAgents.get(os);
    info_UserAgents.put(browser,veces_B+1);
    info_UserAgents.put(os,veces_SO+1);
  }

  public String getAgentsInfo(){
    String res = String.format("%s: %d \n%s: %d \n%s: %d \n%s: %d \n%s: %d \n%s: %d \n","Chrome",info_UserAgents.get("Chrome"),"Firefox",info_UserAgents.get("Firefox"),"IE",info_UserAgents.get("IE"),"Win10",info_UserAgents.get("Win10"),"Android",info_UserAgents.get("Android"),"IOS",info_UserAgents.get("IOS"));
    return res;
  }

  public ShortURL findByKey(String id) {
    return shortURLRepository.findByKey(id);
  }

  public ShortURL save(String url, String sponsor, String ip) {
    ShortURL su = ShortURLBuilder.newInstance()
        .target(url)
        .uri((String hash) -> linkTo(methodOn(UrlShortenerController.class).redirectTo(hash, null))
            .toUri())
        .sponsor(sponsor)
        .createdNow()
        .randomOwner()
        .temporaryRedirect()
        .treatAsSafe()
        .ip(ip)
        .unknownCountry()
        .qr()
        .build();
    return shortURLRepository.save(su);
  }

  public ShortURL mark(ShortURL url, boolean safeness){
    return shortURLRepository.mark(url,safeness);
  }

  //Function to check if an url is reachable
  public boolean checkReachable(String shortUri){
    try{
      URL url = new URL(shortUri);
      HttpURLConnection huc = (HttpURLConnection) url.openConnection();

      Integer responseCode = huc.getResponseCode();
      //log.info("URL CODE: {}", responseCode);

      if(responseCode < 400){
        return true;
      }
      else{
        return false;
      }
    }
    catch(IOException e) {return false;}
  }

}
