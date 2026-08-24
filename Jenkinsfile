pipeline {
    agent any

    environment {
        PROJECT_NAME = 'todo_backend'
        DOCKER_IMAGE = 'pto3b/todo_app'
        DOCKER_TAG = "${BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            checkout scm

            echo 'Checkout successful'
            echo 'Testing from GitHub...'
        }

        stage('Test') {
            steps {
                sh '''
                    chmod +x ./mvnw
                    ./mvnw test
                '''
            }
        }
        stage('Build') {
            steps {
                sh '''

                    ./mvnw clean package -DskipTests
                '''
            }
        }

        stage('Docker Build') {
            steps {
                sh '''
                    docker image prune -af

                    docker build \
                    -t ${DOCKER_IMAGE}:${DOCKER_TAG} .

                    docker images
                '''
            }
        }

        // stage('Welcome Stage') {
        //     steps {
        //         sh '''

        //         echo "Hello, Pipeline for "${PROJECT_NAME}" started......"

        //         '''

        //         sh '''

        //         echo "Build Number is "${BUILD_NUMBER}""

    //         '''
    //     }
    // }
    }
}
