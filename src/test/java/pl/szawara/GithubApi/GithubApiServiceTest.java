package pl.szawara.GithubApi;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import pl.szawara.GithubApi.model.*;
import pl.szawara.GithubApi.service.GitHubApiService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class GithubApiServiceTest {
	@Mock
	private RestTemplateBuilder restTemplateBuilder;
	@Mock
	private RestTemplate restTemplate;
	@InjectMocks
	private GitHubApiService gitHubApiService;


	@BeforeEach
	public void setUp() {
		when(restTemplateBuilder.build()).thenReturn(restTemplate);
		gitHubApiService = new GitHubApiService(restTemplateBuilder);
	}
	@Test
	void testGetServiceList() {
		String userName="user1";
		RepositoryGit repo1=new RepositoryGit();
		repo1.setName("repo1");
		repo1.setOwner(new Owner("user1"));
		repo1.setFork(false);
		RepositoryGit repo2=new RepositoryGit();
		repo2.setName("repo2");
		repo2.setOwner(new Owner("user2"));
		repo2.setFork(true);
		RepositoryGit repo3=new RepositoryGit();
		repo3.setName("repo3");
		repo3.setOwner(new Owner("user3"));
		repo3.setFork(false);

		BranchGit branchGit1=new BranchGit();
		branchGit1.setName("branch1");
		branchGit1.setCommit(new CommitGit("sh1"));

		BranchGit branchGit2=new BranchGit();
		branchGit2.setName("branch2");
		branchGit2.setCommit(new CommitGit("sh2"));
		HttpHeaders httpHeaders=new HttpHeaders();
		httpHeaders.set("Accept","application/json");
		RepositoryGit[] repositoryGits={repo1,repo2,repo3};
		ResponseEntity<RepositoryGit[]> responseEntity=ResponseEntity.ok(repositoryGits);
		Mockito.when(restTemplate.exchange(
				eq("https://api.github.com/users/user1/repos"), // Here's the change
				eq(HttpMethod.GET),
				any(HttpEntity.class),
				eq(RepositoryGit[].class)
		)).thenReturn(responseEntity);

		BranchGit[] branchesGit={branchGit1,branchGit2};
		ResponseEntity<BranchGit[]> responseBranch=ResponseEntity.ok(branchesGit);
		mockBranches("repo1",responseBranch);
		mockBranches("repo3",responseBranch);

		List<RepositoryInfo> result=gitHubApiService.getRepositoryInfoList(userName);


		Assertions.assertEquals(2,result.size());
		Assertions.assertEquals("repo1",result.get(0).getRepositoryName());
		Assertions.assertArrayEquals(branchesGit,result.get(0).getBranches().toArray());
		Assertions.assertEquals("sh1",result.get(0).getBranches().get(0).getCommit().getSha());

		Assertions.assertEquals("repo3",result.get(1).getRepositoryName());
		Assertions.assertArrayEquals(branchesGit,result.get(1).getBranches().toArray());
		Assertions.assertEquals("sh1",result.get(1).getBranches().get(0).getCommit().getSha());

	}
	private void mockBranches(String repoName,ResponseEntity<BranchGit[]> responseBranch){
		Mockito.when(restTemplate.exchange(eq("https://api.github.com/repos/user1/"+repoName+"/branches"),
				eq(HttpMethod.GET),
				any(HttpEntity.class),
				eq(BranchGit[].class))).thenReturn(responseBranch);
	}

}
