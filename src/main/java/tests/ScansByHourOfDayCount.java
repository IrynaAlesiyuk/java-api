package tests;

import core.Actions;
import core.Product;
import core.Project;
import core.metrics.ScansByHourOfDayCountMetric;
import db.MongoDbClient;
import io.restassured.RestAssured;
import models.ProductModel;
import models.ProjectModel;
import models.metrics.ScansByHourOfDayCountModel;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.DateConverterUtils;
import utils.Warnings;

import java.util.List;

import static core.Constants.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ScansByHourOfDayCount {

    private static final Logger LOGGER = LogManager.getLogger(ScansByHourOfDayCount.class.getName());

    private MongoDbClient mongoDbClient = new MongoDbClient();
    private Product product = new Product();
    Project project = new Project();
    ProductModel productModel1;
    ProductModel productModel2;

    private ProjectModel projectModel;
    private static String PROJECT_START = "2019-01-01 00:00:00";
    private static String PROJECT_END = "2023-01-20 00:00:00";

    @BeforeClass
    public void setup() {
        Warnings.disableAccessWarnings();
        LOGGER.info("Create project, sku1 and sku2 products");
        RestAssured.baseURI = "https://api"; //changed as private customer info

        projectModel = project.createProject(2, ENVIRONMENT, PROJECT_START, PROJECT_END, API_KEY); //"yyyy-MM-dd HH:mm:ss" date pattern
        String projectId = projectModel.getProjectId();
        productModel1 = product.createProduct("sku1", API_KEY, projectId);
        productModel2 = product.createProduct("sku2", API_KEY, projectId);
    }

    @AfterMethod
    public void clearDb() {
        LOGGER.info("[Step] - Clear actions from DB");
        mongoDbClient.deleteActionsFromDb1ByProjectId(DB_URL, CREATED_BY_PROJECT_ID, PROJECT_ID, projectModel.getProjectId());
        mongoDbClient.deleteActionsFromDb2ByProjectId(DB_URL, CREATED_BY_PROJECT_ID, projectModel.getProjectId());
        mongoDbClient.closeConnection();
    }

    @AfterClass
    public void clearData() {
        LOGGER.info("[Postcondition] - Delete Project, Project");
        product.deleteProduct(API_KEY, productModel1.getProductId());
        product.deleteProduct(API_KEY, productModel2.getProductId());
        project.deleteProject(API_KEY, projectModel.getProjectId());
    }

    @Test(description = "positive scenario for metric [metric name]")
    public void scenario1() throws InterruptedException {

        String projectId = projectModel.getProjectId();
        String sku1Id = productModel1.getProductId();
        String sku2Id = productModel2.getProductId();

        LOGGER.info("[Step] - Create actions");
        new Actions().createAction(1, sku1Id, "2019-01-01 00:00:00", "appId",
                -83.57997359, 41.67002626, "sensor", API_KEY, projectId);
        new Actions().createAction(1, sku2Id, "2019-01-01 00:59:59", "appId",
                -83.57997359, 41.67002626, "sensor", API_KEY, projectId);

        new Actions().createAction(1, sku1Id, "2019-01-01 01:00:00", "appId",
                -83.57997359, 41.67002626, "sensor", API_KEY, projectId);
        new Actions().createAction(1, sku1Id, "2019-01-02 02:00:00", "appId",
                -103.6650009, 30.36071657, "sensor", API_KEY, projectId);
        new Actions().createAction(1, sku1Id, "2020-03-09 03:01:00", "appId",
                27.5678, 53.8996, "sensor", API_KEY, projectId);
        new Actions().createAction(1, sku1Id, "2020-03-08 04:15:00", "appId",
                -83.57997359, 41.67002626, "sensor", API_KEY, projectId);
        new Actions().createAction(1, sku2Id, "2020-03-05 05:59:00", "appId",
                -83.57997359, 41.67002626, "sensor", API_KEY, projectId);

        new Actions().createAction(20, sku2Id, "2020-03-06 10:10:00", "appId",
                -83.57997359, 41.67002626, "sensor", API_KEY, projectId);

        new Actions().createAction(1, sku1Id, "2020-03-09 12:12:00", "appId",
                -83.57997359, 41.67002626, "sensor", API_KEY, projectId);
        new Actions().createAction(1, sku1Id, "2020-03-08 13:13:00", "appId",
                -83.57997359, 41.67002626, "sensor", API_KEY, projectId);
        new Actions().createAction(1, sku1Id, "2020-02-23 14:14:00", "appId",
                -83.57997359, 41.67002626, "sensor", API_KEY, projectId);
        new Actions().createAction(1, sku1Id, "2020-02-10 15:15:00", "appId",
                -83.57997359, 41.67002626, "sensor", API_KEY, projectId);

        new Actions().createAction(1, sku1Id, "2020-03-04 16:17:00", "appId",
                -83.57997359, 41.67002626, "unknown", API_KEY, projectId);
        new Actions().createAction(1, sku1Id, "2020-03-04 17:17:00", "appId",
                -83.57997359, 41.67002626, "unknown", API_KEY, projectId);
        new Actions().createAction(1, sku1Id, "2020-03-04 18:18:00", "appId",
                -83.57997359, 41.67002626, "unknown", API_KEY, projectId);
        new Actions().createAction(1, sku1Id, "2020-03-09 19:19:00", "appId",
                -83.57997359, 41.67002626, "unknown", API_KEY, projectId);

        new Actions().createAction(55, sku1Id, "2020-03-09 20:20:00", "appId",
                -83.57997359, 41.67002626, "sensor", API_KEY, projectId);
        new Actions().createAction(1, sku1Id, "2020-03-09 21:21:00", "appId",
                -83.57997359, 41.67002626, "sensor", API_KEY, projectId);
        new Actions().createAction(1, sku2Id, "2020-02-25 22:22:00", "appId",
                -103.6650009, 30.36071657, "sensor", API_KEY, projectId);

        long startDate = DateConverterUtils.convertUtcToEpoch("2020-02-25 00:00:00");
        long endDate = DateConverterUtils.convertUtcToEpoch("2020-03-10 23:59:59");
        Thread.sleep(75000);
        ScansByHourOfDayCountMetric scansByHourOfDayCountMetric = new ScansByHourOfDayCountMetric();

        LOGGER.info("[Step] - [since programme start filter] GET request to [metric name] metric");
        List<ScansByHourOfDayCountModel> scansByHourOfDayCountModels = scansByHourOfDayCountMetric.getByHourOfDayCount(API_KEY,
                DateConverterUtils.convertUtcToEpoch("2019-01-01 00:00:00"),
                endDate, projectId, "", "", "");
        assertThat(scansByHourOfDayCountModels.size(), is(18));
        assertThat(scansByHourOfDayCountModels.get(0).getId(), is("0"));
        assertThat(scansByHourOfDayCountModels.get(0).getCount(), is("2"));
        assertThat(scansByHourOfDayCountModels.get(1).getId(), is("1"));
        assertThat(scansByHourOfDayCountModels.get(1).getCount(), is("1"));
        assertThat(scansByHourOfDayCountModels.get(2).getId(), is("2"));
        assertThat(scansByHourOfDayCountModels.get(2).getCount(), is("1"));
        assertThat(scansByHourOfDayCountModels.get(3).getId(), is("3"));
        assertThat(scansByHourOfDayCountModels.get(3).getCount(), is("1"));
        assertThat(scansByHourOfDayCountModels.get(4).getId(), is("4"));
        assertThat(scansByHourOfDayCountModels.get(4).getCount(), is("1"));
        assertThat(scansByHourOfDayCountModels.get(5).getId(), is("5"));
        assertThat(scansByHourOfDayCountModels.get(5).getCount(), is("1"));
        assertThat(scansByHourOfDayCountModels.get(6).getId(), is("10"));
        assertThat(scansByHourOfDayCountModels.get(6).getCount(), is("20"));
        assertThat(scansByHourOfDayCountModels.get(7).getId(), is("12"));
        assertThat(scansByHourOfDayCountModels.get(7).getCount(), is("1"));
        assertThat(scansByHourOfDayCountModels.get(8).getId(), is("13"));
        assertThat(scansByHourOfDayCountModels.get(8).getCount(), is("1"));
        assertThat(scansByHourOfDayCountModels.get(9).getId(), is("14"));
        assertThat(scansByHourOfDayCountModels.get(9).getCount(), is("1"));
        assertThat(scansByHourOfDayCountModels.get(10).getId(), is("15"));
        assertThat(scansByHourOfDayCountModels.get(10).getCount(), is("1"));
        assertThat(scansByHourOfDayCountModels.get(11).getId(), is("16"));
        assertThat(scansByHourOfDayCountModels.get(11).getCount(), is("1"));
        assertThat(scansByHourOfDayCountModels.get(12).getId(), is("17"));
        assertThat(scansByHourOfDayCountModels.get(12).getCount(), is("1"));
        assertThat(scansByHourOfDayCountModels.get(13).getId(), is("18"));
        assertThat(scansByHourOfDayCountModels.get(13).getCount(), is("1"));
        assertThat(scansByHourOfDayCountModels.get(14).getId(), is("19"));
        assertThat(scansByHourOfDayCountModels.get(14).getCount(), is("1"));
        assertThat(scansByHourOfDayCountModels.get(15).getId(), is("20"));
        assertThat(scansByHourOfDayCountModels.get(15).getCount(), is("55"));
        assertThat(scansByHourOfDayCountModels.get(16).getId(), is("21"));
        assertThat(scansByHourOfDayCountModels.get(16).getCount(), is("1"));
        assertThat(scansByHourOfDayCountModels.get(17).getId(), is("22"));
        assertThat(scansByHourOfDayCountModels.get(17).getCount(), is("1"));

        LOGGER.info("[Step] - [since programme start, sku1 filter] GET request to [metric name] metric");
        List<ScansByHourOfDayCountModel> scansByHourOfDayCountModels2 = scansByHourOfDayCountMetric.getByHourOfDayCount(API_KEY,
                DateConverterUtils.convertUtcToEpoch("2019-01-01 00:00:00"), endDate, projectId, sku1Id, "", "");
        assertThat(scansByHourOfDayCountModels2.size(), is(15));
        assertThat(scansByHourOfDayCountModels2.get(0).getId(), is("0"));
        assertThat(scansByHourOfDayCountModels2.get(0).getCount(), is("1"));
        assertThat(scansByHourOfDayCountModels2.get(1).getId(), is("1"));
        assertThat(scansByHourOfDayCountModels2.get(1).getCount(), is("1"));
        assertThat(scansByHourOfDayCountModels2.get(2).getId(), is("2"));
        assertThat(scansByHourOfDayCountModels2.get(2).getCount(), is("1"));
        assertThat(scansByHourOfDayCountModels2.get(3).getId(), is("3"));
        assertThat(scansByHourOfDayCountModels2.get(3).getCount(), is("1"));
        assertThat(scansByHourOfDayCountModels2.get(4).getId(), is("4"));
        assertThat(scansByHourOfDayCountModels2.get(4).getCount(), is("1"));
        assertThat(scansByHourOfDayCountModels2.get(5).getId(), is("12"));
        assertThat(scansByHourOfDayCountModels2.get(5).getCount(), is("1"));
        assertThat(scansByHourOfDayCountModels2.get(6).getId(), is("13"));
        assertThat(scansByHourOfDayCountModels2.get(6).getCount(), is("1"));
        assertThat(scansByHourOfDayCountModels2.get(7).getId(), is("14"));
        assertThat(scansByHourOfDayCountModels2.get(7).getCount(), is("1"));
        assertThat(scansByHourOfDayCountModels2.get(8).getId(), is("15"));
        assertThat(scansByHourOfDayCountModels2.get(8).getCount(), is("1"));
        assertThat(scansByHourOfDayCountModels2.get(9).getId(), is("16"));
        assertThat(scansByHourOfDayCountModels2.get(9).getCount(), is("1"));
        assertThat(scansByHourOfDayCountModels2.get(10).getId(), is("17"));
        assertThat(scansByHourOfDayCountModels2.get(10).getCount(), is("1"));
        assertThat(scansByHourOfDayCountModels2.get(11).getId(), is("18"));
        assertThat(scansByHourOfDayCountModels2.get(11).getCount(), is("1"));
        assertThat(scansByHourOfDayCountModels2.get(12).getId(), is("19"));
        assertThat(scansByHourOfDayCountModels2.get(12).getCount(), is("1"));
        assertThat(scansByHourOfDayCountModels2.get(13).getId(), is("20"));
        assertThat(scansByHourOfDayCountModels2.get(13).getCount(), is("55"));
        assertThat(scansByHourOfDayCountModels2.get(14).getId(), is("21"));
        assertThat(scansByHourOfDayCountModels2.get(14).getCount(), is("1"));

        LOGGER.info("[Step] - [Texas filter] GET request to [metric name] metric");
        List<ScansByHourOfDayCountModel> scansByHourOfDayCountModels3 = scansByHourOfDayCountMetric.getByHourOfDayCount(API_KEY,
                startDate, endDate, projectId, "", "Texas", "");
        assertThat(scansByHourOfDayCountModels3.size(), is(1));
        assertThat(scansByHourOfDayCountModels3.get(0).getId(), is("22"));
        assertThat(scansByHourOfDayCountModels3.get(0).getCount(), is("1"));

        LOGGER.info("[Step] - [sensor filter] GET request to [metric name] metric");
        List<ScansByHourOfDayCountModel> scansByHourOfDayCountModels4 = scansByHourOfDayCountMetric.getByHourOfDayCount(API_KEY,
                startDate, endDate, projectId, "", "", "sensor");
        assertThat(scansByHourOfDayCountModels4.size(), is(9));
        assertThat(scansByHourOfDayCountModels4.get(0).getId(), is("3"));
        assertThat(scansByHourOfDayCountModels4.get(0).getCount(), is("1"));
        assertThat(scansByHourOfDayCountModels4.get(1).getId(), is("4"));
        assertThat(scansByHourOfDayCountModels4.get(1).getCount(), is("1"));
        assertThat(scansByHourOfDayCountModels4.get(2).getId(), is("5"));
        assertThat(scansByHourOfDayCountModels4.get(2).getCount(), is("1"));
        assertThat(scansByHourOfDayCountModels4.get(3).getId(), is("10"));
        assertThat(scansByHourOfDayCountModels4.get(3).getCount(), is("20"));
        assertThat(scansByHourOfDayCountModels4.get(4).getId(), is("12"));
        assertThat(scansByHourOfDayCountModels4.get(4).getCount(), is("1"));
        assertThat(scansByHourOfDayCountModels4.get(5).getId(), is("13"));
        assertThat(scansByHourOfDayCountModels4.get(5).getCount(), is("1"));
        assertThat(scansByHourOfDayCountModels4.get(6).getId(), is("20"));
        assertThat(scansByHourOfDayCountModels4.get(6).getCount(), is("55"));
        assertThat(scansByHourOfDayCountModels4.get(7).getId(), is("21"));
        assertThat(scansByHourOfDayCountModels4.get(7).getCount(), is("1"));
        assertThat(scansByHourOfDayCountModels4.get(8).getId(), is("22"));
        assertThat(scansByHourOfDayCountModels4.get(8).getCount(), is("1"));

        LOGGER.info("[Step] - [empty array] GET request to [metric name] metric");
        List<ScansByHourOfDayCountModel> scansByHourOfDayCountModels5 = scansByHourOfDayCountMetric.getByHourOfDayCount(API_KEY,
                startDate, endDate, projectId, "", "Alaska", "");
        assertThat(scansByHourOfDayCountModels5.size(), is(0));
    }
}
