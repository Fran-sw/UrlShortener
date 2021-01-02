package urlshortener.service;

public class MessageInternal {

    private String content;
    private String answer;

    public MessageInternal() {
    }
  
    public MessageInternal(String Ncontent, String Nanswer) {
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