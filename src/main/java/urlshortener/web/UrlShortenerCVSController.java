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
public ResponseEntity<File> generateShortenedCSV(@RequestParam("csv") MultipartFile csv, @RequestParam(value = "sponsor", required = false) String sponsor,HttpServletRequest request)
  throws MultipartException, FileNotFoundException, IOException {
  if (csv.getOriginalFilename().length()>1) { //Hay fichero, sino es una petición vacía

    File file = new File("shortened.csv");

    InputStream is = csv.getInputStream();
    BufferedReader br = new BufferedReader(new InputStreamReader(is));
    BufferedWriter csvWriter = new BufferedWriter(new FileWriter(file));
    
    String line;
    while ((line = br.readLine()) != null) {
      String shortLine = shortenerCSV(line);
      csvWriter.write(line+";"+shortLine+";exito;\n"); //TODO: Cambiar exito por el resultado real y no escribir si da error
    }

    //returnable=returnable+line+";"+shortLine+";exito;\n";
    //FileWriter csvWriter = new FileWriter(file);
    /*String row;
    while ((row = csvReader.readLine()) != null) {
      String[] data = row.split(",");
      String[] shortened = data;
      for (int i=0;i<data.length;i++){
        shortened[i] = shortenerCSV(data[i]);
      }*/
      //csvWriter.append(String.join(",",shortened));
      //csvWriter.append("\n");
    //}
    //csvReader.close();
    csvWriter.flush();
    csvWriter.close();
    return new ResponseEntity<>(file,HttpStatus.CREATED);
  } else {
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }
}

public String shortenerCSV(String url) {
    UrlValidator urlValidator = new UrlValidator(new String[] {"http","https"});
    if (urlValidator.isValid(url)) {
      ShortURL su = shortUrlService.save(url, "0", "0");
      return su.getUri().toString();
    } else {
      return "ERROR";
    }
  }
}