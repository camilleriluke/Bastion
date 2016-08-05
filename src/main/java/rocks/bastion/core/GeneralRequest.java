package rocks.bastion.core;

import org.apache.http.entity.ContentType;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/**
 * An HTTP request which takes any arbitrary text string as its content body. The {@linkplain GeneralRequest} will not perform
 * any conversions or validation on any user-supplied body content. Use the static factory methods, such as {@link #get(String)}
 * or {@link #post(String, String)} to initialise a new {@linkplain GeneralRequest}.
 * <p>
 * By default, this request will contain no headers (except for the content-type) and no query parameters. Use the {@link #addHeader(String, String)}
 * and {@link #addQueryParam(String, String)}} to add them. Also, initially, the request will have the "text/plain" content-type MIME
 * (which is automatically added to the HTTP headers by Bastion): you can change this content-type by calling the {@link #setContentType(ContentType)}
 * method.
 */
public class GeneralRequest implements HttpRequest {

    /**
     * Represents an empty HTTP content body.
     */
    public static final String EMPTY_BODY = "";

    /**
     * Construct an HTTP request, using the GET method, to be sent on the specified URL. It will also have
     * a descriptive name which is generated by combining the HTTP method with the URL. Use the {@link GeneralRequest#addQueryParam(String, String)}
     * to add query parameters to the request.
     *
     * @param url A non-{@literal null} URL to send the request on
     * @return An HTTP request using the get method
     */
    public static HttpRequest get(String url) {
        return new GeneralRequest(HttpMethod.GET, url, EMPTY_BODY);
    }

    /**
     * Construct an HTTP request, using the POST method, to be sent on the specified URL and with the specified content body.
     * The request will initially be sent with a content-type of "text/plain". It will also have a descriptive name which
     * is generated by combining the HTTP method with the URL. If you would like to send some other representation of content
     * body then we suggest using some other implementation of {@link HttpRequest}.
     *
     * @param url  A non-{@literal null} URL to send the request on
     * @param body A non-{@literal null} string to use as body content. Pass in the {@link #EMPTY_BODY} constant if you
     *             don't want to send a body
     * @return An HTTP request using the get method
     */
    public static GeneralRequest post(String url, String body) {
        return new GeneralRequest(HttpMethod.POST, url, body);
    }

    /**
     * Construct an HTTP request, using the DELETE method, to be sent on the specified URL and with the specified content body.
     * The request will initially be sent with a content-type of "text/plain". It will also have a descriptive name which
     * is generated by combining the HTTP method with the URL. If you would like to send some other representation of content
     * body then we suggest using some other implementation of {@link HttpRequest}.
     *
     * @param url  A non-{@literal null} URL to send the request on
     * @param body A non-{@literal null} string to use as body content. Pass in the {@link #EMPTY_BODY} constant if you
     *             don't want to send a body
     * @return An HTTP request using the get method
     */
    public static GeneralRequest delete(String url, String body) {
        return new GeneralRequest(HttpMethod.DELETE, url, body);
    }

    /**
     * Construct an HTTP request, using the PUT method, to be sent on the specified URL and with the specified content body.
     * The request will initially be sent with a content-type of "text/plain". It will also have a descriptive name which
     * is generated by combining the HTTP method with the URL. If you would like to send some other representation of content
     * body then we suggest using some other implementation of {@link HttpRequest}.
     *
     * @param url  A non-{@literal null} URL to send the request on
     * @param body A non-{@literal null} string to use as body content. Pass in the {@link #EMPTY_BODY} constant if you
     *             don't want to send a body
     * @return An HTTP request using the get method
     */
    public static GeneralRequest put(String url, String body) {
        return new GeneralRequest(HttpMethod.PUT, url, body);
    }

    /**
     * Construct an HTTP request, using the PATCH method, to be sent on the specified URL and with the specified content body.
     * The request will initially be sent with a content-type of "text/plain". It will also have a descriptive name which
     * is generated by combining the HTTP method with the URL. If you would like to send some other representation of content
     * body then we suggest using some other implementation of {@link HttpRequest}.
     *
     * @param url  A non-{@literal null} URL to send the request on
     * @param body A non-{@literal null} string to use as body content. Pass in the {@link #EMPTY_BODY} constant if you
     *             don't want to send a body
     * @return An HTTP request using the get method
     */
    public static GeneralRequest patch(String url, String body) {
        return new GeneralRequest(HttpMethod.PATCH, url, body);
    }

    private CommonRequestAttributes requestAttributes;

    protected GeneralRequest(HttpMethod method, String url, String body) {
        Objects.requireNonNull(method);
        Objects.requireNonNull(url);

        requestAttributes = new CommonRequestAttributes(method, url, body);
        requestAttributes.setContentType(ContentType.TEXT_PLAIN);
    }

    /**
     * Set the content-type that will be used for this request.
     *
     * @param contentType A non-{@literal null} content-type to use for this request
     * @return This request (for method chaining)
     */
    public GeneralRequest setContentType(ContentType contentType) {
        Objects.requireNonNull(contentType);
        requestAttributes.setContentType(contentType);
        return this;
    }

    /**
     * Add a new HTTP header that will be sent with this request.
     *
     * @param name  A non-{@literal null} name for the new header
     * @param value A non-{@literal null} value for the new header
     * @return This request (for method chaining)
     */
    public GeneralRequest addHeader(String name, String value) {
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
    public GeneralRequest addQueryParam(String name, String value) {
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
    public GeneralRequest addRouteParam(String name, String value) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(value);
        requestAttributes.addRouteParam(name, value);
        return this;
    }

    /**
     * Sets the body content that will be sent by this request. You cannot set the body to {@literal null}; instead,
     * if you don't want to send any body content, pass in the {@link #EMPTY_BODY} constant.
     *
     * @param body A non-{@literal null} string to send as the body content
     * @return This request (for method chaining)
     */
    public GeneralRequest setBody(String body) {
        Objects.requireNonNull(body);
        requestAttributes.setBody(body);
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
        return Optional.ofNullable(requestAttributes.contentType());
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
}
