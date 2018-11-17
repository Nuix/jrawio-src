/***********************************************************************************************************************
 *
 * jrawio - a Java(TM) Image I/O SPI Provider for Camera Raw files
 * Copyright (C) 2003 - 2016 by Tidalwave s.a.s.
 *
 ***********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 ***********************************************************************************************************************
 *
 * WWW: http://jrawio.rawdarkroom.org
 * SCM: https://kenai.com/hg/jrawio~src
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio.rawprocessor.nef;

import javax.annotation.Nonnull;
import it.tidalwave.imageio.util.Logger;
import it.tidalwave.imageio.nef.NEFMetadata;
import it.tidalwave.imageio.nef.NikonMakerNote3;
import it.tidalwave.imageio.rawprocessor.PipelineArtifact;
import it.tidalwave.imageio.rawprocessor.raw.CurveOperation;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class NEFCurveOperation extends CurveOperation  
  {
    private final static Logger logger = getLogger(NEFCurveOperation.class);
    
    /*******************************************************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************************************************/
    @Override
    protected double getWhiteLevel (final @Nonnull PipelineArtifact artifact)
      {
        logger.fine("getWhiteLevel()");
        NEFMetadata metadata = (NEFMetadata)artifact.getRAWMetadata();
        NikonMakerNote3 makerNote = metadata.getNikonMakerNote();
        double whiteLevel;
        
        if (makerNote.isCompressionDataAvailable())
          {
            whiteLevel = makerNote.getCompressionDataObject().getWhiteLevel();
            logger.finer(">>>> whiteLevel from linearizationTable: %s", whiteLevel);
          }
        
        else
          {
            whiteLevel = super.getWhiteLevel(artifact);
          }
        
        return whiteLevel;
      }
  }
