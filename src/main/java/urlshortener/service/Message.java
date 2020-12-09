package urlshortener.service;

public class Message {

    private String from;
    private String text;

    public Message() {
    }
  
    public Message(String Nfrom, String Ntext) {
        this.from = Nfrom;
        this.text = Ntext;
    }
  
    public String getFrom() {
        return from;
    }  

    public String getText() {
      return text;
    }
  
    public void setFrom(String Nfrom) {
      this.from = Nfrom;
    }
  
    public void setText(String Ntext) {
      this.text = Ntext;
    }
}