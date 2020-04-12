package core;

import models.ActionModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import utils.DateConverterUtils;

import static io.restassured.RestAssured.with;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

public class Actions {

    private static final Logger LOGGER = LogManager.getLogger(Project.class.getName());
    private final JtwigTemplate JSON_BODY = JtwigTemplate.classpathTemplate("createActionRequest.json");

    public ActionModel createAction(int actionCount, String productId, String actionDate, String app, double longitude, double latitude,
                                    String locationSource, String apiKey, String projectId) {
        JtwigModel model =
                JtwigModel.newModel()
                        .with("productId", productId)
                        .with("actionDate", DateConverterUtils.convertUtcToEpoch(actionDate))
                        .with("app", app)
                        .with("longitude", longitude)
                        .with("latitude", latitude)
                        .with("locationSource", locationSource);
        ActionModel actionModel = new ActionModel();
        for (int actCount = 1; actCount <= actionCount; actCount++) {
            with().log().all().body(JSON_BODY.render(model))
                    .when()
                    .header(AUTHORIZATION, apiKey)
                    .header(CONTENT_TYPE, APPLICATION_JSON)
                    .post("/actions/scans?project=" + projectId)
                    .then()
                    .statusCode(201);
            LOGGER.info("Action is created");
        }
        return actionModel;
    }
}
