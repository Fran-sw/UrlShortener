package urlshortener.web;

import java.net.URI;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import urlshortener.domain.ShortURL;
import urlshortener.service.ClickService;
import urlshortener.service.ShortURLService;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartException;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;

import java.lang.Object;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringWriter;  
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.Base64;
import java.util.*;

import java.net.HttpURLConnection;
import java.net.URL;

@RestController
public class UrlShortenerCVSController {

  private final ShortURLService shortUrlService;

  private final ClickService clickService;

  public UrlShortenerCVSController(ShortURLService shortUrlService, ClickService clickService) {
    this.shortUrlService = shortUrlService;
    this.clickService = clickService;
  }

//Function to shorten al urls in a csv file
@RequestMapping(value = "/csv", method = RequestMethod.POST, produces= MediaType.TEXT_PLAIN_VALUE)
public ResponseEntity<String> generateShortenedCSV( @RequestHeader(value = "User-Agent") String userAgent, @RequestParam("csv") MultipartFile csv, @RequestParam(value = "sponsor", required = false) String sponsor,HttpServletRequest request){
  shortUrlService.processAgents(userAgent);
  try {
    if (csv.getOriginalFilename().length()>1) { //Hay fichero, sino es una petición vacía
      String file = "";
      InputStream is = csv.getInputStream();
      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      StringWriter csvWriter = new StringWriter();
      
      String line;
      while ((line = br.readLine()) != null) {
        String shortLine = shortenerCSV(line, sponsor, request);
        if (shortLine!="ERROR" && shortUrlService.checkReachable(shortLine)){
          csvWriter.write(line+";acortada;"+shortLine+";\n");
        }else{
          csvWriter.write(line+";no acortada;\n"); 
        }
      }
      return new ResponseEntity<>(csvWriter.toString(),HttpStatus.CREATED);
    } else {
      //log.info("ERROR NO FILE", "");
      return new ResponseEntity<>("There was no file",HttpStatus.NOT_FOUND);
    }
  } catch (MultipartException e){
    //log.info("MultipartException", csv.getOriginalFilename());
    return new ResponseEntity<>("Invalid file",HttpStatus.INTERNAL_SERVER_ERROR);
  } catch (FileNotFoundException e){
    //log.info("FileNotFoundException", csv.getOriginalFilename());
    return new ResponseEntity<>("There was no file",HttpStatus.INTERNAL_SERVER_ERROR);
  } catch (IOException e){
    //log.info("IOException", csv.getOriginalFilename());
    return new ResponseEntity<>("Error while reading file",HttpStatus.INTERNAL_SERVER_ERROR);
  }
}

public String shortenerCSV(String url,String sponsor,HttpServletRequest request) {
    UrlValidator urlValidator = new UrlValidator(new String[] {"http","https"});
    if (urlValidator.isValid(url) && shortUrlService.checkReachable(url)) {
      ShortURL su = shortUrlService.save(url, sponsor, request.getRemoteAddr());
      if(!shortUrlService.checkReachable(su.getUri().toString())){
        su = shortUrlService.mark(su,false);
      }
      else{
        su = shortUrlService.mark(su,true);
      }
      return su.getUri().toString();
    } else {
      return "ERROR";
    }
  }
}