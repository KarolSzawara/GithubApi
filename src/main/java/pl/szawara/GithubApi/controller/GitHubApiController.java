package pl.szawara.GithubApi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.szawara.GithubApi.model.RepositoryInfo;
import pl.szawara.GithubApi.service.GitHubApiService;
import pl.szawara.GithubApi.service.RestMessageCreator;

import java.util.List;

@RestController
@RequestMapping("/repositories")
public class GitHubApiController {
    private final GitHubApiService gitHubApiService;
    public GitHubApiController(GitHubApiService gitHubApiService){
        this.gitHubApiService=gitHubApiService;
    }
    @GetMapping("/{username}")
    public ResponseEntity<?> getUserRepository(@PathVariable String username, @RequestHeader("Accept") String aceptHeader){
        if(!aceptHeader.equals("application/json")){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(RestMessageCreator.createMessage(HttpStatus.NOT_ACCEPTABLE,"Unsupported media type"));
        }
        List<RepositoryInfo> repositoryList=gitHubApiService.getRepositoryInfoList(username);
        if(!repositoryList.isEmpty()){
            return ResponseEntity.ok(repositoryList);
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(RestMessageCreator.createMessage(HttpStatus.NOT_FOUND,"GitHub user not found"));
        }
    }
}
