package rocks.bastion.core;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import rocks.bastion.core.json.InvalidJsonException;

import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A form URL-encoded data request for use during Bastion tests. After selecting the HTTP method to send by using one of
 * the static factory methods, you can supply the content data to send with the request using the various {@link #addDataParameter(String, String)}
 * methods. The data will automatically be used to generate a URL encoded string which is used as the body content.
 * <p>
 * Initially, the request is configured to send "application/x-www-form-urlencoded" as its Content-type header but this
 * can be overriden by the user by calling the {@link #overrideContentType(ContentType)} method.
 */
public class FormUrlEncodedRequest implements HttpRequest {

    /**
     * Construct an HTTP request containing a URL-encoded string as its content body. Initially, the request will have
     * the "application/x-www-form-urlencoded" HTTP header and no other additional headers and no query parameters. It
     * will also not contain any data parameters. It will have a descriptive name which is generated by combining the
     * HTTP method with the URL.
     *
     * @param method The HTTP method to use for this request
     * @param url    The URL to send this request on
     * @return An HTTP request containing an empty URL-encoded string in its content body
     */
    public static FormUrlEncodedRequest withMethod(HttpMethod method, String url) {
        return new FormUrlEncodedRequest(method, url);
    }

    /**
     * Construct an HTTP request, using the {@code POST} method, containing a URL-encoded string as its content body.
     * Initially, the request will have the "application/x-www-form-urlencoded" HTTP header and no other additional
     * headers and no query parameters. It will also not contain any data parameters. It will have a descriptive name
     * which is generated by combining the HTTP method with the URL.
     *
     * @param url The URL to send this request on
     * @return An HTTP request containing an empty URL-encoded string in its content body
     */
    public static FormUrlEncodedRequest post(String url) {
        return new FormUrlEncodedRequest(HttpMethod.POST, url);
    }

    /**
     * Construct an HTTP request, using the {@code PUT} method, containing a URL-encoded string as its content body.
     * Initially, the request will have the "application/x-www-form-urlencoded" HTTP header and no other additional
     * headers and no query parameters. It will also not contain any data parameters. It will have a descriptive name
     * which is generated by combining the HTTP method with the URL.
     *
     * @param url The URL to send this request on
     * @return An HTTP request containing an empty URL-encoded string in its content body
     */
    public static FormUrlEncodedRequest put(String url) {
        return new FormUrlEncodedRequest(HttpMethod.PUT, url);
    }

    /**
     * Construct an HTTP request, using the {@code DELETE} method, containing a URL-encoded string as its content body.
     * Initially, the request will have the "application/x-www-form-urlencoded" HTTP header and no other additional
     * headers and no query parameters. It will also not contain any data parameters. It will have a descriptive name
     * which is generated by combining the HTTP method with the URL.
     *
     * @param url The URL to send this request on
     * @return An HTTP request containing an empty URL-encoded string in its content body
     */
    public static FormUrlEncodedRequest delete(String url) {
        return new FormUrlEncodedRequest(HttpMethod.DELETE, url);
    }

    /**
     * Construct an HTTP request, using the {@code DELETE} method, containing a URL-encoded string as its content body.
     * Initially, the request will have the "application/x-www-form-urlencoded" HTTP header and no other additional
     * headers and no query parameters. It will also not contain any data parameters. It will have a descriptive name
     * which is generated by combining the HTTP method with the URL.
     *
     * @param url The URL to send this request on
     * @return An HTTP request containing an empty URL-encoded string in its content body
     */
    public static FormUrlEncodedRequest patch(String url) {
        return new FormUrlEncodedRequest(HttpMethod.PATCH, url);
    }

    private CommonRequestAttributes requestAttributes;
    private List<ApiDataParameter> dataParameters;

    protected FormUrlEncodedRequest(HttpMethod method, String url) throws InvalidJsonException {
        Objects.requireNonNull(method);
        Objects.requireNonNull(url);

        requestAttributes = new CommonRequestAttributes(method, url, "");
        requestAttributes.setContentType(ContentType.APPLICATION_FORM_URLENCODED);

        dataParameters = new LinkedList<>();
    }

    /**
     * Adds a single data parameter to be sent with the URL-encoded string in this request's content-body.
     *
     * @param name  The non-{@literal null} name to use for this data parameter's key
     * @param value The non-{@literal null} value to use for this data parameter's key
     * @return This request (for method chaining)
     */
    public FormUrlEncodedRequest addDataParameter(String name, String value) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(value);
        dataParameters.add(new ApiDataParameter(name, value));
        recomputeBody();
        return this;
    }

    /**
     * Adds data parameters, represented by the given map, to be sent with the URL-encoded string in this request's content-body.
     * Each entry in the map is considered to be a separate data parameter: for each entry in the map, the key will be
     * the data parameter's name and the entry's value will become the data parameter's value.
     *
     * @param parameters The non-{@literal null} map of data parameters to add to this request
     * @return This request (for method chaining)
     */
    public FormUrlEncodedRequest addDataParameters(Map<String, String> parameters) {
        Objects.requireNonNull(parameters);
        List<ApiDataParameter> listFromMap = parameters.entrySet().stream().map(entry ->
                new ApiDataParameter(entry.getKey(), entry.getValue())).collect(Collectors.toList());
        addDataParameters(listFromMap);
        return this;
    }

    /**
     * Adds a whole collection of data parameters to be sent with the URL-encoded string in this request's content-body.
     *
     * @param parameters The non-{@literal null} iterable collection of data parameters to add to this request
     */
    public void addDataParameters(Iterable<ApiDataParameter> parameters) {
        Objects.requireNonNull(parameters);
        for (ApiDataParameter parameter : parameters) {
            dataParameters.add(new ApiDataParameter(parameter.getName(), parameter.getValue()));
        }
        recomputeBody();
    }

    /**
     * Override the content-type that will be used for this request. Initially, the content-type for a {@code FormUrlEncodedRequest}
     * is "application/x-www-form-urlencoded" but you can override what is sent using this method.
     *
     * @param contentType A content-type to use for this request. Can be {@literal null}.
     * @return This request (for method chaining)
     */
    public FormUrlEncodedRequest overrideContentType(ContentType contentType) {
        requestAttributes.setContentType(contentType);
        // Recompute the body because the charset could have changed
        recomputeBody();
        return this;
    }

    /**
     * Add a new HTTP header that will be sent with this request.
     *
     * @param name  A non-{@literal null} name for the new header
     * @param value A non-{@literal null} value for the new header
     * @return This request (for method chaining)
     */
    public FormUrlEncodedRequest addHeader(String name, String value) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(value);
        requestAttributes.addHeader(name, value);
        return this;
    }

    /**
     * Add a new HTTP query parameter that will be sent with this request.
     *
     * @param name  A non-{@literal null} name for the new query parameter
     * @param value A non-{@literal null} value for the new query parameter
     * @return This request (for method chaining)
     */
    public FormUrlEncodedRequest addQueryParam(String name, String value) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(value);
        requestAttributes.addQueryParam(name, value);
        return this;
    }

    /**
     * Add a new HTTP route parameter that will be sent with this request. Put a placeholder for the route parameter in
     * the request URL by surrounding a parameter's name using braces (eg. {@code http://sushi.test/{id}/ingredients}).
     * The URL in the previous example contains one route param which can be replaced with a numerical value using
     * {@code addRouteParam("id", "53")}, for example.
     *
     * @param name  A non-{@literal null} name for the new route parameter
     * @param value A non-{@literal null} value for the new route parameter
     * @return This request (for method chaining)
     */
    public FormUrlEncodedRequest addRouteParam(String name, String value) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(value);
        requestAttributes.addRouteParam(name, value);
        return this;
    }

    @Override
    public String name() {
        return requestAttributes.name();
    }

    @Override
    public String url() {
        return requestAttributes.url();
    }

    @Override
    public HttpMethod method() {
        return requestAttributes.method();
    }

    @Override
    public Optional<ContentType> contentType() {
        return requestAttributes.contentType();
    }

    @Override
    public Collection<ApiHeader> headers() {
        return requestAttributes.headers();
    }

    @Override
    public Collection<ApiQueryParam> queryParams() {
        return requestAttributes.queryParams();
    }

    @Override
    public Collection<RouteParam> routeParams() {
        return requestAttributes.routeParams();
    }

    @Override
    public Object body() {
        return requestAttributes.body();
    }

    @Override
    public long timeout() {
        return requestAttributes.timeout();
    }

    /**
     * See {@link HttpRequest#timeout()} for details.
     * @param timeout the timeout for the request phases, in milliseconds
     */
    public FormUrlEncodedRequest setTimeout(long timeout) {
        requestAttributes.setTimeout(timeout);
        return this;
    }

    private void recomputeBody() {
        Charset encodingCharset = getEncodingCharset();
        String urlEncodedBody = URLEncodedUtils.format(BastionUtils.propertiesToNameValuePairs(dataParameters), encodingCharset);
        requestAttributes.setBody(urlEncodedBody);
    }

    private Charset getEncodingCharset() {
        return contentType().map(ContentType::getCharset).orElse(Charset.defaultCharset());
    }
}
