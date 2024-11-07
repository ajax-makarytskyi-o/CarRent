plugins {
    id("io.github.surpsg.delta-coverage")
}

configure<io.github.surpsg.deltacoverage.gradle.DeltaCoverageConfiguration> {
    val targetBranch = project.properties["diffBase"]?.toString() ?: "refs/remotes/origin/main"
    diffSource.byGit {
        compareWith(targetBranch)
    }

    violationRules.failIfCoverageLessThan(0.6)
    reports {
        html.set(true)
    }
}
