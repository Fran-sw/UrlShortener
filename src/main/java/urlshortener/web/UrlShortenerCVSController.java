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
import org.springframework.web.bind.annotation.RestController;
import urlshortener.domain.ShortURL;
import urlshortener.service.ClickService;
import urlshortener.service.ShortURLService;

//import org.springframework.web.multipart;
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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringWriter;  
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.Base64;

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
@RequestMapping(value = "/csv", method = RequestMethod.POST)
public ResponseEntity<String> generateShortenedCSV(@RequestParam("csv") MultipartFile csv, @RequestParam(value = "sponsor", required = false) String sponsor,HttpServletRequest request)
      throws MultipartException, FileNotFoundException, IOException {
  /*String name = csv.getName();
  String[] check = name.split(".");
  if ((check[check.length-1]=="csv")) { //csv.isFile() && 

    BufferedReader csvReader = new BufferedReader(new FileReader(csv));

    FileWriter csvWriter = new FileWriter(csv);
    String row;
    while ((row = csvReader.readLine()) != null) {
      String[] data = row.split(",");
      String[] shortened = data;
      for (int i=0;i<data.length;i++){
        shortened[i] = shortenerCSV(data[i]);
      }
      csvWriter.append(String.join(",",shortened));
      csvWriter.append("\n");
    }
    csvReader.close();
    csvWriter.flush();

    return csvWriter.toString();*/
    String answer="good";
    return new ResponseEntity<>(answer,HttpStatus.CREATED);/*
  } else {
    return "error";
  }*/
}
public String shortenerCSV(String url) {
    UrlValidator urlValidator = new UrlValidator(new String[] {"http","https"});
    if (urlValidator.isValid(url)) {
      ShortURL su = shortUrlService.save(url, "0", "0");
      return su.getUri().toString();
    } else {
      return "Couldn't convert the url \" "+url+" \"";
    }
  }
}