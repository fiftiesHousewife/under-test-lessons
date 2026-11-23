package io.github.fiftieshousewife.undertest.techniques.openapi;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.report.ValidationReport;
import com.atlassian.oai.validator.restassured.RestAssuredRequest;
import com.atlassian.oai.validator.restassured.RestAssuredResponse;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import java.util.stream.Collectors;

/**
 * A REST Assured filter that checks every request and answer against an OpenAPI document, as the validator's own
 * OpenApiValidationFilter does, but names each contradiction on one line: the request, the field and the rule broken.
 */
public final class DocumentCheck implements Filter {

    private final OpenApiInteractionValidator validator;

    public DocumentCheck(final String document) {
        validator = OpenApiInteractionValidator.createForSpecificationUrl(document).build();
    }

    @Override
    public Response filter(final FilterableRequestSpecification request, final FilterableResponseSpecification expected,
            final FilterContext next) {
        final Response answer = next.next(request, expected);
        final ValidationReport report =
                validator.validate(RestAssuredRequest.of(request), RestAssuredResponse.of(answer));
        if (report.hasErrors()) {
            final String exchange =
                    request.getMethod() + " " + request.getDerivedPath() + " answered " + answer.statusCode();
            throw new AssertionError(report.getMessages().stream()
                    .filter(message -> message.getLevel() == ValidationReport.Level.ERROR)
                    .map(message -> exchange + ", " + field(message) + ": " + message.getMessage())
                    .collect(Collectors.joining(System.lineSeparator())));
        }
        return answer;
    }

    private static String field(final ValidationReport.Message message) {
        final String pointer = message.getContext()
                .flatMap(ValidationReport.MessageContext::getPointers)
                .map(ValidationReport.MessageContext.Pointers::getInstance)
                .orElse("");
        return pointer.isEmpty() ? "body" : "field " + pointer.substring(1);
    }
}
