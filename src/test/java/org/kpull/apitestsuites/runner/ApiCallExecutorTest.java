package org.kpull.apitestsuites.runner;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;
import org.junit.Test;
import org.kpull.apitestsuites.core.ApiCall;
import org.kpull.apitestsuites.core.ApiSuite;
import org.kpull.apitestsuites.core.ApiSuiteBuilder;
import org.kpull.apitestsuites.support.WeatherModel;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.data.MapEntry.entry;

/**
 * @author <a href="mailto:mail@kylepullicino.com">Kyle</a>
 */
public class ApiCallExecutorTest {

    private ApiSuite createApiSuite() {
        // @formatter:off
        return ApiSuiteBuilder.start()
                .name("Open Weather API")
                .environment()
                    .entry("APPID", System.getProperty("WeatherApiKey"))
                    .done()
                .call()
                    .name("Get London's Current Weather")
                    .description("Get the current weather conditions in London, UK.")
                    .request()
                        .method("GET")
                        .url("http://api.openweathermap.org/data/2.5/weather")
                        .type("application/json")
                        .queryParam("q", "London")
                        .queryParam("APPID", "{{APPID}}")
                        .done()
                    .responseModel(WeatherModel.class)
                        .assertions((apiEnvironment, apiCall, statusCode, model) -> {
                            assertThat(model.getDt()).isNotEmpty();
                        })
                    .postCallScript(
                            "environment.putObject('lat', httpResponse.body.object.get('coord').get('lat'));" +
                            "System.out.println(model);"
                    )
                    .done()
                .build();
        // @formatter:on
    }

    @Test
    public void execute() throws Exception {
        ApiSuite apiSuite = createApiSuite();
        ApiCall apiCallToExecute = apiSuite.getApiCall().get(0);
        ApiCallExecutor executor = new ApiCallExecutor(apiSuite.getEnvironment(), apiCallToExecute, new ObjectMapper());
        executor.execute();
        assertThat(apiCallToExecute.getResponse().getStatusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(apiCallToExecute.getResponse().getBody()).isNotEmpty();
        assertThat(apiSuite.getEnvironment()).contains(entry("lat", "51.51"));
    }

}