package pl.szawara.GithubApi;

import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Headers;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import pl.szawara.GithubApi.model.*;
import pl.szawara.GithubApi.service.GitHubApiService;
import pl.szawara.GithubApi.service.RestMessageCreator;

import java.util.List;

import static org.mockito.Mockito.mock;


@RunWith(MockitoJUnitRunner.class)
class GithubApiApplicationTests {
	@Mock
	private RestTemplate restTemplate=mock(RestTemplate.class);
	@InjectMocks
	@Spy

	private GitHubApiService gitHubApiService=new GitHubApiService(new RestTemplateBuilder());


	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);
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
				("https://api.github.com/users/user1/repos"),
				HttpMethod.GET,
				new HttpEntity<Void>(httpHeaders),
				RepositoryGit[].class
		)).thenReturn(responseEntity);

		BranchGit[] branchesGit={branchGit1,branchGit2};
		ResponseEntity<BranchGit[]> responseBranch=ResponseEntity.ok(branchesGit);
		mockBranches("repo1",responseBranch);
		mockBranches("repo1",responseBranch);

		List<RepositoryInfo> result=gitHubApiService.getRepositoryInfoList(userName);


		Assertions.assertEquals(2,result.size());
		Assertions.assertEquals("repo1",result.get(0).getRepositoryName());
		Assertions.assertEquals(branchesGit,result.get(0).getBranches().toArray());
		Assertions.assertEquals("sh1",result.get(0).getBranches().get(0).getCommit().getSha());

		Assertions.assertEquals("repo3",result.get(1).getRepositoryName());
		Assertions.assertEquals(branchesGit,result.get(1).getBranches().toArray());
		Assertions.assertEquals("sh1",result.get(1).getBranches().get(0).getCommit().getSha());

	}
	private void mockBranches(String repoName,ResponseEntity<BranchGit[]> responseBranch){
		Mockito.when(restTemplate.exchange("https://api.github.com/repos/user1/"+repoName+"/branches",
				HttpMethod.GET,
				new HttpEntity<>(new HttpHeaders()),
				BranchGit[].class)).thenReturn(responseBranch);
	}

}
