package com.yahoo.parsec.parsec_templates.tasks

import com.yahoo.parsec.parsec_templates.ParsecTemplatesExtension
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException
import templates.ProjectTemplate
/**
 * @author waynewu
 */


class CreateParsecProjectTask extends AbstractProjectTask {

    String projectGroup
    String projectName
    String projectVersion
    String projectPath
    String projectGroupPath
    ParsecTemplatesExtension pluginExtension

    public CreateParsecProjectTask(){
        super('createParsecProject', "create the folder structure designed for a Parsec project")
        pluginExtension = getProject().getExtensions().findByType(ParsecTemplatesExtension.class)
        if(pluginExtension == null){
            getLogger().info("Project did not create an extension. A new one is created.")
            pluginExtension = new ParsecTemplatesExtension();
        }
    }

    @TaskAction
    void create() throws TaskExecutionException{

        projectGroup = groupName()
        projectName = projectName()
        projectVersion = projectVersion()

        projectGroupPath = projectGroup.replace('.', '/')

        projectPath = projectPath(projectName)

        generateTemplate()
        generateExtra()
        generateGradleFiles()

        if(pluginExtension.createSampleRDL){
            generateSampleRdl()
        }
        if(pluginExtension.createTravisCI){
            generateTravisCI()
        }
    }

    /**
     * Generate the template (folder structure and files)
     * Client can overload this method to create their own base template
     */
    protected void generateTemplate(){
        ProjectTemplate.fromRoot(projectPath) {

            'src' {
                'main' {
                    'java' {
                        "${projectGroupPath}" {}
                    }
                    'rdl' {}
                    'webapp' {}
                    'resources' {}
                }
                'test' {
                    'java' {
                        "${projectGroupPath}" {}
                    }
                    'resources'{}
                }
            }
            'config' {
                'checkstyle' {
                    'checkstyle-suppressions.xml' getText('/templates/checkstyle-suppressions.xml')
                }
                'pmd' {}
                'findbugs' {
                    'excludeFilter.xml' getText('/templates/excludeFilter.xml')
                }
            }
            'README.md' template: '/templates/README.md', projectName: projectName
            'README.sh' getText('/templates/README.sh')
            //'pom.xml' getText('/templates/pom.xml') //TODO: Use built in gradle function to generate pom.xml
        }
    }

    /**
     * Generate gradle files as a gradle_init would
     */
    protected void generateGradleFiles(){
        if(pluginExtension.createWrapper){
            gradle_wrapper()
        }
        ProjectTemplate.fromRoot(projectPath){
            'build.gradle' template: '/templates/build.gradle', applyFromPath: pluginExtension.applyFromPath, projectGroup: projectGroup
            'settings.gradle' template: '/templates/settings.gradle', projectName: projectName
            'gradle.properties' template: '/templates/gradle.properties', projectVersion: projectVersion
        }
    }


    /**
     * Generate extra folder structures and files defined by Client in configurations
     */
    protected void generateExtra(){
        ProjectTemplate.fromRoot(projectPath, pluginExtension.extraTemplate)
    }

    /**
     * Generate sample rdl file
     */
    protected void generateSampleRdl(){
        ProjectTemplate.fromRoot(projectPath){
            'src/main/rdl'{
                'sample.rdl' template: '/templates/sample.rdl', groupName: projectGroup
            }
        }
    }

    /**
     * Generate .travis.yml for CI
     */
    protected void generateTravisCI(){
        ProjectTemplate.fromRoot(projectPath){
            //must use getText here because normal String is needed (not GString)
            '.travis.yml' getText('/templates/.travis.yml')
        }
    }

    /**
     * Run gradle wrapper in the project folder to generate necessary wrapper files
     * NOTE: This might not be the best approach in doing this
     */
    protected void gradle_wrapper(){
        getLogger().debug("gradle -p ${projectPath} wrapper".execute().text)
    }



}
