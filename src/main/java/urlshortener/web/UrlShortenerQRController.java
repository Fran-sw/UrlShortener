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
public class UrlShortenerQRController {

  private final ShortURLService shortUrlService;

  private final ClickService clickService;

  public UrlShortenerQRController(ShortURLService shortUrlService, ClickService clickService) {
    this.shortUrlService = shortUrlService;
    this.clickService = clickService;
  }

  //Function to generate Qr Codes given a string 
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
    
  //RESPONSE MAPPING FOR QR CODE
  @RequestMapping(value = "/linkQR", method = RequestMethod.POST)
  public ResponseEntity<ShortURL> shortenerQR(@RequestParam("url") String url,
                                            @RequestParam(value = "sponsor", required = false)
                                            String sponsor,
                                            HttpServletRequest request) {
    UrlValidator urlValidator = new UrlValidator(new String[] {"http",
        "https"});
    if (urlValidator.isValid(url)) {
      ShortURL su = shortUrlService.save(url, sponsor, request.getRemoteAddr());
      HttpHeaders h = new HttpHeaders();
      h.setLocation(su.getUri());
      
      try {
        String qr = generateQRCodeImage(su.getUri().toString(),250,250);
        su.set_qr(qr);
      } catch (WriterException e) {
          System.out.println("Could not generate QR Code, WriterException :: " + e.getMessage());
      } catch (IOException e) {
        System.out.println("Could not generate QR Code, IOException :: " + e.getMessage());
      }

      return new ResponseEntity<>(su, h, HttpStatus.CREATED);
    } else {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

}
