// Jenkins pipeline for the API smoke suites (testng-smoke.xml and testng-smoke-1/2/3.xml).
//
// Job setup: New Item -> Pipeline -> Definition "Pipeline script from SCM" -> this repository,
// Script Path "Jenkinsfile".
//
// Jenkins needs:
//   - JDK 21 and Maven under Manage Jenkins -> Tools, with the names used in the tools block below
//     (or delete the tools block if the agent already has java 21 and mvn on its PATH).
//   - Three "Secret text" credentials: smoke-base-url, smoke-api-key, smoke-api-secret.
//     ConfigManager reads them as the BASE_URL / API_KEY / API_SECRET_KEY environment variables,
//     so no .env file is needed on the agent.
//   - The JUnit plugin (installed by default). The HTML Publisher plugin is optional - without it
//     the HTML reports are still downloadable under Build Artifacts.

def runCommand(String command) {
    if (isUnix()) {
        sh command
    } else {
        bat command
    }
}

pipeline {
    agent any

    parameters {
        choice(
            name: 'SMOKE_SUITE',
            choices: ['smoke', 'smoke-1', 'smoke-2', 'smoke-3'],
            description: 'smoke = all three | smoke-1 = deal to insured shipment | '
                    + 'smoke-2 = cancelled reservation | smoke-3 = cancelled purchase'
        )
    }

    options {
        // The suites share one staging account, so two builds must never run at the same time.
        disableConcurrentBuilds()
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '50'))
    }

    tools {
        jdk 'JDK21'
        maven 'Maven3'
    }

    environment {
        // One fixed report folder per suite - each Jenkins build keeps its own copy of it.
        REPORTS_DIR = "target/surefire-reports/${params.SMOKE_SUITE}"
    }

    stages {
        stage('Check credentials') {
            // Fails with a clear message naming every missing credential, instead of the bare
            // "ERROR: <id>" Jenkins prints when a credentials() binding can't find one.
            steps {
                script {
                    def missing = []
                    for (id in ['smoke-base-url', 'smoke-api-key', 'smoke-api-secret']) {
                        try {
                            withCredentials([string(credentialsId: id, variable: 'UNUSED')]) {}
                        } catch (err) {
                            missing << id
                        }
                    }
                    if (missing) {
                        error("Missing Jenkins credentials: ${missing.join(', ')}. Add each one as Kind " +
                                "'Secret text' with exactly that ID under Manage Jenkins -> Credentials -> " +
                                "System -> Global credentials, then run the build again.")
                    }
                }
            }
        }

        stage('Run smoke tests') {
            // Bound here rather than pipeline-wide: a pipeline-level binding that fails exits the
            // agent before the post steps run, and junit then crashes with "hudson.Launcher is missing".
            environment {
                BASE_URL       = credentials('smoke-base-url')
                API_KEY        = credentials('smoke-api-key')
                API_SECRET_KEY = credentials('smoke-api-secret')
            }
            steps {
                // maven.test.failure.ignore keeps the build going after a failed test so the reports
                // always get published; the junit step below then marks the build UNSTABLE.
                // Compilation or setup errors still fail the build.
                runCommand "mvn -B clean test -P${params.SMOKE_SUITE} " +
                        "-Dmaven.test.failure.ignore=true -DsurefireReportsDirectory=${env.REPORTS_DIR}"
            }
        }
    }

    post {
        always {
            // allowEmptyResults: when the build stops before the tests run (e.g. missing credentials),
            // the real error stays the last thing in the log instead of a "no test reports" error.
            junit testResults: "${env.REPORTS_DIR}/TEST-*.xml", allowEmptyResults: true
            archiveArtifacts artifacts: "${env.REPORTS_DIR}/**", allowEmptyArchive: true
            script {
                try {
                    publishHTML(target: [
                        reportName           : "Smoke Report (${params.SMOKE_SUITE})",
                        reportDir            : env.REPORTS_DIR,
                        reportFiles          : 'emailable-report.html,index.html',
                        keepAll              : true,
                        alwaysLinkToLastBuild: true,
                        allowMissing         : true
                    ])
                } catch (NoSuchMethodError ignored) {
                    echo 'HTML Publisher plugin not installed - the HTML reports are under Build Artifacts.'
                }
            }
        }
    }
}
