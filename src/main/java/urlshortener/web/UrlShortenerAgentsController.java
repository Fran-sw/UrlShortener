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

import java.net.HttpURLConnection;
import java.net.URL;

import com.blueconic.browscap.Capabilities;
import com.blueconic.browscap.UserAgentParser;
import com.blueconic.browscap.ParseException;
import com.blueconic.browscap.UserAgentService;

import java.util.*;

@RestController
public class UrlShortenerAgentsController {

  private final ShortURLService shortUrlService;

  private final ClickService clickService;

  private UserAgentParser userAgentParser = null;
  Map<String, Integer> info_UserAgents;

  public UrlShortenerAgentsController(ShortURLService shortUrlService, ClickService clickService) {
    this.shortUrlService = shortUrlService;
    this.clickService = clickService;
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

 
  @RequestMapping(value = "/agentsInfo", method = RequestMethod.GET)
  public ResponseEntity<String> agentsInfo(@RequestHeader(value = "User-Agent") String userAgent) {
    Capabilities capabilities = userAgentParser.parse(userAgent);
    String browser = capabilities.getBrowser();
    String os = capabilities.getPlatform();

    HttpHeaders h = new HttpHeaders();

    int veces_B = info_UserAgents.get(browser);
    int veces_SO = info_UserAgents.get(os);
    info_UserAgents.put(browser,veces_B+1);
    info_UserAgents.put(os,veces_SO+1);

    String res = String.format("%s: %d \n%s: %d \n%s: %d \n%s: %d \n%s: %d \n%s: %d \n","Chrome",info_UserAgents.get("Chrome"),"Firefox",info_UserAgents.get("Firefox"),"IE",info_UserAgents.get("IE"),"Win10",info_UserAgents.get("Win10"),"Android",info_UserAgents.get("Android"),"IOS",info_UserAgents.get("IOS"));
    return new ResponseEntity<>(res, h, HttpStatus.OK);
  }
}