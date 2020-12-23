package urlshortener.service;

public class Message {

    private String content;
    private String answer;

    public Message() {
    }
  
    public Message(String Ncontent, String Nanswer) {
        this.content = Ncontent;
        this.answer = Nanswer;
    }
  
    public String getContent() {
        return content;
    } 

    public String getAnswer() {
      return answer;
    }
  
    public void setContent(String Ncontent) {
      this.content = Ncontent;
    }
    
    public void setAnswer(String Nanswer) {
      this.answer = Nanswer;
    }
}