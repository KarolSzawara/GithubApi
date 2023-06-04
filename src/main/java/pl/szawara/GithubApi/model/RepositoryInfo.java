package pl.szawara.GithubApi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RepositoryInfo {
    String repositoryName;
    String ownerLogin;
    List<BranchGit> branches;
}
