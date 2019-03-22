package kr.co.pjm.diving.batch.configuration.web.interceptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import kr.co.pjm.diving.batch.configuration.web.interceptor.wrapper.BufferingClientHttpResponseWrapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggingClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body,
      ClientHttpRequestExecution execution) throws IOException {
    traceRequest(request, body);
    ClientHttpResponse response = execution.execute(request, body);
    return traceResponse(response);
  }

  private void traceRequest(HttpRequest request, byte[] body) throws IOException {
    if (!log.isDebugEnabled()) {
      return;
    }
    
    log.debug("==========================request begin==============================================");
    log.debug("URI : {}", request.getURI());
    log.debug("Method : {}", request.getMethod());
    log.debug("Headers : {}", request.getHeaders());
    log.debug("Request body : {}", new String(body, "UTF-8"));
    log.debug("==========================request end================================================");
  }

  private ClientHttpResponse traceResponse(ClientHttpResponse response) throws IOException {
    if (!log.isDebugEnabled()) {
      return response;
    }
    
    final ClientHttpResponse responseWrapper = new BufferingClientHttpResponseWrapper(response);
    StringBuilder inputStringBuilder = new StringBuilder();
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(responseWrapper.getBody(), "UTF-8"));
    String line = bufferedReader.readLine();
    
    while (line != null) {
      inputStringBuilder.append(line);
      inputStringBuilder.append('\n');
      line = bufferedReader.readLine();
    }
    
    log.debug("==========================response begin=============================================");
    log.debug("Status code : {}", responseWrapper.getStatusCode());
    log.debug("Status text : {}", responseWrapper.getStatusText());
    log.debug("Headers : {}", responseWrapper.getHeaders());
    log.debug("Response body : {}", inputStringBuilder.toString());
    log.debug("==========================response end===============================================");
    
    return responseWrapper;
  }

}
