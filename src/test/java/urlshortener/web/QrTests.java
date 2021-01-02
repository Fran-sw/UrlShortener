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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static urlshortener.fixtures.ShortURLFixture.someUrl;
import static urlshortener.fixtures.ShortURLFixture.shortURL1;
import static urlshortener.fixtures.ShortURLFixture.shortURL2;
import static urlshortener.fixtures.ShortURLFixture.shortURL3;
import static urlshortener.fixtures.ShortURLFixture.shortURL4;
import static urlshortener.fixtures.ShortURLFixture.shortURL5;

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
import org.springframework.http.MediaType;

import urlshortener.domain.ShortURL;
import urlshortener.service.ClickService;
import urlshortener.service.ShortURLService;



public class QrTests {

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
  public void thatvalidURLgetsQRifCheckbox()
      throws Exception {
    configureSave(null);
    when(shortUrlService.checkReachable(any())).thenReturn(true);
    when(shortUrlService.mark(any(),anyBoolean())).thenReturn(shortURL3());

    mockMvc.perform(
        post("/link").param("url", "http://example.com/").param(
            "qr", "yes"))
        .andDo(print())
        .andExpect(redirectedUrl("http://localhost/f684a3c4"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.hash", is("f684a3c4")))
        .andExpect(jsonPath("$.uri", is("http://localhost/f684a3c4")))
        .andExpect(jsonPath("$.target", is("http://example.com/")));
  }

  @Test
  public void thatNOvalidURLgetsQRifCheckbox()
      throws Exception {
    configureSave(null);
    when(shortUrlService.checkReachable(any())).thenReturn(true);
    when(shortUrlService.mark(any(),anyBoolean())).thenReturn(shortURL3());

    mockMvc.perform(
        post("/link").param("url", "httc://example./").param(
            "qr", "yes"))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  public void getQrFromValidhash()
      throws Exception {
        when(shortUrlService.findByKey(any())).thenReturn(shortURL5());

    mockMvc.perform(get("/qr/{id}", "f684a3c4"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.IMAGE_JPEG_VALUE));
  }

  @Test
  public void getQrFromValidhashNoCreated()
      throws Exception {
        when(shortUrlService.findByKey(any())).thenReturn(shortURL4());

    mockMvc.perform(get("/qr/{id}", "f684a3c4"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.IMAGE_JPEG_VALUE));
  }

  @Test
  public void getQrFromNoValidhash()
      throws Exception {
    configureSave(null);
    when(shortUrlService.checkReachable(any())).thenReturn(true);
    when(shortUrlService.mark(any(),anyBoolean())).thenReturn(shortURL3());

    mockMvc.perform(get("/qr/{id}", "g7sdj45g"))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }


  private void configureSave(String qrUrl) {
    when(shortUrlService.save(any(), any(), any()))
        .then((Answer<ShortURL>) invocation -> new ShortURL(
            "f684a3c4",
            "http://example.com/",
            URI.create("http://localhost/f684a3c4"),
            null,
            null,
            null,
            0,
            false,
            null,
            null,
            null,
            qrUrl));
  }
}