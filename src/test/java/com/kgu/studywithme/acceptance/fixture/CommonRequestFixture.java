package com.kgu.studywithme.acceptance.fixture;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static io.restassured.http.ContentType.JSON;
import static io.restassured.http.ContentType.MULTIPART;

public class CommonRequestFixture {
    public static ValidatableResponse getRequest(
            final String path,
            final List<Object> pathParams
    ) {
        return request(given -> given
                .contentType(JSON)
                .when()
                .get(path, pathParams.toArray())
        );
    }

    public static ValidatableResponse getRequest(
            final String accessToken,
            final String path,
            final List<Object> pathParams
    ) {
        return request(given -> given
                .contentType(JSON)
                .auth().oauth2(accessToken)
                .when()
                .get(path, pathParams.toArray())
        );
    }

    public static ValidatableResponse postRequest(
            final String path,
            final List<Object> pathParams
    ) {
        return request(given -> given
                .contentType(JSON)
                .when()
                .post(path, pathParams.toArray())
        );
    }

    public static ValidatableResponse postRequest(
            final Object body,
            final String path,
            final List<Object> pathParams
    ) {
        return request(given -> given
                .contentType(JSON)
                .body(body)
                .when()
                .post(path, pathParams.toArray())
        );
    }

    public static ValidatableResponse postRequest(
            final String accessToken,
            final String path,
            final List<Object> pathParams
    ) {
        return request(given -> given
                .contentType(JSON)
                .auth().oauth2(accessToken)
                .when()
                .post(path, pathParams.toArray())
        );
    }

    public static ValidatableResponse postRequest(
            final String accessToken,
            final Object body,
            final String path,
            final List<Object> pathParams
    ) {
        return request(given -> given
                .contentType(JSON)
                .auth().oauth2(accessToken)
                .body(body)
                .when()
                .post(path, pathParams.toArray())
        );
    }

    public static ValidatableResponse multipartRequest(
            final String fileName,
            final String path,
            final List<Object> pathParams
    ) {
        return request(given -> given
                .contentType(MULTIPART)
                .when()
                .multiPart("file", getFile(fileName))
                .post(path, pathParams.toArray())
        );
    }

    public static ValidatableResponse multipartRequest(
            final String fileName,
            final Map<String, String> params,
            final String path,
            final List<Object> pathParams
    ) {
        final RequestSpecification request = RestAssured.given().log().all()
                .contentType(MULTIPART)
                .request()
                .multiPart("file", getFile(fileName));
        params.keySet().forEach(paramKey -> request.formParam(paramKey, params.get(paramKey)));

        return request.post(path, pathParams.toArray())
                .then().log().all();
    }

    public static ValidatableResponse multipartRequest(
            final String fileName,
            final String accessToken,
            final String path,
            final List<Object> pathParams
    ) {
        return request(given -> given
                .contentType(MULTIPART)
                .auth().oauth2(accessToken)
                .when()
                .multiPart("file", getFile(fileName))
                .post(path, pathParams.toArray())
        );
    }

    public static ValidatableResponse multipartRequest(
            final String fileName,
            final Map<String, String> params,
            final String accessToken,
            final String path,
            final List<Object> pathParams
    ) {
        final RequestSpecification request = RestAssured.given().log().all()
                .contentType(MULTIPART)
                .auth().oauth2(accessToken)
                .request()
                .multiPart("file", getFile(fileName));
        params.keySet().forEach(paramKey -> request.formParam(paramKey, params.get(paramKey)));

        return request.post(path, pathParams.toArray())
                .then().log().all();
    }

    public static ValidatableResponse multipartRequest(
            final List<String> fileNames,
            final String path,
            final List<Object> pathParams
    ) {
        final RequestSpecification request = RestAssured.given().log().all()
                .contentType(MULTIPART)
                .request();
        fileNames.forEach(fileName -> request.multiPart("files", getFile(fileName)));

        return request.post(path, pathParams.toArray())
                .then().log().all();
    }

    public static ValidatableResponse multipartRequest(
            final List<String> fileNames,
            final Map<String, String> params,
            final String path,
            final List<Object> pathParams
    ) {
        final RequestSpecification request = RestAssured.given().log().all()
                .contentType(MULTIPART)
                .request();
        fileNames.forEach(fileName -> request.multiPart("files", getFile(fileName)));
        params.keySet().forEach(paramKey -> request.formParam(paramKey, params.get(paramKey)));

        return request.post(path, pathParams.toArray())
                .then().log().all();
    }

    public static ValidatableResponse multipartRequest(
            final List<String> fileNames,
            final String accessToken,
            final String path,
            final List<Object> pathParams
    ) {
        final RequestSpecification request = RestAssured.given().log().all()
                .contentType(MULTIPART)
                .auth().oauth2(accessToken)
                .request();
        fileNames.forEach(fileName -> request.multiPart("files", getFile(fileName)));

        return request.post(path, pathParams.toArray())
                .then().log().all();
    }

    public static ValidatableResponse multipartRequest(
            final List<String> fileNames,
            final Map<String, String> params,
            final String accessToken,
            final String path,
            final List<Object> pathParams
    ) {
        final RequestSpecification request = RestAssured.given().log().all()
                .contentType(MULTIPART)
                .auth().oauth2(accessToken)
                .request();
        fileNames.forEach(fileName -> request.multiPart("files", getFile(fileName)));
        params.keySet().forEach(paramKey -> request.formParam(paramKey, params.get(paramKey)));

        return request.post(path, pathParams.toArray())
                .then().log().all();
    }

    public static ValidatableResponse patchRequest(
            final String path,
            final List<Object> pathParams
    ) {
        return request(given -> given
                .contentType(JSON)
                .when()
                .patch(path, pathParams.toArray())
        );
    }

    public static ValidatableResponse patchRequest(
            final String accessToken,
            final String path,
            final List<Object> pathParams
    ) {
        return request(given -> given
                .contentType(JSON)
                .auth().oauth2(accessToken)
                .when()
                .patch(path, pathParams.toArray())
        );
    }

    public static ValidatableResponse deleteRequest(
            final String path,
            final List<Object> pathParams
    ) {
        return request(given -> given
                .contentType(JSON)
                .when()
                .delete(path, pathParams.toArray())
        );
    }

    public static ValidatableResponse deleteRequest(
            final String accessToken,
            final String path,
            final List<Object> pathParams
    ) {
        return request(given -> given
                .contentType(JSON)
                .auth().oauth2(accessToken)
                .when()
                .delete(path, pathParams.toArray())
        );
    }

    private static ValidatableResponse request(final Function<RequestSpecification, Response> function) {
        final RequestSpecification given = RestAssured.given().log().all();
        final Response response = function.apply(given);
        return response.then().log().all();
    }

    private static File getFile(final String fileName) {
        final String BASE_PATH = "src/test/resources/files/";
        try {
            return new ClassPathResource(BASE_PATH + fileName).getFile();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
