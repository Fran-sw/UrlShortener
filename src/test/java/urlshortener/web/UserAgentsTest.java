package urlshortener.web;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static urlshortener.fixtures.ShortURLFixture.someUrl;
import static urlshortener.fixtures.ShortURLFixture.shortURL1;
import static urlshortener.fixtures.ShortURLFixture.shortURL2;



import java.net.URI;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.StreamingHttpOutputMessage.Body;
import org.mockito.stubbing.Answer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import urlshortener.domain.ShortURL;
import urlshortener.service.ClickService;
import urlshortener.service.ShortURLService;
import urlshortener.service.ServiceAgents;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.*;

import javax.swing.text.AbstractDocument.Content;


public class UserAgentsTest {

  private MockMvc mockMvc;
  private MockMvc mockMvc2;

  @Mock
  private ClickService clickService;

  @Mock
  private ShortURLService shortUrlService;

  @Mock
  private ServiceAgents serviceAgents;

  @InjectMocks
  private UrlShortenerController urlShortener;

  @InjectMocks
  private UrlShortenerAgentsController agentsCont;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    this.mockMvc = MockMvcBuilders.standaloneSetup(urlShortener).build();
    this.mockMvc2 = MockMvcBuilders.standaloneSetup(agentsCont).build();
  }

  // CONFIRM THAT WITH USER AGENTS != null -> is invoked process of it
  @Test
  public void onClickInvokesAgents() throws Exception{
    
    mockMvc.perform(get("/{id}", "f684a3c4").header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36"))
        .andDo(print());

    verify(serviceAgents).processAgents(any());
  }

  @Test
  public void checkThatCalculationAreOk()
      throws Exception {

        //We try to get the info -> After it calculateTop5 should have been invoked
        // Also expect the list completly clear since it hasnt had time to calculate top 5
        mockMvc2.perform(get("/agentsInfo").header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36"))
        .andDo(print())
        .andExpect(content().json("{}"));

        verify(serviceAgents).calculateTop5();

        //wait 5 sec... and repeat but diferent outcome!
        Thread.sleep(5000);

        mockMvc2.perform(get("/agentsInfo").header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36"))
        .andDo(print())
        .andExpect(content().json("{}"));

        verify(serviceAgents,times(2)).calculateTop5();
      
  }

  
  private Map<String,Integer> createMap(){
    Map<String, Integer> m = new HashMap<String, Integer>();
    m.put("Win10",1);
    m.put("Chrome", 1);
    return m;
  }
}