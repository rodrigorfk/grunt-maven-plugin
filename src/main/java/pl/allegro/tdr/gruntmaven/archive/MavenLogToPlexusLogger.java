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

package pl.allegro.tdr.gruntmaven.archive;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.logging.AbstractLogger;
import org.codehaus.plexus.logging.Logger;

/**
 *
 * @author rodrigokuntzer
 */
public class MavenLogToPlexusLogger extends AbstractLogger{

    private final Log log;
    
    public MavenLogToPlexusLogger(Log log) {
        super(LEVEL_INFO, "maven.logger");
        this.log = log;
    }

    @Override
    public void debug(String message, Throwable throwable) {
        if(throwable != null){
            log.debug(message, throwable);
        }else{
            log.debug(message);
        }
    }

    @Override
    public void info(String message, Throwable throwable) {
        if(throwable != null){
            log.info(message, throwable);
        }else{
            log.info(message);
        }
    }

    @Override
    public void warn(String message, Throwable throwable) {
        if(throwable != null){
            log.warn(message, throwable);
        }else{
            log.warn(message);
        }
    }

    @Override
    public void error(String message, Throwable throwable) {
        if(throwable != null){
            log.error(message, throwable);
        }else{
            log.error(message);
        }
    }

    @Override
    public void fatalError(String message, Throwable throwable) {
        if(throwable != null){
            log.error(message, throwable);
        }else{
            log.error(message);
        }
    }

    @Override
    public Logger getChildLogger(String name) {
        return this;
    }
    
}
