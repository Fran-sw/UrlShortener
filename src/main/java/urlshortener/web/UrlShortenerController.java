package urlshortener.web;

import java.net.URI;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import urlshortener.domain.ShortURL;
import urlshortener.service.ClickService;
import urlshortener.service.ShortURLService;
import urlshortener.service.ServiceAgents;


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
import java.io.BufferedWriter;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.scheduling.annotation.Async;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@RestController
public class UrlShortenerController {

  private final ShortURLService shortUrlService;

  private final ClickService clickService;

  private final ServiceAgents serviceAgents;

  private static final Logger log = LoggerFactory
      .getLogger(ShortURLService.class);


  public UrlShortenerController(ShortURLService shortUrlService, ClickService clickService, ServiceAgents serviceAgents) {
    this.shortUrlService = shortUrlService;
    this.clickService = clickService;
    this.serviceAgents = serviceAgents;
  }

  private static String generateQRCodeImage(String uri,int width, int height)
          throws WriterException, IOException {
      QRCodeWriter qrCodeWriter = new QRCodeWriter();
      BitMatrix bitMatrix = qrCodeWriter.encode(uri, BarcodeFormat.QR_CODE, width, height);
      BufferedImage new_qr = MatrixToImageWriter.toBufferedImage(bitMatrix);
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ImageIO.write(new_qr,"png",bos);
      byte[] qr_b = bos.toByteArray();
      qr_b = Base64.getEncoder().encode(qr_b);
      String qr = new String(qr_b);
      return qr;
  }

  @RequestMapping(value = "/{id:(?!link|index).*}", method = RequestMethod.GET)
  public ResponseEntity<?> redirectTo(@PathVariable String id,
                                      @RequestHeader(value = "User-Agent", required = false) String userAgent,
                                      HttpServletRequest request) {
                                        
    ShortURL l = shortUrlService.findByKey(id);

    //We get user agants
    if(userAgent != null && !userAgent.equals("")){
      serviceAgents.processAgents(userAgent);
    }

    if (l != null) {
      //&& shortUrlService.checkReachable(l.getUri().toString())
      clickService.saveClick(id, extractIP(request));
      return createSuccessfulRedirectToResponse(l);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @RequestMapping(value = "/qr/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
  public ResponseEntity<byte[]> takeQR (@PathVariable String id) throws IOException {

    
      /*  Esto funciona, pero lo genera de 0.
      ShortURL su = shortUrlService.findByKey(id);
      byte[] bytes = generateQrCodeBytes(su.getTarget(), 250, 250);
      System.out.println("Almenos intenta imprimir algo ...");
      */

      //Vamos a probar a generar el byte[] a partir del string que tenemos
      ShortURL su = shortUrlService.findByKey(id);
      String aux = su.getQr();
      byte[] bytes = Base64.getDecoder().decode(aux);
  
      return ResponseEntity
      .ok()
      .contentType(MediaType.IMAGE_JPEG)
      .body(bytes);
  } 

  @RequestMapping(value = "/link", method = RequestMethod.POST)
  public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
                                            @RequestParam(value = "sponsor", required = false)
                                            String sponsor,
                                            @RequestHeader(value = "User-Agent", required = false) String userAgent,
                                            @RequestParam(value = "qr", required = false) String checkboxValue,
                                            HttpServletRequest request) {
    UrlValidator urlValidator = new UrlValidator(new String[] {"http",
        "https"});
    if (urlValidator.isValid(url)) {
      ShortURL su = shortUrlService.save(url, sponsor, request.getRemoteAddr());
      HttpHeaders h = new HttpHeaders();
      h.setLocation(su.getUri());
      HttpStatus reachableHeaders = null;
      if(!shortUrlService.checkReachable(su.getTarget().toString())){
        su = shortUrlService.mark(su,false);
        reachableHeaders = HttpStatus.BAD_REQUEST;
      }
      else{
        su = shortUrlService.mark(su,true);
        reachableHeaders = HttpStatus.CREATED;
      }
      if (checkboxValue != null ){      //Si se requiere qr
        su.setQrUrl("http://localhost:8080/qr/"+su.getHash());
        if (!shortUrlService.existShortURLByUri(su.getHash())){ //Si no existe se genera
          generarQR(su);
        }
      }
      log.info("TENGO hash: {}",su.getHash());
      log.info("TENGO QrUrl: {}",su.getQrUrl());
      return new ResponseEntity<>(su, h, reachableHeaders);
    } else {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

  @Async
  public void generarQR(ShortURL su){   //Public so it can use @async
    log.info("Execute QR method asynchronously.Thread name: " 
    + Thread.currentThread().getName());
    try {
      String qr = generateQRCodeImage(su.getUri().toString(),250,250);
      shortUrlService.setQr(su, qr);
    } catch (WriterException e) {
        System.out.println("Could not generate QR Code, WriterException :: " + e.getMessage());
    } catch (IOException e) {
      System.out.println("Could not generate QR Code, IOException :: " + e.getMessage());
    }
  }

  private String extractIP(HttpServletRequest request) {
    return request.getRemoteAddr();
  }

  private ResponseEntity<?> createSuccessfulRedirectToResponse(ShortURL l) {
    HttpHeaders h = new HttpHeaders();
    h.setLocation(URI.create(l.getTarget()));
    return new ResponseEntity<>(h, HttpStatus.valueOf(l.getMode()));
  }
}
