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
import org.mockito.stubbing.Answer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import urlshortener.domain.ShortURL;
import urlshortener.service.ClickService;
import urlshortener.service.ShortURLService;
import urlshortener.service.ServiceAgents;

import java.util.*;


public class UserAgentsTest {

  private MockMvc mockMvc;

  @Mock
  private ClickService clickService;

  @Mock
  private ShortURLService shortUrlService;

  @Mock
  private ServiceAgents serviceAgents;

  @InjectMocks
  private UrlShortenerAgentsController userAgents;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    this.mockMvc = MockMvcBuilders.standaloneSetup(userAgents).build();
  }

  @Test
  @Ignore
  public void userAgentsTest()
      throws Exception {
        when(serviceAgents.getAgentsInfo()).thenReturn(createMap());

        // SHOULD THERE BE AN OTHER OK FOR URLSERIE? -> maybe they share a lot with shortener test????
        // Index.html shows that might be the case

        // We post an url -> there should not be a count on user agents
        mockMvc.perform(
          post("/link").param("url", "http://example.com/").param(
          "sponsor", "http://sponsor.com/"))
          .andDo(print());

        // We perfom a get to the just created url -> it will start a count of user agents
        mockMvc.perform(
          get("/f684a3c4"))
          .andDo(print());

        // There will be 5 sec delay between each calculation of the top5 useragents
        Thread.sleep(5000);

        // We need to get the user agents
        mockMvc.perform(
          get("/agentsInfo"))
          .andDo(print());
      
  }
  private Map<String,Integer> createMap(){
    Map<String, Integer> m = new HashMap<String, Integer>();
    m.put("Win10",1);
    m.put("Chrome", 1);
    return m;
  }
}