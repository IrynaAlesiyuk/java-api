package core;

import io.restassured.response.ValidatableResponse;
import models.ProjectModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import utils.DateConverterUtils;

import java.util.UUID;

import static io.restassured.RestAssured.with;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

public class Project {

    private static final Logger LOGGER = LogManager.getLogger(Project.class.getName());

    public ProjectModel createProject(Integer numberOfCountries, String environment, String projectStart, String projectEnd, String apiKey) {
        JtwigTemplate jsonBody;
        switch (numberOfCountries) {
            case 0:
                jsonBody = JtwigTemplate.classpathTemplate("createProject0CountriesWithTag.json");
                break;
            case 1:
                jsonBody = JtwigTemplate.classpathTemplate("createProject1CountriesWithTag.json");
                break;
            case 5:
                jsonBody = JtwigTemplate.classpathTemplate("createProject5CountriesWithTag.json");
                break;
            case 6:
                jsonBody = JtwigTemplate.classpathTemplate("createProject6CountriesWithTag.json");
                break;
            case 30:
                jsonBody = JtwigTemplate.classpathTemplate("createProject30CountriesWithTag.json");
                break;
            default:
                jsonBody = JtwigTemplate.classpathTemplate("createProjectRequest.json");
                break;
        }
        JtwigModel model =
                JtwigModel.newModel()
                        .with("projectName", generateProjectName())
                        .with("shortDomains", setShortDomain(environment))
                        .with("projectStartDate", DateConverterUtils.convertUtcToEpoch(projectStart))
                        .with("projectEndDate", DateConverterUtils.convertUtcToEpoch(projectEnd));
        ValidatableResponse response = with().body(jsonBody.render(model))
                .when()
                .header(AUTHORIZATION, apiKey)
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .post("/projects")
                .then()
                .statusCode(201);
        ProjectModel projectModel = new ProjectModel();
        projectModel.setProjectId(response.extract().path("id"));

        LOGGER.info("Project is created with project id=" + projectModel.getProjectId() + ", projectName=" +
                response.extract().path("name") + ", numberOfSkus=" +
                response.extract().path("customFields.NumberOfSKUs") + ", totalUnits=" +
                response.extract().path("customFields.TotalUnits") + ", startsAt=" +
                response.extract().path("startsAt") + ", endsAt=" + response.extract().path("endsAt"));

        return projectModel;
    }

    private String generateProjectName() {
        return "E2E project name " + UUID.randomUUID().toString();
    }

    private String setShortDomain(String environment) {
        String shortDomain = "";
        switch (environment) {
            case "ci":
                shortDomain = "[domain1]"; //changed as private customer info
                break;
            case "amp":
                shortDomain = "[domain2]"; //changed as private customer info
                break;
            case "prod":
                shortDomain = "[domain3]"; //changed as private customer info
                break;
            default:
                LOGGER.info("No such environment");
        }
        return shortDomain;
    }

    public void deleteProject(String apiKey, String projectId) {
        with()
                .header(AUTHORIZATION, apiKey)
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .delete("/projects/" + projectId)
                .then()
                .statusCode(200);
        LOGGER.info("Project with id =" + projectId + " is deleted");
    }
}
