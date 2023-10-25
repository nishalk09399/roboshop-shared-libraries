#!groovy

def decidePipeline(Map configMap){
    application = configMap.get("application")
    //here we are getting nodeJSVM
    switch(application) {
        case 'nodeJSVM':
            echo "application is node JS VM based"
            nodeJSVMCI(configMap)
            break
        case 'JavaVM':
            echo "application is JAVA VM based"
            javaVMCI(configMap)
            break
        default:
            error "unrecognized application"
            break
    }
    
    
}