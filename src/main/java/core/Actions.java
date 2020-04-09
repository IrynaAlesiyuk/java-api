package core;

import io.restassured.response.ValidatableResponse;
import models.ActionModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import utils.DateConverterUtils;

import static io.restassured.RestAssured.with;

public class Actions {

    private static final Logger LOGGER = LogManager.getLogger(Project.class.getName());

    public ActionModel createAction(int actionCount, String productId, String actionDate, String app, double longitude, double latitude,
                                    String locationSource, String apiKey, String projectId) {
        JtwigTemplate jsonBody =
                JtwigTemplate.classpathTemplate("createActionRequest.json");
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
            ValidatableResponse response = with().log().all().body(jsonBody.render(model))
                    .when()
                    .header("Authorization", apiKey)
                    .header("Content-Type", "application/json")
                    .request("POST", "/actions/scans?project=" + projectId)
                    .then()
                    .statusCode(201);

            actionModel.setActionId(response.extract().path("id"));
            LOGGER.info("Action " + actionModel.getActionId() + " is created");
        }
        return actionModel;
    }
}
