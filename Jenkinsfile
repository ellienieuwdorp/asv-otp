pipeline {
    agent { label 'tests' }

    options {
        timestamps()
    }

    post {
        always {
            junit 'modules/**/target/surefire-reports/**.xml'
        }
    }

    stages {
        stage("Run with JDK 8 and maven") {
            agent {
                docker {
                    image 'maven:3.6.0-jdk-8'
                    args '-v maven-repo:/root/.m2 -v sonar-repo:/root/.sonar'
                    reuseNode true
                }
            }
            stages {
                stage("Build") {
                    steps {
                        sh "mvn clean package -DskipTests"
                    }
                }
                stage("Test") {
                    steps {
                        sh "mvn test"
                    }
                }
                stage("Verify") {
                    steps {
                        withSonarQubeEnv("sonarcloud") {
                          sh "mvn sonar:sonar -Dsonar.projectKey=asv-otp -Dsonar.organization=luuknieuwdorp-github -Dsonar.host.url=https://sonarcloud.io -Dsonar.login=76d18d8c98ebb2a91b7993d962b09a25fa31d531"
                          // sh "mvn sonar:sonar -Dsonar.projectKey=Samper1022_asv-swagger-codegen -Dsonar.organization=samper1022-github"
                        }
                        // timeout(time: 30, unit: "MINUTES") {
                        //     waitForQualityGate abortPipeline: true
                        // }
                    }
                }

            }
        }
    }
}