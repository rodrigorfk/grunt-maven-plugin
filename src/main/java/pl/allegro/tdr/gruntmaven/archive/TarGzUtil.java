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
import java.util.zip.GZIPInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;

/**
 *
 * @author Adam Dubiel
 */
public final class TarGzUtil {

    private TarGzUtil() {
    }

    public static void untar(File source, File target, Log logger) {

        target.mkdirs();
        
        FileInputStream fis = null;
        TarArchiveInputStream input = null;
        try{
            fis = new FileInputStream(source);
            input = new TarArchiveInputStream(new GZIPInputStream(fis));
            
            TarArchiveEntry entry = null;
            while ((entry = input.getNextTarEntry()) != null) {
                
                File outputFile = new File(target.getCanonicalPath() + File.separator + entry.getName());
                if (entry.isDirectory()) {
                    outputFile.mkdirs();
                }else{
                    FileOutputStream fos = null;
                    try{
                        fos=new FileOutputStream(outputFile);
                        IOUtils.copy(input, fos);
                    }finally{
                        IOUtils.closeQuietly(fos);
                    }
                }
            }
            
        }catch(IOException ex){
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(fis);
        }
    }
    
}
