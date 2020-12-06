package urlshortener.domain;

import java.net.URI;
import java.sql.Date;

public class ShortURL {

  private String hash;
  private String target;
  private URI uri;
  private String sponsor;
  private Date created;
  private String owner;
  private Integer mode;
  private Boolean safe;
  private String ip;
  private String country;

  private String qr;
  private String qrUrl;


  public ShortURL(String hash, String target, URI uri, String sponsor,
                  Date created, String owner, Integer mode, Boolean safe, String ip,
                  String country,String qr, String qrUrl) {
    this.hash = hash;
    this.target = target;
    this.uri = uri;
    this.sponsor = sponsor;
    this.created = created;
    this.owner = owner;
    this.mode = mode;
    this.safe = safe;
    this.ip = ip;
    this.country = country;
    this.qr = qr;
    this.qrUrl = qrUrl;
  }

  public ShortURL() {
  }

  public String getHash() {
    return hash;
  }

  public void setHash(String hash) {
    this.hash = hash;
  }

  public String getTarget() {
    return target;
  }

  public void setTarget(String target) {
    this.target = target;
  }

  public URI getUri() {
    return uri;
  }

  public void setUri(URI uri) {
    this.uri = uri;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public Integer getMode() {
    return mode;
  }

  public void setMode(Integer mode) {
    this.mode = mode;
  }

  public String getSponsor() {
    return sponsor;
  }

  public void setSponsor(String sponsor) {
    this.sponsor = sponsor;
  }

  public Boolean getSafe() {
    return safe;
  }

  public void setSafe(Boolean safe) {
    this.safe = safe;
  }

  public String getIP() {
    return ip;
  }

  public void setIP(String ip) {
    this.ip = ip;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getQr(){
    return qr;
  }

  public void setQr(String newQr) {
    this.qr = newQr;
  }

  public String getQrUrl(){
    return qrUrl;
  }

  public void setQrUrl(String newQrUrl) {
    this.qrUrl = newQrUrl;
  }
}
