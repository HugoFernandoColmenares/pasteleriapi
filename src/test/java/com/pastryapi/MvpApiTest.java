package com.pastryapi;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(properties = {
        "app.datasource.url=jdbc:sqlite:file:mvp-api-tests?mode=memory&cache=shared",
        "app.seed-data=true"
})
class MvpApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(1)
    void healthIsUp() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("UP")));
    }

    @Test
    @Order(2)
    void createIngredientRejectsBlankName() throws Exception {
        mockMvc.perform(post("/api/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"","price":-1,"unit":"kg","quantity":-4,"minimumQuantity":0,"status":true}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.path", is("/api/ingredients")));
    }

    @Test
    @Order(3)
    void ingredientCrudWorks() throws Exception {
        MvcResult created = mockMvc.perform(post("/api/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Cocoa powder","brand":"Valrhona","price":12.5,"unit":"kg","quantity":3,"minimumQuantity":0.5,"status":true,"observations":"Dutch processed"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.guid").exists())
                .andExpect(jsonPath("$.lowStock", is(false)))
                .andReturn();

        String id = JsonPath.read(created.getResponse().getContentAsString(), "$.guid");

        mockMvc.perform(get("/api/ingredients/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Cocoa powder")));

        mockMvc.perform(put("/api/ingredients/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Cocoa powder","brand":"Valrhona","price":13,"unit":"kg","quantity":0.4,"minimumQuantity":0.5,"status":true,"observations":"Restock soon"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lowStock", is(true)));

        mockMvc.perform(get("/api/ingredients?lowStock=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", greaterThan(0)));

        mockMvc.perform(delete("/api/ingredients/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/ingredients/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    @Order(4)
    void recipeWithUnknownIngredientReturns404() throws Exception {
        mockMvc.perform(post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Ghost cake","description":"Missing pantry","servings":6,"active":true,"lines":[{"ingredientId":"%s","quantity":1}]}
                                """.formatted(UUID.randomUUID())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    @Order(5)
    void produceSucceedsThenConflictsWhenStockRunsOut() throws Exception {
        MvcResult recipes = mockMvc.perform(get("/api/recipes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].guid").exists())
                .andReturn();

        String recipeId = JsonPath.read(recipes.getResponse().getContentAsString(), "$.content[0].guid");

        mockMvc.perform(post("/api/recipes/" + recipeId + "/produce")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"batches\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.batches", is(1)))
                .andExpect(jsonPath("$.updatedStock").isArray());

        mockMvc.perform(post("/api/recipes/" + recipeId + "/produce")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"batches\":10000}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.shortages").isArray());
    }
}
