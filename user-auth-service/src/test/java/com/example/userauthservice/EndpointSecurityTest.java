package com.example.userauthservice;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:endpoints;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver", "spring.datasource.username=sa",
        "spring.datasource.password=", "spring.jpa.hibernate.ddl-auto=create-drop",
        "app.default-email=admin@endpoints.test", "app.default-password=AdminTest123!",
        "app.jwt.expiration-ms=900000", "app.signup-role-name=USER"})
@AutoConfigureMockMvc
class EndpointSecurityTest {
    @Autowired MockMvc mvc;

    @Test
    void allEndpointsAndAuthorizationWorkWithoutOpenSessionInView() throws Exception {
        String admin = token(login("admin@endpoints.test", "AdminTest123!"), "accessToken");
        String signup = "{\"email\":\"alice@endpoints.test\",\"mobile\":\"+14155552671\",\"password\":\"StrongPass123!\",\"signUpUsing\":\"EMAIL\"}";
        String created = ok(post("/api/v1/users/signup").contentType("application/json").content(signup));
        Number id = JsonPath.read(created, "$.data.id");
        String login = login("alice@endpoints.test", "StrongPass123!");
        assertThat((java.util.List<String>) JsonPath.<java.util.List<String>>read(login, "$.data.roles")).containsExactly("USER");
        String user = token(login, "accessToken");
        mvc.perform(get("/api-docs")).andExpect(status().isOk());
        mvc.perform(get("/api/v1/users")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/users").header("Authorization", "Bearer invalid.jwt.token")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/users").header("Authorization", "Bearer " + token(login, "refreshToken"))).andExpect(status().isUnauthorized());
        for (String path : new String[]{"/api/v1/roles", "/api/v1/admin/users"}) {
            mvc.perform(get(path).header("Authorization", "Bearer " + user)).andExpect(status().isForbidden());
            ok(get(path).header("Authorization", "Bearer " + admin));
        }
        for (String suffix : new String[]{id + "/ID", "alice/USERNAME", "alice@endpoints.test/EMAIL", "+14155552671/MOBILE"}) {
            ok(get("/api/v1/users/" + suffix).header("Authorization", "Bearer " + user));
        }
        ok(get("/api/v1/users?searchTerm=alice&searchType=USERNAME&active=true").header("Authorization", "Bearer " + admin));
        mvc.perform(post("/api/v1/users/signup").contentType("application/json").content(signup)).andExpect(status().isConflict());
        mvc.perform(post("/api/v1/users/login").contentType("application/json").content("{}")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/v1/users/999999/ID").header("Authorization", "Bearer " + admin)).andExpect(status().isNotFound());
        mvc.perform(get("/api/v1/users?pageSize=0").header("Authorization", "Bearer " + admin)).andExpect(status().isBadRequest());
        mvc.perform(put("/api/v1/users/update/1").header("Authorization", "Bearer " + user).contentType("application/json").content("{\"username\":\"hacked\"}")).andExpect(status().isForbidden());
        ok(put("/api/v1/users/update/" + id).header("Authorization", "Bearer " + user).contentType("application/json").content("{\"username\":\"aliceupdated\"}"));
        user = token(login("alice@endpoints.test", "StrongPass123!"), "accessToken");
        ok(put("/api/v1/users/update-password/" + id).header("Authorization", "Bearer " + user).contentType("application/json").content("{\"password\":\"ChangedPass123!\"}"));
        user = token(login("alice@endpoints.test", "ChangedPass123!"), "accessToken");
        String role = ok(post("/api/v1/roles/create").header("Authorization", "Bearer " + admin).contentType("application/json").content("{\"name\":\" reader \" ,\"description\":\"test\",\"defaultRole\":false}"));
        Number roleId = JsonPath.read(role, "$.data.id");
        assertThat(JsonPath.<String>read(role, "$.data.name")).isEqualTo("READER");
        ok(get("/api/v1/roles/" + roleId).header("Authorization", "Bearer " + admin));
        ok(put("/api/v1/roles/update/" + roleId).header("Authorization", "Bearer " + admin).contentType("application/json").content("{\"name\":\"READER\",\"description\":\"updated\",\"defaultRole\":false}"));
        mvc.perform(post("/api/v1/roles/create").header("Authorization", "Bearer " + admin).contentType("application/json").content("{\"name\":\"reader\",\"description\":\"duplicate\"}")).andExpect(status().isConflict());
        mvc.perform(post("/api/v1/roles/create").header("Authorization", "Bearer " + admin).contentType("application/json").content("{}")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/v1/roles/admin").header("Authorization", "Bearer " + admin)).andExpect(status().isBadRequest());
        ok(delete("/api/v1/roles/delete/" + roleId).header("Authorization", "Bearer " + admin));
        mvc.perform(get("/api/v1/roles/" + roleId).header("Authorization", "Bearer " + admin)).andExpect(status().isNotFound());
        ok(delete("/api/v1/users/" + id).header("Authorization", "Bearer " + user));
        mvc.perform(get("/api/v1/users").header("Authorization", "Bearer " + user)).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/v1/users/login").contentType("application/json").content(loginBody("alice@endpoints.test", "ChangedPass123!"))).andExpect(status().isUnauthorized());
        ok(get("/api/v1/admin/users?searchTerm=aliceupdated&searchType=USERNAME&active=false&deleted=true").header("Authorization", "Bearer " + admin));
    }

    private String ok(MockHttpServletRequestBuilder request) throws Exception {
        MvcResult result = mvc.perform(request).andExpect(status().isOk()).andReturn();
        return result.getResponse().getContentAsString();
    }
    private String login(String email, String password) throws Exception {
        return ok(post("/api/v1/users/login").contentType("application/json").content(loginBody(email, password)));
    }
    private String loginBody(String email, String password) {
        return "{\"email\":\"" + email + "\",\"password\":\"" + password + "\",\"signUpUsing\":\"EMAIL\"}";
    }
    private String token(String response, String field) { return JsonPath.read(response, "$.data." + field); }
}
