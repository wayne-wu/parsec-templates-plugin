#Parsec Template
[![Build Status](https://travis-ci.org/wayne-wu/parsec-template-plugin.svg?branch=master)](https://travis-ci.org/wayne-wu/parsec-template-plugin) [![Coverage Status](https://coveralls.io/repos/github/wayne-wu/parsec-template-plugin/badge.svg?branch=master)](https://coveralls.io/github/wayne-wu/parsec-template-plugin?branch=master)

Parsec Template Plugin is a gradle plugin extended from townsfold's [gradle templates plugin](https://github.com/townsfolk/gradle-templates).
It provides similar functionality as Maven's archetype, which generates files and directories based on a template. In parsec templates plugin, a custom template is provided which is tailored
for Parsec. See the default output for a sample of what the folder structures will look like.

##Installation:

In order to run the parsec command directly in a directory without build.gradle. You must set up gradle's init script.


`$ vim ~/.gradle/init.gradle `, then enter the following code:

```
gradle.beforeProject { prj ->
   prj.apply from: 'https://raw.githubusercontent.com/wayne-wu/parsec-template-plugin/master/installation/apply.groovy'
}

```

##Usage:

To create the folder structure, type in

`$ gradle createParsecProject -PgroupId='your.group.name' -PartifactId='your_project_name'`

If you do not specify the groupId or artifactId, you will be prompted to do so.

##Configuration:

On top of the base structure that the plugin already provides, you can also add in additional items for the plugin to generate.

Below is a sample of how you can configure the plugin (eg. inside build.gradle):

```
parsecTemplate {
    extraTemplate = {
        'src/main' {
                'sample' {
                    'test.txt' template: './test.txt'
                }
            }
        }
    }
}
```

Once you run `gradle createParsecProject` , the directory "sample" will be made under /src/main as well as the file "test.txt" based on the given template.
You can put the template of the file anywhere as long as you specify the location (relative to your build script).

The default `build.gradle` file will apply from Parsec Base Build, which provides all the necessary settings for you to
get started. In cases when you would like the build.gradle to inherit from a different file. You can set:

```
parsecTemplate {
    applyFromPath = 'PATH/yourfile.gradle'
}
```

Below are the properties available for the plugin:

| Property      | Type  | Default | Description |
|:-------------:|:-----:|:-------:|:-----------:|
| extraTemplate  | Closure | {}       | Extra folders and files to be generated by the plugin |
| applyFromPath  | String  | https://dl.bintray.com/wayne-wu/Gradle/parsec-base-build/1.0.1/parsec.gradle |The file path that the generated build.gradle should apply from|
| createSampleRDL| boolean | false    | Generates a sample.rdl file that serves as an example |
| createTravisCI | boolean | true     | Generates .travis.yml for Travis CI. Disable this if you would like to use an alternative CI tool |
| createWrapper  | boolean | true     | Creates gradle wrapper in the project folder |

##Default Output:

If no extraTemplate is provided, below is the output that you should be getting:
```
── your_project_name
    ├── README.md
    ├── README.sh
    ├── build.gradle
    ├── config
    │   ├── checkstyle
    │   │   └── checkstyle-suppressions.xml
    │   ├── findbugs
    │   │   └── excludeFilter.xml
    │   └── pmd
    ├── gradle
    │   └── wrapper
    │       ├── gradle-wrapper.jar
    │       └── gradle-wrapper.properties
    ├── gradle.properties
    ├── gradlew
    ├── gradlew.bat
    ├── settings.gradle
    └── src
        ├── main
        │   ├── java
        │   │   └── your
        │   │       └── group
        │   │           └── name
        │   ├── rdl
        │   ├── resources
        │   └── webapp
        └── test
            ├── java
            │   └── your
            │       └── group
            │           └── name
            └── resources
```
