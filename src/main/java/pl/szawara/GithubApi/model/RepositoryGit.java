package pl.szawara.GithubApi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class RepositoryGit {
    String name;
    Owner owner;
    Boolean fork;
}
