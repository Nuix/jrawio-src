/***********************************************************************************************************************
 *
 * jrawio - a Java(TM) Image I/O SPI Provider for Camera Raw files
 * ===============================================================
 *
 * Copyright (C) 2003-2009 by Tidalwave s.a.s. (http://www.tidalwave.it)
 * http://jrawio.tidalwave.it
 *
 ***********************************************************************************************************************
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
 ***********************************************************************************************************************
 *
 * $Id$
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio.rawprocessor.raw;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.awt.image.DataBufferUShort;
import java.awt.image.WritableRaster;
import it.tidalwave.imageio.raw.TagRational;
import it.tidalwave.imageio.rawprocessor.OperationSupport;
import it.tidalwave.imageio.rawprocessor.ColorMatrix;
import it.tidalwave.imageio.rawprocessor.RAWImage;
import it.tidalwave.imageio.util.Logger;
import javax.annotation.CheckForNull;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public abstract class ColorConversionOperation extends OperationSupport
  {
    private final static Logger logger = getLogger(ColorConversionOperation.class);
    
    /*******************************************************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************************************************/
    public void process (final @Nonnull RAWImage image)
      {
        final ColorMatrix colorMatrix = getColorMatrix(image);
        
        if (colorMatrix != null)
          {
            applyMatrix(image.getImage().getRaster(), colorMatrix);
          }
      }
    
    /*******************************************************************************************************************
     * 
     * 
     ******************************************************************************************************************/
    @CheckForNull
    protected ColorMatrix getColorMatrix (final @Nonnull RAWImage image)
      {
        final ColorMatrix colorMatrix = getColorMatrixXYZ(image);
        
        if (colorMatrix != null)
          {
            logger.info(">>>> colorMatrix in XYZ space: %s", colorMatrix);
            ColorMatrix colorMatrixRGB = colorMatrix.product(ColorMatrix.XYZ_TO_RGB);
            logger.info(">>>> colorMatrix in RGB space: %s", colorMatrixRGB);
            colorMatrixRGB.normalizeRows(); // FIXME: investigate if it's correct
            logger.info(">>>> colorMatrix in RGB space, normalized: %s", colorMatrixRGB);
            colorMatrixRGB = colorMatrixRGB.inverse();
            logger.info(">>>> final colorMatrix: %s", colorMatrixRGB);
            return colorMatrixRGB;
          }
        
        return null;
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @CheckForNull
    protected ColorMatrix getColorMatrixXYZ (final @Nonnull RAWImage image)
      {
        return null;   
      }
    
    /*******************************************************************************************************************
     *
     * @param matrix
     * @return
     *
     ******************************************************************************************************************/
    @CheckForNull
     protected static ColorMatrix getMatrix (final @Nonnull TagRational[] matrix)
        {
          final double c[] = new double[matrix.length];
        
          for (int i = 0; i < c.length; i++)
            {
              c[i] = matrix[i].doubleValue();
            }
        
          return new ColorMatrix(c);
        }
    
    /*******************************************************************************************************************
     *
     * @param matrix
     * @return
     *
     ******************************************************************************************************************/
    @CheckForNull
    protected static ColorMatrix getMatrix (final @Nonnull int[] matrix, final @Nonnegative double scale)
      {
        final double c[] = new double[matrix.length];
        
        for (int i = 0; i < c.length; i++)
          {
            c[i] = scale * matrix[i];
          }
        
        return new ColorMatrix(c);
      }
    
    /*******************************************************************************************************************
     *
     * TODO: rewrite using a standard Java library!
     * @param raster
     *
     ******************************************************************************************************************/
    private static void applyMatrix (@Nonnull WritableRaster raster, final @Nonnull ColorMatrix colorMatrix1)
      {
        logger.fine("applyMatrix(%s, %s)", raster, colorMatrix1);
        
        long time = System.currentTimeMillis();
        final DataBufferUShort dataBuffer = (DataBufferUShort)raster.getDataBuffer();
        final short[] data = dataBuffer.getData();
        int w = raster.getWidth();
        int h = raster.getHeight();
        
        while (raster.getParent() != null)
          {
            raster = (WritableRaster)raster.getParent();
          }
        
        int pixelStride = raster.getNumBands();
        int scanStride = raster.getWidth() * pixelStride;
        
        logger.finer(">>>> pixelStride: %d, scanStride: %d", pixelStride, scanStride);
        logger.finer(">>>> offset: %d", dataBuffer.getOffset());
        
        // FIXME: you should be able to find the offset of the child raster within the parent
        // Until you don't do, scan over the whole parent raster
        w = raster.getWidth();
        h = raster.getHeight();
        
        for (int y = 0; y < h; y++)
          {
            int scan = y * scanStride + dataBuffer.getOffset();
            
            for (int x = 0; x < w; x++)
              {
                colorMatrix1.process(data, scan);
                scan += pixelStride;
              }
            
            Thread.yield();
          }
        
        logger.finest(">>>> applyMatrix() completed ok in %d msec.", + (System.currentTimeMillis() - time));
      }
  }
