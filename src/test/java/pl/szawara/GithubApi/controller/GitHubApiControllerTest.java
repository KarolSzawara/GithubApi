package pl.szawara.GithubApi.controller;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import pl.szawara.GithubApi.model.*;
import pl.szawara.GithubApi.service.GitHubApiService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GitHubApiControllerTest {

    @Mock
    private GitHubApiService gitHubApiService;

    @InjectMocks
    private GitHubApiController gitHubApiController;

    @Test
    public void testGetUserRepositoriesValidUserJsonFormat() {
        // Prepare test data
        String username = "testuser";
        List<RepositoryInfo> repositories = new ArrayList<>();
        repositories.add(new RepositoryInfo());
        // Add some repositories to the list

        // Mock the service method
        Mockito.when(gitHubApiService.getRepositoryInfoList(username)).thenReturn(repositories);

        // Call the controller method
        ResponseEntity<?> responseEntity = gitHubApiController.getUserRepository(username, "application/json");

        // Assert the response
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals(repositories, responseEntity.getBody());
    }

    @Test
    public void testGetUserRepositories_UserNotFound() {
        // Prepare test data
        String username = "nonexistentuser";


        Mockito.when(gitHubApiService.getRepositoryInfoList(username)).thenReturn(Collections.EMPTY_LIST);


        ResponseEntity<?> responseEntity = gitHubApiController.getUserRepository(username, "application/json");


        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        Assertions.assertNotNull(responseEntity.getBody());

    }
    @Test
    public void testGetUserRepositories_ErrorHeader() {
        // Prepare test data
        String username = "user";

        ResponseEntity<?> responseEntity = gitHubApiController.getUserRepository(username, "application/xml");


        Assertions.assertEquals(HttpStatus.NOT_ACCEPTABLE, responseEntity.getStatusCode());
        Assertions.assertNotNull(responseEntity.getBody());

    }
}