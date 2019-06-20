 pipeline {
    agent { label 'dev-2.x' }

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
                          sh "mvn sonar:sonar -Dsonar.projectKey=asv-otp-orig -Dsonar.organization=luuknieuwdorp-github -Dsonar.host.url=https://sonarcloud.io -Dsonar.login=486640787d37093ab5f887d0bba209ada0a98f26"
                        }
                    }
                }

            }
        }
    }
}