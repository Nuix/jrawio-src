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
 * $Id: CR2ImageReader.java 55 2008-08-21 19:43:51Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.cr2;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.logging.Logger;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.cr2.CanonCR2MakerNote;
import it.tidalwave.imageio.raw.Directory;
import it.tidalwave.imageio.raw.RAWMetadataSupport;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.TIFFImageReaderSupport;
import it.tidalwave.imageio.tiff.TIFFMetadataSupport;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: CR2ImageReader.java 55 2008-08-21 19:43:51Z fabriziogiudici $
 *
 ******************************************************************************/
public class CR2ImageReader extends TIFFImageReaderSupport
  {
    private final static Logger logger = Logger.getLogger("it.tidalwave.imageio.crw.CR2ImageReader");

    /*******************************************************************************
     * 
     * @param originatingProvider
     * 
     *******************************************************************************/
    protected CR2ImageReader (ImageReaderSpi originatingProvider)
      {
        super(originatingProvider, CanonCR2MakerNote.class, CR2Metadata.class);
      }

    /*******************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    protected WritableRaster loadRAWRaster() throws IOException
      {
        logger.fine("loadRAWRaster(iis: " + iis + ")");
        long time = System.currentTimeMillis();
        
        CR2RasterReader rasterReader = new CR2RasterReader();
        IFD rasterIFD = ((CR2Metadata)metadata).getRasterIFD();
        CanonCR2MakerNote cr2MakerNote = (CanonCR2MakerNote)makerNote;
        rasterReader.setWidth(cr2MakerNote.getSensorWidth());
        rasterReader.setHeight(cr2MakerNote.getSensorHeight());
        rasterReader.setBitsPerSample(12); // FIXME - gets from the model
        rasterReader.setCFAPattern(new byte[] { 0, 1, 1, 2 }); // FIXME RGGB - gets from the model
        iis.seek(rasterIFD.getStripOffsets());
        rasterReader.setStripByteCount(rasterIFD.getStripByteCounts()); 
        rasterReader.setCompression(rasterIFD.getCompression().intValue()); 
        
        if (rasterIFD.isCanonTileInfoAvailable())
          {
            int[] tileInfo = rasterIFD.getCanonTileInfo();
            rasterReader.setCanonTileWidth(tileInfo[1]);
            rasterReader.setCanonLastTileWidth(tileInfo[2]);
          }
        
        WritableRaster raster = rasterReader.loadRaster(iis, this);
        logger.finer(">>>> loadRAWRaster() completed ok in " + (System.currentTimeMillis() - time) + " msec.");
        
        return raster;
      }
  }
