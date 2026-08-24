pipeline {
    agent any

    environment {
        PROJECT_NAME = 'todo_backend'
        DOCKER_IMAGE = 'pto3b/todo_app'
        DOCKER_TAG = "${BUILD_NUMBER}"
    }

    stages {
        stage('Welcome Stage') {
            steps {
                echo 'Hello, Pipeline for "${PROJECT_NAME}" started......'
                echo 'Build Number is: "${BUILD_NUMBER}"'
            }
        }
    }
}
