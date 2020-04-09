package core.metrics;

import io.restassured.response.ValidatableResponse;
import models.metrics.ScansByHourOfDayCountModel;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.function.Supplier;

import static io.restassured.RestAssured.given;

public class ScansByHourOfDayCountMetric {
    private static final Logger LOGGER = LogManager.getLogger(ScansByHourOfDayCountMetric.class.getName());

    public List<ScansByHourOfDayCountModel> getByHourOfDayCount(String apiKey, long fromDate, long toDate, String projectId,
                                                                String product, String region, String locationSource) {
        Supplier<ValidatableResponse> responseSupplier = () -> given().with().log().all().header("Authorization", apiKey)
                .header("Content-Type", "application/json")
                .request("GET", "/[api]/[meticName]/" + //changed as private customer info
                        "compute?type=scans&to=" + toDate + "&from=" + fromDate
                        + "&project=" + projectId + "&product=" + product + "&region=" + region + "&locationSource="
                        + locationSource)
                .then().log().all()
                .statusCode(200);

        List<ScansByHourOfDayCountModel> scansByHourOfDayCountModel = responseSupplier.get().
                extract().jsonPath().getList("$", ScansByHourOfDayCountModel.class);
        LOGGER.info("Scans by hour of day" + scansByHourOfDayCountModel);

        return scansByHourOfDayCountModel;
    }
}

