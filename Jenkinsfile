pipeline {
    agent any

    options {
        timestamps()
    }
    
    stages {
        stage("Maven setup") {
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
                        sh "mvn test -Dtest=RequirementTests"
                    }
                }
                stage("Verify") {
                    steps {
                        withSonarQubeEnv("sonarcloud") {
                          sh "mvn sonar:sonar -Dsonar.projectKey=luuknieuwdorp_asv-otp -Dsonar.organization=luuknieuwdorp-github"
                        }
                    }
                }

            }
        }
    }
}