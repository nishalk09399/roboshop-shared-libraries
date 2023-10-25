//this pipeline is use to run the roboshop catalogue component this is the CI process 
def call(Map configMap){

    //mapName.get("key-name")
    def component =configMap.get("component")
    echo "component is: $component"
    pipeline {
        agent { node { label 'AGENT-1' } }
        environment{

            packageVersion = ''
        
        }

        stages {
            stage('GET version') {
                steps {
                    script{
                        def packageJson = readJSON(file: 'package.json')
                        def packageVersion = packageJson.version
                        echo "version: ${packageVersion}"
                    }
                }
            }
            stage('Install dependencies') {
                steps {
                    sh 'ls -ltr'
                    sh 'pwd'
                    sh 'npm install'
                    echo "npm installed successfully done the process"
                }
            }
            stage('Unit test') {
                steps {
                    echo "unit testing is done here"
                }
            }
            //sonar-scanner command expect sonar-project.properties should be available to that solution 
            // stage('Sonar Scan') {
            //     steps {
            //         sh 'ls -ltr'
            //         sh 'sonar-scanner'
            //     }
            // }

            stage('Build') {
                steps {
                    sh 'ls -ltr'
                    sh "zip -r ${component}.zip ./* --exclude=.git --exclude=.zip"
                }
            }


            stage('SAST') {
                steps {
                    echo "SAST Done"
                }
            }

            // install the pipeline utility plugin in jenkins
            stage('publish artifact'){
                steps {
                    nexusArtifactUploader(
                        nexusVersion: 'nexus3',
                        protocol: 'http',
                        nexusUrl: '172.31.32.49:8081/',
                        groupId: 'com.roboshop',
                        version: '$packageVersion',
                        repository: "${component}",
                        credentialsId: 'nexus-auth',
                        artifacts: [
                            [artifactId: "${component}",
                            classifier: '',
                            file: "${component}.zip",
                            type: 'zip']
                        ]
                    )  
                }
            }  

            // here we are calling the CD Job the CD is downstrem job and CI is upstream job here 
            stage('Deploy') {
                steps {
                    echo "Deployment"

                    build job: "jenk-robo-catalogue-deploy/", wait: true
                }
            }
            
        }


        post { 
            always { 
                echo 'cleaning the workspace'
                deleteDir()
            }
        }
    }
    }
