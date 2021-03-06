package urlshortener.web;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
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
import static urlshortener.fixtures.ShortURLFixture.shortURL4;

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
import org.springframework.web.bind.EscapedErrors;

import urlshortener.domain.ShortURL;
import urlshortener.service.ClickService;
import urlshortener.service.ShortURLService;

public class RechableTest {

  private MockMvc mockMvc;

  @Mock
  private ClickService clickService;

  @Mock
  private ShortURLService shortUrlService;

  @InjectMocks
  private UrlShortenerController urlShortener;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    this.mockMvc = MockMvcBuilders.standaloneSetup(urlShortener).build();
  }

  @Test
  public void testRechableOk()
      throws Exception {
        configureSave(null);
        when(shortUrlService.checkReachable(any())).thenReturn(true);
        when(shortUrlService.mark(any(),anyBoolean())).thenReturn(shortURL1());
    
        mockMvc.perform(post("/link").param("url", "http://example.com/"))
            .andDo(print())
            .andExpect(redirectedUrl("http://localhost/f684a3c4"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.hash", is("f684a3c4")))
            .andExpect(jsonPath("$.uri", is("http://localhost/f684a3c4")))
            .andExpect(jsonPath("$.target", is("http://example.com/")))
            .andExpect(jsonPath("$.safe", is(true)));
  }

  @Test
  public void testRechableNotOk()
      throws Exception {
        configureSave(null);
        when(shortUrlService.checkReachable(any())).thenReturn(false);
        when(shortUrlService.mark(any(),anyBoolean())).thenReturn(shortURL4());
    
        mockMvc.perform(post("/link").param("url", "http://examplee.com/"))
            .andDo(print())
            .andExpect(redirectedUrl("http://localhost/f684a3c4"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.hash", is("f684a3c4")))
            .andExpect(jsonPath("$.uri", is("http://localhost/f684a3c4")))
            .andExpect(jsonPath("$.target", is("http://examplee.com/")))
            .andExpect(jsonPath("$.safe", is(false)));
  }

  private void configureSave(String sponsor) {
    when(shortUrlService.save(any(), any(), any()))
        .then((Answer<ShortURL>) invocation -> new ShortURL(
            "f684a3c4",
            "http://example.com/",
            URI.create("http://localhost/f684a3c4"),
            sponsor,
            null,
            null,
            0,
            false,
            null,
            null,
            null,
            null));
  }

}