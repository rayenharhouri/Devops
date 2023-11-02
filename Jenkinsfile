pipeline {
    agent any

    environment {
        // Define environment variables to store credentials and repo info
        GIT_CREDENTIALS = credentials('a3c80f6a-a0b6-4d74-a75a-1b2d940dee69')
        GIT_REPO_URL = 'github.com/rayenharhouri/Devops.git'
        GIT_BRANCH = 'master'
        REPO_DIR = 'Devops'
        // Sonar credential
        SONAR_CREDENTIALS = credentials('7d6e4135-6edc-4db3-af32-2062eb456dd9')
        //SONAR_HOST_URL = 'http://192.168.122.133:9000'
        registry = "rinodev/devops"
        registryCredential = '22f4111c-e2f8-4a38-9ad7-852a47fa8048'
        dockerImage = ''
    }
    stages {
        stage('Clone my Branch from Git') {
            steps {
                script {
                    if (fileExists(REPO_DIR)) {
                        dir(REPO_DIR) {
                            sh 'git pull'
                        }
                    } else {
                        sh "git clone --branch ${env.GIT_BRANCH} https://rayenharhouri:${env.GIT_CREDENTIALS}@${env.GIT_REPO_URL} ${REPO_DIR}"
                    }
                }
            }
        }
        stage('Clean and compile with Maven') {
            steps {
                dir(REPO_DIR) {
                    sh 'mvn clean compile'
                }
            }
        }
        stage('JUnit Tests') {
                    steps {
                        dir(REPO_DIR) {
                            sh 'mvn test'
                        }
                    }
                }
        stage('SonarQube Analysis') {
            steps {
                dir(REPO_DIR) {
                    withCredentials([usernamePassword(credentialsId: '7d6e4135-6edc-4db3-af32-2062eb456dd9', usernameVariable: 'SONAR_USER', passwordVariable: 'SONAR_PASSWORD')]) {
                        sh "mvn clean verify sonar:sonar -Dsonar.login=\$SONAR_USER -Dsonar.password=\$SONAR_PASSWORD "
                    }
                }
            }
        }

        stage('Nexus Deployment') {
            steps {
                dir(REPO_DIR) {
                    sh "mvn deploy -DskipTests=true "
                }
            }
        }
stage('Building  image') {

            steps {
dir(REPO_DIR){
                script {

                    dockerImage = docker.build registry + ":$BUILD_NUMBER"

                }
}

            }

        }

        stage('Deploy  image') {

            steps {
                dir(REPO_DIR){

                script {

                    docker.withRegistry( '', registryCredential ) {

                        dockerImage.push()

                    }

                }
                }

            }
        }
        stage('Docker Compose Up') {
            steps {
                script {
                    dir(REPO_DIR) {
                        sh "docker compose up -d"
                    }
                }
            }
        }
    }
}