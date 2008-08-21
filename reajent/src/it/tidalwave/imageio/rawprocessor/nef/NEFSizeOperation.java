/*******************************************************************************
 *
 * jrawio - a Java(TM) ImageIO API Spi Provider for RAW files
 * ==========================================================
 *
 * Copyright (C) 2003-2008 by Fabrizio Giudici
 * Project home page: http://jrawio.tidalwave.it
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 *
 *******************************************************************************
 *
 * $Id: NEFSizeOperation.java 55 2008-08-21 19:43:51Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor.nef;

import java.awt.Dimension;
import java.util.logging.Logger;
import java.awt.Insets;
import it.tidalwave.imageio.nef.NEFMetadata;
import it.tidalwave.imageio.nef.NikonCaptureEditorMetadata;
import it.tidalwave.imageio.rawprocessor.RAWImage;
import it.tidalwave.imageio.rawprocessor.raw.SizeOperation;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: NEFSizeOperation.java 55 2008-08-21 19:43:51Z fabriziogiudici $
 *
 ******************************************************************************/
public class NEFSizeOperation extends SizeOperation
  {
    private final static Logger logger = getLogger(NEFSizeOperation.class);
    
    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
    protected Insets getCrop (RAWImage image)
      {
        logger.fine("getCrop()");
        Insets crop = super.getCrop(image);
        int rotation = normalized(image.getRotation());
        crop = rotateCrop(crop, rotation);
        logger.finer(">>>> Standard crop - left: " + crop.left + ", top: " + crop.top + ", right: " + crop.right + ", bottom: " + crop.bottom);

        NEFMetadata metadata = (NEFMetadata)image.getRAWMetadata();
        NikonCaptureEditorMetadata nceMetadata = (NikonCaptureEditorMetadata)metadata.getCaptureEditorMetadata();

        if (nceMetadata != null)
          {
            double scale = 0.5;
            //
            // NCE crop settings are relative to the rotated image
            //
            int left = (int)Math.round(nceMetadata.getCropLeft() * scale);
            int top = (int)Math.round(nceMetadata.getCropTop() * scale);
            int right = (int)Math.round(nceMetadata.getCropWidth() * scale);
            int bottom = (int)Math.round(nceMetadata.getCropHeight() * scale);
            logger.fine(">>>> NCE crop - left: " + left + ", top: " + top + ", right: " + right + ", bottom: " + bottom);
            
            if (metadata.getPrimaryIFD().getModel().trim().equals("NIKON D1X"))
              {
                bottom /= 2; // ??  
              } 

            Dimension size = getSize(image);
            
            if ((rotation == 90) || (rotation == 270))
              {
                int tmp = size.width;
                size.width = size.height;
                size.height = tmp;
              }
            
            logger.fine(">>>> Standard size: " + size.width + " x " + size.height);

            crop.left += left;
            crop.top += top;
            crop.right += size.width - right;
            crop.bottom += size.height - bottom;
          }
        
        logger.fine(">>>> returning: " + crop);
        
        return crop;
      }
    
    private int normalized (int angle)
      {
        while (angle < 0)
          {
            angle += 360;  
          }
        
        return angle % 360;
      }
    
    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
/*    protected Dimension getSize (RAWImage image)
      {
        logger.fine("getSize()");
                
        return new Dimension(width, height);
      }*/
  }
