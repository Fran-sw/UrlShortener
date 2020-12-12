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

import java.net.HttpURLConnection;
import java.net.URL;


import java.util.*;

@RestController
public class UrlShortenerAgentsController {

  private final ServiceAgents serviceAgents;

  private final ClickService clickService;

  public UrlShortenerAgentsController(ServiceAgents serviceAgents, ClickService clickService) {
    this.serviceAgents = serviceAgents;
    this.clickService = clickService;   
  }

 
  @RequestMapping(value = "/agentsInfo", method = RequestMethod.GET)
  public ResponseEntity<Map<String, Integer>> agentsInfo(@RequestHeader(value = "User-Agent") String userAgent) {
    HttpHeaders h = new HttpHeaders();
    //serviceAgents.processAgents(userAgent);
    Map<String, Integer> res = serviceAgents.getAgentsInfo();
    serviceAgents.calculateTop5();

    return new ResponseEntity<>(res, h, HttpStatus.OK);
  }
}