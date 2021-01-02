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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.annotation.Async;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;



@Service
public class ShortURLService {
  

  private final ShortURLRepository shortURLRepository;

  private static final Logger log = LoggerFactory
      .getLogger(ShortURLService.class);

  public ShortURLService(ShortURLRepository shortURLRepository) {
    this.shortURLRepository = shortURLRepository;
  }

  public ShortURL findByKey(String id) {
    return shortURLRepository.findByKey(id);
  }

  public ShortURL save(String url, String sponsor, String ip) {
    ShortURL su = ShortURLBuilder.newInstance()
        .target(url)
        .uri((String hash) -> linkTo(methodOn(UrlShortenerController.class).redirectTo(hash, null, null))
            .toUri())
        .sponsor(sponsor)
        .createdNow()
        .randomOwner()
        .temporaryRedirect()
        .treatAsSafe()
        .ip(ip)
        .unknownCountry()
        .qr()
        .qrUrl()
        .build();
    return shortURLRepository.save(su);
  }

  public ShortURL mark(ShortURL url, boolean safeness){
    return shortURLRepository.mark(url,safeness);
  }

  public boolean existShortURLByUri(String uri){
    return shortURLRepository.existShortURLByUri(uri);
  }

  public void setQr(ShortURL urlSafe, String newQr){
        shortURLRepository.setQr(urlSafe, newQr);
  }

  public String getQrcode(String hash){
    return shortURLRepository.findByKey(hash).getQr();
  }

  //Function to check if an url is reachable
  //@Async
  public boolean checkReachable(String shortUri){
    //log.info("Execute checkReachable method asynchronously.Thread name: " 
    //+ Thread.currentThread().getName());
    try{
      URL url = new URL(shortUri);
      HttpURLConnection huc = (HttpURLConnection) url.openConnection();

      Integer responseCode = huc.getResponseCode();
      //log.info("URL CODE: ", responseCode);

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
