/*
 * (C) Copyright 2021 Nuxeo (http://nuxeo.com/).
 * This is unpublished proprietary source code of Nuxeo SA. All rights reserved.
 * Notice of copyright on this source code does not indicate publication.
 *
 * Contributors:
 *     Julien Carsique <jcarsique@nuxeo.com>
 */

void setGitHubBuildStatus(String context) {
    step([
            $class       : 'GitHubCommitStatusSetter',
            reposSource  : [$class: 'ManuallyEnteredRepositorySource', url: 'https://github.com/nuxeo/nuxeo-insight-client'],
            contextSource: [$class: 'ManuallyEnteredCommitContextSource', context: context],
            errorHandlers: [[$class: 'ShallowAnyErrorHandler']]
    ])
}

String getMavenArgs() {
    def args = '-V -B -PJX'
    if (env.TAG_NAME || env.BRANCH_NAME ==~ 'master.*') {
        args += ' deploy -P-nexus'
        if (env.TAG_NAME) {
            args += ' -Prelease -DskipTests'
        }
    } else if (env.BRANCH_NAME ==~ 'sprint-.*') {
        args += ' deploy -Pnexus'
    } else {
        args += ' package'
    }
    return args
}

String getVersion() {
    String version = readMavenPom().getVersion()
    version = env.TAG_NAME ? version : version + "-" + env.BRANCH_NAME.replace('/', '-')
    assert version ==~ /[0-9A-Za-z\-._]*/
    return version
}

pipeline {
    agent {
        label "jenkins-ai-nuxeo11"
    }
    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(daysToKeepStr: '60', numToKeepStr: '60', artifactNumToKeepStr: '5'))
        timeout(time: 1, unit: 'HOURS')
    }
    environment {
        VERSION = "${getVersion()}"
    }
    stages {
        stage('Init') {
            steps {
                container('platform11') {
                    sh """#!/bin/bash -e
jx step git credentials
git config credential.helper store
"""
                }
            }
        }
        stage('Build') {
            environment {
                MAVEN_OPTS = "-Xms512m -Xmx1g"
                MAVEN_ARGS = getMavenArgs()
            }
            steps {
                setGitHubBuildStatus('build')
                container('platform11') {
                    sh "mvn ${MAVEN_ARGS}"
                }
            }
            post {
                always {
                    sh "find . -name '*-reports' -type d"
                    junit allowEmptyResults: true, testResults: '**/target/*-reports/*.xml'
                    archiveArtifacts artifacts: '**/target/*.log, **/log/*.log, **/nxserver/config/distribution.properties, ' +
                            '**/target/*-reports/*, **/target/results/*.html, **/target/*.png, **/target/*.html',
                            allowEmptyArchive: true
                    setGitHubBuildStatus('build')
                }
            }
        }
        stage('Upgrade version stream') {
            when {
                tag '*'
            }
            steps {
                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                    container('platform11') {
                        sh """#!/bin/bash -xe
jx step create pr regex --regex 'version: (.*)' --version $VERSION --files packages/nuxeo-insight-client.yml -r https://github.com/nuxeo/jx-ai-versions
"""
                    }
                }
            }
        }
    }
    post {
        always {
            script {
                if (env.BRANCH_NAME ==~ 'master.*' || env.TAG_NAME || env.BRANCH_NAME ==~ 'sprint.*') {
                    step([$class: 'JiraIssueUpdater', issueSelector: [$class: 'DefaultIssueSelector'], scm: scm])
                }
            }
        }
    }
}
