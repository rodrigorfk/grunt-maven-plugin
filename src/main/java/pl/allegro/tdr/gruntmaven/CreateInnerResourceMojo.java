/*
 * Copyright 2014 rodrigokuntzer.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.allegro.tdr.gruntmaven;

import java.io.File;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import pl.allegro.tdr.gruntmaven.resources.Resource;

/**
 *
 * @author rodrigokuntzer
 */
@Mojo(name = "create-inner-resource", defaultPhase = LifecyclePhase.VALIDATE)
public class CreateInnerResourceMojo extends BaseMavenGruntMojo{
    
    private static final String INNER_PROPERTIES_RESOURCE_NAME = "grunt-maven.json";
    
    private static final int FILTERED_RESOURCES_JSON_LENGTH = 100;
    
    /**
     * Name of resources that should be filtered by Maven. When using integrated
     * workflow, be sure to make Grunt ignore there resources, as it will overwrite
     * filtered values.
     */
    @Parameter(property = "filteredResources")
    protected String[] filteredResources;
    
    protected void createInnerPropertiesResource() {
        Resource.from("/" + INNER_PROPERTIES_RESOURCE_NAME, getLog())
                .withFilter("filesToWatch", pathToWatchDirectory() + File.separator + "**")
                .withFilter("directoryToWatch", pathToWatchDirectory())
                .withFilter("projectRootPath", basedir())
                .withFilter("targetPath", target())
                .withFilter("sourceDirectory", sourceDirectory)
                .withFilter("jsSourceDirectory", jsSourceDirectory)
                .withFilter("nodeConfigPath", nodeConfigPath)
                .withFilter("filteredFiles", filteredResourcesAsJSONArray())
                .copyAndOverwrite(pathToWorkflowTasksDirectory() + INNER_PROPERTIES_RESOURCE_NAME);
    }
    
    private String filteredResourcesAsJSONArray() {
        StringBuilder builder = new StringBuilder(FILTERED_RESOURCES_JSON_LENGTH);
        builder.append("[");

        builder.append("\"").append("**/").append(npmOfflineModulesFile).append("\"").append(", ");
        int index;
        for (index = 0; index < filteredResources.length; ++index) {
            builder.append("\"").append(filteredResources[index]).append("\"").append(", ");
        }
        builder.delete(builder.length() - 2, builder.length());

        builder.append("]");
        return builder.toString();
    }
    
    private String pathToWatchDirectory() {
        return fullJsSourceDirectory();
    }
    
    protected String pathToWorkflowTasksDirectory() {
        return pathToGruntBuildDirectory() + File.separator;
    }
    
    private String pathToGruntBuildDirectory() {
        return gruntBuildDirectory + File.separator;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        createInnerPropertiesResource();
    }
}
