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
import java.util.List;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.codehaus.plexus.util.FileUtils;
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

    @Override
    protected List<Executable> getExecutables() {
        unpackModules();
        copyPackageGruntFile();
        return Arrays.asList(createNpmInstallExecutable(), createNpmRebuildExecutable());
    }

    private void unpackModules() {
        String nodeModulesPath = gruntBuildDirectory + File.separator + NODE_MODULES_DIR_NAME;
        File targetModulesPath = new File(nodeModulesPath);
        if (targetModulesPath.exists()) {
            getLog().info("Found existing node_modules at " + nodeModulesPath + " , not going to overwrite them.");
            return;
        }

        if (npmOfflineModulesFilePath == null) {
            npmOfflineModulesFilePath = relativeJsSourceDirectory();
        }

        File offlineModules = new File(basedir() + File.separator + npmOfflineModulesFilePath + File.separator + npmOfflineModulesFile);
        File targetPath = new File(gruntBuildDirectory, "node_modules");

        TarUtil.untar(offlineModules, targetPath, getLog());
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
        String packageJsonPath = basedir() + File.separator + relativeJsSourceDirectory() + File.separator + PACKAGE_JSON_NAME;
        String gruntFilePath = basedir() + File.separator + relativeJsSourceDirectory() + File.separator + GRUNTFILE_NAME;
        
        try{
            File targetPath = new File(gruntBuildDirectory);
            FileUtils.copyFileToDirectory(new File(packageJsonPath), targetPath);
            FileUtils.copyFileToDirectory(new File(gruntFilePath), targetPath);
        }catch(IOException ex){
            getLog().error(ex);
        }
    }
}
