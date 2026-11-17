package com.example.appareil;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.appareil.entity.Categorie;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AppareilApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void categorieCanBeCreatedAndRead() {
        String url = "http://localhost:" + port + "/api/controller/categorie/";
        Categorie category = new Categorie();
        category.setLabel("Eclairage");

        ResponseEntity<Categorie> created = restTemplate.postForEntity(url, category, Categorie.class);
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(created.getBody()).isNotNull();
        assertThat(created.getBody().getId()).isNotNull();

        ResponseEntity<Categorie> found = restTemplate.getForEntity(
                url + "id/" + created.getBody().getId(), Categorie.class);
        assertThat(found.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(found.getBody()).isNotNull();
        assertThat(found.getBody().getLabel()).isEqualTo("Eclairage");
    }

}
