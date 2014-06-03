/*
 * Copyright 2014 Adam Dubiel.
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
package pl.allegro.tdr.gruntmaven.archive;

import java.io.*;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.archiver.tar.TarUnArchiver;

/**
 *
 * @author Adam Dubiel
 */
public final class TarUtil {

    private TarUtil() {
    }

    public static void untar(File source, File target, Log logger) {

        target.mkdirs();
        TarUnArchiver tarUnArchiver = new TarUnArchiver();
        tarUnArchiver.enableLogging(new MavenLogToPlexusLogger(logger));
        tarUnArchiver.setSourceFile(source);
        tarUnArchiver.setDestDirectory(target);
        tarUnArchiver.extract();
        
    }
    
}
