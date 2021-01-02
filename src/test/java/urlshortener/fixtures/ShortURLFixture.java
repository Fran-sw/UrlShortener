package urlshortener.fixtures;

import urlshortener.domain.ShortURL;
import java.net.URI;


public class ShortURLFixture {

  public static ShortURL url1() {
    return new ShortURL("1", "http://www.unizar.es/", null, null, null, null, null, false,
        null, null,null,null);
  }

  public static ShortURL url1modified() {
    return new ShortURL("1", "http://www.unizar.org/", null, null, null, null, null, false,
        null, null,null,null);
  }

  public static ShortURL url2() {
    return new ShortURL("2", "http://www.unizar.es/", null, null, null, null, null, false,
        null, null,null,null);
  }

  public static ShortURL url3() {
    return new ShortURL("3", "http://www.google.es/", null, null, null, null, null, false,
        null, null,null,null);
  }

  public static ShortURL badUrl() {
    return new ShortURL(null, null, null, null, null, null, null, false,
        null, null,null,null);
  }

  public static ShortURL urlSponsor() {
    return new ShortURL("3", null, null, "sponsor", null, null, null,
        false, null, null,null,null);
  }

  public static ShortURL urlSafe() {
    return new ShortURL("4", null, null, "sponsor", null, null, null, true,
        null, null,null,null);
  }

  public static ShortURL someUrl() {
    return new ShortURL("someKey", "http://example.com/", null, null, null,
        null, 307, true, null, null,null,null);
  }

  public static ShortURL shortURL1() {
    try{
      URI uri = new URI("http://localhost/f684a3c4");
      return new ShortURL("f684a3c4", "http://example.com/", uri, null, null,
      null, null, true, "127.0.0.1", null,null,null);
    }
   catch (Exception e) {
      return null;
    }
  }

  public static ShortURL shortURL2() {
    try{
      URI uri = new URI("http://localhost/f684a3c4");
      return new ShortURL("f684a3c4", "http://example.com/", uri, "http://sponsor.com/", null,
      null, null, true, "127.0.0.1", null,null,null);
    }
   catch (Exception e) {
      return null;
    }
  }

  public static ShortURL shortURL3() {
    try{
      URI uri = new URI("http://localhost/f684a3c4");
      return new ShortURL("f684a3c4", "http://example.com/", uri, null, null,
      null, null, true, "127.0.0.1", null,null,"http://localhost:8080/qr/f684a3c4");
    }
   catch (Exception e) {
      return null;
    }
  }

  public static ShortURL shortURL4() {
    try{
      URI uri = new URI("http://localhost/f684a3c4");
      return new ShortURL("f684a3c4", "http://examplee.com/", uri, "http://sponsor.com/", null,
      null, null, false, "127.0.0.1", null,null,"http://localhost:8080/qr/f684a3c4");
    }
   catch (Exception e) {
      return null;
    }
  }

  public static ShortURL shortURL5() {
    try{
      URI uri = new URI("http://localhost/f684a3c4");
      return new ShortURL("f684a3c4", "http://example.com/", uri, "http://sponsor.com/", null,
      null, null, false, "127.0.0.1", null,"ASDEWRD2345256eDAS","http://localhost:8080/qr/f684a3c4");
    }
   catch (Exception e) {
      return null;
    }
  }

}
