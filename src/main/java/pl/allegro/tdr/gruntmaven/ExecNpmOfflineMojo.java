/*
 * Copyright 2014 original author or authors.
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

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;
import pl.allegro.tdr.gruntmaven.archive.TarGzUtil;
import pl.allegro.tdr.gruntmaven.archive.TarUtil;
import pl.allegro.tdr.gruntmaven.executable.Executable;

/**
 * Run NPM rebuild.
 *
 * @author Adam Dubiel
 */
@Mojo(name = "npm-offline", defaultPhase = LifecyclePhase.TEST)
public class ExecNpmOfflineMojo extends ExecNpmMojo {

    private static final String NODE_MODULES_DIR_NAME = "node_modules";
    private static final String PACKAGE_JSON_NAME = "package.json";
    private static final String GRUNTFILE_NAME = "Gruntfile.js";

    private static final String NPM_REBUILD_COMMAND = "rebuild";
    
    @Parameter(property = "npmRebuild", defaultValue = "true")
    protected boolean npmRebuild = true;

    @Override
    protected List<Executable> getExecutables() {
        boolean unpackModules = unpackModules();
        copyPackageGruntFile();
        if(unpackModules && npmRebuild){
            return Arrays.asList(createNpmRebuildExecutable());
        }
        return Collections.EMPTY_LIST;
    }

    private boolean unpackModules() {
        String nodeModulesPath = gruntBuildDirectory + File.separator + NODE_MODULES_DIR_NAME;
        File targetModulesPath = new File(nodeModulesPath);
        if (targetModulesPath.exists()) {
            getLog().info("Found existing node_modules at " + nodeModulesPath + " , not going to overwrite them.");
            return false;
        }

        if (npmOfflineModulesFilePath == null) {
            npmOfflineModulesFilePath = basedir() + File.separator + relativeJsSourceDirectory();
        }

        File offlineModules = new File(npmOfflineModulesFilePath + File.separator + npmOfflineModulesFile);
        File targetPath = new File(gruntBuildDirectory);
        
        getLog().info("Node modules required, now proceeding to offline unpack the file "+offlineModules.getAbsolutePath());
        
        final String tarFileName = offlineModules.getName().toLowerCase();
        if(tarFileName.endsWith(".tar.gz")){
            TarGzUtil.untar(offlineModules, targetPath, getLog());
            return true;
        }else if(tarFileName.endsWith(".tar")){
            TarUtil.untar(offlineModules, targetPath, getLog());
            return true;
        }else{
            throw new RuntimeException("invalid extension for offline modules : "+tarFileName);
        }
    }

    private Executable createNpmInstallExecutable() {
        Executable executable = new Executable(npmExecutable);
        executable.addArgument(NPM_INSTALL_COMMAND);
        executable.addArgument("--ignore-scripts");
        appendNoColorsArgument(executable);

        executable.addEnvironmentVars(npmEnvironmentVar);

        return executable;
    }

    private Executable createNpmRebuildExecutable() {
        Executable executable = new Executable(npmExecutable);
        executable.addArgument(NPM_REBUILD_COMMAND);
        appendNoColorsArgument(executable);

        return executable;
    }

    private void copyPackageGruntFile() {
        String packageJsonPath = basedir() + File.separator + nodeConfigPath + File.separator + PACKAGE_JSON_NAME;
        String gruntFilePath = basedir() + File.separator + nodeConfigPath + File.separator + GRUNTFILE_NAME;
        
        try{
            File targetPath = new File(gruntBuildDirectory);
            final File packgeJsonFile = new File(packageJsonPath);
            if(packgeJsonFile.exists()){
                FileUtils.copyFileToDirectory(packgeJsonFile, targetPath);
            }
            final File gruntFileFile = new File(gruntFilePath);
            if(gruntFileFile.exists()){
                FileUtils.copyFileToDirectory(gruntFileFile, targetPath);
            }
        }catch(IOException ex){
            getLog().error(ex);
        }
    }
}
