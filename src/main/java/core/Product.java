package core;

import io.restassured.response.ValidatableResponse;
import models.ProductModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import static io.restassured.RestAssured.with;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

public class Product {

    private static final Logger LOGGER = LogManager.getLogger(Product.class.getName());
    private final JtwigTemplate JSON_BODY = JtwigTemplate.classpathTemplate("createProductRequest.json");

    public ProductModel createProduct(String productName, String apiKey, String projectId) {

        JtwigModel model =
                JtwigModel.newModel()
                        .with("productName", productName);
        ValidatableResponse response = with().body(JSON_BODY.render(model))
                .when()
                .header(AUTHORIZATION, apiKey)
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .post("/products?project=" + projectId)
                .then()
                .statusCode(201);
        ProductModel productModel = new ProductModel();
        productModel.setProductId(response.extract().path("id"));
        productModel.setProductName(response.extract().path("name"));

        LOGGER.info("Product '" + productModel.getProductName() + "' with id=" + productModel.getProductId() + " is created");
        return productModel;
    }

    public void deleteProduct(String apiKey, String product) {
        with().header("Authorization", apiKey)
                .header("Content-Type", "application/json")
                .request("DELETE", "/products/" + product)
                .then()
                .statusCode(200);
        LOGGER.info("Product with id =" + product + " is deleted");
    }
}
