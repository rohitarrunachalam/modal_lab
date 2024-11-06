pipeline {
    agent any
    
    environment {
        DOCKER_REGISTRY = 'your-docker-registry' // e.g., 'docker.io/yourusername'
        DOCKER_CREDENTIALS = 'docker-cred-id' // Jenkins credentials ID for Docker registry
        GIT_BRANCH = "${env.BRANCH_NAME}"
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build and Test') {
            agent {
                docker {
                    image 'maven:3.9-eclipse-temurin-17'
                    args '-v $HOME/.m2:/root/.m2'
                }
            }
            steps {
                script {
                    // Determine the profile based on branch
                    def mavenProfile = ''
                    if (env.GIT_BRANCH == 'main') {
                        mavenProfile = 'production'
                    } else if (env.GIT_BRANCH == 'staging') {
                        mavenProfile = 'staging'
                    }
                    
                    // Build and test with appropriate profile
                    sh "mvn clean package -P${mavenProfile}"
                }
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Build Docker Image') {
            steps {
                script {
                    def dockerfile = 'Dockerfile'
                    def tag = ''
                    
                    if (env.GIT_BRANCH == 'main') {
                        tag = 'latest'
                    } else if (env.GIT_BRANCH == 'staging') {
                        tag = 'staging'
                    }
                    
                    docker.withRegistry("https://${DOCKER_REGISTRY}", DOCKER_CREDENTIALS) {
                        def customImage = docker.build("${DOCKER_REGISTRY}/myapp:${tag}")
                        customImage.push()
                    }
                }
            }
        }
        
        stage('Deploy') {
            steps {
                script {
                    if (env.GIT_BRANCH == 'main') {
                        // Deploy to production
                        sh """
                            docker stop myapp-prod || true
                            docker rm myapp-prod || true
                            docker run -d --name myapp-prod -p 8082:8081 ${DOCKER_REGISTRY}/myapp:latest
                        """
                    } else if (env.GIT_BRANCH == 'staging') {
                        // Deploy to staging
                        sh """
                            docker stop myapp-staging || true
                            docker rm myapp-staging || true
                            docker run -d --name myapp-staging -p 8081:8081 ${DOCKER_REGISTRY}/myapp:staging
                        """
                    }
                }
            }
        }
        
        stage('Verification Tests') {
            steps {
                script {
                    def testPort = env.GIT_BRANCH == 'main' ? '8082' : '8081'
                    // Wait for application to start
                    sh "sleep 10"
                    
                    // Simple health check
                    sh "curl -f http://localhost:${testPort} || exit 1"
                }
            }
        }
    }
    
    post {
        failure {
            echo 'Pipeline failed! Sending notifications...'
            // Add notification logic here (email, Slack, etc.)
        }
        success {
            echo 'Pipeline succeeded! Application deployed successfully.'
        }
        always {
            cleanWs()
        }
    }
}