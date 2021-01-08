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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.http.MediaType;
import urlshortener.domain.ShortURL;
import urlshortener.service.ClickService;
import urlshortener.service.ShortURLService;
import urlshortener.service.ServiceAgents;

//Websocket
import urlshortener.service.MessageInternal;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.bind.annotation.ExceptionHandler;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.net.URL;


@Controller
public class UrlShortenerCVSController {
  private static final Logger log = LoggerFactory
      .getLogger(ShortURLService.class);

  private final ShortURLService shortUrlService;

  private final ClickService clickService;

  private final ServiceAgents serviceAgents;

  @Autowired
  private SimpMessagingTemplate simpMessagingTemplate;

  
  @MessageMapping("/chat")
  @SendToUser("/topic/messages")
  public void send(MessageInternal message, @Header("simpSessionId") String sessionId, SimpMessageHeaderAccessor ha) throws Exception {
    //Cogemos la Url a la que se accederá una vez acortada
    String remoteAddr = message.getAnswer();
    remoteAddr = remoteAddr.substring(0, remoteAddr.length() - 1);
    String ip = (String) ha.getSessionAttributes().get("ip");
    SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create();
    accessor.setHeader(SimpMessageHeaderAccessor.SESSION_ID_HEADER, sessionId);
    String contenido = message.getContent();
    //Aseguramos que habrá un salto de línea al final del contenido, para asegurar un conteo correcto de líneas
    //contenido = contenido + System.lineSeparator()+System.lineSeparator();
    //contenido.replaceAll("\n", "");
    if (contenido.length()>0) {
      String[] lines = contenido.split("\n", -1); 
      int count = lines.length-1;
      int posicion = 0;
      MessageInternal resultado;
      while (posicion<count) {
        //Quitamos espacios
        lines[posicion] = lines[posicion].replaceAll("\\s+","");
        if(lines[posicion].length()>0){
          if(shortUrlService.checkReachable(lines[posicion])){
            ShortURL shorturl = shortenerCSV(lines[posicion], "", ip);
            if (shorturl.getSafe()){
              resultado = new MessageInternal("empty",lines[posicion]+";true;"+remoteAddr+shorturl.getUri().toString()+";\n");
              simpMessagingTemplate.convertAndSendToUser(sessionId, "/topic/messages",resultado, accessor.getMessageHeaders());
            }else{
              resultado = new MessageInternal("empty",lines[posicion]+";false;La url no es alcanzable\n");
              simpMessagingTemplate.convertAndSendToUser(sessionId, "/topic/messages",resultado, accessor.getMessageHeaders());
            }
          }else{
            resultado = new MessageInternal("empty",lines[posicion]+";false;La url no es alcanzable\n");
            simpMessagingTemplate.convertAndSendToUser(sessionId, "/topic/messages",resultado, accessor.getMessageHeaders());
          }
        }
        posicion++;
      }
    }else{
      MessageInternal error = new MessageInternal("TODO","El fichero está vacio");
      simpMessagingTemplate.convertAndSendToUser(sessionId, "/topic/messages",error, accessor.getMessageHeaders());
    }
  }

  public UrlShortenerCVSController(ShortURLService shortUrlService, ClickService clickService,ServiceAgents serviceAgents) {
    this.shortUrlService = shortUrlService;
    this.clickService = clickService;
    this.serviceAgents = serviceAgents;
  }

  @ExceptionHandler({ MultipartException.class})
  public void handleMultipartException() {
    log.info("Invalid file");
  }

  @ExceptionHandler({ FileNotFoundException.class})
  public void handleFileNotFoundException() {
    log.info("There was no file");
  }

  @ExceptionHandler({ IOException.class})
  public void handleIOException() {
    log.info("Error while reading file");
  }

  public ShortURL shortenerCSV(String url,String sponsor,String remoteAddress) {
    UrlValidator urlValidator = new UrlValidator(new String[] {"http","https"});
    ShortURL su = shortUrlService.save(url, sponsor, remoteAddress);
    if (urlValidator.isValid(url) && shortUrlService.checkReachable(su.getTarget().toString())) {
      su = shortUrlService.mark(su,true);
      return su;
    }else{
      su = shortUrlService.mark(su,false);
      return su;
    }
  }
}