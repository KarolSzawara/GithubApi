package pl.szawara.GithubApi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.szawara.GithubApi.model.BranchGit;
import pl.szawara.GithubApi.model.RepositoryGit;
import pl.szawara.GithubApi.model.RepositoryInfo;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GitHubApiService {
    private static final String GITHUB_API_URL = "https://api.github.com";

    private RestTemplate restTemplate;

    public GitHubApiService(RestTemplateBuilder restTemplateBuilder){
        this.restTemplate=restTemplateBuilder.build();
    }
    private List<RepositoryGit> getUserRepositories(String userName){
        String url = GITHUB_API_URL+"/users/"+userName+"/repos";

        HttpEntity<Void> httpEntity=createEntity(new String[]{"Accept"}, new String[]{"application/json"});

        ResponseEntity<RepositoryGit[]> response=restTemplate.exchange(url, HttpMethod.GET,httpEntity,RepositoryGit[].class);
        RepositoryGit[] repo =response.getBody();
        if (repo!=null){
            return Arrays.stream(repo)
                    .filter(repositoryGit -> !repositoryGit.getFork())
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
    private HttpEntity<Void> createEntity(String[] keys,String[] values){
        if(keys.length!=values.length){
            return null;
        }
        HttpHeaders headers=new HttpHeaders();
        for(int i=0 ; i<keys.length;i++){
            headers.set(keys[i],values[i]);
        }
        return  new HttpEntity<>(headers);
    }
    private List<BranchGit> getUserBranches(String user,String repoName){
        String url = GITHUB_API_URL+"/repos/"+user+"/"+repoName+"/branches";
        HttpEntity<Void> httpEntity=createEntity(new String[]{"Accept"}, new String[]{"application/json"});
        ResponseEntity<BranchGit[]> response=restTemplate.exchange(url, HttpMethod.GET,httpEntity,BranchGit[].class);
        BranchGit[] branches=response.getBody();
        if(branches!=null){
            return Arrays.asList(branches);
        }
        return Collections.emptyList();
    }
    public List<RepositoryInfo> getRepositoryInfoList(String userName){
        List<RepositoryGit> repositoryGitList=getUserRepositories(userName);
        if(repositoryGitList.isEmpty()){
            return Collections.emptyList();
        }
        List<RepositoryInfo> resRepoInfoList= new ArrayList<>();
        for(RepositoryGit it:repositoryGitList){
            List<BranchGit> branch=getUserBranches(userName,it.getName());
            resRepoInfoList.add(new RepositoryInfo(it.getName(),it.getOwner().getLogin(),branch));
        }
        return resRepoInfoList;
    }

}
