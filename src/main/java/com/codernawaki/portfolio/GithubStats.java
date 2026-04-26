package com.codernawaki.portfolio;

import java.io.Serializable;

public record GithubStats(int stars, String lastCommitDate, String repoName) implements Serializable {
    private static final long serialVersionUID = 1L;
}
