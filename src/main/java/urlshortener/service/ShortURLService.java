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


@Service
public class ShortURLService {

  private final ShortURLRepository shortURLRepository;

  public ShortURLService(ShortURLRepository shortURLRepository) {
    this.shortURLRepository = shortURLRepository;
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
  public boolean check_Reachable(String short_uri){
    try{
      URL url = new URL(short_uri);
      HttpURLConnection huc = (HttpURLConnection) url.openConnection();

      int responseCode = huc.getResponseCode();
      if(responseCode == 200){
        return true;
      }
      else{
        return false;
      }
    }
    catch(IOException e) {return false;}
  }

}
