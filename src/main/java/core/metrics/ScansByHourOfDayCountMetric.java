package core.metrics;

import io.restassured.response.ValidatableResponse;
import models.metrics.ScansByHourOfDayCountModel;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

public class ScansByHourOfDayCountMetric {
    private static final Logger LOGGER = LogManager.getLogger(ScansByHourOfDayCountMetric.class.getName());

    public List<ScansByHourOfDayCountModel> getByHourOfDayCount(String apiKey, long fromDate, long toDate, String projectId,
                                                                String product, String region, String locationSource) {

        ValidatableResponse response = given().with().log().all()
                .header(AUTHORIZATION, apiKey)
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .get("/[api]/[meticName]/" + //changed as private customer info
                        "compute?type=scans&to=" + toDate + "&from=" + fromDate
                        + "&project=" + projectId + "&product=" + product + "&region=" + region + "&locationSource="
                        + locationSource)
                .then().log().all()
                .statusCode(200);

        List<ScansByHourOfDayCountModel> scansByHourOfDayCountModel = response.extract().jsonPath()
                .getList("$", ScansByHourOfDayCountModel.class);
        LOGGER.info("Scans by hour of day" + scansByHourOfDayCountModel);

        return scansByHourOfDayCountModel;
    }
}

