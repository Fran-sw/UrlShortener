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
  private static final Logger log = LoggerFactory
      .getLogger(ShortURLService.class);

  private final ShortURLRepository shortURLRepository;

  Map<String, Integer> infoUserAgents;
  private UserAgentParser userAgentParser = null;


  public ShortURLService(ShortURLRepository shortURLRepository) {
    this.shortURLRepository = shortURLRepository;

    try{
      this.userAgentParser = new UserAgentService().loadParser(); // handle IOException and ParseException
    }
    catch(IOException e){}
    catch(ParseException e){} 

    this.infoUserAgents = new HashMap<String, Integer>();
  }

  public void processAgents(String userAgent){
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
